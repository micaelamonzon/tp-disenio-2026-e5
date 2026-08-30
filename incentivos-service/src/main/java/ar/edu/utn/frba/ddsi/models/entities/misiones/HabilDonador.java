package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;

import java.util.List;

@Data
public class HabilDonador extends Mision{
    private Integer cantidadDeBienes;


    public HabilDonador(String nombre,Integer cantidadDeBienes){
        this.setNombre(nombre);
        this.cantidadDeBienes = cantidadDeBienes;
        setInsigniaGanadora(Insignia.HABILDONADOR);
    }


    @Override
    public Boolean seCompletoLaMision(List<DonacionSegmentada> donaciones) {

        //donación que supere X cantidad de bienes.
       DonacionSegmentada donacionQueCumple = donaciones.stream().filter(d -> d.getBienes().size() > this.cantidadDeBienes).findFirst().orElse(null);

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
