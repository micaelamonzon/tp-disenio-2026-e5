package ar.edu.utn.frba.ddsi.dtos;

public record CambiarPasswordRequestDTO(String token, String nuevaPassword) {
}
