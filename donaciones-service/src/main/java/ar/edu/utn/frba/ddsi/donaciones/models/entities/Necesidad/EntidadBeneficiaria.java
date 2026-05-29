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

    public void registrarNecesidadExtraordinaria(Subcategoria subcategoria,
                                                 String descripcion,
                                                 int cantidadRequerida) {
        this.necesidades.add(
                new NecesidadExtraordinaria(subcategoria, descripcion, cantidadRequerida)
        );
    }

    public void registrarNecesidadRecurrente(Subcategoria subcategoria,
                                             String descripcion,
                                             int cantidadObjetivo,
                                             TipoPeriodo periodo) {
        this.necesidades.add(
                new NecesidadRecurrente(subcategoria, descripcion, cantidadObjetivo, periodo)
        );
    }

    public List<Necesidad> obtenerNecesidadesPendientes() {
        return necesidades.stream()
                .filter(n -> !n.isEstaSatisfecha())
                .toList();
    }
}