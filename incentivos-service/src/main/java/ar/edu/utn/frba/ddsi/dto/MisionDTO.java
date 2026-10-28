package ar.edu.utn.frba.ddsi.dto;


import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.CategoriaDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.NombreDeMision;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public record MisionDTO(
       Long Id,
       NombreDeMision nombre,
       Long categoriaId,
       Integer ordenSecuencial,
       String insigniaGanadora,
       Integer meses,
       Integer mesInicial,
       Integer mesFinal,
       Integer objetivoDeCategoriasDistintas,
       Integer cantidadDeDonaciones,
       Integer cantidadDeBienes
) {
}

