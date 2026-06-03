package ar.edu.utn.frba.ddsi.models.entities.persona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Insignia {

    private final int[] internalEncoding;
    private final String nombre;

    public static final Insignia RACHA = new Insignia(new int[]{0x1F469, 0x200D, 0x1F527}, "RACHA");
    public static final Insignia DONACIONEXITOSA = new Insignia(new int[]{0x1F468, 0x200D, 0x1F527}, "DONACIONEXITOSA");
    public static final Insignia COMPLETITUD = new Insignia(new int[]{0x1F469, 0x200D, 0x1F692}, "COMPLETITUD");
    public static final Insignia HABILDONADOR = new Insignia(new int[]{0x1F9B8, 0x200D, 0x2642, 0xFE0F}, "HABILDONADOR");



    private Insignia(int[] internalEncoding, String nombre) {
        this.internalEncoding = internalEncoding;
        this.nombre = nombre;
    }

    //lo transformo a emojin/texto, cosa que sea imprimible en pantalla
    public String texto() {
        return new String(internalEncoding, 0, internalEncoding.length);
    }
}
