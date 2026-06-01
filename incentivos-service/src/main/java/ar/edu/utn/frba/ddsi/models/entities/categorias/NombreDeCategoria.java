package ar.edu.utn.frba.ddsi.models.entities.categorias;

public enum NombreDeCategoria {
    COLABORADOR,
    SOSTENEDOR,
    TRANSFORMADOR;

    public NombreDeCategoria obtenerSiguiente() {
        NombreDeCategoria[] categorias = NombreDeCategoria.values();
        int siguienteOrdinal = this.ordinal() + 1;


        if (siguienteOrdinal >= categorias.length) {
            return null; // devuelvo null si es la ultima categoria
        }

        return categorias[siguienteOrdinal];
    }
}
