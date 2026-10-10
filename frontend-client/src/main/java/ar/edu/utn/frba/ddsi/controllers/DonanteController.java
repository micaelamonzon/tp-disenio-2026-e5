package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.dtos.entidades.EntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.misiones.MisionFrontDTO;
import ar.edu.utn.frba.ddsi.services.DonacionesApiService.DonanteNoEncontradoException;
import ar.edu.utn.frba.ddsi.services.DonacionesApiService.EntidadNoEncontradaException;
import ar.edu.utn.frba.ddsi.services.DonacionesService;
import ar.edu.utn.frba.ddsi.services.IncentivosService;
import ar.edu.utn.frba.ddsi.utils.UsuarioActual;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/donante")
public class DonanteController {

    private final DonacionesService donacionesService;
    private final IncentivosService incentivosService;
    private final UsuarioActual usuarioActual;

    public DonanteController(DonacionesService donacionesService,
                             IncentivosService incentivosService,
                             UsuarioActual usuarioActual) {
        this.donacionesService = donacionesService;
        this.incentivosService = incentivosService;
        this.usuarioActual = usuarioActual;
    }

    // Id del donante logueado (viene del token de autenticacion-service, guardado en la sesión)
    private Long idDonanteActual() {
        return usuarioActual.idEnDominio();
    }

    @GetMapping("/misDonaciones")
    public String misDonaciones(@RequestParam(required = false) String estado,
                                @RequestParam(required = false) String categoria,
                                @RequestParam(required = false) String subcategoria,
                                Model model) {
        model.addAttribute("titulo", "Mis donaciones");
        model.addAttribute("tabActiva", "donaciones");
        model.addAttribute("estados", DonacionDTO.ESTADOS);
        model.addAttribute("estadoSel", estado);
        model.addAttribute("categoriaSel", categoria);
        model.addAttribute("subcategoriaSel", subcategoria);
        try {
            // Humanos y jurídicos son tablas distintas en donaciones: cada uno tiene su endpoint
            List<DonacionDTO> todas = usuarioActual.obtener().esDonanteHumano()
                    ? donacionesService.obtenerDonacionesDeHumano(idDonanteActual())
                    : donacionesService.obtenerDonacionesDeJuridico(idDonanteActual());
            model.addAttribute("totalSinFiltrar", todas.size());
            model.addAttribute("categorias", donacionesService.categorias(todas));
            model.addAttribute("subcategorias", donacionesService.subcategorias(todas, categoria));
            model.addAttribute("donaciones", donacionesService.filtrar(todas, estado, categoria, subcategoria));
        } catch (DonanteNoEncontradoException e) {
            model.addAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", "El servicio de donaciones no está disponible. Intentá de nuevo en unos minutos.");
        }
        return "acceso/donante/misDonaciones";
    }

    @GetMapping("/entidadesBeneficiarias")
    public String entidadesBeneficiarias(
            @RequestParam(required = false) String q,
            Model model) {
        model.addAttribute("titulo", "Entidades beneficiarias");
        model.addAttribute("tabActiva", "entidades");
        model.addAttribute("busqueda", q);
        try {
            List<EntidadBeneficiariaDTO> todas =
                    donacionesService.obtenerEntidades();
            model.addAttribute("totalEntidades", todas.size());
            model.addAttribute("entidades",
                    donacionesService.buscarEntidades(todas, q));
        } catch (RuntimeException e) {
            model.addAttribute("error",
                    "El servicio de donaciones no está disponible. "
                            + "Intentá de nuevo en unos minutos.");
        }
        return "acceso/donante/entidadesBeneficiarias";
    }

    // Datos de la entidad y necesidades se piden por separado, si fallan las
    // necesidades, se muestran los datos de la entidad
    @GetMapping("/entidadesBeneficiarias/{idEntidad}")
    public String detalleEntidad(@PathVariable Long idEntidad, Model model) {
        model.addAttribute("titulo", "Entidad beneficiaria");
        model.addAttribute("tabActiva", "entidades");
        try {
            EntidadBeneficiariaDTO entidad = donacionesService.obtenerEntidad(idEntidad);
            model.addAttribute("titulo", entidad.getRazonSocial());
            model.addAttribute("entidad", entidad);
        } catch (EntidadNoEncontradaException e) {
            model.addAttribute("error", e.getMessage());
            return "acceso/donante/entidadDetalle";
        } catch (RuntimeException e) {
            model.addAttribute("error",
                    "El servicio de donaciones no está disponible. "
                            + "Intentá de nuevo en unos minutos.");
            return "acceso/donante/entidadDetalle";
        }
        try {
            model.addAttribute("necesidades", donacionesService.obtenerNecesidades(idEntidad));
        } catch (RuntimeException e) {
            model.addAttribute("errorNecesidades",
                    "No pudimos obtener las necesidades de esta entidad en este momento.");
        }
        return "acceso/donante/entidadDetalle";
    }

    @GetMapping("/incentivos")
    public String incentivos(Model model) {
        model.addAttribute("titulo", "Incentivos");
        model.addAttribute("tabActiva", "incentivos");
        try {
            List<MisionFrontDTO> misiones = incentivosService.obtenerMisiones(idDonanteActual()).stream()
                    .sorted(Comparator.comparing(MisionFrontDTO::categoria)
                            .thenComparing(MisionFrontDTO::ordenSecuencial))
                    .toList();
            model.addAttribute("misiones", misiones);
            model.addAttribute("puedeComenzar", puedeComenzar(misiones));
        } catch (RuntimeException e) {
            model.addAttribute("error", "El servicio de incentivos no está disponible. Intentá de nuevo en unos minutos.");
        }
        return "acceso/donante/incentivos";
    }

    @PostMapping("/incentivos/comenzar/{misionId}")
    public String comenzarMision(@PathVariable Long misionId, RedirectAttributes redirect) {
        try {
            incentivosService.comenzarMision(idDonanteActual(), misionId);
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorComenzar",
                    "No pudimos comenzar la misión. Intentá de nuevo en unos minutos.");
        }
        return "redirect:/donante/incentivos";
    }


    private boolean puedeComenzar(List<MisionFrontDTO> misiones) {
        if (misiones.isEmpty()) return false;
        boolean sinMisionEnCurso = misiones.stream()
                .noneMatch(m -> m.estado().name().equals("ACTUAL")
                        || m.estado().name().equals("COMPLETADA"));
        if (!sinMisionEnCurso) return false;
        try {
            return incentivosService.obtenerPerfil(idDonanteActual()).categoriaActual() == null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @GetMapping("/seguimiento")
    public String seguimiento(Model model) {
        model.addAttribute("titulo", "Seguimiento de entregas");
        model.addAttribute("tabActiva", "seguimiento");
        return "acceso/donante/seguimiento";
    }

    @GetMapping("/notificaciones")
    public String notificaciones(Model model) {
        model.addAttribute("titulo", "Notificaciones");
        model.addAttribute("tabActiva", "notificaciones");
        return "acceso/donante/notificaciones";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("titulo", "Mi Perfil");
        model.addAttribute("tabActiva", "");
        try {
            model.addAttribute("perfil", incentivosService.obtenerPerfil(idDonanteActual()));
        } catch (RuntimeException e) {
            model.addAttribute("error", "El servicio de incentivos no está disponible. Intentá de nuevo en unos minutos.");
        }
        return "acceso/donante/perfil";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("titulo", "Logout");
        model.addAttribute("tabActiva", "");

        return "acceso/donante/logout";
    }


}