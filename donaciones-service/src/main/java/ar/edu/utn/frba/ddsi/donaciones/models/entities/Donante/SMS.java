package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

public class SMS implements MedioDeNotificacion{
    private String numeroDeTelefono;

    public SMS(String numeroDeTelefono) {
        this.numeroDeTelefono = numeroDeTelefono;
    }


}
