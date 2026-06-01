package ar.edu.utn.frba.ddsi.models.entities.persona;

import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaColaborador;
import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaDeDonante;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import lombok.Data;

import java.util.List;

@Data
public class Perfil {
    Insignia insignia;
    Boolean insigniaPrivada;
    String nombreDeUsuario;
    CategoriaDeDonante categoria;
    Integer totalHistoricoPorPeriodo;
    List<MetricaMensual> evolucionMensual;

    public Perfil (Insignia insignia, Boolean insigniaPrivada, String nombreDeUsuario, CategoriaDeDonante categoria) {
        this.insignia = insignia;
        this.insigniaPrivada = insigniaPrivada;
        this.nombreDeUsuario = nombreDeUsuario;
        this.categoria = categoria;
    }

    public void subirDeCategoria(CategoriaDeDonante categoria, List<DonacionSinSegmentar> donaciones){
        if(categoria.pasaSiguienteCategoria(donaciones)){
            this.categoria = categoria;
        }
    }
}
