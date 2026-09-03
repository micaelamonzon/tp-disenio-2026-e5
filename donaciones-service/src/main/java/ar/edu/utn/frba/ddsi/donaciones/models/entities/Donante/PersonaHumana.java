package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PersonaHumana{
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDateTime fechaDeNaciemiento;
    private Integer DNI;
    private String genero;
    private String direccion;
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
    private MedioDeNotificacion medioDeNotificacionPredeterminado;
    private List<DonacionSinSegmentar> donaciones = new ArrayList<>(); //al acceder a esta donacion sabremos que donaciones segmentadas salieron


    public PersonaHumana(String nombre,String apellido,Integer numeroDeDocumento,String genero,String direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.DNI = numeroDeDocumento;
        this.genero = genero;
        this.direccion = direccion;
    }

    public Integer calcularEdad() {
        LocalDateTime fechaActual = LocalDateTime.now();
        Integer edadCalculada = fechaActual.getYear() - this.fechaDeNaciemiento.getYear();
        return edadCalculada;
    }


    public void agregarMedioDeNotificacion(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }


    public void agregarDonacion(DonacionSinSegmentar donacion){
        this.donaciones.add(donacion);
    }

}