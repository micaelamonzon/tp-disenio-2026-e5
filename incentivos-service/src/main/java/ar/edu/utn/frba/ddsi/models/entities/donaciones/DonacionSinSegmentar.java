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
    private Long entidadBeneficiariaId;

    public DonacionSinSegmentar(List<Bien> bienes,LocalDateTime fechaDeIngreso,Boolean donacionEntregada,Long entidadBeneficiariaId){
        this.bienes = bienes;
        this.fechaDeIngreso = fechaDeIngreso;
        this.donacionEntregada = donacionEntregada;
        this.entidadBeneficiariaId = entidadBeneficiariaId;
    }

}
