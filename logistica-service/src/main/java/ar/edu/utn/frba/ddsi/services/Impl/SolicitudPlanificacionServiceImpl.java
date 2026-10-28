package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.models.entities.EstadoPlanificacion;
import ar.edu.utn.frba.ddsi.models.entities.SolicitudPlanificacion;
import ar.edu.utn.frba.ddsi.repository.impl.SolicitudPlanificacionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SolicitudPlanificacionServiceImpl {

    private static SolicitudPlanificacionRepository repository;

    public SolicitudPlanificacionServiceImpl(SolicitudPlanificacionRepository repository){
        SolicitudPlanificacionServiceImpl.repository = repository;
    }

    public static void marcarComoFallida(String s, String mensaje) {
    }

    public void registrarSolicitud (String requestId){
        repository.save(new SolicitudPlanificacion(requestId));
    }

    public static boolean yaFueProcesado(String requestId){
        Optional<SolicitudPlanificacion> solicitud = repository.findById(requestId);

        if(solicitud.isEmpty()) {
            System.err.println("Callback recibido para requestId desconocido: " + requestId);
            return true;
        }
        return solicitud.get().getEstado() != EstadoPlanificacion.PENDIENTE;
    }

    public static void marcarComoCompletada(String requestId, String resultadoJson){
        repository.findById(requestId).ifPresentOrElse(
                solicitud -> {solicitud.completar(resultadoJson);
                repository.save(solicitud);
                },
                () -> System.err.println("No se pudo marcar como completada: requestId desconocido " + requestId)
        );
    }

    public void marcarComoFallada(String requestId, String mensajeError) {
        repository.findById(requestId).ifPresentOrElse(
                solicitud -> {
                    solicitud.fallar(mensajeError);
                    repository.save(solicitud);
                },
                () -> System.err.println("No se pudo marcar como fallida: requestId desconocido " + requestId)
        );
    }
}
