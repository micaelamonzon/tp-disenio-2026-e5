package ar.edu.utn.frba.ddsi.donaciones;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Donante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV.ImportadorDeCSV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;


@SpringBootTest
public class DonacionesServiceApplicationTests {

    @Test
    void comprobarImportadorCsv(){
        ImportadorDeCSV importadorDeCSV = new ImportadorDeCSV();
        ArrayList<Donante> donantes = importadorDeCSV.importarCsv("C:\\Users\\Analía\\Desktop\\donantes_import.csv");

        Donante nuevoDonante = donantes.get(0);

        Assertions.assertEquals(20000,donantes.size());
        Assertions.assertEquals( "Ana", ((PersonaHumana) nuevoDonante).getNombre());
        Assertions.assertEquals( "Navarro", ((PersonaHumana) nuevoDonante).getApellido());

        
    }
}
