package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@DiscriminatorValue("HabilDonador")
public class HabilDonador extends Mision{
    @Column
    private Integer cantidadDeBienes;


    public HabilDonador(NombreDeMision nombre,Integer cantidadDeBienes){
        this.setNombre(nombre);
        this.cantidadDeBienes = cantidadDeBienes;
        setInsigniaGanadora(Insignia.HABILDONADOR);
    }


    @Override
    public AvanceDeMision seCompletoLaMision(List<DonacionSegmentada> donaciones) {

        //donación que supere X cantidad de bienes.
       DonacionSegmentada donacionQueCumple = donaciones.stream().filter(d -> d.getCantidadDeBienes() > this.cantidadDeBienes).findFirst().orElse(null);

           if(donacionQueCumple != null){
             return new AvanceDeMision(100, 0, Boolean.TRUE);
           }

            return new AvanceDeMision(0, 100, Boolean.FALSE);
    }


}
