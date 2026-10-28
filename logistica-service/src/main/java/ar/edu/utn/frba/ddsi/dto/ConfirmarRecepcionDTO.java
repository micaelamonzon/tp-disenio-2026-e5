package ar.edu.utn.frba.ddsi.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConfirmarRecepcionDTO {
    private List<String> fotosUrl;
    private String motivo; // solo para no recibida
}