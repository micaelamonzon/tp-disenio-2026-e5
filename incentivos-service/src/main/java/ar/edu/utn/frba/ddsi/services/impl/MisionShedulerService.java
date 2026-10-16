package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.models.entities.misiones.Estado;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionParticular;
import ar.edu.utn.frba.ddsi.repositories.EstadoMisionRepository;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.services.impl.NotificacionesPublisher;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.TipoDeDonante;
import java.util.Objects;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class MisionShedulerService {
    private static final Logger loggerMisionesEnCurso = LoggerFactory.getLogger(MisionShedulerService.class);
    private final IncentivosRepository incentivosRepository;
    private final IncentivosService incentivosService;
    private final EstadoMisionRepository estadoMisionRepository;
    private final ReactiveAdapterRegistry webFluxAdapterRegistry;
    private final NotificacionesPublisher notificacionesPublisher;

    public MisionShedulerService(IncentivosRepository incentivosRepository, IncentivosService incentivosService, EstadoMisionRepository estadoMisionRepository, ReactiveAdapterRegistry webFluxAdapterRegistry,
                                 NotificacionesPublisher notificacionesPublisher) {
        this.incentivosRepository = incentivosRepository;
        this.incentivosService = incentivosService;
        this.estadoMisionRepository = estadoMisionRepository;
        this.webFluxAdapterRegistry = webFluxAdapterRegistry;
        this.notificacionesPublisher = notificacionesPublisher;
    }

    @Scheduled(fixedRate = 2 * 60 * 1000)
    @Transactional
    public void chequearMisionesEnCurso() {
        incentivosRepository.findAll().forEach(donante -> {
            try {
                MisionParticular misionParticular = donante.getMisionEnCurso();

                // para un usurio que es nuevo o que ya pasó la ultimaa mision de la ultima categoria
                if (misionParticular == null)
                    return;

                EstadoDeMision estadoAntes = misionParticular.getEstado();
                misionParticular.avanzarMision();

                // solo entra si la misión PASÓ a completada en esta ee ronda? (no si ya lo estaba)
                boolean recienCompletada = estadoAntes != EstadoDeMision.COMPLETADA
                        && misionParticular.getEstado() == EstadoDeMision.COMPLETADA;


                if (recienCompletada) {

                    // la guardo en una variable porque más abajo la misión en curso del donante cambia
                    String insignia = misionParticular.getMision().getInsigniaGanadora();
                    donante.getPerfil().agregarInsignia(insignia);

                    Estado nuevoEstadoMision = new Estado(EstadoDeMision.COMPLETADA, donante, misionParticular);

                    misionParticular.agregarEstado(nuevoEstadoMision);

                    this.estadoMisionRepository.save(nuevoEstadoMision);

                    loggerMisionesEnCurso.info("Misión completada para donante {}, misión {}", donante.getId(), misionParticular.getMision().getNombre());

                    // Evento: el donante cumplió una misión
                    notificarDonante(donante, "¡Felicitaciones! Completaste la misión " + misionParticular.getMision().getNombre());

                    // Difusión de la insignia en n8n: una sola vez, al completarse la misión
                    difundirInsignia(donante, insignia);
                        if(incentivosService.pasaSiguienteCategoria(donante)){

                            loggerMisionesEnCurso.info("Donante {} pasa de la categoria {} a la siguiente categoría {}", donante.getId(), donante.getMisionEnCurso().getMision().getCategoria().getNombre(), donante.getMisionEnCurso().getMision().getCategoria().getCategoriaSiguiente());

                            // Guardamos las categorías ANTES de setearSiguienteCategoria,
                            // porque ese método cambia la misión en curso del donante
                            Object categoriaActual = donante.getMisionEnCurso().getMision().getCategoria().getNombre();
                            Object categoriaSiguiente = donante.getMisionEnCurso().getMision().getCategoria().getCategoriaSiguiente();

                            incentivosService.setearSiguienteCategoria(donante);

                            // Evento: el donante cambió de categoría (solo si realmente subió:
                            // en la última categoría no hay siguiente)
                            if (categoriaSiguiente != null && !Objects.equals(categoriaActual, categoriaSiguiente)) {
                                notificarDonante(donante, "¡Subiste de categoría! Ahora sos donante " + categoriaSiguiente);
                            }
                        }else{
                            incentivosService.pasaASiguienteMisionDeLaMismaCategoria(donante);
                            loggerMisionesEnCurso.info("Donante {} no pasa a la siguiente categoría, pero avanza a la mision {} de la misma categoría {}", donante.getId(), donante.getMisionEnCurso() ,donante.getMisionEnCurso().getMision().getCategoria().getNombre());
                        }
                    }
            } catch (Exception e) {

                loggerMisionesEnCurso.error("Error chequeando misión del donante {}", donante.getId(), e);
            }
        });
    }

    // Difunde la insignia en n8n. Tiene su propio try/catch para que, si n8n no responde,
    // no se corte el resto del procesamiento del donante (cambio de misión o de categoría).
    private void difundirInsignia(Donante donante, String insignia) {
        try {
            incentivosService.publicarYDifundirInsignia(
                    donante.getIdEnDonaciones(),
                    insignia,
                    donante.getTipoDeDonante().name());
        } catch (Exception e) {
            loggerMisionesEnCurso.error("No se pudo difundir la insignia {} del donante {}", insignia, donante.getId(), e);
        }
    }


    // Publica el aviso en la cola. notificaciones-service solo resuelve donantes
    // humanos (su id en donaciones), así que para jurídicos no se publica.
    private void notificarDonante(Donante donante, String mensaje) {
        if (donante.getTipoDeDonante() != TipoDeDonante.HUMANO) {
            loggerMisionesEnCurso.info("Donante {} es jurídico: no se publica la notificación", donante.getId());
            return;
        }
        notificacionesPublisher.publicarParaDonante(donante.getIdEnDonaciones(), mensaje);
    }
}

