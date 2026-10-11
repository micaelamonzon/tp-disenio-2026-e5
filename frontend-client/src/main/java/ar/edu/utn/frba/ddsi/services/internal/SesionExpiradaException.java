package ar.edu.utn.frba.ddsi.services.internal;

// El servicio de autenticación rechazó el refresh token: hay que volver a iniciar sesión.
// La atrapa SesionExpiradaHandler, que cierra la sesión y redirige al login.
public class SesionExpiradaException extends RuntimeException {
    public SesionExpiradaException() {
        super("La sesión expiró. Volvé a iniciar sesión.");
    }
}
