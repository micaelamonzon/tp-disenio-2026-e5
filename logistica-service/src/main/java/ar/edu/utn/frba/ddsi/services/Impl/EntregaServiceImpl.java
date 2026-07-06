package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.models.entities.DonacionesClient;
import ar.edu.utn.frba.ddsi.dto.ConfirmarRecepcionDTO;
import ar.edu.utn.frba.ddsi.dto.EntregaDTO;
import ar.edu.utn.frba.ddsi.dto.EntidadContactoDTO;
import ar.edu.utn.frba.ddsi.dto.MedioDeNotificacionDTO;
import ar.edu.utn.frba.ddsi.dto.PersonaContactoDTO;
import ar.edu.utn.frba.ddsi.models.entities.Entrega;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ar.edu.utn.frba.ddsi.services.NotificacionesPublisher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EntregaServiceImpl {

    // repositorio en memoria de entregas (se pierde al reiniciar el servicio)
    private final Map<Long, Entrega> entregas = new HashMap<>();
    private final AtomicLong contador = new AtomicLong(1);
    private final DonacionesClient donacionesClient;
    private final NotificacionesPublisher notificacionesPublisher;
    private final String adminContacto;

    public EntregaServiceImpl(DonacionesClient donacionesClient,
                              NotificacionesPublisher notificacionesPublisher,
                              @Value("${notificaciones.admin.contacto}") String adminContacto) {
        this.donacionesClient = donacionesClient;
        this.notificacionesPublisher = notificacionesPublisher;
        this.adminContacto = adminContacto;
    }

    // Adopción al nuevo modelo de Entrega: ya no recibe patente ni busca camión,
    // porque la entrega no conoce al camión (la relación quedó Camion -> Ruta ->
    // PuntoDeEntrega -> Entrega, como en el diagrama de clases).
    // Ahora recibe una lista de donaciones en vez de una sola
    public EntregaDTO crearEntrega(List<Long> donacionesIds, Long entidadId, Long donanteId) {
        Entrega entrega = new Entrega(donacionesIds, entidadId, donanteId);
        Long id = contador.getAndIncrement();
        entrega.setId(id);
        entregas.put(id, entrega);
        return toDTO(entrega);
    }

    public EntregaDTO iniciarTraslado(Long entregaId, String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.iniciarTraslado();
        // Como una entrega ahora agrupa varias donaciones, se avisa a
        // donaciones-service el cambio de estado de cada una
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.iniciarTraslado(donacionId, responsableId);
        }
        return toDTO(entrega);
    }

    // Inicio de ruta: el chofer indica que arranca su recorrido y todas las
    // entregas asignadas a la ruta pasan a "En traslado" (requerimiento de dominio 3).
    // Reutiliza iniciarTraslado, así cada entrega valida su transición de estado y avisa a donaciones-service
    // TODO: cuando esté el módulo de rutas del planificador, este método va a
    // recibir el id de la Ruta y obtener las entregas desde sus PuntosDeEntrega
    public List<EntregaDTO> iniciarRuta(List<Long> entregaIds, String choferId) {
        List<EntregaDTO> resultado = new java.util.ArrayList<>();
        for (Long entregaId : entregaIds) {
            resultado.add(this.iniciarTraslado(entregaId, choferId));
        }
        // Notificacion de inicio de ruta con enlace al seguimiento del camion
        String aviso = "El+camion+inicio+la+ruta.+Segui+tu+entrega+en:+"
                + "http://localhost:8083/logistica-service/camiones/monitoreo/ruta/camion/1/recorrido";
        for (Long entregaId : entregaIds) {
            notificarInteresados(buscar(entregaId), aviso);
        }
        return resultado;
    }

    public EntregaDTO confirmarRecepcion(Long entregaId, ConfirmarRecepcionDTO body,
                                         String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.confirmarRecepcion(body.getFotosUrl());
        // Se notifica a donaciones-service por cada donación de la entrega
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.confirmarEntrega(donacionId, responsableId);
        }

        // Comprobante de entrega para la entidad y el donante
        // TODO: agregar el camion responsable cuando este ok el modulo de rutas
        String comprobante = "Entrega+realizada+con+exito.+Fecha:+" + entrega.getFechaEntrega()
                + ".+Entrega+nro+" + entrega.getId();
        notificarInteresados(entrega, comprobante);

        return toDTO(entrega);
    }

    public EntregaDTO marcarNoRecibida(Long entregaId, ConfirmarRecepcionDTO body,
                                       String responsableId) {
        Entrega entrega = buscar(entregaId);
        entrega.marcarNoRecibida(body.getMotivo());
        // Se notifica a donaciones-service por cada donación de la entrega
        for (Long donacionId : entrega.getDonacionesIds()) {
            donacionesClient.marcarEntregaFallida(donacionId, body.getMotivo(), responsableId);
        }

        // Aviso de entrega no satisfactoria a entidad, donante y administradores
        String aviso = "Entrega+nro+" + entrega.getId() + "+no+pudo+concretarse.+Motivo:+"
                + body.getMotivo().replace(" ", "+");
        notificarInteresados(entrega, aviso);
        notificacionesPublisher.publicar(adminContacto, aviso, "SMS");

        return toDTO(entrega);
    }

    public EntregaDTO volverAPendiente(Long entregaId) {
        Entrega entrega = buscar(entregaId);
        entrega.volverAPendiente();
        return toDTO(entrega);
    }

    // Eliminamos el Map de camiones y registrarCamion(): solo se usaban para
    // asociar el camión al crear la entrega, y eso ya no corresponde con el
    // nuevo modelo. Los camiones ahora se gestionan desde RepositoryCamiones

    // Consulta los contactos a donaciones-service y publica la notificacion en la
    // cola para cada interesado - si un contacto falla no se corta la operacion
    private void notificarInteresados(Entrega entrega, String mensaje) {
        try {
            EntidadContactoDTO entidad = donacionesClient.obtenerEntidad(entrega.getEntidadBeneficiariaId());
            if (entidad != null && entidad.representantes() != null
                    && !entidad.representantes().isEmpty()
                    && !entidad.representantes().get(0).mediosDeNotificacion().isEmpty()) {
                MedioDeNotificacionDTO medio = entidad.representantes().get(0).mediosDeNotificacion().get(0);
                notificacionesPublisher.publicar(medio.datoDeContacto(), mensaje, medio.tipoDeNotificacion());
            }
        } catch (Exception e) {
            System.out.println("No se pudo notificar a la entidad: " + e.getMessage());
        }
        if (entrega.getDonanteId() != null) {
            try {
                PersonaContactoDTO donante = donacionesClient.obtenerDonanteHumano(entrega.getDonanteId());
                if (donante != null && donante.medioDeNotificacionPredeterminado() != null) {
                    MedioDeNotificacionDTO medio = donante.medioDeNotificacionPredeterminado();
                    notificacionesPublisher.publicar(medio.datoDeContacto(), mensaje, medio.tipoDeNotificacion());
                }
            } catch (Exception e) {
                System.out.println("No se pudo notificar al donante: " + e.getMessage());
            }
        }
    }

    private Entrega buscar(Long id) {
        Entrega e = entregas.get(id);
        if (e == null) throw new RuntimeException("Entrega no encontrada: " + id);
        return e;
    }

    private EntregaDTO toDTO(Entrega e) {
        // Sacamos la patente del camión (la entrega ya no lo conoce) y
        // donacionSegmentadaId pasó a ser la lista donacionesIds
        return new EntregaDTO(
                e.getId(),
                e.getDonacionesIds(),
                e.getEntidadBeneficiariaId(),
                e.getEstadoActual().name(),
                e.getFechaEntrega(),
                e.getFotosUrl(),
                e.getMotivoNoRecepcion()
        );
    }
}