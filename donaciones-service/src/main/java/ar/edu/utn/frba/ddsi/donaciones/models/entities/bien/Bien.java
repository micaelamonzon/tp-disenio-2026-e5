package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Bien {
    private String Nombre;
    private String descripcion;
    private Foto foto;
    private Subcategoria subcategoria;

    public boolean sePuedeDonar(){
        if(subcategoria.isTipoDePerecidad()){
            return subcategoria.getFechaDeVencimiento().isAfter(LocalDateTime.now());
        }
        if(subcategoria.getEsUsado() != null){
            return subcategoria.getEsUsado() == EstadoDeUso.NUEVO;
        }
        return true;
    }
}
