package ar.edu.utn.frba.ddsi.notificaciones.models.entities;

public interface MedioDeNotificacion {
    boolean notificar(String destinatario, Notificacion notificacion);
}
