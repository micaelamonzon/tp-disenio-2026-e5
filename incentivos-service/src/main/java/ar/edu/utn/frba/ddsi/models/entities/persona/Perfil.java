package ar.edu.utn.frba.ddsi.models.entities.persona;
import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaDeMision;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Perfil {
    Long id;
    ArrayList<Insignia> insignias;
    String nombreDeUsuario; // lo sacamos del nombre y apellido del donante o de la razón social de la empresa
    CategoriaDeMision categoria;
    Integer totalHistoricoPorPeriodo;
    ArrayList<MetricaMensual> evolucionMensual;
    Boolean insigniasPrivadas;


    public Perfil (String nombreDeUsuario, CategoriaDeMision categoria) {
        this.insignias = new ArrayList<>();
        this.insigniasPrivadas = false;
        this.nombreDeUsuario = nombreDeUsuario;
        this.categoria = categoria;
        this.evolucionMensual = new ArrayList<>();
    }

    public void agregarInsignia(Insignia insignia){
        this.insignias.add(insignia);
    }
}
