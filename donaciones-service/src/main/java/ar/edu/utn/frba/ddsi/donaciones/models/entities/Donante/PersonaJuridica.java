package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PersonaJuridica implements Donante {
    private String cuit;
    private String razonSocial;
    private Tipo tipo;
    private String rubro;
    private List<PersonaHumana> personasRepresentantes = new ArrayList<>();
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();


    public PersonaJuridica(String cuit,String razonSocial,Tipo tipo, String rubro) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro= rubro;
    }

    @Override
    public void mediosDeNotificaciones(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }

}
