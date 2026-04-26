package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import lombok.Data;

import java.util.List;

@Data
public class Necesidad {
    private Subcategoria subcategoria;
    private String descripcion;
    private TipoDeNecesidad tipoDeNecesidad;
  private List<Bien> bienesNecesitados;
}
