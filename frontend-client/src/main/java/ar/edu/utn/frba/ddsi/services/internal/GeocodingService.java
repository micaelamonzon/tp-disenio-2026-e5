package ar.edu.utn.frba.ddsi.services.internal;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {
    private static final WebClient client = WebClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", "DonaTrack-UTN-TP")
            .build();

    /** Devuelve {lat, lng} o null si no encuentra la dirección. */
    public static double[] geocodificar(String direccion) {
        try {
            List<Map<String, Object>> res = client.get()
                    .uri(u -> u.path("/search")
                            .queryParam("q", direccion + ", Buenos Aires, Argentina")
                            .queryParam("format", "json").queryParam("limit", 1).build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();
            if (res == null || res.isEmpty()) return null;
            return new double[]{
                    Double.parseDouble((String) res.get(0).get("lat")),
                    Double.parseDouble((String) res.get(0).get("lon"))};
        } catch (Exception e) {
            return null; // si falla, se registra igual sin coordenadas
        }
    }
}