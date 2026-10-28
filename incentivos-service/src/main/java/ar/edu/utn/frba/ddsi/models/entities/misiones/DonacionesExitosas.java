package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("DonacionesExitosas")
public class DonacionesExitosas extends Mision{
    @Column
    private Integer cantidadDeDonacioneExitosas;

    public DonacionesExitosas(NombreDeMision nombre,Integer cantidadDeDonaciones){
        this.setNombre(nombre);
        this.cantidadDeDonacioneExitosas = cantidadDeDonaciones;

    }

    //lograr X donaciones que sean recibidas exitosamente por una entidad beneficiaria.
    @Override
    public AvanceDeMision seCompletoLaMision(List<DonacionSegmentada> donaciones) {

        int cantInicial = 0;

        for(int i = 0; i< donaciones.size(); i++){
            if(donaciones.get(i).getDonacionEntregada()){
                cantInicial ++;

            }
        }

        return this.calcularProgresoyDistancia(cantInicial, cantidadDeDonacioneExitosas);
    }
}
