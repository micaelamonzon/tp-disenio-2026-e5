package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class HabilDonador extends Mision{
    private Integer cantidadDeBienes;


    public HabilDonador(String nombre,Integer cantidadDeBienes){
        super(nombre);
        this.cantidadDeBienes = cantidadDeBienes;
    }


    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {
        //donación que supere X cantidad de bienes.
       DonacionSinSegmentar donacionQueCumple = donaciones.stream().filter(d -> d.getBienes().size() > this.cantidadDeBienes).findFirst().orElse(null);

           if(donacionQueCumple != null){
               this.setProgreso(100);
               this.setDistanciaDelObjetivo(0);
               this.setEstadoDeMision(EstadoDeMision.COMPLETADA);
               this.setFechaCompletada(java.time.LocalDate.now());
               return Boolean.TRUE;
           }

           this.setProgreso(0);
           this.setDistanciaDelObjetivo(100);
           this.setEstadoDeMision(EstadoDeMision.BLOQUEADA);
           return Boolean.FALSE;
    }


}
