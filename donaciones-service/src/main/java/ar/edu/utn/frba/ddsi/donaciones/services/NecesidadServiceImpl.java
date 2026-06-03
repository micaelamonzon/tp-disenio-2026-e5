package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.NecesidadDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
@Service
public class NecesidadServiceImpl implements NecesidadService{
    private final Map<Long, Map<Long, Necesidad>> necesidadesPorEntidad = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    public List<NecesidadDTO> obtenerNecesidades(Long idEntidad) {
        return necesidadesPorEntidad
                .getOrDefault(idEntidad, new HashMap<>())
                .entrySet().stream()
                .map(e -> toDTO(e.getKey(), e.getValue()))
                .toList();
    }

    public NecesidadDTO crearNecesidad(Long idEntidad, NecesidadDTO body) {
        Necesidad nueva = construir(body);
        Long id = contador.getAndIncrement();
        necesidadesPorEntidad
                .computeIfAbsent(idEntidad, k -> new HashMap<>())
                .put(id, nueva);
        return toDTO(id, nueva);
    }

    public NecesidadDTO modificarNecesidad(Long idEntidad, Long idNecesidad, NecesidadDTO body) {
        Map<Long, Necesidad> mapa = necesidadesPorEntidad.get(idEntidad);
        if (mapa == null || !mapa.containsKey(idNecesidad)) {
            throw new RuntimeException("Necesidad no encontrada");
        }
        Necesidad modificada = construir(body);
        mapa.put(idNecesidad, modificada);
        return toDTO(idNecesidad, modificada);
    }

    public void eliminarNecesidad(Long idEntidad, Long idNecesidad) {
        Map<Long, Necesidad> mapa = necesidadesPorEntidad.get(idEntidad);
        if (mapa == null || mapa.remove(idNecesidad) == null) {
            throw new RuntimeException("Necesidad no encontrada");
        }
    }

    private Necesidad construir(NecesidadDTO body) {
        Subcategoria sub = new Subcategoria(body.subcategoria(), false, null);
        if ("RECURRENTE".equalsIgnoreCase(body.tipo())) {
            return new NecesidadRecurrente(sub, body.descripcion(),
                    body.cantidad(), TipoPeriodo.valueOf(body.periodo()));
        }
        return new NecesidadExtraordinaria(sub, body.descripcion(), body.cantidad());
    }

    private NecesidadDTO toDTO(Long id, Necesidad n) {
        String tipo;
        int cantidad;
        String periodo = null;

        if (n instanceof NecesidadRecurrente nr) {
            tipo = "RECURRENTE";
            cantidad = nr.getCantidadObjetivoPorPeriodo();
            periodo = nr.getPeriodo().name();
        } else {
            NecesidadExtraordinaria ne = (NecesidadExtraordinaria) n;
            tipo = "EXTRAORDINARIA";
            cantidad = ne.getCantidadRequerida();
        }

        return new NecesidadDTO(
                id,
                tipo,
                n.getSubcategoria().getNombre(),
                n.getDescripcion(),
                n.isEstaSatisfecha(),
                cantidad,
                periodo
        );
}}

