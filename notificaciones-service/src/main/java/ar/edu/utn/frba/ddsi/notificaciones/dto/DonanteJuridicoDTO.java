package ar.edu.utn.frba.ddsi.notificaciones.dto;

import java.util.List;

// lo que devuelve donaciones-service en GET /donante/juridico/{id}
public record DonanteJuridicoDTO(
        Long id,
        String razonSocial,
        List<Medio> mediosDeNotificacion
) {
    public record Medio(String tipoDeNotificacion, String datoDeContacto) {}
}