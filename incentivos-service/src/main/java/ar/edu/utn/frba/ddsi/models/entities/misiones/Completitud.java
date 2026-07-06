package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;

import java.util.List;

@Data
public class Completitud extends Mision {

    private Integer objetivoDeCategoriasDistintas;


    public Completitud(String nombre,Integer numeroDeCategoriasDistintas){
        this.setNombre(nombre);
        this.objetivoDeCategoriasDistintas = numeroDeCategoriasDistintas;
        setInsigniaGanadora(Insignia.COMPLETITUD);
    }

    //Completitud: realizar donaciones de X categorías distintas
    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {

        List<Categoria> todasLasCategorias = donaciones.stream()
                .flatMap(d -> d.getBienes().stream())
                .map(bien -> bien.getSubcategoria().getCategoria())
                .toList();

        long categoriasUnicas = todasLasCategorias.stream()
                .distinct()
                .count();

        Integer progreso =(int) Math.min(categoriasUnicas / objetivoDeCategoriasDistintas, 1.0);
        Integer progresoActual = progreso * 100;
        this.setProgreso(progresoActual);

        boolean cumple = categoriasUnicas >= objetivoDeCategoriasDistintas ;

        Integer distanciaRestante = objetivoDeCategoriasDistintas - this.getProgreso();
        this.setDistanciaDelObjetivo(Math.max(0, distanciaRestante));

        if (cumple) {
            this.setEstadoDeMision(EstadoDeMision.COMPLETADA);
            this.setFechaCompletada(java.time.LocalDate.now());
            return Boolean.TRUE;
        }

        this.setEstadoDeMision(EstadoDeMision.BLOQUEADA);
        return Boolean.FALSE;

    }


}
