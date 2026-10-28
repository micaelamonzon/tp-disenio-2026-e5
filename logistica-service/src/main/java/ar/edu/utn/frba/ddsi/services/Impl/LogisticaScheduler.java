package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.CamionDTO;
import ar.edu.utn.frba.ddsi.dto.DonacionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Donacion;
import ar.edu.utn.frba.ddsi.services.DonacionesClient;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryDonacion;
import ar.edu.utn.frba.ddsi.services.CamionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogisticaScheduler {
    private final PlanificacionRutas planificacionRutas;
    private final DonacionesClient donacionesClient;
    private final CamionService camionService;
    private final RepositoryDonacion repositoryDonacion;

    public LogisticaScheduler(PlanificacionRutas planificacionRutas, DonacionesClient donacionesClient, CamionService camionService, RepositoryDonacion repositoryDonacion){
        this.planificacionRutas = planificacionRutas;
        this.donacionesClient = donacionesClient;
        this.camionService = camionService;
        this.repositoryDonacion = repositoryDonacion;
    }

    @Scheduled(cron = "0 0 2 * * *")
//    @Scheduled(fixedRate = 120000)
    public void planificadorRutasDelDiaSiguiente(){
        List<DonacionDTO> donaciones = donacionesClient.obtenerDonacionesAsignadas();

       donaciones.forEach(d-> {

           this.repositoryDonacion.save(
               new Donacion(
                       d.codigo().longValue(),
                       d.tipoDonante(),
                       d.entidadBeneficiariaId(),
                       d.latitudEntidad(),
                       d.longitudEntidad(),
                       d.donanteId(),
                       d.direccionEntidad(),
                       d.subcategoria(),
                       d.estado()
               )
       );});

        List<CamionDTO> camiones = camionService.obtenerCamionesDisponibles();

        if(donaciones.isEmpty() || camiones.isEmpty()) return;

        planificacionRutas.solicitarPlanificacion(camiones, donaciones);

    }
}
