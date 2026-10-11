package ar.edu.utn.frba.ddsi.controllers;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
import ar.edu.utn.frba.ddsi.services.SeguimientoService;
import ar.edu.utn.frba.ddsi.utils.UsuarioActual;
import java.util.List;

@Controller
@RequestMapping("/entidadBeneficiaria")
public class EntidadController {

    private static final String REDIRECT_RECEPCION = "redirect:/entidadBeneficiaria/recepcionDeDonaciones";

    private final NotificacionesApiService notificacionesApiService;
    private final SeguimientoService seguimientoService;
    private final LogisticaApiService logisticaApiService;
    private final FotoStorageService fotoStorageService;
    private final UsuarioActual usuarioActual;

    public EntidadController(NotificacionesApiService notificacionesApiService,
                             SeguimientoService seguimientoService,
                             LogisticaApiService logisticaApiService,
                             FotoStorageService fotoStorageService,
                             UsuarioActual usuarioActual) {
        this.notificacionesApiService = notificacionesApiService;
        this.seguimientoService = seguimientoService;
        this.logisticaApiService = logisticaApiService;
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
        return "acceso/entidadBeneficiaria/registrarNecesidades";
    }


    @GetMapping("/seguimientoDeEntrega")
    public String seguimientoDeEntrega(Model model) {
        model.addAttribute("titulo", "Seguimiento de entrega");
        model.addAttribute("tabActiva", "seguimiento");
        try {
            model.addAttribute("camiones",
                    seguimientoService.obtenerCamionesEnCaminoAEntidad(idEntidadActual()));
        } catch (RuntimeException e) {
            model.addAttribute("camiones", List.of());
            model.addAttribute("errorSeguimiento",
                    "No pudimos obtener la ubicación de los camiones. Intentá de nuevo en unos minutos.");
        }
        return "acceso/entidadBeneficiaria/seguimientoDeEntrega";
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

    @GetMapping("/perfil")
    public String pefil(Model model) {
        model.addAttribute("titulo", "Mi perfil");
        model.addAttribute("tabActiva", " ");
        return "acceso/entidadBeneficiaria/perfil";
    }

    private List<EntregaDTO> filtrarPorEstado(List<EntregaDTO> entregas, String estado) {
        return entregas.stream()
                .filter(e -> estado.equals(e.getEstadoActual()))
                .toList();
    }
}
