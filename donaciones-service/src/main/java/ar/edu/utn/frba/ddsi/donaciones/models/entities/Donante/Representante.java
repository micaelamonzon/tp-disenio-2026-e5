package ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Persistente;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "representante")
public class Representante extends Persistente {
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "dni")
    private Integer DNI;

    // Los medios de contacto pertenecen al representante: se guardan y se borran con él
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "representante_id", referencedColumnName = "id")
    private List<MedioDeNotificacion> mediosDeNotificacion = new ArrayList<>();

    public Representante(String nombre, String apellido, Integer dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.DNI = dni;
    }
}
