package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.config.RestProperties;
import ar.edu.utn.frba.ddsi.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.RankingMensual;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class RankingMensualService {

    private final RestTemplate restTemplate;
    private final IncentivosRepository repository;
    private final RestProperties propiedades;
    private final IncentivosService incentivosService;

    public RankingMensualService(RestTemplate restTemplate, IncentivosRepository repository, RestProperties propiedades,IncentivosService incentivosService) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.propiedades = propiedades;
        this.incentivosService = incentivosService;
    }

    //primer dia del mes, se repite todos los meses
    @Scheduled(cron = "0 0 0 1 * *")
    public void ejecutarRankingMensual() {
        incentivosService.calcularYGuardarRanking();
    }
}
