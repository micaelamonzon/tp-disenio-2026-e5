package ar.edu.utn.frba.ddsi.services;

import ar.edu.utn.frba.ddsi.dtos.donaciones.RegistroAdminRequestDTO;
import ar.edu.utn.frba.ddsi.services.internal.SesionExpiradaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RegistroAdminService {

    private static final Logger log = LoggerFactory.getLogger(RegistroAdminService.class);

    private final DonacionesApiService donacionesApi;
    private final AuthApiService authApi;

    public RegistroAdminService(DonacionesApiService donacionesApi, AuthApiService authApi) {
        this.donacionesApi = donacionesApi;
        this.authApi = authApi;
    }

    @SuppressWarnings("rawtypes")
    public record Resultado(Long donanteId, String tipo, List<Map> donaciones, List<String> avisos) {}

    // El donante quedó registrado (o ya existía) pero la donación falló: la pantalla permite reintentar
    public static class DonacionFallidaException extends RuntimeException {
        private final Long donanteId;
        private final String tipo;
        private final List<String> avisos;

        public DonacionFallidaException(String mensaje, Long donanteId, String tipo,
                                        List<String> avisos, Throwable causa) {
            super(mensaje, causa);
            this.donanteId = donanteId;
            this.tipo = tipo;
            this.avisos = avisos;
        }
        public Long getDonanteId() { return donanteId; }
        public String getTipo() { return tipo; }
        public List<String> getAvisos() { return avisos; }
    }

    @SuppressWarnings("rawtypes")
    public Resultado registrar(RegistroAdminRequestDTO req) {
        validar(req);
        List<String> avisos = new ArrayList<>();
        boolean creado = req.donanteExistente() == null;
        boolean esJuridica;
        Long donanteId;

        if (!creado) {
            esJuridica = "juridica".equals(req.donanteExistente().tipo());
            donanteId = req.donanteExistente().id();
        } else {
            var nuevo = req.donanteNuevo();
            esJuridica = "juridica".equals(nuevo.tipo());
            String email = nuevo.email().trim();

            // 1) email libre  2) donante  3) usuario sin contraseña (si falla, se borra el donante)
            authApi.validarEmailDisponible(email);
            donanteId = donacionesApi.crearDonante(esJuridica, nuevo.datos());
            try {
                authApi.crearUsuario(email, null,
                        esJuridica ? "DONANTE_JURIDICO" : "DONANTE_HUMANO", donanteId, nuevo.nombre());
            } catch (RuntimeException e) {
                donacionesApi.eliminarDonanteConToken(esJuridica, donanteId);
                throw e;
            }

            // 4) mail para elegir contraseña: si falla no se deshace nada, solo se avisa
            try {
                authApi.solicitarRecuperacion(email);
                avisos.add("Le enviamos un correo a " + email + " para que elija su contraseña. "
                        + "El enlace vence en 15 minutos; después puede usar «Olvidé mi contraseña» en el login.");
            } catch (RuntimeException e) {
                log.warn("No se pudo pedir el mail de contraseña para {}", email, e);
                avisos.add("No se pudo enviar el correo a " + email + ". El donante puede usar "
                        + "«Olvidé mi contraseña» en el login para elegir su clave.");
            }
        }

        // 5) la donación
        try {
            List<Map> donaciones = donacionesApi.crearDonacion(esJuridica, donanteId, req.donacion());
            return new Resultado(donanteId, tipo(esJuridica), donaciones, avisos);
        } catch (SesionExpiradaException e) {
            throw e;
        } catch (RuntimeException e) {
            String msg = creado
                    ? "El donante quedó registrado (#" + donanteId + ") pero no se pudo registrar la donación. Reintentá."
                    : "No se pudo registrar la donación.";
            throw new DonacionFallidaException(msg, donanteId, tipo(esJuridica), avisos, e);
        }
    }

    private String tipo(boolean esJuridica) {
        return esJuridica ? "juridica" : "humana";
    }

    private void validar(RegistroAdminRequestDTO req) {
        if (req == null || req.donacion() == null) {
            throw new IllegalArgumentException("Faltan los datos de la donación.");
        }
        boolean existente = req.donanteExistente() != null;
        boolean nuevo = req.donanteNuevo() != null;
        if (existente == nuevo) {
            throw new IllegalArgumentException("Indicá un donante existente o los datos de uno nuevo.");
        }
        if (existente) {
            if (req.donanteExistente().id() == null) {
                throw new IllegalArgumentException("Falta el id del donante.");
            }
        } else {
            var n = req.donanteNuevo();
            if (n.email() == null || n.email().isBlank()) {
                throw new IllegalArgumentException("El email del donante es obligatorio.");
            }
            if (n.nombre() == null || n.nombre().isBlank() || n.datos() == null) {
                throw new IllegalArgumentException("Faltan los datos del donante.");
            }
            if (!"humana".equals(n.tipo()) && !"juridica".equals(n.tipo())) {
                throw new IllegalArgumentException("Tipo de donante inválido.");
            }
        }
    }
}