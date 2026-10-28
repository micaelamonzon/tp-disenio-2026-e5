package ar.edu.utn.frba.ddsi.donaciones.dto;

public record NecesidadRankingDTO(Long id, String tipo, String descripcion,
                                  Long entidadId, String entidadRazonSocial,
                                  int cantidadSolicitada, int cantidadRecibida) {}
