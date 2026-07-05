package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.config.LogisticaProperties;
import ar.edu.utn.frba.ddsi.config.N8NProperties;
import ar.edu.utn.frba.ddsi.dto.*;
import ar.edu.utn.frba.ddsi.models.entities.Camion;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlanificacionRutas {

    private static final int MAXIMO_DONACIONES = 100;
    private static final String WAREHOUSE_ADRESS = "Av. Corrientes 1234, CABA";
    private static final double WAREHOUSE_LAT = -34.3556;
    private static final double WAREHOUSE_LON = -58.42015;

    private final RestTemplate restTemplate;
    private final LogisticaProperties properties;

    public PlanificacionRutas(RestTemplate restTemplate, LogisticaProperties properties){
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void solicitarPlanificacion(List<Camion> camiones, List<DonacionDTO> donaciones){
        List<List<DonacionDTO>> lotes = dividirEnLotes(donaciones, MAXIMO_DONACIONES);

        lotes.forEach(lote -> enviarLote(lote,camiones));
    }

    private void enviarLote(List<DonacionDTO> lote, List<Camion>camiones){
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
                new WarehouseDTO(WAREHOUSE_LAT,WAREHOUSE_LON,WAREHOUSE_ADRESS),
                lote.stream().map(this::convertirADelivery).toList(),
                camiones.stream().map(this::convertirATruck).toList()
        );

        URI uri = UriComponentsBuilder
                .fromUriString(properties.getUrlPlanificador())
                .build()
                .toUri();

        restTemplate.postForObject(uri,solicitud,String.class);
    }

    private DeliveryDTO convertirADelivery(DonacionDTO donacion, EntidadBeneficiariaDTO entidadBeneficiaria){
        return new DeliveryDTO(
                donacion.codigo(),
                entidadBeneficiaria.latitud(),
                entidadBeneficiaria().longitud(),
                entidadBeneficiaria().direccion(),
                donacion.pesoKg(),
                donacion.volumenM3()
        );
    }

    private TruckDTO convertirATruck(Camion camion) {
        return new TruckDTO(
                camion.getPatente(),
                camion.getCapacidadCarga(),
                camion.getVolumen()
        );
    }

    private List<List< DonacionDTO>> dividirEnLotes(List<DonacionDTO> donaciones, int tamañoLote){
        List<List<DonacionDTO>> lotes = new ArrayList<>();
        for(int i=0; i < donaciones.size(); i += tamañoLote){
            lotes.add(donaciones.subList(i,
                    Math.min(i + tamañoLote, donaciones.size())));
        }
        return lotes;
    }
}
