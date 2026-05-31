package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import org.springframework.stereotype.Service;

@Service
public class RankingMensualService {
    private final IncentivosRepository repository;

    public RankingMensualService(IncentivosRepository repository){
        this.repository = repository;
    } //TODO : terminar rankingMensual, que necesito los endpoints de donantes.
         /*       // segundo 0 minuto 0 horas 00:00 dia del mes 1 * todos los meses * cualqueir dia
    @Scheduled(cron = "0 0 0 1 * *")
    public void calcularRankingMensual(){
        List<PersonaHumana> donantes = repository.findAllDonantes();

        List<PersonaHumana> ranking = donantes.stream()
                .sorted(Comparator.comparingInt(PersonaHumana::
                        getMisionesCumplidasEnMesActual))
                .toList().reversed().stream().limit(3).toList();

    RankingMensual resultado = new RankingMensual(
            LocalDate.now(),
            ranking.get(0),
            ranking.get(1),
            ranking.get(2)
    );

    repository.guardarRanking(resultado);
}*/
}
