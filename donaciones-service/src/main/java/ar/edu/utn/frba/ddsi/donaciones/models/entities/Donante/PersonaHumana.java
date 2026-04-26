package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
public class PersonaHumana implements Donante {
    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer numeroDeDocumento;
    private String genero;
    private String direccion;
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
    private MedioDeNotificacion medioDeNotificacionPredeterminado;

    public PersonaHumana(String nombre,String apellido,Integer edad,Integer numeroDeDocumento,String genero,String direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.numeroDeDocumento = numeroDeDocumento;
        this.genero = genero;
        this.direccion = direccion;

    }

    @Override
    public void mediosDeNotificaciones(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }

}