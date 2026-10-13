package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "camiones")
public class nCamion {
    @Id
    private String id;
    @Indexed(unique = true)
    private String patente;
    private Double volumen;
    private Double altura;
    private Double capacidadCarga;
    private Boolean disponible;
    private List<Posicion> posiciones = new ArrayList<>();
    private String rutaId;

    public nCamion(String patente, Double volumen, Double altura, Double capacidadCarga, Boolean disponible) {
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