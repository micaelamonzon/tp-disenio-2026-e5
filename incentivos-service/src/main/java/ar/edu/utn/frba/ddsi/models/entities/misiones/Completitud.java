package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("Completitud")
public class Completitud extends Mision {

    @Column
    private Integer objetivoDeCategoriasDistintas;

    public Completitud(NombreDeMision nombre,Integer objetivoDeCategoriasDistintas){
        this.setNombre(nombre);
        this.objetivoDeCategoriasDistintas = objetivoDeCategoriasDistintas;
        setInsigniaGanadora(Insignia.COMPLETITUD);
    }

    //Completitud: realizar donaciones de X categorías distintas
    @Override
    public AvanceDeMision seCompletoLaMision(List<DonacionSegmentada> donaciones) {

        List<Categoria> todasLasCategorias = donaciones.stream()
                .map(d -> d.getCategoria())
                .toList();

        Long categoriasUnicas = todasLasCategorias.stream()
                .distinct()
                .count();


      return this.calcularProgresoyDistancia(categoriasUnicas.intValue(),this.objetivoDeCategoriasDistintas);
    }




}
