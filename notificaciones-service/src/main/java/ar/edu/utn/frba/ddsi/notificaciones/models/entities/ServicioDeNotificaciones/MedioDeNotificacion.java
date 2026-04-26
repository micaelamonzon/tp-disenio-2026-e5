package ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones;

public interface MedioDeNotificacion {
    void notificar(String destinatario, Notificacion notificacion);
}
