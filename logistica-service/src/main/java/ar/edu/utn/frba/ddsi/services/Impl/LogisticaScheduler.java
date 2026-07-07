package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.DonacionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.DonacionesClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogisticaScheduler {
    private final PlanificacionRutas planificacionRutas;
    private final DonacionesClient donacionesClient;
    private final CamionService camionService;

    public LogisticaScheduler(PlanificacionRutas planificacionRutas, DonacionesClient donacionesClient, CamionService camionService){
        this.planificacionRutas = planificacionRutas;
        this.donacionesClient = donacionesClient;
        this.camionService = camionService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void planificadorRutasDelDiaSiguiente(){
        List<DonacionDTO> donaciones = donacionesClient.obtenerDonacionesAsignadas();

        List<Camion> camiones = camionService.obtenerCamionesDisponibles();

        if(donaciones.isEmpty() || camiones.isEmpty()) return;

        planificacionRutas.solicitarPlanificacion(camiones, donaciones);

        //Actualizamos el estado
        for(DonacionDTO donacion : donaciones) {
            donacionesClient.marcarComoListaParaEntregar(Long.valueOf(donacion.codigo()));
        }
    }
}
