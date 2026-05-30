package ar.edu.utn.frba.ddsi.models.entities.persona;

import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PersonaHumana {
    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer DNI;
    private String genero;
    private String direccion;
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
    private MedioDeNotificacion medioDeNotificacionPredeterminado;
    private List<Bien> donaciones = new ArrayList<>();
    private Perfil perfil;
    private Mision mision;



    public PersonaHumana(String nombre,String apellido,Integer edad,Integer numeroDeDocumento,String genero,String direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.DNI = numeroDeDocumento;
        this.genero = genero;
        this.direccion = direccion;
    }


    public void agregarMedioDeNotificacion(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }
}
