package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Tipo;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Mision {
    String nombre;
    Tipo tipo;
    Insignia insigniaGanadora;
    EstadoDeMision estadoDeMision;
    LocalDate fechaCompletada;

    public Mision(String nombre){
        this.nombre = nombre;
    }

    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones){
        Boolean seCompleto = tipo.seCompletoLaMision(donaciones);
            if(seCompleto){
                this.estadoDeMision = EstadoDeMision.COMPLETADA;
                this.fechaCompletada = LocalDate.now();
            }else{
                this.estadoDeMision = EstadoDeMision.BLOQUEADA;
            }
        return seCompleto;
    }
}
