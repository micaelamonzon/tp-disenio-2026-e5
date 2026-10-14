package ar.edu.utn.frba.ddsi.notificaciones.controllers;

import ar.edu.utn.frba.ddsi.notificaciones.converters.MedioDeNotificacionAttributeConverter;
import ar.edu.utn.frba.ddsi.notificaciones.dto.NotificacionResponseDTO;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Donante;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Notificacion;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.Email;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.SMS;
import ar.edu.utn.frba.ddsi.notificaciones.models.entities.ServicioDeNotificaciones.WhatsApp;
import ar.edu.utn.frba.ddsi.notificaciones.models.repositories.NotificacionRepository;
import ar.edu.utn.frba.ddsi.notificaciones.services.DonanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/servicioDeNotificaciones")
public class controllerDeNotificaciones {

    private final Email email;
    private final SMS sms;
    private final WhatsApp whatsApp;
    private final NotificacionRepository notificacionRepository;
    private final DonanteService donanteService;
    private final MedioDeNotificacionAttributeConverter medioConverter;

    @Autowired
    public controllerDeNotificaciones(Email email, SMS sms, WhatsApp whatsApp,
                                      NotificacionRepository notificacionRepository,
                                      DonanteService donanteService,
                                      MedioDeNotificacionAttributeConverter medioConverter) {
        this.email = email;
        this.sms = sms;
        this.whatsApp = whatsApp;
        this.notificacionRepository = notificacionRepository;
        this.donanteService = donanteService;
        this.medioConverter = medioConverter;
    }

    @PostMapping("/notificar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String notificar(@RequestParam String destinatario,
                            @RequestParam String mensaje,
                            @RequestParam String medio,
                            // Opcionales: permiten asociar la notificacion a su dueño
                            // para poder listarla despues en la pantalla del donante o de la entidad.
                            // Si no se mandan, el endpoint funciona igual que antes.
                            @RequestParam(required = false) Long donanteId,
                            @RequestParam(required = false) Long entidadId) {

        destinatario = URLDecoder.decode(destinatario, StandardCharsets.UTF_8);

        System.out.println("Destinatario recibido: [" + destinatario + "]");
        System.out.println("Mensaje recibido: [" + mensaje + "]");
        System.out.println("Medio recibido: [" + medio + "]");

        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);
        notificacion.setEntidadBeneficiariaId(entidadId); // null si no viene

        // Si viene donanteId, intentamos asociar el donante (y actualizar su copia local).
        // Si falla el GET a donaciones, igual enviamos: aca el destinatario ya viene resuelto,
        // asi que no vale la pena perder la notificacion por no poder asociarla.
        if (donanteId != null) {
            try {
                Donante donante = donanteService.resolverYActualizarDonante(donanteId);
                notificacion.setDonante(donante);
            } catch (RestClientException | IllegalStateException e) {
                System.err.println("No se pudo asociar el donante id=" + donanteId + ": " + e.getMessage());
            }
        }

        MedioDeNotificacion medioDeNotificacion = switch (medio.toUpperCase()) {
            case "EMAIL" -> email;
            case "SMS" -> sms;
            case "WHATSAPP" -> whatsApp;
            default -> throw new IllegalArgumentException("Medio no soportado: " + medio);
        };

        notificacion.setMedioDeNotificacion(medioDeNotificacion);
        notificacion.enviar();
        notificacionRepository.save(notificacion); // persiste la notificación con su estado final

        return "Notificacion " + notificacion.getEstado() + " por " + medio;
    }

    // Notificaciones de un donante (req. 8). Recurso anidado: las notificaciones
    // dependen de su destinatario, no tiene sentido listarlas sueltas.
    // Si el donante no tiene notificaciones (o todavía no existe en este servicio),
    // devuelve lista vacía y no 404: no tener notificaciones no es un error.
    @GetMapping("/donantes/{donanteId}/notificaciones")
    public List<NotificacionResponseDTO> notificacionesDeDonante(@PathVariable Long donanteId) {
        return notificacionRepository.findByDonante_IdOrderByFechaEnvioDesc(donanteId)
                .stream()
                .map(this::aDTO)
                .toList();
    }

    // Notificaciones de una entidad beneficiaria (req. 13). Mismo criterio que la de donante.
    @GetMapping("/entidades/{entidadId}/notificaciones")
    public List<NotificacionResponseDTO> notificacionesDeEntidad(@PathVariable Long entidadId) {
        return notificacionRepository.findByEntidadBeneficiariaIdOrderByFechaEnvioDesc(entidadId)
                .stream()
                .map(this::aDTO)
                .toList();
    }

    // Convierte la entidad al DTO de respuesta. Reutiliza el converter para
    // traducir el medio (objeto) a su nombre (EMAIL/SMS/WHATSAPP).
    private NotificacionResponseDTO aDTO(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getId(),
                n.getMensaje(),
                medioConverter.convertToDatabaseColumn(n.getMedioDeNotificacion()),
                n.getEstado() != null ? n.getEstado().name() : null,
                n.getFechaEnvio()
        );
    }
}