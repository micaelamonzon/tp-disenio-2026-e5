package ar.edu.utn.frba.ddsi.dtos;

// Body de POST /api/auth
public record LoginRequestDTO(String email, String password) {
}
