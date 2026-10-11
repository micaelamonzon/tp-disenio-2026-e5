package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import ar.edu.utn.frba.ddsi.models.entities.Posicion;
import ar.edu.utn.frba.ddsi.repository.RepositoryCamiones;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryEntregas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "logistica.simulador.habilitado", havingValue = "true")
public class SimuladorGpsService {

    private final RepositoryEntregas repositoryEntregas;
    private final RepositoryCamiones repositoryCamiones;
    private final RutaCamionService rutaCamionService;
    private final int paso;

    public SimuladorGpsService(RepositoryEntregas repositoryEntregas,
                               RepositoryCamiones repositoryCamiones,
                               RutaCamionService rutaCamionService,
                               @Value("${logistica.simulador.paso:5}") int paso) {
        this.repositoryEntregas = repositoryEntregas;
        this.repositoryCamiones = repositoryCamiones;
        this.rutaCamionService = rutaCamionService;
        this.paso = paso;
    }

    @Scheduled(fixedRateString = "${logistica.simulador.intervalo-ms:5000}")
    public void avanzarCamiones() {
        repositoryEntregas.findAll().stream()
                .filter(e -> e.getCamionId() != null)
                .collect(Collectors.groupingBy(Entrega::getCamionId))
                .forEach(this::avanzar);
    }

    private void avanzar(String camionId, List<Entrega> entregas) {
        boolean hayActivas = entregas.stream().anyMatch(e ->
                e.getEstadoActual() == EstadoEntrega.PENDIENTE || e.getEstadoActual() == EstadoEntrega.EN_TRASLADO);
        if (!hayActivas) return;

        Camion camion = repositoryCamiones.findById(camionId).orElse(null);
        if (camion == null) return;

        List<PosicionDTO> ruta = rutaCamionService.armarRuta(entregas).ruta();
        int ultimo = ruta.size() - 1;

        // Por qué punto de la ruta iba: se lee de la última posición simulada (no se deduce por cercanía)
        int actual = camion.getPosiciones().stream()
                .filter(p -> p.getFecha() != null && p.getIndiceRuta() != null)
                .max(Comparator.comparing(Posicion::getFecha))
                .map(Posicion::getIndiceRuta)
                .orElse(-1);

        if (actual >= ultimo) return; // terminó el recorrido completo

        int siguiente = actual < 0 ? 0 : Math.min(actual + paso, ultimo);
        PosicionDTO punto = ruta.get(siguiente);

        Posicion nueva = new Posicion(punto.latitud(), punto.longitud());
        nueva.setIndiceRuta(siguiente);
        camion.agregarPosicion(nueva);
        repositoryCamiones.save(camion);
    }
}