package ar.edu.utn.frba.ddsi.dtos.donaciones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BienDTO {
    private String nombre;
    private String descripcion;
    private SubcategoriaDTO subcategoria;
    private LocalDateTime fechaDeVencimiento;
    private String esUsado;      // USADO / NUEVO
    private String tipoUnidad;   // UNIDADES, KILOGRAMOS, ...
    private Integer cantidad;
}

