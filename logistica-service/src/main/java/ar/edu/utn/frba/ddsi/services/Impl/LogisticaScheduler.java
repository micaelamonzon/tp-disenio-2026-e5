package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.DonacionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.DonacionesClient;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public class LogisticaScheduler {
    private final PlanificacionRutas planificacionRutas;
    //private final EstadoDonacionServiceimpl estadoDonacionesService;
    private final CamionService camionService;

    public LogisticaScheduler(PlanificacionRutas planificacionRutas, CamionService camionService){
        this.planificacionRutas = planificacionRutas;
        this.camionService = camionService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void planificadorRutasDelDiaSiguiente(){
        List<DonacionDTO> donaciones = List.of(); // TODO:  no sabiamos como traernos una funcion de otro servicio

        List<Camion> camiones = camionService.obtenerCamionesDisponibles();

        if(donaciones.isEmpty() || camiones.isEmpty()) return;

        planificacionRutas.solicitarPlanificacion(camiones, donaciones);
    }
}
