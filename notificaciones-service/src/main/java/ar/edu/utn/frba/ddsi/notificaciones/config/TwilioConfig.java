package ar.edu.utn.frba.ddsi.notificaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.from.number}")
    private String fromNumber;

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Value("${sendgrid.from.email}")
    private String sendgridFromEmail;

    @Value("${twilio.whatsapp.number}")
    private String whatsappNumber;

    public String getAccountSid() { return accountSid; }
    public String getAuthToken() { return authToken; }
    public String getFromNumber() { return fromNumber; }
    public String getSendgridApiKey() { return sendgridApiKey; }
    public String getSendgridFromEmail() { return sendgridFromEmail; }
    public String getWhatsappNumber() { return whatsappNumber; }
}