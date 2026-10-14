package ar.edu.utn.frba.ddsi.notificaciones.adapters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// adapter que manda los emails por SMTP (Gmail) usando JavaMailSender de Spring
@Component
@Profile("!test")
@ConditionalOnProperty(name = "notificaciones.email.proveedor", havingValue = "smtp")
public class SmtpEmailAdapter implements EmailAdapter {

    private final JavaMailSender mailSender;
    private final String remitente;

    public SmtpEmailAdapter(JavaMailSender mailSender,
                            @Value("${spring.mail.username}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    // si el envío falla, la excepción llega a Notificacion.enviar() y queda FALLIDA
    @Override
    public void enviar(String destinatario, String mensaje) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(remitente);
        mail.setTo(destinatario);
        mail.setSubject("Notificación DonaTrack");
        mail.setText(mensaje);
        try {
            mailSender.send(mail);
        } catch (MailException e) {
            throw new RuntimeException("No se pudo enviar el email por SMTP: " + e.getMessage(), e);
        }
    }
}