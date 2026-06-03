package ar.edu.utn.frba.ddsi.donaciones.dto;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public record DonacionSinSegmentarDTO(
        List<BienDTO> bienes,
//        List<DonacionSegmentadaDTO> donacionesSegmentadas,
        LocalDateTime fechaDeIngreso,
        Boolean donacionEntregada,
        Long organizacionId

) {
}
