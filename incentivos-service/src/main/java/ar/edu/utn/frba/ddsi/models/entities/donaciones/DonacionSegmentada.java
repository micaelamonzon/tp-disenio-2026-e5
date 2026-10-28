package ar.edu.utn.frba.ddsi.models.entities.donaciones;

import ar.edu.utn.frba.ddsi.models.entities.persistente.Persistente;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "DonacionSegmentada")
public class DonacionSegmentada extends Persistente {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id", referencedColumnName = "id")
    private Donante donante;

    @Column
    private Long idEnDonaciones;

   @Column
    private LocalDateTime fechaDeIngreso;

    @Column
    private Boolean donacionEntregada;

    @Column
    private Long entidadBeneficiariaId;

    @Column
    private Integer cantidadDeBienes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private Categoria categoria;

    public DonacionSegmentada(Long idEnDonaciones,LocalDateTime fechaDeIngreso, Boolean donacionEntregada, Long entidadBeneficiariaId, Integer cantidadDeBienes, Categoria categoria) {
        this.idEnDonaciones = idEnDonaciones;
        this.fechaDeIngreso = fechaDeIngreso;
        this.donacionEntregada = donacionEntregada;
        this.entidadBeneficiariaId = entidadBeneficiariaId;
        this.cantidadDeBienes = cantidadDeBienes;
        this.categoria = categoria;
    }

}
