package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PersonaJuridica{
    private String cuit;
    private String razonSocial;
    private Tipo tipo;
    private String rubro;
    private List<Representante> personasRepresentantes = new ArrayList<>();
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
    private List<Bien> donaciones = new ArrayList<>();

    public PersonaJuridica(String cuit,String razonSocial,Tipo tipo, String rubro) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro= rubro;
    }


    public void agregarMedioDeNotificacion(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }

}
