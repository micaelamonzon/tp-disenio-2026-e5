package ar.edu.utn.frba.ddsi.dtos;

public record NotificacionMensajeDTO(String idEvento, String destinatario, Long donanteId,
                                     Long entidadId, String mensaje, String medio,
                                     String tipoDonante) {
}