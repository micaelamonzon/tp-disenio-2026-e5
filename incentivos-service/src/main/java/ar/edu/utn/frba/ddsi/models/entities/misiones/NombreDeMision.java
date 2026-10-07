package ar.edu.utn.frba.ddsi.models.entities.misiones;

public enum NombreDeMision {
    RACHA,
    HABIL_DONADOR,
    DONACIONES_EXITOSAS,
    COMPLETITUD;

    public static boolean perteneceAEnum(String texto) {
        try {
            NombreDeMision.valueOf(texto);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
