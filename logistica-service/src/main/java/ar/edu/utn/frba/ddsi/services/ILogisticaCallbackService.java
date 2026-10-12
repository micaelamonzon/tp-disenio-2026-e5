package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dto.PlanningResultDTO;
import org.springframework.http.ResponseEntity;

public interface ILogisticaCallbackService {
    public ResponseEntity<Void> recibirCallback(String firma, String bodyRaw);
    public void actualizarSolicitud(String requestId, PlanningResultDTO resultado);
    public void marcarSolicitudFallida(String requestId, String mensaje);
    public void procesarRutasCompletadas(PlanningResultDTO data);
    public Long extraerDonacionId(String deliveryCode);
}
