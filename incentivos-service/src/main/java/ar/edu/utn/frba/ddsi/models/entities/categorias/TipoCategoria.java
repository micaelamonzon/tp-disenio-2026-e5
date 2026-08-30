package ar.edu.utn.frba.ddsi.models.entities.categorias;

public enum TipoCategoria {
    COLABORADOR,
    SOSTENEDOR,
    TRANSFORMADOR;

    public static TipoCategoria obtenerCategoriaSiguiente(String actual) {
        TipoCategoria categoriaActual = TipoCategoria.valueOf(actual);

        if(categoriaActual == COLABORADOR){
            return TipoCategoria.SOSTENEDOR;
        }else if(categoriaActual == SOSTENEDOR){
            return TipoCategoria.TRANSFORMADOR;
        }else {
            return null;
        }
    }
}
