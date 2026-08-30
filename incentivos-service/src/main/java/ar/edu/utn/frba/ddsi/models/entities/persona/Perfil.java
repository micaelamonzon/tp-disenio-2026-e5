package ar.edu.utn.frba.ddsi.models.entities.persona;
import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaMision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Perfil {
    ArrayList<Insignia> insignias;
    Boolean insigniaPrivada;
    String nombreDeUsuario; // lo sacamos del nombre y apellido del donante o de la razón social de la empresa
    CategoriaMision categoria;
    Integer totalHistoricoPorPeriodo;
    ArrayList<MetricaMensual> evolucionMensual;

    public Perfil (String nombreDeUsuario, CategoriaMision categoria) {
        this.insignias = new ArrayList<>();
        this.insigniaPrivada = false;
        this.nombreDeUsuario = nombreDeUsuario;
        this.categoria = categoria;
        this.evolucionMensual = new ArrayList<>();
    }

    public void agregarInsignia(Insignia insignia){
        this.insignias.add(insignia);
    }
}
