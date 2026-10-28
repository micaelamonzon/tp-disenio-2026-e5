package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonantesHumanosRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonantesJuridicosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

// Aviso a donantes (humanos y jurídicos) que no registran actividad hace más de 20 días,
// para incentivarlos a hacer una nueva donación (Eventos - Parte I).
@Service
public class InactividadScheduler {

    private static final Logger log = LoggerFactory.getLogger(InactividadScheduler.class);

    private static final int DIAS_INACTIVIDAD = 20;

    // Tope de avisos por ejecución: si muchos donantes se vuelven inactivos el mismo
    // día (por ejemplo, los importados por CSV), no mandamos miles de mails/SMS reales
    // de golpe. Los que no entren hoy se avisan en las próximas ejecuciones.
    private static final int MAX_AVISOS_POR_EJECUCION = 100;

    private static final String MENSAJE =
            "¡Te extrañamos! Hace más de 20 días que no donás. "
                    + "Acercate al depósito de UTN Solidaria y seguí ayudando.";

    private final DonantesHumanosRepository donantesHumanosRepository;
    private final DonantesJuridicosRepository donantesJuridicosRepository;
    private final NotificacionesPublisher notificacionesPublisher;

    public InactividadScheduler(DonantesHumanosRepository donantesHumanosRepository,
                                DonantesJuridicosRepository donantesJuridicosRepository,
                                NotificacionesPublisher notificacionesPublisher) {
        this.donantesHumanosRepository = donantesHumanosRepository;
        this.donantesJuridicosRepository = donantesJuridicosRepository;
        this.notificacionesPublisher = notificacionesPublisher;
    }

    // Todos los días a las 4 AM: horario de baja carga, y no se superpone con
    // los otros procesos nocturnos (0, 2 y 3 AM).
    // @Transactional porque las donaciones del donante se cargan de forma LAZY.
    @Scheduled(cron = "0 0 4 * * *")
    // @Scheduled(cron = "0 * * * * *") // TEMPORAL PARA PROBAR
    @Transactional
    public void notificarDonantesInactivos() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.minusDays(DIAS_INACTIVIDAD);
        int avisados = 0;

        for (PersonaHumana donante : donantesHumanosRepository.findAll()) {
            if (avisados >= MAX_AVISOS_POR_EJECUCION) break;
            try {
                if (debeAvisar(donante.getFechaDeRegistro(), donante.getDonaciones(),
                        donante.getFechaUltimoAvisoInactividad(), limite)
                        // Solo marcamos como avisado si el mensaje quedó publicado en la cola
                        && notificacionesPublisher.publicarParaDonante(donante.getId(), MENSAJE)) {
                    donante.setFechaUltimoAvisoInactividad(ahora);
                    donantesHumanosRepository.save(donante);
                    avisados++;
                }
            } catch (Exception e) {
                // Un donante con problemas no corta el proceso para el resto
                log.error("Error chequeando inactividad del donante humano {}", donante.getId(), e);
            }
        }

        for (PersonaJuridica donante : donantesJuridicosRepository.findAll()) {
            if (avisados >= MAX_AVISOS_POR_EJECUCION) break;
            try {
                if (debeAvisar(donante.getFechaDeRegistro(), donante.getDonaciones(),
                        donante.getFechaUltimoAvisoInactividad(), limite)
                        && notificacionesPublisher.publicarParaDonanteJuridico(donante.getId(), MENSAJE)) {
                    donante.setFechaUltimoAvisoInactividad(ahora);
                    donantesJuridicosRepository.save(donante);
                    avisados++;
                }
            } catch (Exception e) {
                log.error("Error chequeando inactividad del donante jurídico {}", donante.getId(), e);
            }
        }
        log.info("Aviso de inactividad: {} donantes notificados", avisados);
    }

    // Se avisa si la última actividad (registro o última donación) es anterior al límite
    // y todavía no se le avisó después de esa actividad (para no repetir el aviso)
    private boolean debeAvisar(LocalDateTime fechaDeRegistro, List<DonacionSinSegmentar> donaciones,
                               LocalDateTime ultimoAviso, LocalDateTime limite) {
        LocalDateTime ultimaActividad = Stream.concat(
                        Stream.ofNullable(fechaDeRegistro),
                        donaciones.stream().map(DonacionSinSegmentar::getFechaDeIngreso))
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (ultimaActividad == null || !ultimaActividad.isBefore(limite)) return false;
        return ultimoAviso == null || !ultimoAviso.isAfter(ultimaActividad);
    }
}