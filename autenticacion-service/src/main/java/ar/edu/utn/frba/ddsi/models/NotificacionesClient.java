package ar.edu.utn.frba.ddsi.models;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificacionesClient {
    private static RestClient client = null;

    public NotificacionesClient(RestClient notificacionesRestClient) {
        this.client = notificacionesRestClient;
    }

    public static void enviarEmail(String destinatario, String mensaje) {
        client.post()
                .uri(b -> b.path("/servicioDeNotificaciones/notificar")
                        .queryParam("destinatario", "{destinatario}")
                        .queryParam("mensaje", "{mensaje}")
                        .queryParam("medio", "EMAIL")
                        .build(destinatario, mensaje))
                .retrieve()
                .toBodilessEntity();
    }
}
