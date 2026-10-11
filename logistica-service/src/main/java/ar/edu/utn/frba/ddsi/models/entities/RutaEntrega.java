package ar.edu.utn.frba.ddsi.models.entities;

import ar.edu.utn.frba.ddsi.dto.PosicionDTO;

import java.util.List;

public class RutaEntrega {
    private String entregaId;
    private String estadoActual;
    private String direccion;
    private PosicionDTO destino;
    private List<PosicionDTO> rutaCompleta;
}
