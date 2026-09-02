package ar.edu.utn.frba.ddsi.notificaciones.adapters;

public interface EmailAdapter {
    void enviar(String destinatario, String mensaje);
}