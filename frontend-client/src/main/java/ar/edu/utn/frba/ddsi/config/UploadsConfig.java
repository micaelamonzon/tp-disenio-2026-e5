package ar.edu.utn.frba.ddsi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

// Sirve las fotos subidas por las entidades en /uploads/**
@Configuration
public class UploadsConfig implements WebMvcConfigurer {

    private final String directorio;

    public UploadsConfig(@Value("${fotos.directorio}") String directorio) {
        this.directorio = directorio;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = Paths.get(directorio).toAbsolutePath().normalize().toUri().toString();
        if (!ubicacion.endsWith("/")) ubicacion += "/";
        registry.addResourceHandler("/uploads/**").addResourceLocations(ubicacion);
    }
}