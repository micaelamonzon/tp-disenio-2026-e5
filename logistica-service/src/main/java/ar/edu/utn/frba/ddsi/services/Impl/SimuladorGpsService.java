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
    private final OsrmRutaService osrmRutaService;
    private final int paso;

    public SimuladorGpsService(RepositoryEntregas repositoryEntregas,
                               RepositoryCamiones repositoryCamiones,
                               OsrmRutaService osrmRutaService,
                               @Value("${logistica.simulador.paso:5}") int paso) {
        this.repositoryEntregas = repositoryEntregas;
        this.repositoryCamiones = repositoryCamiones;
        this.osrmRutaService = osrmRutaService;
        this.paso = paso;
    }

    @Scheduled(fixedRateString = "${logistica.simulador.intervalo-ms:5000}")
    public void avanzarCamiones() {

        repositoryEntregas.findAll().stream()
                .filter(e -> e.getCamionId() != null)
                .filter(e -> e.getEstadoActual() == EstadoEntrega.PENDIENTE
                        || e.getEstadoActual() == EstadoEntrega.EN_CAMINO)
                .collect(Collectors.toMap(Entrega::getCamionId, Function.identity(), (a, b) -> a))
                .values()
                .forEach(this::avanzar);
    }

    private void avanzar(Entrega entrega) {
        Camion camion = repositoryCamiones.findById(entrega.getCamionId()).orElse(null);
        if (camion == null) return;

        List<PosicionDTO> ruta = osrmRutaService.obtenerRutaDesdeDeposito(
                Double.parseDouble(entrega.getLatitud()),
                Double.parseDouble(entrega.getLongitud()));
        int ultimo = ruta.size() - 1;


        int actual = camion.getPosiciones().stream()
                .filter(p -> p.getFecha() != null)
                .max(Comparator.comparing(Posicion::getFecha))
                .map(p -> indiceMasCercano(ruta, p))
                .orElse(-1);

        if (actual >= ultimo) return;

        int siguiente = actual < 0 ? 0 : Math.min(actual + paso, ultimo);
        PosicionDTO punto = ruta.get(siguiente);

        camion.agregarPosicion(new Posicion(punto.latitud(), punto.longitud()));
        repositoryCamiones.save(camion);
    }

    private int indiceMasCercano(List<PosicionDTO> ruta, Posicion p) {
        int mejor = 0;
        double mejorD = Double.MAX_VALUE;
        for (int i = 0; i < ruta.size(); i++) {
            double dLat = ruta.get(i).latitud() - p.getLatitud();
            double dLon = ruta.get(i).longitud() - p.getLongitud();
            double d = dLat * dLat + dLon * dLon;
            if (d < mejorD) { mejorD = d; mejor = i; }
        }
        return mejor;
    }
}