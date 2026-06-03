package ar.edu.utn.frba.ddsi.donaciones.repositories;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Donacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.donacion.Estado;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class DonacionesRepositoryImpl implements DonacionesRepository{

    private final Map<Long, DonacionSegmentada> donaciones = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Long guardar(DonacionSegmentada donacion) {
        Long id = contador.getAndIncrement();
        donaciones.put(id, donacion);
        return id;
    }

    @Override
    public List<DonacionSegmentada> findByEstado(String nombreEstado) {
        return donaciones.values().stream()
                .filter(d -> d.getNombreEstadoActual().equals(nombreEstado))
                .toList();
    }


}
