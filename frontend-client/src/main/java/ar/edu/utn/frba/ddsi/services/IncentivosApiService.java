package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.MisionFrontDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.PerfilDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.RankingMensualDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.TipoDeDonante;
import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import ar.edu.utn.frba.ddsi.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class IncentivosApiService {
    private static final Logger log = LoggerFactory.getLogger(IncentivosApiService.class);
    private final WebApiCallerService apiCaller;
    private final String incentivosServiceUrl;

    public IncentivosApiService(@Value("${incentivos.service.url}") String incentivosServiceUrl,
                                WebApiCallerService apiCaller) {
        this.incentivosServiceUrl = incentivosServiceUrl;
        this.apiCaller = apiCaller;
    }


    public List<MisionFrontDTO> obtenerTodasLasMisiones(Long idDonante, TipoDeDonante tipo) {
        try {
            List<MisionFrontDTO> misiones = apiCaller.getList(
                    incentivosServiceUrl + "/misiones/todas/{id}?tipoDeDonante={tipo}",
                    MisionFrontDTO.class, idDonante, tipo);
            System.out.println("Obtener todas las misiones  con" + idDonante + tipo);
            return misiones != null ? misiones : List.of();
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a incentivos-service", e);
            throw new RuntimeException("No se pudieron obtener las misiones", e);
        }
    }

    public PerfilDTO obtenerPerfil(Long idDonante, TipoDeDonante tipo) {
        try {
            System.out.println("Obtener perfil con " + idDonante + " " + tipo);
            PerfilDTO perfil = apiCaller.get(incentivosServiceUrl + "/perfil/{id}?tipoDeDonante={tipo}",
                    PerfilDTO.class, idDonante, tipo);

            if (perfil == null) {
                throw new IllegalStateException("El servicio de incentivos devolvió un perfil vacío");
            }
            return perfil;
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a incentivos-service", e);
            throw new RuntimeException("No se pudo obtener el perfil", e);
        }
    }

    public void comenzarMision(Long idDonante, Long misionId, TipoDeDonante tipo) {
        try {
            System.out.println("Comenzar mision con " + idDonante + " " + misionId + " " + tipo);
            apiCaller.post(incentivosServiceUrl + "/donante/{id}/mision/{misionId}?tipoDeDonante={tipo}",
                    "", Void.class, idDonante, misionId, tipo);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error comenzando misión {} del donante {}", misionId, idDonante, e);
            throw new RuntimeException("No se pudo comenzar la misión", e);
        }
    }

    // ==========================================
    // MÉTODOS DE RANKING Y MÉTRICAS
    // ==========================================

    public RankingMensualDTO obtenerUltimoRanking() {
        try {
            return apiCaller.get(incentivosServiceUrl + "/ranking/ultimo", RankingMensualDTO.class);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo el último ranking", e);
            return null; // O manejar según necesidad
        }
    }

    public List<RankingMensualDTO> buscarRankings(Integer mes, Integer anio) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(incentivosServiceUrl)
                    .path("/ranking/historial");

            if (mes != null) builder.queryParam("mes", mes);
            if (anio != null) builder.queryParam("anio", anio);

            List<RankingMensualDTO> list = apiCaller.getList(builder.toUriString(), RankingMensualDTO.class);
            return list != null ? list : List.of();
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error buscando historial de rankings", e);
            return List.of();
        }
    }
}
