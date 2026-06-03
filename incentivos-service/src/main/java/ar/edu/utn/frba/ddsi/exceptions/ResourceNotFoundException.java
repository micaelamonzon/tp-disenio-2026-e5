package ar.edu.utn.frba.ddsi.exceptions;

import lombok.Data;
import org.springframework.stereotype.Component;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
