package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.persona.Insignia;
import lombok.Data;

import java.util.List;

@Data
public class Racha extends Mision{
    private Integer meses; //minimo 2 meses y maximo 12 meses
    private Integer mesInicial; // ej: marzo -> 3
    private Integer mesFinal; // ej: abril -> 4


    public Racha(String nombre,Integer meses,Integer mesInicial, Integer mesFinal){
        this.setNombre(nombre);
        this.meses = meses;
        this.mesInicial = mesInicial; //empiezo en el mes 1
        this.mesFinal = mesFinal; // termino en el num de mes
        this.setInsigniaGanadora(Insignia.RACHA);
    }

    @Override
    public Boolean seCompletoLaMision(List<DonacionSegmentada> donaciones) {
        // pedirle al servicio de donaciones,
        // todas las donaciones de la persona donante y que se hayan hecho durante los x meses consecutivos

        //por lo menos una donacion tiene que haber sido donada al mes siguiente
        int mesI = this.mesInicial;
        int mesF = this.mesFinal;

        while(mesI <= mesF){
            int finalMesI = mesI;
            boolean cumple = donaciones.stream().map(DonacionSegmentada::getFechaDeIngreso).
                    anyMatch( f ->  f.getMonthValue() == finalMesI);

            if(cumple){
                Integer progreso = (int)Math.min(1 / meses, 1.0);
                Integer progresoActual = progreso * 100;
                this.setProgreso(progresoActual);

                Integer distanciaRestante = meses - this.getProgreso();
                this.setDistanciaDelObjetivo(Math.max(0, distanciaRestante));
            }else{
                Integer progreso = (int)Math.min(1.0/ meses, 1.0);
                Integer progresoActual = progreso * 100;
                this.setProgreso(getProgreso() - progresoActual);
            }
            mesI += 1;
        }

        if (this.getProgreso() == 100) {
            this.setEstado(EstadoDeMision.COMPLETADA);
            this.setFechaCompletada(java.time.LocalDate.now());
            return Boolean.TRUE;
        } else {
            this.setEstado(EstadoDeMision.BLOQUEADA);
            return Boolean.FALSE;
        }
    }

}
