package ar.edu.utn.frba.ddsi.controllers;

import ar.edu.utn.frba.ddsi.dtos.donaciones.DonacionDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroEntidadBeneficiariaDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroPersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroPersonaJuridicaDTO;
import ar.edu.utn.frba.ddsi.services.AuthApiService;
import ar.edu.utn.frba.ddsi.services.DonacionesApiService;
import ar.edu.utn.frba.ddsi.services.DonacionesService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/acceso")
public class AccesoController {
    private final DonacionesService donacionesService;
    private final DonacionesApiService donacionesApiService;
    private final AuthApiService authApiService;

    public AccesoController(DonacionesService donacionesService,
                            DonacionesApiService donacionesApiService, AuthApiService authApiService) {
        this.donacionesService = donacionesService;
        this.donacionesApiService = donacionesApiService;
        this.authApiService = authApiService;
    }

    private List<DonacionDTO> obtenerTodasLasDonacionesPublicas() {
        return donacionesService.obtenerDonacionesEntregadas();
    }

    @GetMapping({"", "/", "/landing"})
    public String landing(Model model) {
        model.addAttribute("titulo", "Inicio");
        model.addAttribute("tabActiva", "landing");
        try {
            List<DonacionDTO> todas = obtenerTodasLasDonacionesPublicas();
            java.time.LocalDateTime haceUnMes = java.time.LocalDateTime.now().minusMonths(1);
            // 3 donaciones con mas bienes del ultimo mes
            List<DonacionDTO> destacadas = todas.stream()
                    .filter(d -> d.getFechaDeIngreso() != null && d.getFechaDeIngreso().isAfter(haceUnMes))
                    .sorted(Comparator.comparingInt(DonacionDTO::getCantidadTotalItems).reversed())
                    .limit(3) // Máximo 3 para las destacar en el mes
                    .toList();

            model.addAttribute("donacionesDestacadas", destacadas);
        } catch (RuntimeException e) {
            model.addAttribute("error", "El servicio de donaciones no está disponible. Intentá de nuevo en unos minutos.");
            model.addAttribute("donacionesDestacadas", List.of());
        }
        return "acceso/landigPage/landing";
    }

    @GetMapping("/galeria")
    public String galeria(Model model) {
        model.addAttribute("titulo", "Galería de Donaciones");
        model.addAttribute("tabActiva", "galeria");
        try {
            List<DonacionDTO> todas = obtenerTodasLasDonacionesPublicas();

            // Filtrar las 6 donaciones HISTÓRICAS con más bienes
            List<DonacionDTO> donacionesGaleria = todas.stream()
                    .filter(d -> d.getFotoPath() != null && !d.getFotoPath().isBlank())
                    .filter(d -> Boolean.TRUE.equals(d.getDonacionEntregada())
                            || "ENTREGADA".equalsIgnoreCase(d.getEstadoActual()))
                    .sorted(Comparator.comparingInt(DonacionDTO::getCantidadTotalItems).reversed())
                    .limit(6) // Máximo 6 históricas
                    .toList();

            model.addAttribute("donaciones", donacionesGaleria);
        } catch (RuntimeException e) {
            model.addAttribute("error", "El servicio de donaciones no está disponible. Intentá de nuevo en unos minutos.");
            model.addAttribute("donaciones", List.of());
        }
        return "acceso/landigPage/galeria";
    }

    @GetMapping("/mapa")
    public String mapa(Model model) {
        model.addAttribute("titulo", "Mapa de Donaciones");
        model.addAttribute("tabActiva", "mapa");
        try {
            List<DonacionDTO> todas = obtenerTodasLasDonacionesPublicas();

            List<DonacionDTO> donacionesEntregadas = todas.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getDonacionEntregada())
                            || "ENTREGADA".equalsIgnoreCase(d.getEstadoActual()))
                    .toList();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registeredModules();

            String donacionesJson = mapper.writeValueAsString(donacionesEntregadas);

            model.addAttribute("donacionesMapaJson", donacionesJson);
            model.addAttribute("donacionesMapa", donacionesEntregadas); // Para las tarjetas HTML
        } catch (Exception e) {
            model.addAttribute("error", "El servicio de donaciones no está disponible.");
            model.addAttribute("donacionesMapaJson", "[]");
            model.addAttribute("donacionesMapa", List.of());
        }
        return "acceso/landigPage/mapa";
    }

    @GetMapping({"/legal", "/terminos"})
    public String legal(Model model) {
        model.addAttribute("titulo", "Términos y Legales");
        model.addAttribute("tabActiva", "legal");
        return "acceso/landigPage/legal";
    }

    @GetMapping("/sobre-nosotros")
    public String sobreNosotros(Model model) {
        model.addAttribute("titulo", "Sobre Nosotros");
        model.addAttribute("tabActiva", "sobre-nosotros");
        return "acceso/landigPage/sobre-nosotros";
    }

    @GetMapping("/contacto")
    public String contacto(Model model, HttpServletRequest request) {
        request.getSession(true);

        model.addAttribute("titulo", "Contacto");
        model.addAttribute("tabActiva", "contacto");

        return "acceso/landigPage/contacto";
    }

    @PostMapping("/contacto")
    public String procesarContacto(@RequestParam String nombre, Model model) {
        model.addAttribute("titulo", "Contacto");
        model.addAttribute("tabActiva", "contacto");
        model.addAttribute("mensajeEnviado", "¡Gracias " + nombre + "! Tu mensaje ha sido enviado con éxito.");

        return "acceso/landigPage/contacto";
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("titulo", "Iniciar Sesión");
        model.addAttribute("tabActiva", "");
        return "acceso/landigPage/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("titulo", "Registro de Usuarios");
        model.addAttribute("tabActiva", "registro");

        model.addAttribute("registroPersonaHumana", new RegistroPersonaHumanaDTO());
        model.addAttribute("registroPersonaJuridica", new RegistroPersonaJuridicaDTO());
        model.addAttribute("registroEntidadBeneficiaria", new RegistroEntidadBeneficiariaDTO());

        return "acceso/landigPage/registro";
    }
    @PostMapping("/registro/donante-humana")
    public String registrarPersonaHumana(@ModelAttribute RegistroPersonaHumanaDTO dto, Model model) {
        try {
            // Primero se valida el email: si ya tiene cuenta no se crea nada en donaciones
            authApiService.validarEmailDisponible(dto.getEmail());
            Long idDominio = donacionesApiService.registrarDonanteHumano(dto);

            try {
                authApiService.crearUsuario(
                        dto.getEmail(),
                        dto.getPassword(),
                        "DONANTE_HUMANO",
                        idDominio,
                        dto.getNombre() + " " + dto.getApellido()
                );
            } catch (RuntimeException e) {
                // Sin usuario no puede entrar: se intenta deshacer el alta
                donacionesApiService.eliminarDonanteHumano(idDominio);
                throw e;
            }

            return "redirect:/acceso/login?registroExitoso=true";
        } catch (Exception e) {
            model.addAttribute("titulo", "Registro de Usuarios");
            model.addAttribute("tabActiva", "registro");
            model.addAttribute("tipoFormulario", "donante-humana");
            model.addAttribute("error", "No se pudo registrar la persona humana: " + e.getMessage());
            return "acceso/landigPage/registro";
        }
    }

    @PostMapping("/registro/donante-juridica")
    public String registrarPersonaJuridica(@ModelAttribute RegistroPersonaJuridicaDTO dto, Model model) {
        try {
            authApiService.validarEmailDisponible(dto.getEmail());
            Long idDominio = donacionesApiService.registrarDonanteJuridico(dto);

            try {
                authApiService.crearUsuario(
                        dto.getEmail(),
                        dto.getPassword(),
                        "DONANTE_JURIDICO",
                        idDominio,
                        dto.getRazonSocial()
                );
            } catch (RuntimeException e) {
                donacionesApiService.eliminarDonanteJuridico(idDominio);
                throw e;
            }

            return "redirect:/acceso/login?registroExitoso=true";
        } catch (Exception e) {
            model.addAttribute("titulo", "Registro de Usuarios");
            model.addAttribute("tabActiva", "registro");
            model.addAttribute("tipoFormulario", "donante-juridica");
            model.addAttribute("error", "No se pudo registrar la empresa/entidad: " + e.getMessage());
            return "acceso/landigPage/registro";
        }
    }

    @PostMapping("/registro/entidad-beneficiaria")
    public String registrarEntidadBeneficiaria(@ModelAttribute RegistroEntidadBeneficiariaDTO dto, Model model) {
        try {
            authApiService.validarEmailDisponible(dto.getEmail());
            Long idDominio = donacionesApiService.registrarEntidadBeneficiaria(dto);

            try {
                authApiService.crearUsuario(
                        dto.getEmail(),
                        dto.getPassword(),
                        "ENTIDAD_BENEFICIARIA",
                        idDominio,
                        dto.getRazonSocial()
                );
            } catch (RuntimeException e) {
                donacionesApiService.eliminarEntidadBeneficiaria(idDominio);
                throw e;
            }

            return "redirect:/acceso/login?registroExitoso=true";
        } catch (Exception e) {
            model.addAttribute("titulo", "Registro de Usuarios");
            model.addAttribute("tabActiva", "registro");
            model.addAttribute("tipoFormulario", "entidad-beneficiaria");
            model.addAttribute("error", "No se pudo registrar la entidad beneficiaria: " + e.getMessage());
            return "acceso/landigPage/registro";
        }
    }

}
