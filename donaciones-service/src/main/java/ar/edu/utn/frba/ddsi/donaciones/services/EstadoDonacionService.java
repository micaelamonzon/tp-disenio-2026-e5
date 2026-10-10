package ar.edu.utn.frba.ddsi.donaciones.services;

import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSegmentadaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;

public interface EstadoDonacionService {
    Long registrarDonacion(DonacionSegmentada donacion);
    DonacionSegmentadaDTO obtenerHistorial(Long id);
    DonacionSegmentadaDTO asignar(Long id, String responsableId);
    DonacionSegmentadaDTO listaParaEntregar(Long id,String responsableId);
    DonacionSegmentadaDTO iniciarTraslado(Long id,  String responsableId);
    DonacionSegmentadaDTO entregar(Long id, String responsableId);
    DonacionSegmentadaDTO fallarEntrega(Long id, String justificacion, String responsableId);
    DonacionSegmentadaDTO vencer(Long id, String responsableId);
}
