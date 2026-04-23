package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Bien {
    private String Nombre;
    private String descripcion;
    private Foto foto;
    private Subcategoria subcategoria;
    /*
    public void sePuedeDonar(DonacionSegmentada donacion){
        donacion.subcategoria.tipoDePerecidad() == true &&
                donacion.subcategoria.esUsado() == EstadoDeUso.Nuevo;
    }

     */
}
