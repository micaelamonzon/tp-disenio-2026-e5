package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.persona.Bien;
import ar.edu.utn.frba.ddsi.models.entities.persona.Categoria;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;

import java.util.List;

@Data
public class Completitud extends Mision {

    private List<Categoria> categorias;

    public Completitud(String nombre,List<Categoria> categorias){
        this.setNombre(nombre);
        this.categorias = categorias;
        setInsigniaGanadora(Insignia.COMPLETITUD);
    }

    //Completitud: realizar donaciones de X categorías distintas
    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {
        this.setProgreso(0);
        List<Categoria> todasLasCategorias = donaciones.stream()
                .flatMap(d -> d.getBienes().stream())
                .map(bien -> bien.getSubcategoria().getCategoria())
                .toList();

        long categoriasUnicas = todasLasCategorias.stream()
                .distinct()
                .count();

        boolean todasSonDistintas = todasLasCategorias.size() == categoriasUnicas;

        int distanciaRestante = 100 - this.getProgreso();
        this.setDistanciaDelObjetivo(Math.max(0, distanciaRestante));

        if (todasSonDistintas) {
            this.setEstadoDeMision(EstadoDeMision.COMPLETADA);
            this.setFechaCompletada(java.time.LocalDate.now());
            this.subirProgreso(donaciones.size());
            return Boolean.TRUE;
        }

        this.setEstadoDeMision(EstadoDeMision.BLOQUEADA);
        return Boolean.FALSE;

    }

    public void subirProgreso(Integer cantDonaciones){
       super.subirProgreso(cantDonaciones);
    }


}
