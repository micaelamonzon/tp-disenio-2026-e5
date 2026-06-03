package ar.edu.utn.frba.ddsi.donaciones.dto;
import lombok.Data;


public class NecesidadDTO {
    private Long id;
    private String tipo;
    private String subcategoria;
    private String descripcion;
    private boolean estaSatisfecha;
    private int cantidad;
    private String periodo; // null si es extraordinaria
}