package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;
import ar.edu.utn.frba.ddsi.dto.RutaCamionDTO;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import ar.edu.utn.frba.ddsi.models.entities.EstadoEntrega;
import ar.edu.utn.frba.ddsi.repository.impl.RepositoryEntregas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RutaCamionService {



    private final RepositoryEntregas repositoryEntregas;
    private final OsrmRutaService osrmRutaService;
    private final double depositoLat;
    private final double depositoLon;

    public RutaCamionService(RepositoryEntregas repositoryEntregas,
                             OsrmRutaService osrmRutaService,
                             @Value("${logistica.deposito.lat}") double depositoLat,
                             @Value("${logistica.deposito.lon}") double depositoLon) {
        this.repositoryEntregas = repositoryEntregas;
        this.osrmRutaService = osrmRutaService;
        this.depositoLat = depositoLat;
        this.depositoLon = depositoLon;
    }

    // Ruta del camión de esta entrega (o solo de la entrega si todavía no tiene camión)
    public RutaCamionDTO armarRutaPara(Entrega entrega) {
        List<Entrega> entregas = entrega.getCamionId() == null
                ? List.of(entrega)
                : repositoryEntregas.findByCamionId(entrega.getCamionId()).stream()
                .filter(e -> e.getEstadoActual() == EstadoEntrega.PENDIENTE
                        || e.getEstadoActual() == EstadoEntrega.EN_TRASLADO)
                .toList();
        return armarRuta(entregas);
    }

    public RutaCamionDTO armarRuta(List<Entrega> entregas) {
        List<Entrega> ordenadas = ordenarPorCercania(entregas);
        List<PosicionDTO> paradas = ordenadas.stream()
                .map(e -> new PosicionDTO(Double.parseDouble(e.getLatitud()), Double.parseDouble(e.getLongitud())))
                .toList();

        OsrmRutaService.RutaConParadas r = osrmRutaService.obtenerRutaConParadas(paradas);

        Map<String, Integer> indices = new HashMap<>();
        for (int i = 0; i < ordenadas.size(); i++) {
            indices.put(ordenadas.get(i).getId(), r.indicesParadas().get(i));
        }
        return new RutaCamionDTO(r.ruta(), indices);
    }

    // Vecino más cercano: desde el depósito, siempre a la parada más próxima a la última
    private List<Entrega> ordenarPorCercania(List<Entrega> entregas) {
        List<Entrega> pendientes = new ArrayList<>(entregas);
        pendientes.sort(Comparator.comparing(Entrega::getId)); // base estable para que el orden no cambie entre llamadas
        List<Entrega> orden = new ArrayList<>();
        double lat = depositoLat, lon = depositoLon;

        while (!pendientes.isEmpty()) {
            Entrega mejor = null;
            double mejorD = Double.MAX_VALUE;
            for (Entrega e : pendientes) {
                double dLat = Double.parseDouble(e.getLatitud()) - lat;
                double dLon = Double.parseDouble(e.getLongitud()) - lon;
                double d = dLat * dLat + dLon * dLon;
                if (d < mejorD) { mejorD = d; mejor = e; }
            }
            orden.add(mejor);
            pendientes.remove(mejor);
            lat = Double.parseDouble(mejor.getLatitud());
            lon = Double.parseDouble(mejor.getLongitud());
        }
        return orden;
    }
}