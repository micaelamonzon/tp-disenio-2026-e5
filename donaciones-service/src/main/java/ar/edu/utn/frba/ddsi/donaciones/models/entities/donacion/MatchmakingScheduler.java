package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.services.MatchmakingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MatchmakingScheduler {

    private final MatchmakingService matchmakingService;

    public MatchmakingScheduler(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    //Se ejecuta todos los días automáticamente a las 3am
    @Scheduled(cron = "0 0 3 * * ?")
    public void ejecutarMatchmakingNocturno() {
        matchmakingService.ejecutarProcesoMatchmaking();
    }
}
