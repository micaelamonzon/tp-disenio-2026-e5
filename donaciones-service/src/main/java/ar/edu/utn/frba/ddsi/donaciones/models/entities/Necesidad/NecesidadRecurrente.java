package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Getter
@NoArgsConstructor
@Entity
@Table(name = "necesidad_recurrente")
public class NecesidadRecurrente extends Necesidad {
    @Column(name = "cantidad_objetiva_por_periodo")
    private int cantidadObjetivoPorPeriodo;

    @Column(name = "cantidad_recibida_en_periodo")
    private int cantidadRecibidaEnPeriodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo")
    private TipoPeriodo periodo;

    public NecesidadRecurrente(Subcategoria subcategoria, String descripcion,
                               int cantidadObjetivoPorPeriodo, TipoPeriodo periodo) {
        super(subcategoria, descripcion);
        this.cantidadObjetivoPorPeriodo = cantidadObjetivoPorPeriodo;
        this.cantidadRecibidaEnPeriodo = 0;
        this.periodo = periodo;
    }

    @Override
    public void satisfacer(int cantidad) {
        this.cantidadRecibidaEnPeriodo += cantidad;
        if (this.cantidadRecibidaEnPeriodo >= this.cantidadObjetivoPorPeriodo) {
            marcarComoSatisfecha();
        }
    }

    // Se llama al inicio de cada nuevo período (puede usarlo un @Scheduled)
    public void reiniciarPeriodo() {
        this.cantidadRecibidaEnPeriodo = 0;
        // no se resetea estaSatisfecha globalmente, depende del criterio del equipo
    }
}
