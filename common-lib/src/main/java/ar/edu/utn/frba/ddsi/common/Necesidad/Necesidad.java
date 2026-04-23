package ar.edu.utn.frba.ddsi.common.Necesidad;

import lombok.Data;

@Data
public class Necesidad {
    private String subcategoria;
    private String descripcion;
    private TipoDeNecesidad tipoDeNecesidad;
  //  private List<Bien> bienesNecesitados; // TO DO "Bien"
}
