package ar.edu.utn.frba.ddsi.dtos.logistica;

import lombok.Data;

@Data
public class CamionDTO {
    private String id;
    private String patente;
    private Double volumen;
    private Double altura;
    private Double capacidadCarga;
    private Boolean disponible;
    private String rutaId;
}