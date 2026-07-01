package ar.edu.utn.frba.ddsi.models.entities;

import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;

import java.util.ArrayList;
import java.util.List;


public class Camiones {
    private String patente;
    private double volumen;
    private double altura;
    private double capacidadCarga;
    private Deposito deposito;
    private List<Bien> bienes;

    public void Camion(String patente, double volumen, double altura, double capacidadCarga){
        this.patente = patente;
        this.volumen = volumen;
        this.altura = altura;
        this.capacidadCarga = capacidadCarga;
        this.bienes = new ArrayList<>();
    }

    
}
