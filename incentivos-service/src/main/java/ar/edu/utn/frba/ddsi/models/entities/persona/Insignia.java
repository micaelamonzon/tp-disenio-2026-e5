package ar.edu.utn.frba.ddsi.models.entities.persona;

public class Insignia {

    public static final Insignia COLABORADORA = new Insignia(new int[]{0x1F469, 0x200D, 0x1F527}); // esta es el emojin de mujer mecanica
    public static final Insignia COLABORADOR = new Insignia(new int[]{0x1F468, 0x200D, 0x1F527}); // esta es el emojin de hombre mecanico
    public static final Insignia SOSTENEDORA = new Insignia(new int[]{0x1F469, 0x200D, 0x1F692}); // esta es el emojin de bombera
    public static final Insignia SOSTENEDOR = new Insignia(new int[]{0x1F468, 0x200D, 0x1F692	}); // esta es el emojin de bombero
    public static final Insignia TRANSFORMADORA = new Insignia(new int[]{0x1F9B8, 0x200D, 0x2640, 0xFE0});// este el emojin de superheroina
    public static final Insignia TRANSFORMADOR = new Insignia(new int[]{0x1F9B8, 0x200D, 0x2642, 0xFE0F});// este el emojin de superheroe

    private final int[] internalEncoding;

    private Insignia(int[] internalEncoding) {
        this.internalEncoding = internalEncoding;
    }

    public String texto() {
        return new String(internalEncoding, 0, internalEncoding.length);
    }
}
