package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import lombok.Data;

import java.util.List;
@Data
public class DonacionesExitosas extends Mision{
    private Integer cantidadDeDonaciones;

    public DonacionesExitosas(String nombre,Integer cantidadDeDonaciones){
        super(nombre);
        this.cantidadDeDonaciones = cantidadDeDonaciones;
    }
    //lograr X donaciones que sean recibidas exitosamente por una entidad beneficiaria.
    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {
        int cantInicial = 0;

        for(int i = 0; i< donaciones.size(); i++){
            if(donaciones.get(i).getDonacionEntregada()){
                cantInicial ++;
                subirProgreso(donaciones.size());
                int distanciaRestante = 100 - this.getProgreso();
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

    public void subirProgreso(Integer cantDonaciones){
        super.subirProgreso(cantDonaciones);
    }


}
