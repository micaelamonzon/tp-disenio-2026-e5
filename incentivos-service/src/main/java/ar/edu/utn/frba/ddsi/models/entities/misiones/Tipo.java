package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;

import java.util.List;

public interface Tipo {
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones);
}
