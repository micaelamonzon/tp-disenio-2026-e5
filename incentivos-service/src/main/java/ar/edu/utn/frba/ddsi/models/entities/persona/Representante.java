package ar.edu.utn.frba.ddsi.models.entities.persona;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Representante {
    private String nombre;
    private String apellido;
    private Integer DNI;
    private ArrayList<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
}
