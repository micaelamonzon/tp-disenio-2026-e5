package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DonacionAdminDTO(
        Long id,
        String estadoActual,
        SubcategoriaDTO subcategoria,
        List<BienDTO> bienes,
        LocalDateTime fechaDeIngreso,
        String tipoDonante,
        Long idDelDonante,
        String nombreDonante,
        boolean puedeVencerse
) {}
