package ar.edu.utn.frba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "punto_de_entrega")
public class PuntosDeEntrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "entidad_beneficiaria_id")
    private Long entidadBeneficiariaId;

    @ManyToOne
    @JoinColumn(name = "entrega_id")
    private Entrega entrega;
}