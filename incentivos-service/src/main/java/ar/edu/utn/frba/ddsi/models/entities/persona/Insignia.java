package ar.edu.utn.frba.ddsi.models.entities.persona;

import lombok.Getter;

public class Insignia {

    public static final Insignia COLABORADORA = new Insignia(new int[]{0x1F469, 0x200D, 0x1F527}, "Colaboradora"); // esta es el emojin de mujer mecanica
    public static final Insignia COLABORADOR = new Insignia(new int[]{0x1F468, 0x200D, 0x1F527}, "Colaborador"); // esta es el emojin de hombre mecanico
    public static final Insignia SOSTENEDORA = new Insignia(new int[]{0x1F469, 0x200D, 0x1F692}, "Sostenedora"); // esta es el emojin de bombera
    public static final Insignia SOSTENEDOR = new Insignia(new int[]{0x1F468, 0x200D, 0x1F692	}, "Sostenedor"); // esta es el emojin de bombero
    public static final Insignia TRANSFORMADORA = new Insignia(new int[]{0x1F9B8, 0x200D, 0x2640, 0xFE0}, "Transformadora");// este el emojin de superheroina
    public static final Insignia TRANSFORMADOR = new Insignia(new int[]{0x1F9B8, 0x200D, 0x2642, 0xFE0F}, "Transformador");// este el emojin de superheroe

    private final int[] internalEncoding;
    @Getter
    private final String nombre;

    private Insignia(int[] internalEncoding, String nombre) {
        this.internalEncoding = internalEncoding;
        this.nombre = nombre;
    }

    public String texto() {
        return new String(internalEncoding, 0, internalEncoding.length);
    }
}
