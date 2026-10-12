package ar.edu.utn.frba.ddsi.donaciones.dto;

import java.util.List;

// Body opcional de PATCH /donacion/{id}/entregar: las URLs de las fotos
// que cargó la entidad al confirmar la recepción (las manda logística)
public record FotosEntregaDTO(List<String> fotosUrl) {}