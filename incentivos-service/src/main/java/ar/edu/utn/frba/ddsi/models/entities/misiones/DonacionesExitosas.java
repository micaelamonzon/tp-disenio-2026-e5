package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class DonacionesExitosas extends Mision{
    private Integer cantidadDeDonaciones;
    private Long entidadBeneficiariaId;

    public DonacionesExitosas(String nombre,Integer cantidadDeDonaciones){
        this.setNombre(nombre);
        this.cantidadDeDonaciones = cantidadDeDonaciones;
        setInsigniaGanadora(Insignia.DONACIONEXITOSA);
    }
    //lograr X donaciones que sean recibidas exitosamente por una entidad beneficiaria.
    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {

        int cantInicial = 0;

        for(int i = 0; i< donaciones.size(); i++){
            if(donaciones.get(i).getDonacionEntregada() && Objects.equals(entidadBeneficiariaId, donaciones.get(i).getEntidadBeneficiariaId())){
                cantInicial ++;

                Integer progreso =(int) Math.min(cantInicial / cantidadDeDonaciones, 1.0);
                Integer progresoActual = progreso * 100;
                this.setProgreso(progresoActual);

                Integer distanciaRestante = cantidadDeDonaciones - this.getProgreso();
                this.setDistanciaDelObjetivo(Math.max(0, distanciaRestante));
            }
        }
        if(cantInicial >= cantidadDeDonaciones){
            this.setEstadoDeMision(EstadoDeMision.COMPLETADA);
            this.setFechaCompletada(java.time.LocalDate.now());
            return Boolean.TRUE;
        }else{
            this.setEstadoDeMision(EstadoDeMision.BLOQUEADA);
            return Boolean.FALSE;
        }


    }
}
