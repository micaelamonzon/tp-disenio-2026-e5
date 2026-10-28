package ar.edu.utn.frba.ddsi.dtos.donaciones;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NecesidadRankingDTO {
    private Long id;
    private String tipo;
    private String descripcion;
    private Long entidadId;
    private String entidadRazonSocial;
    private int cantidadSolicitada;
    private int cantidadRecibida;
}