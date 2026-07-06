package ar.edu.utn.frba.ddsi.models.entities.persona;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import ar.edu.utn.frba.ddsi.models.entities.categorias.Categoria;


import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Perfil {
    List<Insignia> insignias;
    Boolean insigniaPrivada;
    String nombreDeUsuario; // lo sacamos del nombre y apellido del donante o de la razón social de la empresa
    Categoria categoria;
    Integer totalHistoricoPorPeriodo;
    List<MetricaMensual> evolucionMensual;

    public Perfil (String nombreDeUsuario, Categoria categoria) {
        this.insignias = new ArrayList<>();
        this.insigniaPrivada = false;
        this.nombreDeUsuario = nombreDeUsuario;
        this.categoria = categoria;
    }

    public void agregarInsignia(Insignia insignia){
        this.insignias.add(insignia);
    }
}
