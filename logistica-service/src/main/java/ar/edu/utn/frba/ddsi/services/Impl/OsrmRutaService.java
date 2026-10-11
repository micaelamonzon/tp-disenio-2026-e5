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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class OsrmRutaService {

    private static final Logger log = LoggerFactory.getLogger(OsrmRutaService.class);

    private final double DEPOSITO_LAT;
    private final double DEPOSITO_LON;
    private final long MIN_INTERVALO_MS;


    private final RestClient restClient;
    private final Map<String, List<PosicionDTO>> cache = new ConcurrentHashMap<>();
    private long ultimaLlamada = 0;
    private final String routePath;

    public OsrmRutaService(@Value("${osrm.base-url}") String baseUrl,
                           @Value("${osrm.user-agent}") String userAgent,
                           @Value("${osrm.route-path}") String routePath,
                           @Value("${logistica.intervalo.osmr}") long intervaloMs,
                           @Value("${logistica.deposito.lat}") double depositoLat,
                           @Value("${logistica.deposito.lon}") double depositoLon) {
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
}