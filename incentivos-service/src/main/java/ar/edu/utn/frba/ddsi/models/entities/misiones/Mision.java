package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.categoriasDeMisiones.CategoriaDeMision;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Mision")
@DiscriminatorColumn(name="discriminador")
public class Mision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NombreDeMision nombre;

    @Column(length = 16)
    private String insigniaGanadora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_mision_id", referencedColumnName = "id")
    private CategoriaDeMision categoria; //id de categoria a la que pertenece la mision

    @Column
    private Integer ordenSecuencial; //orden de la mision dentro de la categoria

    public Mision(NombreDeMision nombre, CategoriaDeMision categoria, Integer ordenSecuencial, String insigniaGanadora ) {
        this.categoria = categoria;
        this.ordenSecuencial = ordenSecuencial;
        this.nombre = nombre;
        this.insigniaGanadora = insigniaGanadora;
    }



    public AvanceDeMision seCompletoLaMision(List<DonacionSegmentada> donaciones){
        //lo reescriben las clases hijas
        return null;
    }

    public AvanceDeMision calcularProgresoyDistancia(Integer intento, Integer objetivo){

        double progreso = Math.min((double) intento / objetivo, 1.0);
        Integer progresoActual = (int) (progreso * 100);

        boolean cumple = intento >= objetivo;

        Integer distanciaRestante = Math.max(0, 100 - progresoActual);

        return new AvanceDeMision(progresoActual, distanciaRestante, cumple);

    }


}
