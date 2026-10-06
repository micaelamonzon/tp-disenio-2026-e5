package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.dto.NecesidadDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.repositories.EntidadBeneficiariaRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.NecesidadesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NecesidadServiceImpl implements NecesidadService{

    private final NecesidadesRepository necesidadesRepository;
    private final EntidadBeneficiariaRepository entidadBeneficiariaRepository;
    private final CatalogoSubcategoriasService catalogoSubcategorias;

    public NecesidadServiceImpl(NecesidadesRepository necesidadesRepository,
                                EntidadBeneficiariaRepository entidadBeneficiariaRepository,
                                CatalogoSubcategoriasService catalogoSubcategorias) {
        this.necesidadesRepository = necesidadesRepository;
        this.entidadBeneficiariaRepository = entidadBeneficiariaRepository;
        this.catalogoSubcategorias = catalogoSubcategorias;
    }

    @Transactional(readOnly = true)
    public List<NecesidadDTO> obtenerNecesidades(Long idEntidad) {
        return necesidadesRepository.findByEntidadBeneficiariaId(idEntidad)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public NecesidadDTO crearNecesidad(Long idEntidad, NecesidadDTO body) {
        EntidadBeneficiaria entidad = entidadBeneficiariaRepository.findById(idEntidad)
                .orElseThrow(() -> new RuntimeException("Entidad beneficiaria no encontrada"));
        Necesidad nueva = construir(body);
        nueva.setEntidadBeneficiaria(entidad);
        return toDTO(necesidadesRepository.save(nueva));
    }

    @Transactional
    public NecesidadDTO modificarNecesidad(Long idEntidad, Long idNecesidad, NecesidadDTO body) {
        Necesidad existente = buscarDeEntidad(idEntidad, idNecesidad);
        Subcategoria sub = catalogoSubcategorias.obtenerOCrear(body.subcategoria(), false, null);

        // Mismo tipo: se actualizan los datos conservando el id
        if (existente instanceof NecesidadRecurrente nr && esRecurrente(body)) {
            nr.setSubcategoria(sub);
            nr.setDescripcion(body.descripcion());
            nr.setCantidadObjetivoPorPeriodo(body.cantidad());
            nr.setPeriodo(TipoPeriodo.valueOf(body.periodo()));
            return toDTO(necesidadesRepository.save(nr));
        }
        if (existente instanceof NecesidadExtraordinaria ne && !esRecurrente(body)) {
            ne.setSubcategoria(sub);
            ne.setDescripcion(body.descripcion());
            ne.setCantidadRequerida(body.cantidad());
            return toDTO(necesidadesRepository.save(ne));
        }

        // Cambio de tipo (recurrente <-> extraordinaria): con herencia JOINED no se
        // puede cambiar la clase de una fila, así que se reemplaza por una nueva.
        EntidadBeneficiaria entidad = existente.getEntidadBeneficiaria();
        necesidadesRepository.delete(existente);
        Necesidad nueva = construir(body);
        nueva.setEntidadBeneficiaria(entidad);
        return toDTO(necesidadesRepository.save(nueva));
    }

    @Transactional
    public void eliminarNecesidad(Long idEntidad, Long idNecesidad) {
        necesidadesRepository.delete(buscarDeEntidad(idEntidad, idNecesidad));
    }

    // Busca la necesidad y verifica que pertenezca a la entidad indicada
    private Necesidad buscarDeEntidad(Long idEntidad, Long idNecesidad) {
        Necesidad necesidad = necesidadesRepository.findById(idNecesidad)
                .orElseThrow(() -> new RuntimeException("Necesidad no encontrada"));
        if (necesidad.getEntidadBeneficiaria() == null
                || !idEntidad.equals(necesidad.getEntidadBeneficiaria().getId())) {
            throw new RuntimeException("Necesidad no encontrada");
        }
        return necesidad;
    }

    private boolean esRecurrente(NecesidadDTO body) {
        return "RECURRENTE".equalsIgnoreCase(body.tipo());
    }

    private Necesidad construir(NecesidadDTO body) {
        // La necesidad solo informa el nombre de la subcategoría: se busca en el catálogo
        // para que el matchmaking la cruce con donaciones de exactamente la misma subcategoría.
        Subcategoria sub = catalogoSubcategorias.obtenerOCrear(body.subcategoria(), false, null);
        if (esRecurrente(body)) {
            return new NecesidadRecurrente(sub, body.descripcion(),
                    body.cantidad(), TipoPeriodo.valueOf(body.periodo()));
        }
        return new NecesidadExtraordinaria(sub, body.descripcion(), body.cantidad());
    }

    private NecesidadDTO toDTO(Necesidad n) {
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
                n.getId(),
                tipo,
                n.getSubcategoria().getNombre(),
                n.getDescripcion(),
                n.isEstaSatisfecha(),
                cantidad,
                periodo
        );
    }
}
