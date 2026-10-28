package ar.edu.utn.frba.ddsi.models.entities.persona;

public enum Insignia {
    RACHA(new int[]{0x1F525}),//Fuego
    DONACIONEXITOSA(new int[]{0x2B50}), //Estrellita
    COMPLETITUD(new int[]{0x26A1}),//Rayo
    HABILDONADOR(new int[]{0x1F3C5}); //Hábil Donador

    private final int[] internalEncoding;

    Insignia(int[] internalEncoding) {
        this.internalEncoding = internalEncoding;
    }

    public String texto() {
        return new String(internalEncoding, 0, internalEncoding.length);
    }
}
