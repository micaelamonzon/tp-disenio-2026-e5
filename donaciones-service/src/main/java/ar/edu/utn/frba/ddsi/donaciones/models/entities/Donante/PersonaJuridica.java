package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mision.Mision;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PersonaJuridica{
    private Long id;
    private String cuit;
    private String razonSocial;
    private Tipo tipo;
    private String rubro;
    private List<Representante> personasRepresentantes = new ArrayList<>();
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();
    private List<DonacionSinSegmentar> donaciones = new ArrayList<>();
    private List<Mision> misiones = new ArrayList<>();
    private CategoriaDeDonante categoria;

    public PersonaJuridica(String cuit,String razonSocial,Tipo tipo, String rubro) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro= rubro;
    }

    public PersonaJuridica(String cuit, String razonSocial) {

    }


    public void agregarMedioDeNotificacion(MedioDeNotificacion unMedio) {
        this.mediosDeNotificacion.add(unMedio);
    }

    public void agregarMision(Mision mision){
        this.misiones.add(mision);
    }
    public void agregarDonacion(DonacionSinSegmentar donacion){
        this.donaciones.add(donacion);
    }
}
