package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;

import java.time.LocalDateTime;
import java.util.List;

public record PersonaDonanteDTO (
        Long idHumano,
        String nombre,
        String apellido,
        Long idJuridico,
        String razonSocial,
        LocalDateTime fechaDeRegistro,
        List<DonacionIncentivosDTO> donaciones
){}
