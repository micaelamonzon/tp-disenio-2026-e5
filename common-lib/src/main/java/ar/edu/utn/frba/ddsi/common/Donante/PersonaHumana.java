package ar.edu.utn.frba.ddsi.common.Donante;

import lombok.Data;

import java.util.List;
@Data
public class PersonaHumana implements Donante {
    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer numeroDeDocumento;
    private String genero;
    private String direccion;
    private List<MedioDeNotificacion> medioDeNotificacion;
    private MedioDeNotificacion medioDeNotificacionPredeterminado;

//    public void donar(List<Bien> bienes) {
//        System.out.println(nombre + " está realizando una donación.");
//    }

    public void registrarse(Donante persona) {

    }
}