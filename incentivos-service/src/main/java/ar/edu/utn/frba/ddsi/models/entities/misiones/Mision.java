package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaDeMision;
import ar.edu.utn.frba.ddsi.models.entities.categorias.TipoCategoria;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Mision {
    private Long id;
    private String nombre;
    private Integer distanciaDelObjetivo = 0; //no va
    private Integer progreso = 0; // no va
    private EstadoDeMision estado; // no va
    private Insignia insigniaGanadora;
    private LocalDate fechaUltimoAvance;
    private LocalDate fechaCompletada;
    //id de categoria a la que pertenece la mision
    private CategoriaDeMision categoria;
    private Integer ordenSecuencial; //orden de la mision dentro de la categoria

    public Mision(String nombre, EstadoDeMision estado) {
        this.nombre = nombre;
        this. estado = estado;
        this.definirInsigniaGanadora();
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

    public Boolean seCompletoLaMision(List<DonacionSegmentada> donaciones){
        //lo reescriben las clases hijas
        return Boolean.FALSE;
    }



}
