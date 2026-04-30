package ar.edu.utn.frba.ddsi.donaciones.models.entities.formulario;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Donante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;

public class FormularioDeDonacion {
    private Donante donante;
    private String descripcionGeneral;
    private DonacionSinSegmentar donacion;

    public  FormularioDeDonacion(Donante donante, String descripcionGeneral, DonacionSinSegmentar donacion) {

        //TODO: implementar que se cree una nueva instancia de una donacion no segmentada
        // y que luego se guarde en el atributo de "donacion", asi queda asociado el formualrio con la donacion


    }


}
