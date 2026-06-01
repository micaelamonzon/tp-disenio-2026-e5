package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mision.Mision;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
public class PersonaHumana{
    private Long id;
    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer numeroDeDocumento;
    private String genero;
    private String direccion;
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
    private MedioDeNotificacion medioDeNotificacionPredeterminado;
    private List<DonacionSinSegmentar> donaciones = new ArrayList<>(); //al acceder a esta donacion sabremoscuales salieron
    private List<Mision> misiones = new ArrayList<>();
    private CategoriaDonante categoria;

    public PersonaHumana(String nombre,String apellido,Integer edad,Integer numeroDeDocumento,String genero,String direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.numeroDeDocumento = numeroDeDocumento;
        this.genero = genero;
        this.direccion = direccion;

    }


    public void agregarMedioDeNotificacion(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }

    public void agregarMision(Mision mision){
        this.misiones.add(mision);
    }


}