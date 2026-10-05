package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.services.DonacionesApiService.DonanteNoEncontradoException;
import ar.edu.utn.frba.ddsi.services.DonacionesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/donante")
public class DonanteController {

    private final DonacionesService donacionesService;

    // PROVISORIO, id fijo de un donante de prueba. Existe solo para probar la comunicación con la API de donaciones
    // mientras no hay login
    private final Long idDonanteEtapa1;

    public DonanteController(DonacionesService donacionesService,
                             @Value("${etapa1.donante.id}") Long idDonanteEtapa1) {
        this.donacionesService = donacionesService;
        this.idDonanteEtapa1 = idDonanteEtapa1;
    }


    // TODO: modificarlo para obtener el id del usuario logueado (sesión/token del servicio de autenticacion)
    private Long idDonanteActual() {
        return idDonanteEtapa1;
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
            List<DonacionDTO> todas = donacionesService.obtenerDonacionesDeHumano(idDonanteActual());
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
    public String entidadesBeneficiarias(Model model) {
        model.addAttribute("titulo", "Entidades beneficiarias");
        model.addAttribute("tabActiva", "entidades");
        return "acceso/donante/entidadesBeneficiarias";
    }

    @GetMapping("/incentivos")
    public String incentivos(Model model) {
        model.addAttribute("titulo", "Incentivos");
        model.addAttribute("tabActiva", "incentivos");
        return "acceso/donante/incentivos";
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
}