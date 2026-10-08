package ar.edu.utn.frba.ddsi.dtos;

// Body de POST /api/auth/refresh (mismo formato que ya envía el front)
public record RefreshRequestDTO(String refreshToken) {
}
