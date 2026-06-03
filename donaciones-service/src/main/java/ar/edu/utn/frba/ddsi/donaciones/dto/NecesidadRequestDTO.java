package ar.edu.utn.frba.ddsi.donaciones.dto;
import lombok.Data;


public class NecesidadRequestDTO {
    private String tipo;
    private String subcategoria;
    private String descripcion;
    private int cantidad;
    private String periodo;
}