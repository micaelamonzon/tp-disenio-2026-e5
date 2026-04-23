package ar.edu.utn.frba.ddsi.common.Necesidad;
import lombok.Data;

@Data
public class NecesidadExtraordinaria implements TipoDeNecesidad {
    private Integer cantRequerida;
    private Boolean estaSatisfecha = false;

    @Override
    public void satisfacerNecesidad(){
        this.estaSatisfecha = (this.cantRequerida == 0);
    }
}
