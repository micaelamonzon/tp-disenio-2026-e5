package ar.edu.utn.frba.ddsi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CamionResponseDTO {
    private Long id;
    private String patente;
    private Double volumen;
    private Double altura;
    private Double capacidadCarga;
    private Boolean disponible;
    private String rutaId; // solo id de la ruta asignada, si tiene una (o null si no tiene)
}
