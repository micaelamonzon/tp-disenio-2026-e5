package ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones;

import ar.edu.utn.frba.ddsi.models.entities.misiones.NombreDeMision;

public enum TipoCategoria {
    COLABORADOR,
    SOSTENEDOR,
    TRANSFORMADOR;

    public static TipoCategoria obtenerCategoriaSiguiente(TipoCategoria actual) {

        if(actual == COLABORADOR){
            return TipoCategoria.SOSTENEDOR;
        }else if(actual == SOSTENEDOR) {
            return TipoCategoria.TRANSFORMADOR;
        }else if(actual == TRANSFORMADOR){
                return null;
        } else {
            return null;
        }
    }

    public static boolean perteneceAEnum(String texto) {
        try {
            TipoCategoria.valueOf(texto);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
