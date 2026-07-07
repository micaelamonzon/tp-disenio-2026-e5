package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
public class Camion {
    private Long id;
    private String patente;
    private Double volumen;
    private Double altura;
    private Double capacidadCarga;
    private Ruta ruta;
    private List<Posicion> posiciones;
    Boolean disponible;

    public Camion() {
    }

    public Camion(String patente, Double volumen, Double altura, Double capacidadCarga, Boolean disponible){
        this.patente = patente;
        this.volumen = volumen;
        this.altura = altura;
        this.capacidadCarga = capacidadCarga;
        this.posiciones = new ArrayList<>();
        this.disponible = disponible;
    }

    public Posicion ultimaPosicion(){
        return this.posiciones.get(this.posiciones.size() - 1);
    }

    public void agregarPosicion(Posicion posicion){
        this.posiciones.add(posicion);
    }
    
}
