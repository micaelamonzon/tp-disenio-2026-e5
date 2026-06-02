package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class HabilDonador implements Tipo{
    private Integer cantidadDeBienes;
    private Integer distanciaDelObjetivo;
    private Integer progreso;


    public HabilDonador(Integer cantidadDeBienes){
        this.cantidadDeBienes = cantidadDeBienes;
    }


    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {
        //donación que supere X cantidad de bienes.
       DonacionSinSegmentar donacionQueCumple = donaciones.stream().filter(d -> d.getBienes().size() > this.cantidadDeBienes).findFirst().orElse(null);

           if(donacionQueCumple != null){
               this.progreso = 100;
               this.distanciaDelObjetivo = 0;
               return Boolean.TRUE;
           }

           this.progreso = 0;
           this.distanciaDelObjetivo = 100;

           return Boolean.FALSE;
    }


}
