package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categorias.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Mision {
    private String nombre;
    private Integer distanciaDelObjetivo = 0;
    private Integer progreso = 0;
    private EstadoDeMision estadoDeMision;
    private Insignia insigniaGanadora;
    private LocalDate fechaCompletada;
    private Categoria categoria;

    public Mision(String nombre, EstadoDeMision estadoDeMision) {
        this.nombre = nombre;
        this.estadoDeMision = estadoDeMision;
    }

    public void definirInsigniaGanadora(){
        if(Objects.equals(this.nombre, "RACHA")){
            this.insigniaGanadora = Insignia.RACHA;
        }
        else if(Objects.equals(this.nombre, "DONACIONEXITOSA")){
            this.insigniaGanadora = Insignia.DONACIONEXITOSA;
        }
        else if(Objects.equals(this.nombre, "COMPLETITUD")){
            this.insigniaGanadora = Insignia.COMPLETITUD;
        }
        else if(Objects.equals(this.nombre, "HABILDONADOR")){
            this.insigniaGanadora = Insignia.HABILDONADOR;
        }
    }

    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones){
        //lo reescriben las clases hijas
        return Boolean.FALSE;
    }



}
