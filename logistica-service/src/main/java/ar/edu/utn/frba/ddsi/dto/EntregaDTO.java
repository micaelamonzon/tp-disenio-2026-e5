package ar.edu.utn.frba.ddsi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EntregaDTO(
        Long id,
        List<Long> donacionesIds,        // ids de las donaciones segmentadas (donaciones-service)
        Long entidadBeneficiariaId,
        String estadoActual,
        LocalDateTime fechaEntrega,
        List<String> fotosUrl,
        String motivoNoRecepcion
) {}