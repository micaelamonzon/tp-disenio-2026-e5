package ar.edu.utn.frba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "camion")
public class Camion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patente", unique = true)
    private String patente;

    @Column(name = "volumen")
    private Double volumen;

    @Column(name = "altura")
    private Double altura;

    @Column(name = "capacidad_carga")
    private Double capacidadCarga;

    @Column(name = "disponible")
    private Boolean disponible;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "camion_id")
    private List<Posicion> posiciones = new ArrayList<>();

    @OneToOne(mappedBy = "camion")
    private Ruta ruta;

    public Camion(String patente, Double volumen, Double altura, Double capacidadCarga, Boolean disponible) {
        this.patente = patente;
        this.volumen = volumen;
        this.altura = altura;
        this.capacidadCarga = capacidadCarga;
        this.disponible = disponible;
        this.posiciones = new ArrayList<>();
    }

    public Posicion ultimaPosicion() {
        return this.posiciones.get(this.posiciones.size() - 1);
    }

    public void agregarPosicion(Posicion posicion) {
        this.posiciones.add(posicion);
    }
}