package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionIncentivosDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ServiceWebHook {
    private static final Logger loggerWebHookIncentivos = LoggerFactory.getLogger(ServiceWebHook.class);
    private final RestClient client;

    public ServiceWebHook(RestClient incentivosClient) {
        this.client = incentivosClient;
    }

    @Async("webhookExecutor")
    public void notificarNuevoDonante(PersonaDonanteDTO donante) {
        enviar("incentivos/webhooks/donante", donante);
    }


    @Async("webhookExecutor")
    public void notificarNuevosDonantes(List<PersonaDonanteDTO> donantes) {
        if (donantes == null || donantes.isEmpty()) return;
        enviar("incentivos/webhooks/donantes", donantes);
    }

    @Async("webhookExecutor")
    public void notificarNuevasDonaciones(List<DonacionIncentivosDTO> donaciones,TipoDonante tipo, Long id) {
        if (donaciones == null || donaciones.isEmpty()) return;
        enviarDonacion("incentivos/webhooks/donaciones/lote/{tipoDonante}/{id}", donaciones, tipo, id);
    }


    public void notificarDonacionEntregada(DonacionSegmentada donacion) {
        if (donacion == null) return;
        enviarDonacionEntregada(donacion.getId());
    }

    private void enviar(String path, Object body) {
        try {
            client.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            loggerWebHookIncentivos.info("Se envia la entidad {} a incentivos", body);
        } catch (Exception e) {
            loggerWebHookIncentivos.error("No se pudo enviar webhook a {} porque sucedio  {}", path, e.getMessage());
        }
    }

    private void enviarDonacion(String path, Object body, TipoDonante tipo, Long id) {
        try {
            client.post()
                    .uri("incentivos/webhooks/donaciones/lote/{tipoDonante}/{id}", tipo.name(), id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            loggerWebHookIncentivos.info("Se envia la entidad {} a incentivos", body);
        } catch (Exception e) {
            loggerWebHookIncentivos.error("No se pudo enviar webhook a {} porque sucedio  {}", path, e.getMessage());
        }
    }

    private void enviarDonacionEntregada(Long idDonacionEntregada) {
        try {
            client.post()
                    .uri("incentivos/webhooks/donacion/{idDonacionEnDonaciones}/fueEntregada", idDonacionEntregada)
                    .retrieve()
                    .toBodilessEntity();

            loggerWebHookIncentivos.info("Se envió el POST de donación entregada a incentivos, ID {}", idDonacionEntregada);
        } catch (Exception e) {
            loggerWebHookIncentivos.error("No se pudo enviar el webhook de donación entregada {} a incentivos: {}",
                    idDonacionEntregada, e.getMessage());
        }
    }
}
