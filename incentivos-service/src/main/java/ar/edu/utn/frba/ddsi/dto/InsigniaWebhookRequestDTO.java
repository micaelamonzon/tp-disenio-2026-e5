package ar.edu.utn.frba.ddsi.dto;

import lombok.Data;

@Data
public class InsigniaWebhookRequestDTO {
    private String nombreUsuario;
    private String nombreInsignia;
    private String emojiInsignia;

    public InsigniaWebhookRequestDTO(String nombreUsuario, String nombreInsignia){
        this.nombreUsuario = nombreUsuario;
        this.nombreInsignia = nombreInsignia;
        this.emojiInsignia = emojiInsignia;
    }
}
