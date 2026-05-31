package ar.edu.utn.frba.ddsi;
import ar.edu.utn.frba.ddsi.config.RestProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication

@EnableConfigurationProperties(RestProperties.class)
public class IncentivosServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(IncentivosServiceApplication.class, args);
    }
}
