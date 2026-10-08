package ar.edu.utn.frba.ddsi.dtos;

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

    public boolean esDonante() {
        return this == DONANTE_HUMANO || this == DONANTE_JURIDICO;
    }
}
