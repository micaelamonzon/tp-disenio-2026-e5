package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import lombok.Data;

import java.util.List;
import java.time.LocalDateTime;
import java.time.Month;
@Data
public class Racha extends Mision{

    private Integer meses; //minimo 2 meses y maximo 12 meses
    private Integer mesInicial;
    private Integer mesFinal;


    public Racha(String nombre,Integer meses){
        super(nombre);
        this.meses = meses;
        this.mesInicial = 1; //empiezo en el mes 1
        this.mesFinal = meses; // termino en el num de mes
    }

    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {
        // pedirle al servicio de donaciones,
        // todas las donaciones de la persona donante y que se hayan hecho durante los x meses consecutivos


        //por lo menos una donacion tiene que haber sido donada al mes siguiente
        int mesI = this.mesInicial;
        int mesF = this.mesFinal;

        while(mesI <= mesF){
            int finalMesI = mesI;
            boolean cumple = donaciones.stream().map(DonacionSinSegmentar::getFechaDeIngreso).
                    anyMatch( f ->  f.getMonthValue() == finalMesI);
            if(!cumple){
                int distanciaRestante = 100 - this.getProgreso();
                this.setDistanciaDelObjetivo(Math.max(0, distanciaRestante));
                this.bajarProgreso();
            }else {
                this.subirProgreso();
            }
            mesI += 1;
        }

        if (this.getProgreso() == 100) {
            this.setEstadoDeMision(EstadoDeMision.COMPLETADA);
            this.setFechaCompletada(java.time.LocalDate.now());
            return Boolean.TRUE;
        } else {
            this.setEstadoDeMision(EstadoDeMision.BLOQUEADA);
            return Boolean.FALSE;
        }
    }
    public void subirProgreso(){
        super.subirProgreso(this.meses);
    }
    public void bajarProgreso(){
        super.bajarProgreso(this.meses);
    }
}
