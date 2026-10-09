package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.models.entities.misiones.Estado;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionParticular;
import ar.edu.utn.frba.ddsi.repositories.EstadoMisionRepository;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
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

    public MisionShedulerService(IncentivosRepository incentivosRepository, IncentivosService incentivosService, EstadoMisionRepository estadoMisionRepository, ReactiveAdapterRegistry webFluxAdapterRegistry) {
        this.incentivosRepository = incentivosRepository;
        this.incentivosService = incentivosService;
        this.estadoMisionRepository = estadoMisionRepository;
        this.webFluxAdapterRegistry = webFluxAdapterRegistry;
    }

    @Scheduled(fixedRate = 2 * 60 * 1000)
    @Transactional
    public void chequearMisionesEnCurso() {
        incentivosRepository.findAll().forEach(donante -> {
            try {
                MisionParticular misionParticular = donante.getMisionEnCurso();

                // para un usurio que es nuevo o que ya esta en la ultimaa mision
                if (misionParticular == null) return;


                if (misionParticular.getEstado() == EstadoDeMision.COMPLETADA) {
                    donante.setMisionEnCurso(null);
                    return;
                }

                if (misionParticular != null) {
                    misionParticular.avanzarMision();
                    loggerMisionesEnCurso.info("Misión en curso chequeada, ID donante {} con misión actual {}", donante.getId(), misionParticular.getMision().getNombre());

                    if(misionParticular.getEstado() == EstadoDeMision.COMPLETADA){

                        donante.getPerfil().agregarInsignia(misionParticular.getMision().getInsigniaGanadora());

                        Estado nuevoEstadoMision = new Estado(EstadoDeMision.COMPLETADA,donante,misionParticular);

                        misionParticular.agregarEstado(nuevoEstadoMision);

                        this.estadoMisionRepository.save(nuevoEstadoMision);


                        loggerMisionesEnCurso.info("Misión completada para donante {}, misión {}", donante.getId(), misionParticular.getMision().getNombre());

                        if(incentivosService.pasaSiguienteCategoria(donante)){

                        loggerMisionesEnCurso.info("Donante {} pasa de la categoria {} a la siguiente categoría {}", donante.getId(), donante.getMisionEnCurso().getMision().getCategoria().getNombre(), donante.getMisionEnCurso().getMision().getCategoria().getCategoriaSiguiente());
                        incentivosService.setearSiguienteCategoria(donante);
                        }else{
                            incentivosService.pasaASiguienteMisionDeLaMismaCategoria(donante);
                            loggerMisionesEnCurso.info("Donante {} no pasa a la siguiente categoría, pero avanza a la mision {} de la misma categoría {}", donante.getId(), donante.getMisionEnCurso() ,donante.getMisionEnCurso().getMision().getCategoria().getNombre());
                        }
                    }
                }
            } catch (Exception e) {

                loggerMisionesEnCurso.error("Error chequeando misión del donante {}", donante.getId(), e);
            }
        });
    }
}

