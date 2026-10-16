package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DatosPersonalesDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroEntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroPersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroPersonaJuridicaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.NecesidadDTO;
import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import ar.edu.utn.frba.ddsi.services.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DonacionesApiService {

    private static final Logger log = LoggerFactory.getLogger(DonacionesApiService.class);
    // Registro (sin usuario logueado todavía): llamadas directas, sin token
    private final WebClient webClient = WebClient.builder().build();
    // Consultas: pasan por el caller, que agrega el token del usuario y lo renueva si vence
    private final WebApiCallerService apiCaller;
    private final String donacionesServiceUrl;

    public DonacionesApiService(@Value("${donaciones.service.url}") String donacionesServiceUrl,
                                WebApiCallerService apiCaller) {
        this.donacionesServiceUrl = donacionesServiceUrl;
        this.apiCaller = apiCaller;
    }


    // GET /servicioDeDonaciones/donaciones/humano/{id}
    public List<DonacionDTO> obtenerDonacionesDeHumano(Long idDonante) {
        try {
            return apiCaller.getList(donacionesServiceUrl + "/donaciones/humano/{id}", DonacionDTO.class, idDonante);
        } catch (WebClientResponseException.NotFound e) {
            throw new DonanteNoEncontradoException(idDonante);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las donaciones", e);
        }
    }

    // GET /servicioDeDonaciones/donaciones/juridico/{id}
    public List<DonacionDTO> obtenerDonacionesDeJuridico(Long idDonante) {
        try {
            return apiCaller.getList(donacionesServiceUrl + "/donaciones/juridico/{id}", DonacionDTO.class, idDonante);
        } catch (WebClientResponseException.NotFound e) {
            throw new DonanteNoEncontradoException(idDonante);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error llamando a donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las donaciones", e);
        }
    }

    // GET /servicioDeDonaciones/admin/donaciones?estado=...
    public List<DonacionDTO> obtenerDonacionesPorEstado(String estado) {
        try {
            return apiCaller.getList(donacionesServiceUrl + "/admin/donaciones?estado={estado}",
                    DonacionDTO.class, estado);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo donaciones por estado", e);
            throw new RuntimeException("No se pudieron obtener las donaciones", e);
        }
    }

    // PATCH /servicioDeDonaciones/donacion/{id}/vencer?responsableId=...
    public void vencerDonacion(Long id, String responsableId) {
        try {
            apiCaller.patch(donacionesServiceUrl + "/donacion/{id}/vencer?responsableId={resp}",
                    DonacionDTO.class, id, responsableId);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error marcando donación como vencida", e);
            throw new RuntimeException("No se pudo marcar la donación como vencida", e);
        }
    }


    // GET /servicioDeDonaciones/donaciones/entregadas
    // Datos personales para el perfil del donante
    // GET /donante/humano/{id} o /donante/juridico/{id}
    public DatosPersonalesDTO obtenerDatosPersonales(Long idDonante, boolean esHumano) {
        try {
            String ruta = esHumano ? "/donante/humano/{id}" : "/donante/juridico/{id}";
            Map<?, ?> d = apiCaller.get(donacionesServiceUrl + ruta, Map.class, idDonante);
            if (d == null) {
                return null;
            }
            List<String> contactos = new java.util.ArrayList<>();
            if (d.get("mediosDeNotificacion") instanceof List<?> medios) {
                for (Object m : medios) {
                    if (m instanceof Map<?, ?> medio && medio.get("datoDeContacto") != null) {
                        contactos.add(tipoDeContactoLegible(medio.get("tipoDeNotificacion")) + ": " + medio.get("datoDeContacto"));
                    }
                }
            }
            if (esHumano) {
                String nombre = (texto(d.get("nombre")) + " " + texto(d.get("apellido"))).trim();
                return new DatosPersonalesDTO(nombre, "DNI", texto(d.get("DNI")), texto(d.get("direccion")),
                        null, null, contactos);
            }
            return new DatosPersonalesDTO(texto(d.get("razonSocial")), "CUIT", texto(d.get("cuit")), null,
                    texto(d.get("rubro")), tipoOrganizacionLegible(d.get("tipo")), contactos);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo los datos personales del donante {}", idDonante, e);
            throw new RuntimeException("No se pudieron obtener los datos personales", e);
        }
    }

    private String texto(Object valor) {
        return valor != null ? String.valueOf(valor) : "";
    }

    private String tipoDeContactoLegible(Object tipo) {
        String t = texto(tipo);
        return switch (t) {
            case "EMAIL" -> "Email";
            case "WHATSAPP" -> "WhatsApp";
            default -> t;
        };
    }

    private String tipoOrganizacionLegible(Object tipo) {
        String t = texto(tipo);
        if (t.isBlank()) return null;
        return Character.toUpperCase(t.charAt(0)) + t.substring(1).toLowerCase();
    }

    public List<DonacionDTO> obtenerDonacionesEntregadas() {
        try {
            return apiCaller.getList(donacionesServiceUrl + "/donaciones/entregadas", DonacionDTO.class);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo las donaciones entregadas de donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las donaciones entregadas", e);
        }
    }

    public static class DonanteNoEncontradoException extends RuntimeException {
        public DonanteNoEncontradoException(Long id) {
            super("No encontramos al donante " + id + ".");
        }
    }

    // GET /servicioDeDonaciones/entidades
    public List<EntidadBeneficiariaDTO> obtenerEntidades() {
        try {
            return apiCaller.getList(donacionesServiceUrl + "/entidades", EntidadBeneficiariaDTO.class);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo entidades de donaciones-service", e);
            throw new RuntimeException("No se pudieron obtener las entidades", e);
        }
    }


    public EntidadBeneficiariaDTO obtenerEntidad(Long idEntidad) {
        try {
            return apiCaller.get(donacionesServiceUrl + "/entidad/{id}", EntidadBeneficiariaDTO.class, idEntidad);
        } catch (WebClientResponseException.NotFound e) {
            throw new EntidadNoEncontradaException(idEntidad);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo la entidad {} de donaciones-service", idEntidad, e);
            throw new RuntimeException("No se pudo obtener la entidad", e);
        }
    }

    // GET /servicioDeDonaciones/entidad/{id}/necesidades
    public List<NecesidadDTO> obtenerNecesidades(Long idEntidad) {
        try {
            return apiCaller.getList(donacionesServiceUrl + "/entidad/{id}/necesidades", NecesidadDTO.class, idEntidad);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error obteniendo necesidades de la entidad {}", idEntidad, e);
            throw new RuntimeException("No se pudieron obtener las necesidades", e);
        }
    }

    public static class EntidadNoEncontradaException extends RuntimeException {
        public EntidadNoEncontradaException(Long id) {
            super("No encontramos la entidad beneficiaria " + id + ".");
        }
    }


    public Long registrarDonanteHumano(RegistroPersonaHumanaDTO dto) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("nombre", dto.getNombre());
            body.put("apellido", dto.getApellido());
            body.put("edad", dto.getEdad());
            body.put("DNI", dto.getDNI());
            body.put("genero", dto.getGenero());

            Map<String, Object> medioEmail = medioEmail(dto.getEmail());
            body.put("mediosDeNotificacion", List.of(medioEmail));
            body.put("medioDeNotificacionPredeterminado", medioEmail);
            body.put("donaciones", List.of());

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/donante/humano")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extraerId(response);
        } catch (Exception e) {
            log.error("Error registrando donante humano en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar el donante humano", e);
        }
    }


    public Long registrarDonanteJuridico(RegistroPersonaJuridicaDTO dto) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("razonSocial", dto.getRazonSocial());
            body.put("cuit", dto.getCuit());
            body.put("rubro", dto.getRubro());

            if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
                body.put("tipo", dto.getTipo().toUpperCase().trim());
            } else {
                body.put("tipo", "EMPRESA"); // Colocar una constante por defecto si viene vacío
            }

            body.put("mediosDeNotificacion", List.of(medioEmail(dto.getEmail())));

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/donante/juridico")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extraerId(response);
        } catch (Exception e) {
            log.error("Error registrando donante jurídico en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la empresa o donante jurídico", e);
        }
    }

    public Long registrarEntidadBeneficiaria(RegistroEntidadBeneficiariaDTO dto) {
        try {
            Map<String, Object> representante = new HashMap<>();
            if (dto.getRepresentante() != null) {
                representante.put("nombre", dto.getRepresentante().getNombre());
                representante.put("apellido", dto.getRepresentante().getApellido());
                representante.put("numeroDeDocumento", dto.getRepresentante().getNumeroDeDocumento());
            }
            representante.put("mediosDeNotificacion", List.of(medioEmail(dto.getEmail())));

            Map<String, Object> body = new HashMap<>();
            body.put("razonSocial", dto.getRazonSocial());
            body.put("direccion", dto.getDireccion());
            body.put("telefono", dto.getTelefono());
            body.put("representantes", List.of(representante));

            Map response = webClient.post()
                    .uri(donacionesServiceUrl + "/entidad")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return extraerId(response);
        } catch (Exception e) {
            log.error("Error registrando entidad beneficiaria en donaciones-service", e);
            throw new RuntimeException("No se pudo registrar la entidad beneficiaria", e);
        }
    }

    // Bajas para deshacer un registro cuando falla la creación del usuario en autenticación
    public void eliminarDonanteHumano(Long id) {
        eliminar("/donante/humano/" + id);
    }

    public void eliminarDonanteJuridico(Long id) {
        eliminar("/donante/juridico/" + id);
    }

    public void eliminarEntidadBeneficiaria(Long id) {
        eliminar("/entidad/" + id);
    }

    private void eliminar(String path) {
        try {
            webClient.delete()
                    .uri(donacionesServiceUrl + path)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            // No se relanza: el error que importa mostrar es el del registro
            log.error("No se pudo deshacer el alta {} en donaciones-service", path, e);
        }
    }

    // Mismo formato que MedioDeNotificacionDTO de donaciones (tipoDeNotificacion, datoDeContacto)
    private Map<String, Object> medioEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        return Map.of("tipoDeNotificacion", "EMAIL", "datoDeContacto", email.trim());
    }

    // Sin id no se puede vincular el usuario con su donante/entidad: se corta el registro
    private Long extraerId(Map response) {
        if (response == null || !(response.get("id") instanceof Number id)) {
            throw new IllegalStateException("donaciones-service no devolvió el id del alta");
        }
        return id.longValue();
    }

}
