package ar.edu.utn.frba.ddsi.dto;

import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;

import java.util.ArrayList;
import java.util.List;

public record DonacionDTO(
        List<BienDTO> bienesDelMismoTipo,
        SubcategoriaDTO subcategoria
)
{
}
