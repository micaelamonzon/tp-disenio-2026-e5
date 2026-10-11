package ar.edu.utn.frba.ddsi.services.Impl;
import ar.edu.utn.frba.ddsi.dto.OsmrResponseDTO;
import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.models.entities.OsmrResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;


@Service
public class OsrmRutaService {

    private static final Logger log = LoggerFactory.getLogger(OsrmRutaService.class);

    private final double DEPOSITO_LAT;
    private final double DEPOSITO_LON;
    private final long MIN_INTERVALO_MS;

    private final String baseUrl;
    private final String multiRoutePath;
    private final Map<String, RutaConParadas> cacheParadas = new ConcurrentHashMap<>();

    public record RutaConParadas(List<PosicionDTO> ruta, List<Integer> indicesParadas) {}

    private final RestClient restClient;
    private final Map<String, List<PosicionDTO>> cache = new ConcurrentHashMap<>();
    private long ultimaLlamada = 0;
    private final String routePath;

    public OsrmRutaService(@Value("${osrm.base-url}") String baseUrl,
                           @Value("${osrm.user-agent}") String userAgent,
                           @Value("${osrm.route-path}") String routePath,
                           @Value("${logistica.intervalo.osmr}") long intervaloMs,
                           @Value("${logistica.deposito.lat}") double depositoLat,
                           @Value("${osrm.multi-route-path}") String multiRoutePath,
                           @Value("${logistica.deposito.lon}") double depositoLon) {
        this.baseUrl = baseUrl;
        this.multiRoutePath = multiRoutePath;
        this.MIN_INTERVALO_MS = intervaloMs;
        this.routePath = routePath;
        this.DEPOSITO_LAT = depositoLat;
        this.DEPOSITO_LON = depositoLon;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();
    }

    public List<PosicionDTO> obtenerRutaDesdeDeposito(double destLat, double destLon) {
        String clave = destLat + "," + destLon;
        List<PosicionDTO> cacheada = cache.get(clave);
        return cacheada != null ? cacheada : calcularRuta(clave, destLat, destLon);
    }

    // synchronized: serializa las llamadas a OSRM para respetar el límite
    private synchronized List<PosicionDTO> calcularRuta(String clave, double destLat, double destLon) {
        List<PosicionDTO> cacheada = cache.get(clave); // otro hilo pudo calcularla mientras esperábamos
        if (cacheada != null) return cacheada;

        esperarTurno();
        try {
            // OSRM espera lon,lat (al revés que latitud/longitud)
            OsmrResponseDTO resp = restClient.get()
                    .uri(routePath,
                            DEPOSITO_LON, DEPOSITO_LAT, destLon, destLat)
                    .retrieve()
                    .body(OsmrResponseDTO.class);

            if (resp == null || !"Ok".equals(resp.codigo()) || resp.ruta() == null || resp.ruta().isEmpty()) {
                throw new IllegalStateException("OSRM no devolvio una ruta para la entrega");
            }

            List<PosicionDTO> ruta = resp.ruta().get(0).geometry().coordenadas().stream()
                    .map(punto -> new PosicionDTO(punto.get(1), punto.get(0))) // [lon, lat] -> (lat, lon)
                    .toList();

            cache.put(clave, ruta);
            return ruta;

        } catch (RestClientException | IllegalStateException e) {
            log.warn("No se pudo obtener la ruta a ({}, {}): {}", destLat, destLon, e.getMessage());
            // Línea recta como respaldo; no se cachea para reintentar en la próxima consulta
            return List.of(new PosicionDTO(DEPOSITO_LAT, DEPOSITO_LON), new PosicionDTO(destLat, destLon));
        } finally {
            ultimaLlamada = System.currentTimeMillis();
        }
    }

    private void esperarTurno() {
        long espera = MIN_INTERVALO_MS - (System.currentTimeMillis() - ultimaLlamada);
        if (espera > 0) {
            try {
                Thread.sleep(espera);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public RutaConParadas obtenerRutaConParadas(List<PosicionDTO> paradas) {
        String coords = coordenadas(paradas);
        RutaConParadas cacheada = cacheParadas.get(coords);
        return cacheada != null ? cacheada : calcularRutaConParadas(coords, paradas);
    }

    // OSRM: "lon,lat;lon,lat;..." empezando por el depósito
    private String coordenadas(List<PosicionDTO> paradas) {
        StringBuilder sb = new StringBuilder(DEPOSITO_LON + "," + DEPOSITO_LAT);
        paradas.forEach(p -> sb.append(';').append(p.longitud()).append(',').append(p.latitud()));
        return sb.toString();
    }

    private synchronized RutaConParadas calcularRutaConParadas(String coords, List<PosicionDTO> paradas) {
        RutaConParadas cacheada = cacheParadas.get(coords);
        if (cacheada != null) return cacheada;

        esperarTurno();
        try {
            OsmrResponseDTO resp = restClient.get()
                    .uri(URI.create(baseUrl + String.format(multiRoutePath, coords)))
                    .retrieve()
                    .body(OsmrResponseDTO.class);

            if (resp == null || !"Ok".equals(resp.codigo()) || resp.ruta() == null || resp.ruta().isEmpty()) {
                throw new IllegalStateException("OSRM no devolvió una ruta");
            }

            List<PosicionDTO> ruta = resp.ruta().get(0).geometry().coordenadas().stream()
                    .map(punto -> new PosicionDTO(punto.get(1), punto.get(0)))
                    .toList();

            RutaConParadas resultado = new RutaConParadas(ruta, indicesDeParadas(ruta, paradas));
            cacheParadas.put(coords, resultado);
            return resultado;

        } catch (RestClientException | IllegalStateException e) {
            log.warn("No se pudo obtener la ruta con paradas: {}", e.getMessage());
            // Respaldo: líneas rectas depósito -> parada 1 -> parada 2 ... (no se cachea)
            List<PosicionDTO> recta = new ArrayList<>();
            recta.add(new PosicionDTO(DEPOSITO_LAT, DEPOSITO_LON));
            recta.addAll(paradas);
            List<Integer> indices = IntStream.rangeClosed(1, paradas.size()).boxed().toList();
            return new RutaConParadas(recta, indices);
        } finally {
            ultimaLlamada = System.currentTimeMillis();
        }
    }

    // Para cada parada, el punto de la ruta más cercano, buscando siempre hacia adelante
    private List<Integer> indicesDeParadas(List<PosicionDTO> ruta, List<PosicionDTO> paradas) {
        List<Integer> indices = new ArrayList<>();
        int desde = 0;
        for (PosicionDTO parada : paradas) {
            int mejor = desde;
            double mejorD = Double.MAX_VALUE;
            for (int i = desde; i < ruta.size(); i++) {
                double dLat = ruta.get(i).latitud() - parada.latitud();
                double dLon = ruta.get(i).longitud() - parada.longitud();
                double d = dLat * dLat + dLon * dLon;
                if (d < mejorD) { mejorD = d; mejor = i; }
            }
            indices.add(mejor);
            desde = mejor;
        }
        return indices;
    }
}