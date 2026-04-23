package ar.edu.utn.frba.ddsi.donaciones.models.entities.bien;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Subcategoria {
    private String nombre;
    private LocalDateTime fechaDeVencimiento;
    private EstadoDeUso esUsado;
    private boolean tipoDePerecidad;
}
