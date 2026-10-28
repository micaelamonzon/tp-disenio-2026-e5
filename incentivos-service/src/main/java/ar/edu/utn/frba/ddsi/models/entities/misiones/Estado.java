package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Entity
@Table(name = "Estado")
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoDeMision estado;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "estado_actual")
    private Boolean estado_actual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id", referencedColumnName = "id")
    private Donante donante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_particular_id", referencedColumnName = "id")
    private MisionParticular misionParticular;

    public Estado(EstadoDeMision estadoNuevo, Donante donante, MisionParticular misionParticular ) {
        this.estado = estadoNuevo;
        this.donante = donante;
        this.misionParticular = misionParticular;
        this.fecha = LocalDateTime.now();
        this.estado_actual = true;

    }

}
