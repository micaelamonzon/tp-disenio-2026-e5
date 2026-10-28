package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PuntosDeEntrega {
    private String id;
    private Integer orden;
    private Long entidadBeneficiariaId;
    private String entregaId;
}