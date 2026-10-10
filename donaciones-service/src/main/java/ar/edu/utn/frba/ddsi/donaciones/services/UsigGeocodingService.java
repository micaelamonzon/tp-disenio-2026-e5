package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.CoordenadasDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsigGeocodingService {
    private final String URL_NORMALIZAR;
    private final String URL_CONVERTIR;

    private final RestClient usigClient;
    private final ConcurrentHashMap<String, Optional<CoordenadasDTO>> cache = new ConcurrentHashMap<>();


    public UsigGeocodingService(@Value("${url.normalizar}") String urlNormalizar,
                                @Value("${url.convertir}") String urlConvertir, RestClient usigClient) {
        this.URL_NORMALIZAR = urlNormalizar;
        this.URL_CONVERTIR = urlConvertir;
        this.usigClient = usigClient;

    }

    public Optional<CoordenadasDTO> geocodificar(String direccion) {
        String clave = direccion.trim().toLowerCase();

        Optional<CoordenadasDTO> enCache = cache.get(clave);
        if (enCache != null) {
            return enCache;
        }

        Optional<CoordenadasDTO> resultado = consultar(direccion);
        if (resultado.isPresent()) {          // solo guardar si se encontró
            cache.put(clave, resultado);
        }
        return resultado;
    }

    private Optional<CoordenadasDTO> consultar(String direccion) {

        String direccionLimpia = limpiarDireccion(direccion);

        JsonNode normalizado = usigClient.get()
                .uri(URL_NORMALIZAR + "?geocodificar=true&direccion={d}", direccionLimpia)
                .retrieve()
                .body(JsonNode.class);

        System.out.println("NORMALIZAR -> " + normalizado);

        if (normalizado == null) {
            return Optional.empty();
        }

        JsonNode lista = normalizado.path("direccionesNormalizadas");
        if (!lista.isArray() || lista.isEmpty()) {
            return Optional.empty();
        }

        JsonNode primera = lista.get(0);

        JsonNode coords = primera.path("coordenadas");

        if (coords.isMissingNode() || coords.path("x").isMissingNode()) {
            return Optional.empty();
        }

        double x = coords.path("x").asDouble();
        double y = coords.path("y").asDouble();
        String direccionNormalizada = primera.path("direccion").asText();


        JsonNode convertidoAlatYlong = usigClient.get()
                .uri(URL_CONVERTIR + "?x={x}&y={y}&output=lonlat", x, y)
                .retrieve()
                .body(JsonNode.class);

        System.out.println("CONVERTIR -> " + convertidoAlatYlong);

        if (convertidoAlatYlong == null) {
            return Optional.empty();
        }

        JsonNode res = convertidoAlatYlong.path("resultado");
        if (res.isMissingNode()) {
            return Optional.empty();
        }


        double longitud = res.path("x").asDouble();
        double latitud = res.path("y").asDouble();

        return Optional.of(new CoordenadasDTO(latitud, longitud, direccionNormalizada));
    }

    private String limpiarDireccion(String direccion) {
        var m = java.util.regex.Pattern
                .compile("^\\s*([^,\\d]+?)\\s+(\\d+)")
                .matcher(direccion);
        if (m.find()) {
            return m.group(1).trim() + " " + m.group(2);
        }
        return direccion.trim();
    }
}
