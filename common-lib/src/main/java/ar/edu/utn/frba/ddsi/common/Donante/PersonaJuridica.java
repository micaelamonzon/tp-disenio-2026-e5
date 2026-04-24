package ar.edu.utn.frba.ddsi.common.Donante;

import lombok.Data;

import java.util.List;
@Data
public class PersonaJuridica implements Donante {
    private String razonSocial;
    private Tipo tipo;
    private String rubro;
    private List<PersonaHumana> personasRepresentantes;
    private List<MedioDeNotificacion> medioDeNotificacion;

    //public void donar(List<Bien> bienes) {
    //    System.out.println("La entidad " + razonSocial + " está donando.");
    //}

    public void registrarse(Donante persona) {
    }
}