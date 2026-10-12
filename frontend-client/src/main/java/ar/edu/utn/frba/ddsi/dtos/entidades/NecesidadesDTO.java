package ar.edu.utn.frba.ddsi.dtos.entidades;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NecesidadesDTO {
    private List<NecesidadDTO> necesidades = new ArrayList<>();
}
