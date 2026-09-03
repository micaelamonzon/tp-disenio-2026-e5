package ar.edu.utn.frba.ddsi.models.entities.persona;

public enum Insignia {
    RACHA(new int[]{0x1F469, 0x200D, 0x1F527}),
    DONACIONEXITOSA(new int[]{0x1F468, 0x200D, 0x1F527}),
    COMPLETITUD(new int[]{0x1F469, 0x200D, 0x1F692}),
    HABILDONADOR(new int[]{0x1F9B8, 0x200D, 0x2642, 0xFE0F});

    private final int[] internalEncoding;

    Insignia(int[] internalEncoding) {
        this.internalEncoding = internalEncoding;
    }

    public String texto() {
        return new String(internalEncoding, 0, internalEncoding.length);
    }
}
