package ar.edu.utn.frba.ddsi.dtos;

// Respuesta de login y refresh (mismo formato que el AuthResponseDTO del front)
public record TokensDTO(String accessToken, String refreshToken) {
}
