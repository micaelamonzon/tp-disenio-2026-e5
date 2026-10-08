package ar.edu.utn.frba.ddsi.models;

// Roles Donante Humano y jurídico se separan porque en donaciones son
// tablas distintas y solo la persona humana accede a Incentivos
public enum Rol {
    DONANTE_HUMANO,
    DONANTE_JURIDICO,
    ENTIDAD_BENEFICIARIA,
    ADMIN
}
