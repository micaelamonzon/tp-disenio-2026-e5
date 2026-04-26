package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Data
public class EntidadBeneficiaria {
    private String razonSocial;
    private String direccion;
    private Integer telefono;
    private List<String> correosDeRepresentantes;
    private List<Necesidad> necesidades;


    public EntidadBeneficiaria() {
        this.necesidades = new ArrayList<>();
    }

    public void registradorNecesidad(Subcategoria subcategoria,String descripcion, TipoDeNecesidad tipo){
        Necesidad nuevaNecesidad = new Necesidad();

        nuevaNecesidad.setSubcategoria(subcategoria);
        nuevaNecesidad.setDescripcion(descripcion);
        nuevaNecesidad.setTipoDeNecesidad(tipo);

        // Lo agrego a la lista de necesidades
        this.necesidades.add(nuevaNecesidad);
    }

    public List<Necesidad> obtenerNecesidadesPendientes() {
        return necesidades.stream()
                .filter(n -> !n.getTipoDeNecesidad().getEstaSatisfecha())
                .toList();


    }
}
