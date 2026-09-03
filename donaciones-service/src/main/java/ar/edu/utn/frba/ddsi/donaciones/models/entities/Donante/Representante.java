package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Representante {
    private Long id;
    private String nombre;
    private String apellido;
    private Integer DNI;
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
}
