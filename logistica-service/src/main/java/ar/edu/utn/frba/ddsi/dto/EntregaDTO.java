package ar.edu.utn.frba.ddsi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EntregaDTO(
        Long id,
        Long donacionSegmentadaId,
        Long entidadBeneficiariaId,
        String patenteCamion,
        String estadoActual,
        LocalDateTime fechaEntrega,
        List<String> fotosUrl,
        String motivoNoRecepcion
) {}
