package ar.edu.utn.frba.ddsi.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "donacion")
public class Donacion {
    @Id
    private String id;
    private Long idEnDonaciones;
    private Long entidadBeneficiariaId;
    private Long donanteId;
    private Double latitud;
    private Double longitud;
    private String destino;
    private String subcategoria;
    private String estado;
    private TipoDeDonante tipoDeDonante;

    public Donacion(Long idEnDonaciones,TipoDeDonante tipoDeDonante,Long entidadBeneficiariaId,Double latitud,Double longitud, Long donanteId, String destino, String subcategoria, String estado) {
        this.idEnDonaciones = idEnDonaciones;
        this.tipoDeDonante = tipoDeDonante;
        this.entidadBeneficiariaId = entidadBeneficiariaId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.donanteId = donanteId;
        this.destino = destino;
        this.subcategoria = subcategoria;
        this.estado = estado;
    }
}
