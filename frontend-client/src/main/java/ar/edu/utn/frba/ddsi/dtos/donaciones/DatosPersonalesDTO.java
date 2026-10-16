package ar.edu.utn.frba.ddsi.dtos.donaciones;

import java.util.List;

// Datos personales del donante para su perfil
public record DatosPersonalesDTO(
        String nombre,          // "Nombre Apellido" o razón social
        String tipoDocumento,   // "DNI" o "CUIT"
        String documento,
        String direccion,       // solo humano (si la cargó)
        String rubro,           // solo jurídico
        String tipoOrganizacion,// solo jurídico: Empresa, ONG, Gubernamental, Institución
        List<String> contactos  // "Email: x@y.com", "SMS: 11..."
) {
}
