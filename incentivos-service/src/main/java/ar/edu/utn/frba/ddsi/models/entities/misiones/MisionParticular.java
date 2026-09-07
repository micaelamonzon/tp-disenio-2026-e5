package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.persistente.Persistente;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "MisionParticular")
public class MisionParticular extends Persistente {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id", referencedColumnName = "id")
    private Donante donante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mision_id", referencedColumnName = "id")
    private Mision mision;

    @Column
    private Integer distanciaDelObjetivo;

    @Column
    private Integer progreso;

    @Enumerated(EnumType.STRING)
    private EstadoDeMision estado;

    @Column
    private LocalDate fechaCompletada;

    @Column
    private LocalDate fechaUltimoAvance;


    public MisionParticular(Mision mision) {
        this.mision = mision;
        this.estado=EstadoDeMision.ACTUAL;
    }

    public void avanzarMision() {

        AvanceDeMision avance = this.mision.seCompletoLaMision(donante.getDonaciones());

        this.progreso = avance.getProgreso();
        this.distanciaDelObjetivo = avance.getDistanciaDelObjetivo();
        this.setFechaUltimoAvance(LocalDate.now());

        if(avance.getCumple()){
            this.setEstado(EstadoDeMision.COMPLETADA);
            this.setFechaCompletada(java.time.LocalDate.now());
        }
    }
}
