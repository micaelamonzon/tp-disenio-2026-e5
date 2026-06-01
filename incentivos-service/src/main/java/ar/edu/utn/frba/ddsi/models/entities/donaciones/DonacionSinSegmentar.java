package ar.edu.utn.frba.ddsi.models.entities.donaciones;

import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DonacionSinSegmentar {
    private List<Bien> bienes;
    private LocalDateTime fechaDeIngreso;
    private Boolean donacionEntregada;

    public DonacionSinSegmentar(List<Bien> bienes,LocalDateTime fechaDeIngreso,Boolean donacionEntregada){
        this.bienes = bienes;
        this.fechaDeIngreso = fechaDeIngreso;
        this.donacionEntregada = donacionEntregada;
    }
    public void agregarBien(Bien bien){
        this.bienes.add(bien);
    }

}
