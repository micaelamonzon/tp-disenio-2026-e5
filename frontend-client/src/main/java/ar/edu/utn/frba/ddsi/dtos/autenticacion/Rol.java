package ar.edu.utn.frba.ddsi.dtos.autenticacion;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.TipoDeDonante;

// Roles de DonaTrack: los mismos nombres que emite autenticacion-service en el claim "rol"
public enum Rol {
    DONANTE_HUMANO("Persona Donante"),
    DONANTE_JURIDICO("Persona Donante Jurídica"),
    ENTIDAD_BENEFICIARIA("Entidad Beneficiaria"),
    ADMIN("Persona Administradora");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    // Texto para mostrar en pantalla
    public String getEtiqueta() {
        return etiqueta;
    }


    public TipoDeDonante tipoDeDonante() {
        return switch (this) {
            case DONANTE_HUMANO -> TipoDeDonante.HUMANO;
            case DONANTE_JURIDICO -> TipoDeDonante.JURIDICO;
            default -> throw new IllegalStateException("El rol " + this + " no es un tipo de donante");
        };
    }
}
