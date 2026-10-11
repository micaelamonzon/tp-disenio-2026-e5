package ar.edu.utn.frba.ddsi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Guarda las fotos que sube la entidad y devuelve sus URLs.
// Los servicios de dominio solo guardan la URL (no el archivo).
// Hoy las fotos quedan en una carpeta local del front; en el despliegue (entrega 6)
// se puede reemplazar por un almacenamiento en la nube sin tocar nada más.
@Service
public class FotoStorageService {

    private final Path directorio;
    private final String urlBase;

    public FotoStorageService(@Value("${fotos.directorio}") String directorio,
                              @Value("${fotos.url-base}") String urlBase) {
        this.directorio = Paths.get(directorio).toAbsolutePath().normalize();
        this.urlBase = urlBase;
    }

    public List<String> guardar(List<MultipartFile> fotos) {
        try {
            Files.createDirectories(directorio);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de fotos", e);
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile foto : fotos) {
            if (foto.isEmpty()) continue;

            // Validamos también en el servidor: la validación del JS se puede saltear
            String tipo = foto.getContentType();
            if (!"image/jpeg".equals(tipo) && !"image/png".equals(tipo)) {
                throw new IllegalArgumentException("Solo se aceptan fotos en formato JPG o PNG.");
            }

            // Nombre único para no pisar fotos con el mismo nombre
            String nombre = UUID.randomUUID() + ("image/png".equals(tipo) ? ".png" : ".jpg");
            try {
                foto.transferTo(directorio.resolve(nombre));
            } catch (IOException e) {
                throw new RuntimeException("No se pudo guardar la foto", e);
            }
            urls.add(urlBase + nombre);
        }
        return urls;
    }
}