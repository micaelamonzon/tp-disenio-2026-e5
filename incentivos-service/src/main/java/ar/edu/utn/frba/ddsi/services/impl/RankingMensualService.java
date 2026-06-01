package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class RankingMensualService {

    private final IncentivosService incentivosService;

    public RankingMensualService(IncentivosService incentivosService) {
        this.incentivosService = incentivosService;
    }
                    //primer dia del mes, se repite todos los meses
    @Scheduled(cron = "0 0 0 1 * *")
    public void ejecutarRankingMensual() {
        incentivosService.calcularYGuardarRanking();
    }
}
