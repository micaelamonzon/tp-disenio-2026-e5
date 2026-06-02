package ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.Necesidad;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

public class MatchmakingScheduler {
    private final MotorDeMatchmaking motorMatchmaking;
    private final RepoDonaciones repoDonaciones;   // Para buscar las donaciones pendientes
    private final RepoNecesidades repoNecesidades; // Para buscar las necesidades activas
    private final RepoMatcheo repoMatcheo;         // Donde se guardan los rankings

    private final List<Strategy_AlgoritmosMatchmaking> estrategiasActivas;

    //Se ejecuta todos los días automáticamente a las 3am
    @Scheduled(cron = "0 0 3 * * ?")
    public void ejecutarMatchmakingNocturno() {
        List<Donacion> donacionesPendientes = repoDonaciones.findByEstado(Estado.EN_DEPOSITO);
        List<Necesidad> necesidadesActivas = repoNecesidades.findByEstaSatisfechaFalse();

        if (donacionesPendientes.isEmpty() || necesidadesActivas.isEmpty() || estrategiasActivas.isEmpty()) {
            return;
        }

        for (Donacion donacion : donacionesPendientes) {
            PropuestaMatch propuesta = new PropuestaMatch();
            propuesta.setDonacion(donacion);

            List<List<Necesidad>> todosLosRankings = new ArrayList<>();
            //Ejecutamos cualquier cantidad de algoritmos existentes
            for (Strategy_AlgoritmosMatchmaking estrategia : estrategiasActivas) {
                motorMatchmaking.setEstrategiaActual(estrategia);
                List<Necesidad> ranking = motorMatchmaking.generarRanking(donacion, necesidadesActivas);

                todosLosRankings.add(ranking);

                RankingPorAlgoritmo rankingIndividual = new RankingPorAlgoritmo();
                rankingIndividual.setNombreAlgoritmo(estrategia.getClass().getSimpleName());
                rankingIndividual.setNecesidades(ranking);
                propuesta.getRankingsIndividuales().add(rankingIndividual);
            }

            //Intersección entre todos los rankings
            List<Necesidad> coincidencias = new ArrayList<>(todosLosRankings.get(0));
            for (int i = 1; i < todosLosRankings.size(); i++) {
                coincidencias.retainAll(todosLosRankings.get(i)); // Conserva solo lo común en TODOS los rankings
            }

            if (!coincidencias.isEmpty()) {
                propuesta.setRankingConjunto(coincidencias); //Si hubo coincidencia global
                propuesta.getRankingsIndividuales().clear(); // Limpiamos individuales para no duplicar datos en la BD
            }

            // Si coincidencias está vacío en propuesta ya están los rankings por separado
            repoMatcheo.save(propuesta);

        }



    }


}
