package ar.edu.utn.frba.ddsi.donaciones.dto;


import java.time.LocalDateTime;

public record DonacionAsignadaDTO(
         Long id,
         String estadoActual,
         Integer cantidad,
         LocalDateTime fechaDeAsignacion,
         String subcategoria,
         String descripcion,
         Boolean donacionEntregada
) {
}
