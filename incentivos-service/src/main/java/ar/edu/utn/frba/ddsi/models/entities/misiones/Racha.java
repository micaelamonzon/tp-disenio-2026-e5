package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("Racha")
public class Racha extends Mision{
    @Column
    private Integer meses; //minimo 2 meses y maximo 12 meses
    @Column
    private Integer mesInicial; // ej: marzo -> 3
    @Column
    private Integer mesFinal; // ej: abril -> 4


    public Racha(NombreDeMision nombre,Integer mesInicial, Integer mesFinal){
        this.setNombre(nombre);
        this.meses = mesFinal - mesInicial + 1; //calculo la cantidad de meses que dura la racha
        this.mesInicial = mesInicial; //empiezo en el mes 1
        this.mesFinal = mesFinal; // termino en el num de mes
        this.setInsigniaGanadora(Insignia.RACHA);
    }

    @Override
    public AvanceDeMision seCompletoLaMision(List<DonacionSegmentada> donaciones) {

        //por lo menos una donacion tiene que haber sido donada al mes siguiente
        int mesI = this.mesInicial;
        int mesF = this.mesFinal;
        int contadorDeMesesConDonaciones = 0;

        while(mesI <= mesF){
            int mes = mesI;
            boolean cumple = donaciones.stream().map(DonacionSegmentada::getFechaDeIngreso).
                    anyMatch( f ->  f.getMonthValue() == mes);

            if(cumple){
              contadorDeMesesConDonaciones++;
            }
            mesI += 1;
        }

        return this.calcularProgresoyDistancia(contadorDeMesesConDonaciones,meses);
    }

}
