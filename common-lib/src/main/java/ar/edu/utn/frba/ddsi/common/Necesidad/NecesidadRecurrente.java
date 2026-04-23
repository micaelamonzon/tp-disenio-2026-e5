package ar.edu.utn.frba.ddsi.common.Necesidad;

import lombok.Data;

@Data
public class NecesidadRecurrente implements TipoDeNecesidad {
    private Integer periodo;
    private Integer cantidadObjetivo;
    private Integer cantidadRecibida;
    private Boolean estaSatisfecha = false;

    @Override
    public void satisfacerNecesidad() { //
        this.estaSatisfecha = (this.cantidadRecibida >= this.cantidadObjetivo);
    }

}
