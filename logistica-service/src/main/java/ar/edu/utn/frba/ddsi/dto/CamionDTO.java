package ar.edu.utn.frba.ddsi.dto;

public record CamionDTO(
         String id,
         String patente,
         Double volumen,
         Double altura,
         Double capacidadCarga,
         Boolean disponible
) {
}
