package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import lombok.Data;

import java.util.List;
@Data
public class DonacionesExitosas implements Tipo{

    private Integer cantidadDeDonaciones;
    private Integer distanciaDelObjetivo = 100; //lo que falta hasta completar la cantidad de donaciones
    private Integer progreso;



    public DonacionesExitosas(Integer cantidadDeDonaciones){
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
                this.distanciaDelObjetivo -= this.progreso;
            }
        }

        return cantInicial == cantidadDeDonaciones;
    }

    public void subirProgreso(Integer cantDonaciones){
        this.progreso += (100/cantDonaciones);
    }


}
