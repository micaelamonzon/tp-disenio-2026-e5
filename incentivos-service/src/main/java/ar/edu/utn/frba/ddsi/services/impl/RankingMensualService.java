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

    public RankingMensualService(RestTemplate restTemplate, IncentivosRepository repository, RestProperties propiedades) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.propiedades = propiedades;
    }

    //primer dia del mes, se repite todos los meses
    @Scheduled(cron = "0 0 0 1 * *")
    public void ejecutarRankingMensual() {
        YearMonth mesPasado = YearMonth.now().minusMonths(1);
        String url = propiedades.getUrl() + "/servicioDeDonaciones/donantes";
        ResponseEntity<PersonaDonanteDTO[]> response = restTemplate.getForEntity(url, PersonaDonanteDTO[].class);
        List<PersonaDonanteDTO> dtos = Arrays.asList(response.getBody());

        List<Donante> donantesLocales = dtos.stream().map(
                dto -> {
                    List<Mision> misionesLocales = dto.misiones().stream()
                            .map(misionDTO -> {
                                Mision m = new Mision(misionDTO.nombre());
                                m.setNombre(misionDTO.nombre());
                                m.setEstadoDeMision(misionDTO.estado());
                                m.setFechaCompletada(misionDTO.fechaCompletada());
                                return m;
                            }).toList();

                    return new Donante(dto.id(),
                            null, null, dto.nombre(), null, null, null,
                            null, null, null, misionesLocales, null
                    );
                }).toList();

        List<Donante> podio = donantesLocales.stream()
                .sorted(Comparator.comparingInt((Donante d) -> d.calcularMisionesCumplidasEn(mesPasado)).reversed())
                .limit(3)
                .toList();

        RankingMensual ranking = new RankingMensual(LocalDate.now(), podio.get(0), podio.get(1), podio.get(2));
    }
}
