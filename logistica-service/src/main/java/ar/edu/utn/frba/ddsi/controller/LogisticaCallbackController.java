package ar.edu.utn.frba.ddsi.controller;
import ar.edu.utn.frba.ddsi.services.ILogisticaCallbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/logistica-service")
public class LogisticaCallbackController {
    private final ILogisticaCallbackService logisticaCallbackService;


    public LogisticaCallbackController(ILogisticaCallbackService logisticaCallbackService){
        this.logisticaCallbackService = logisticaCallbackService;
    }

    @PostMapping("/callback-planificacion")
    public ResponseEntity<Void> recibirCallback(@RequestHeader("X-Signature") String firma, @RequestBody String bodyRaw) {
        return this.logisticaCallbackService.recibirCallback(firma, bodyRaw);
    }

}
