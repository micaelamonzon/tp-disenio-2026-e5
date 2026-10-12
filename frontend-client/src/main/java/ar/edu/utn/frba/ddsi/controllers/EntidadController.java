package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.entidades.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.NecesidadDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.NecesidadesDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.PerfilEntidadDTO;
import ar.edu.utn.frba.ddsi.services.EntidadesApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ar.edu.utn.frba.ddsi.dtos.logistica.EntregaDTO;
import ar.edu.utn.frba.ddsi.services.FotoStorageService;
import ar.edu.utn.frba.ddsi.services.LogisticaApiService;
import ar.edu.utn.frba.ddsi.services.NotificacionesApiService;
import ar.edu.utn.frba.ddsi.dtos.logistica.RutaEntregaDTO;
import org.springframework.web.bind.annotation.ResponseBody;
import tools.jackson.databind.ObjectMapper;
import ar.edu.utn.frba.ddsi.utils.UsuarioActual;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/entidadBeneficiaria")
public class EntidadController {

    private static final String REDIRECT_RECEPCION = "redirect:/entidadBeneficiaria/recepcionDeDonaciones";

    private final NotificacionesApiService notificacionesApiService;
    private final EntidadesApiService entidadesApiService;
    private final ObjectMapper objectMapper;
    private final LogisticaApiService logisticaApiService;
    private final FotoStorageService fotoStorageService;
    private final UsuarioActual usuarioActual;

    public EntidadController(NotificacionesApiService notificacionesApiService,
                             ObjectMapper objectMapper,
                             LogisticaApiService logisticaApiService,
                             FotoStorageService fotoStorageService,
                             EntidadesApiService entidadesApiService,
                             UsuarioActual usuarioActual) {
        this.notificacionesApiService = notificacionesApiService;
        this.objectMapper = objectMapper;
        this.logisticaApiService = logisticaApiService;
        this.entidadesApiService = entidadesApiService;
        this.fotoStorageService = fotoStorageService;
        this.usuarioActual = usuarioActual;
    }

    // Id de la entidad logueada en donaciones-service (viene de la sesión)
    private Long idEntidadActual() {
        return usuarioActual.idEnDominio();
    }

    @GetMapping("/estadoDeDonaciones")
    public String estadoDeDonaciones(Model model) {
        model.addAttribute("titulo", "Estado de donaciones");
        model.addAttribute("tabActiva", "estado");
        try {
            model.addAttribute("donaciones",
                    entidadesApiService.obtenerDonacionesAsignadas(idEntidadActual()));
        } catch (RuntimeException e) {
            model.addAttribute("donaciones", List.of());
            model.addAttribute("errorDonaciones",
                    "No pudimos cargar tus donaciones asignadas. Intenta de nuevo en unos minutos.");
        }
        return "acceso/entidadBeneficiaria/estadoDeDonaciones";
    }


    // Req. 12: lista las entregas de la entidad que están en camino (para confirmar)
    // y las ya entregadas (con sus fotos)
    @GetMapping("/recepcionDeDonaciones")
    public String recepcionDeDonaciones(Model model) {
        model.addAttribute("titulo", "Recepcion de donaciones");
        model.addAttribute("tabActiva", "recepcion");
        try {
            // TODO: si logística agrega un filtro por entidad, usarlo en vez de filtrar acá
            List<EntregaDTO> entregasDeLaEntidad = logisticaApiService.obtenerEntregas().stream()
                    .filter(e -> idEntidadActual().equals(e.getEntidadBeneficiariaId()))
                    .toList();
            model.addAttribute("pendientes", filtrarPorEstado(entregasDeLaEntidad, "EN_TRASLADO"));
            model.addAttribute("confirmadas", filtrarPorEstado(entregasDeLaEntidad, "ENTREGADA"));
        } catch (RuntimeException e) {
            model.addAttribute("pendientes", List.of());
            model.addAttribute("confirmadas", List.of());
            model.addAttribute("errorCarga",
                    "No pudimos obtener tus entregas. Intentá de nuevo en unos minutos.");
        }
        return "acceso/entidadBeneficiaria/recepcionDeDonaciones";
    }

    // Req. 12: la entidad confirma la recepción adjuntando fotos
    @PostMapping("/recepcionDeDonaciones/{entregaId}/confirmar")
    public String confirmarRecepcion(@PathVariable String entregaId,
                                     @RequestParam(value = "fotos", required = false) List<MultipartFile> fotos,
                                     RedirectAttributes redirect) {
        if (fotos == null || fotos.stream().allMatch(MultipartFile::isEmpty)) {
            redirect.addFlashAttribute("errorRecepcion", "Adjuntá al menos una foto de la donación recibida.");
            return REDIRECT_RECEPCION;
        }
        try {
            // Seguridad: la entidad solo puede confirmar entregas propias
            EntregaDTO entrega = logisticaApiService.obtenerEntregaPorId(entregaId);
            if (entrega == null || !idEntidadActual().equals(entrega.getEntidadBeneficiariaId())) {
                redirect.addFlashAttribute("errorRecepcion", "Esa entrega no corresponde a tu entidad.");
                return REDIRECT_RECEPCION;
            }

            List<String> urls = fotoStorageService.guardar(fotos);
            logisticaApiService.confirmarRecepcion(entregaId, urls, String.valueOf(idEntidadActual()));
            redirect.addFlashAttribute("mensajeRecepcion", "¡Recepción confirmada! Gracias por avisarnos.");
        } catch (IllegalArgumentException e) {
            // Error de validación de las fotos (formato)
            redirect.addFlashAttribute("errorRecepcion", e.getMessage());
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorRecepcion",
                    "No pudimos confirmar la recepción. Intentá de nuevo en unos minutos.");
        }
        return REDIRECT_RECEPCION;
    }

    @GetMapping("/registrarNecesidades")
    public String registrarNecesidades(Model model) {
        model.addAttribute("titulo", "Registrar necesidades");
        model.addAttribute("tabActiva", "registrar");
        try {
            model.addAttribute("subcategorias", entidadesApiService.obtenerSubcategorias());
        } catch (RuntimeException e) {
            model.addAttribute("subcategorias", List.of());
            model.addAttribute("errorSubcategorias",
                    "No pudimos cargar las subcategorías. Intentá de nuevo en unos minutos.");
        }
        return "acceso/entidadBeneficiaria/registrarNecesidades";
    }

    @PostMapping("/necesidades")
    public String enviarNecesidades(@ModelAttribute NecesidadesDTO necesidades,
                                    RedirectAttributes redirect) {
        int enviadas = 0;
        int fallidas = 0;

        for (NecesidadDTO n : necesidades.getNecesidades()) {
            try {
                String periodo = (n.getPeriodo() == null || n.getPeriodo().isBlank()) ? null : n.getPeriodo();
                NecesidadDTO dto = new NecesidadDTO(null, n.getTipo(), n.getSubcategoria(),
                        n.getDescripcion(), false, n.getCantidad(), periodo);
                entidadesApiService.crearNecesidad(idEntidadActual(), dto);
                enviadas++;
            } catch (RuntimeException e) {
                fallidas++;
            }
        }

        redirect.addFlashAttribute("enviadas", enviadas);
        redirect.addFlashAttribute("fallidas", fallidas);
        return "redirect:/entidadBeneficiaria/registrarNecesidades";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("titulo", "Mi perfil");
        model.addAttribute("tabActiva", " ");
        try {
            EntidadBeneficiariaDTO entidad = entidadesApiService.obtenerPerfil(idEntidadActual());
            model.addAttribute("entidad", entidad);
            model.addAttribute("iniciales", iniciales(entidad.getRazonSocial()));
        } catch (RuntimeException e) {
            model.addAttribute("errorPerfil",
                    "No pudimos cargar los datos de tu entidad. Intentá de nuevo en unos minutos.");
        }
        return "acceso/entidadBeneficiaria/perfil";
    }

    private String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String n = nombre.trim();
        return n.substring(0, Math.min(2, n.length())).toUpperCase();
    }


    // Req. 14: mapa con los camiones que traen donaciones a la entidad
    // Usa el mismo mapa que el donante (js/donante/seguimiento.js)
    // logística ya devuelve la ruta recortada hasta la parada de esta entidad
    @GetMapping("/seguimientoDeEntrega")
    public String seguimientoDeEntrega(Model model) {
        model.addAttribute("titulo", "Seguimiento de entrega");
        model.addAttribute("tabActiva", "seguimiento");
        List<RutaEntregaDTO> entregas = List.of();
        try {
            entregas = logisticaApiService.obtenerRutasDeEntidad(idEntidadActual());
        } catch (RuntimeException e) {
            model.addAttribute("errorSeguimiento",
                    "No pudimos obtener la ubicación de los camiones. Intentá de nuevo en unos minutos.");
        }
        model.addAttribute("entregas", entregas);
        model.addAttribute("entregasJson", objectMapper.writeValueAsString(entregas));
        return "acceso/entidadBeneficiaria/seguimientoDeEntrega";
    }

    // El mapa consulta esto cada 5 segundos para mover el camión (mismo esquema que el donante)
    @GetMapping("/seguimientoDeEntrega/datos")
    @ResponseBody
    public List<RutaEntregaDTO> seguimientoDeEntregaDatos() {
        try {
            return logisticaApiService.obtenerRutasDeEntidad(idEntidadActual());
        } catch (RuntimeException e) {
            return List.of(); // el JS reintenta en el próximo ciclo
        }
    }

    @GetMapping("/notificaciones")
    public String notificaciones(Model model) {
        model.addAttribute("titulo", "Notificaciones");
        model.addAttribute("tabActiva", "notificaciones");

        // Si notificaciones-service no responde, mostramos un aviso en la
        // pág en vez de que explote con un error 500
        try {
            model.addAttribute("notificaciones",
                    notificacionesApiService.obtenerNotificacionesDeEntidad(idEntidadActual()));
        } catch (RuntimeException e) {
            model.addAttribute("notificaciones", List.of());
            model.addAttribute("errorNotificaciones",
                    "No pudimos cargar tus notificaciones. Intentá de nuevo en unos minutos.");
        }
        return "acceso/entidadBeneficiaria/notificaciones";
    }

//    @GetMapping("/perfil")
//    public String pefil(Model model) {
//        model.addAttribute("titulo", "Mi perfil");
//        model.addAttribute("tabActiva", " ");
//        return "acceso/entidadBeneficiaria/perfil";
//    }

    private List<EntregaDTO> filtrarPorEstado(List<EntregaDTO> entregas, String estado) {
        return entregas.stream()
                .filter(e -> estado.equals(e.getEstadoActual()))
                .toList();
    }
}
