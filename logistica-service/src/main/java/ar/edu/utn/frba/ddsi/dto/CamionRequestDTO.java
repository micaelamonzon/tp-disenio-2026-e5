package ar.edu.utn.frba.ddsi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionRequestDTO {
    private String patente;
    private Double volumen;
    private Double altura;
    private Double capacidadCarga;
    private Boolean disponible;
}