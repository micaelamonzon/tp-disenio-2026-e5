package ar.edu.utn.frba.ddsi.models.entities.misiones;

import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;

import java.util.List;
import java.time.LocalDateTime;
import java.time.Month;

public class Racha implements  Tipo{

    private Integer meses; //minimo 2 meses y maximo 12 meses
    private Integer mesInicial;
    private Integer mesFinal;
    private Integer distanciaDelObjetivo = 100;
    private Integer progreso;


    public Racha(Integer meses){
        this.meses = meses;
        this.mesInicial = 1; //empiezo en el mes 1
        this.mesFinal = meses; // termino en el num de mes
    }

    @Override
    public Boolean seCompletoLaMision(List<DonacionSinSegmentar> donaciones) {
        // pedirle al servicio de donaciones,
        // todas las donaciones de la persona donante y que se hayan hecho durante los x meses consecutivos

        for(int i = 0; i < donaciones.size(); i++){
            int numeroDeMes = donaciones.get(i).getFechaDeIngreso().getMonthValue();
            if(numeroDeMes >= mesInicial && numeroDeMes <= mesFinal){
                this.subirProgreso(donaciones.size());
                this.distanciaDelObjetivo -= this.progreso;
            }
        }

        //por lo menos una donacion tiene que haber sido donada al mes siguiente
        int mesI = this.mesInicial;
        int mesF = this.mesFinal;

        while(mesI <= mesF){
            int finalMesI = mesI;
            boolean cumple = donaciones.stream().map(DonacionSinSegmentar::getFechaDeIngreso).
                    anyMatch( f ->  f.getMonthValue() == finalMesI);
            if(!cumple){
                this.bajarProgreso();
            }
            mesI += 1;
        }

        return this.progreso == 100; //se habra completado cuando el progreso sea del 100
    }
    public void subirProgreso(Integer cantDonaciones){
        this.progreso += (100/cantDonaciones);
    }
    public void bajarProgreso(){
        this.progreso -= (100/meses);
    }
}
