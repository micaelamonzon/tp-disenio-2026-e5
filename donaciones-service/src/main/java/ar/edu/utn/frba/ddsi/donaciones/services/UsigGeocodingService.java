package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.CoordenadasDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsigGeocodingService {
    private final RestClient nominatimClient = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", "DonaTrack-UTN-TP")
            .build();

    public Optional<CoordenadasDTO> geocodificar(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            return Optional.empty();
        }

        try {
            // Se le suma la localidad para acotar la búsqueda a Buenos Aires
            String direccionCompleta = direccion + ", Buenos Aires, Argentina";

            List<Map<String, Object>> respuesta = nominatimClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", direccionCompleta)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (respuesta == null || respuesta.isEmpty()) {
                return Optional.empty();
            }

            Map<String, Object> primerResultado = respuesta.get(0);
            double latitud = Double.parseDouble((String) primerResultado.get("lat"));
            double longitud = Double.parseDouble((String) primerResultado.get("lon"));
            String displayNombre = (String) primerResultado.get("display_name");

            return Optional.of(new CoordenadasDTO(latitud, longitud, displayNombre));

        } catch (Exception e) {
            System.err.println("Error al consultar Nominatim: " + e.getMessage());
            return Optional.empty();
        }
    }
}
