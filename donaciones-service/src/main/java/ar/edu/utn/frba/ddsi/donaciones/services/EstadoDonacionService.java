package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

public interface EstadoDonacionService {
    Long registrarDonacion(DonacionSegmentada donacion);
    DonacionSegmentadaDTO obtenerHistorial(Long id);
    DonacionSegmentadaDTO asignar(Long id);
    DonacionSegmentadaDTO listaParaEntregar(Long id);
    DonacionSegmentadaDTO iniciarTraslado(Long id);
    DonacionSegmentadaDTO entregar(Long id);
    DonacionSegmentadaDTO fallarEntrega(Long id, String justificacion);
    DonacionSegmentadaDTO vencer(Long id);
}
