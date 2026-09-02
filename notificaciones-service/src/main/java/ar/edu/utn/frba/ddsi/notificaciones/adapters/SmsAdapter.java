package ar.edu.utn.frba.ddsi.notificaciones.adapters;

public interface SmsAdapter {
    void enviar(String destinatario, String mensaje);
}