package ar.edu.utn.frba.ddsi.dtos.logistica;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EntregaDTO {
    private String id;
    private List<Long> donacionesIds;
    private Long entidadBeneficiariaId;
    private String estadoActual;
    private LocalDateTime fechaEntrega;
    private List<String> fotosUrl;
    private String motivoNoRecepcion;
}