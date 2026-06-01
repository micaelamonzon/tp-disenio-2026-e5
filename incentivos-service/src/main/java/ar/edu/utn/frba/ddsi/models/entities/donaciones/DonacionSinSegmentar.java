package ar.edu.utn.frba.ddsi.models.entities.donaciones;

import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DonacionSinSegmentar {
    private List<Bien> bienes;
    LocalDateTime fechaDeIngreso;

    public DonacionSinSegmentar(List<Bien> bienes,LocalDateTime fechaDeIngreso){
        this.bienes = bienes;
        this.fechaDeIngreso = fechaDeIngreso;
    }
    public void agregarBien(Bien bien){
        this.bienes.add(bien);
    }

}
