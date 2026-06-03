package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class Mision {
    private String nombre;
    private Insignia insigniaGanadora;
    private EstadoDeMision estadoDeMision;
    private LocalDate fechaCompletada;
    private Integer distanciaDelObjetivo;
    private Integer progreso;

    public Mision(String nombre, EstadoDeMision estado){
        this.nombre = nombre;
        this.progreso = 0;
        this.distanciaDelObjetivo = 100;
        this.estadoDeMision = estado;
    }

    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones){
        //lo reescriben las clases hijas
        return Boolean.FALSE;
    }

    public void subirProgreso(Integer unNumero){
        this.progreso += (100/unNumero);
    }
    public void bajarProgreso(Integer unNumero){
        this.progreso -= (100/unNumero);
    }
}
