package ar.edu.utn.frba.ddsi.donaciones;

import ar.edu.utn.frba.ddsi.donaciones.config.NotificacionesProperties;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV.ImportadorDeCSV;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;

@SpringBootApplication
@EnableConfigurationProperties(NotificacionesProperties.class)
public class DonacionesServiceApplication {
    public static void main(String[] args) throws CsvValidationException, IOException {
        SpringApplication.run(DonacionesServiceApplication.class, args);
    }




}
