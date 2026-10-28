package ar.edu.utn.frba.ddsi.dtos.donaciones;

import java.util.Map;

// Lo que manda admin.js al front. UNO de los dos: donanteExistente o donanteNuevo.
public record RegistroAdminRequestDTO(
        DonanteExistente donanteExistente,
        DonanteNuevo donanteNuevo,
        Map<String, Object> donacion) {

    public record DonanteExistente(Long id, String tipo) {}              // tipo: "humana" | "juridica"

    public record DonanteNuevo(String tipo, String email, String nombre,
                               Map<String, Object> datos) {}             // datos = body del donante, como lo arma el JS
}