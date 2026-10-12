package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.config.LogisticaProperties;
import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.repository.impl.SolicitudPlanificacionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpMethod;


import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PlanificacionRutas {

    private final int MAXIMO_DONACIONES;
    private final String DEPOSITO_DIRECCION;
    private final double DEPOSITO_LAT;
    private final double DEPOSITO_LON;


    private  final SolicitudPlanificacionRepository solicitudRepository;
    private final RestTemplate restTemplate;
    private final LogisticaProperties properties;
    private final SolicitudPlanificacionServiceImpl servicePlani;

    public PlanificacionRutas(RestTemplate restTemplate, LogisticaProperties properties, SolicitudPlanificacionRepository solicitudRepository, SolicitudPlanificacionServiceImpl servicePlani, @Value("${logistica.deposito.lat}") double depositoLat,
                              @Value("${logistica.deposito.lon}") double depositoLon, @Value("${logistica.max.donaciones}") int maxDonaciones, @Value("${logistica.deposito.direccion}") String depositoDireccion) {
        this.DEPOSITO_LAT = depositoLat;
        this.DEPOSITO_LON = depositoLon;
        this.DEPOSITO_DIRECCION = depositoDireccion;
        this.MAXIMO_DONACIONES = maxDonaciones;
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.solicitudRepository = solicitudRepository;
        this.servicePlani = servicePlani;
    }

    public void solicitarPlanificacion(List<CamionDTO> camiones, List<DonacionDTO> donaciones) {
        List<List<DonacionDTO>> lotes = dividirEnLotes(donaciones, MAXIMO_DONACIONES);

        lotes.forEach(lote -> enviarLote(lote, camiones));
    }

    private void enviarLote(List<DonacionDTO> lote, List<CamionDTO> camiones) {
        String requestId = UUID.randomUUID().toString();

        LocalDateTime mañana = LocalDateTime.now().plusDays(1)
                .withHour(9).withMinute(0).withSecond(0);
        LocalDateTime fin = mañana.withHour(18);

        PlanRouteDTO solicitud = new PlanRouteDTO(
                requestId,
                new TimeWindowDTO(
                        mañana.toString() + "Z",
                        fin.toString() + "Z"
                ),
                new WarehouseDTO(DEPOSITO_LAT, DEPOSITO_LON, DEPOSITO_DIRECCION),
                lote.stream().map(this::convertirADelivery).toList(),
                camiones.stream().map(this::convertirATruck).toList(),
                properties.getCallbackUrl()
        );

        URI uri = UriComponentsBuilder
                .fromUriString(properties.getUrlPlanificador())
                .build()
                .toUri();

        servicePlani.registrarSolicitud(requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getTokenPlanificador());

        HttpEntity<PlanRouteDTO> request = new HttpEntity<>(solicitud, headers);

        System.out.println("Enviando al planificador: " + solicitud);

        restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
    }

    private DeliveryDTO convertirADelivery(DonacionDTO donacion) {
        if(donacion.volumenM3() == null || donacion.pesoKg() == null){
            throw new RuntimeException("El peso o el volumen de la donacion no puede ser nulo");
        }
        return new DeliveryDTO(
                donacion.codigo().toString(),
                donacion.latitudEntidad(),
                donacion.longitudEntidad(),
                donacion.direccionEntidad(),
                donacion.pesoKg(),
                donacion.volumenM3()
        );
    }

    private TruckDTO convertirATruck(CamionDTO camion) {
        return new TruckDTO(
                camion.id(),
                camion.capacidadCarga(),
                camion.volumen()
        );
    }

    private List<List<DonacionDTO>> dividirEnLotes(List<DonacionDTO> donaciones, int tamañoLote) {
        List<List<DonacionDTO>> lotes = new ArrayList<>();
        for (int i = 0; i < donaciones.size(); i += tamañoLote) {
            lotes.add(donaciones.subList(i,
                    Math.min(i + tamañoLote, donaciones.size())));
        }
        return lotes;
    }
}