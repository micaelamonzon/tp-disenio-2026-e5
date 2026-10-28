package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.persistente.Persistente;
import ar.edu.utn.frba.ddsi.models.entities.persona.Donante;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import jakarta.persistence.CascadeType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
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
    private LocalDateTime fechaCompletada;

    @Column
    private LocalDateTime fechaUltimoAvance;

    @OneToMany(mappedBy = "misionParticular", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Estado> historialDeEstados = new ArrayList<>();


    public MisionParticular(Mision mision, Donante donante) {
        this.mision = mision;
        this.donante = donante;
        this.estado = EstadoDeMision.ACTUAL;
        this.progreso= 0;
        this.distanciaDelObjetivo = 100;
    }

    public void avanzarMision() {

        AvanceDeMision avance = this.mision.seCompletoLaMision(donante.getDonaciones());

        this.progreso = avance.getProgreso();
        this.distanciaDelObjetivo = avance.getDistanciaDelObjetivo();
        this.setFechaUltimoAvance(LocalDateTime.now());

        if(avance.getCumple()){
            this.setEstado(EstadoDeMision.COMPLETADA);
            this.setFechaCompletada(java.time.LocalDateTime.now());
        }
    }


    public void agregarEstado(Estado estado) {
        if(this.historialDeEstados.isEmpty()){
            this.historialDeEstados.add(estado);
            return;
        }
        Estado estadoAnterior = this.getHistorialDeEstados().get(this.getHistorialDeEstados().size() - 1);
        estadoAnterior.setEstado_actual(false);
        this.historialDeEstados.add(estado);
    }

}
