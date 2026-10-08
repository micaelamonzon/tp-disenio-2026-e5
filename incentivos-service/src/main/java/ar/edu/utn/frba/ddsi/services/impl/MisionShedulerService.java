package ar.edu.utn.frba.ddsi.services.impl;

import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionParticular;
import ar.edu.utn.frba.ddsi.repositories.IncentivosRepository;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class MisionShedulerService {
    private static final Logger loggerMisionesEnCurso = LoggerFactory.getLogger(MisionShedulerService.class);
    private final IncentivosRepository incentivosRepository;

    public MisionShedulerService(IncentivosRepository incentivosRepository) {
        this.incentivosRepository = incentivosRepository;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // cada 10 minutos para ir probando
//    @Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Buenos_Aires") // para mas adelante
    @Transactional
    public void chequearMisionesEnCurso() {
        incentivosRepository.findAll().forEach(donante -> {
            try {
                MisionParticular misionParticular = donante.getMisionEnCurso();
                if (misionParticular != null) {
                    misionParticular.avanzarMision();
                    loggerMisionesEnCurso.info("Misión en curso chequeada, ID donante {} con misión actual {}",
                            donante.getId(), misionParticular.getMision().getNombre());
                }
            } catch (Exception e) {
                loggerMisionesEnCurso.error("Error chequeando misión del donante {}", donante.getId(), e);
            }
        });
    }
}

