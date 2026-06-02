package ar.edu.utn.frba.ddsi.donaciones.models.entities.Necesidad;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@Getter
@NoArgsConstructor
public class NecesidadRecurrente extends Necesidad {
    private int cantidadObjetivoPorPeriodo;
    private int cantidadRecibidaEnPeriodo;

    @Enumerated(EnumType.STRING)
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
