package ar.edu.utn.frba.ddsi.donaciones;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Categoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV.ImportadorDeCSV;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.Segmentador;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.util.ArrayList;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;


@SpringBootTest
public class DonacionesServiceApplicationTests {

    @Test
    void comprobarImportadorCsv() throws CsvValidationException, IOException {
        ImportadorDeCSV importadorDeCSV = new ImportadorDeCSV();
        importadorDeCSV.importarCsv("C:\\Users\\Analía\\Desktop\\donantes_import.csv");
        ArrayList<PersonaHumana> donantesHumanos = importadorDeCSV.getDonantesHumanos();
        ArrayList<PersonaJuridica> donantesJuridicos = importadorDeCSV.getDonantesJuridicos();

        PersonaHumana nuevoDonante = donantesHumanos.get(0);

        Integer cant = donantesHumanos.size() + donantesJuridicos.size();

        Assertions.assertEquals(20000,cant);
        Assertions.assertEquals( "Ana",  nuevoDonante.getNombre());
        Assertions.assertEquals( "Navarro", nuevoDonante.getApellido());
        Assertions.assertEquals( 28456905, nuevoDonante.getNumeroDeDocumento());
        Assertions.assertEquals( "ananavarro3658@yahoo.com", nuevoDonante.getMediosDeNotificacion().get(0).getDatoDeContacto());
        Assertions.assertEquals("+54 11 5181-9600", nuevoDonante.getMediosDeNotificacion().get(1).getDatoDeContacto());

        
    }
    @Test
    void  comprobarSegmentador(){

        Segmentador segmentador = new Segmentador();

        Categoria alimentos = new Categoria("Alimentos");
        Categoria ropa = new Categoria("Ropa");
        Categoria muebles = new Categoria("Muebles");

        Subcategoria cereales = new Subcategoria("Cereales",true, alimentos);
        Subcategoria remeras = new Subcategoria("Remeras",false, ropa);
        Subcategoria sillas = new Subcategoria("Sillas",false, muebles);

        Bien bien1 = new Bien("8 paquetes de arroz"," ",null,cereales,null,null,null,8);
        Bien bien2 = new Bien("5 paquetes de arroz"," ",null,cereales,null,null,null,5);
        Bien bien3 = new Bien("10 remeras"," ",null,remeras,null,null,null,10);
        Bien bien4 = new Bien("15 remeras"," ",null,remeras,null,null,null,5);
        Bien bien5 = new Bien("10 sillas"," ",null,sillas,null,null,null,10);

        DonacionSinSegmentar donacionSinSegmentar = new DonacionSinSegmentar();
        donacionSinSegmentar.agregarBien(bien1);
        donacionSinSegmentar.agregarBien(bien2);
        donacionSinSegmentar.agregarBien(bien3);
        donacionSinSegmentar.agregarBien(bien4);
        donacionSinSegmentar.agregarBien(bien5);

        ArrayList<DonacionSegmentada> donacionesNuevas = segmentador.segmentar(donacionSinSegmentar);

        Assertions.assertEquals(3,donacionesNuevas.size());

    }
}
