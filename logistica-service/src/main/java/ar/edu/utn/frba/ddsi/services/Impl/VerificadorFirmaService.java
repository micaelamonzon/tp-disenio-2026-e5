package ar.edu.utn.frba.ddsi.services.Impl;

import ar.edu.utn.frba.ddsi.config.LogisticaProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class VerificadorFirmaService {
    private final LogisticaProperties propiedades;

    public VerificadorFirmaService(LogisticaProperties propiedades) {
        this.propiedades = propiedades;
    }
    public boolean verificar(String firmaRecibida, String bodyRaw){ // firma recibida: X-SIGNATURE
        try{
            String firmaEsperada = calcularFirma(bodyRaw);

            return MessageDigest.isEqual(
                    firmaEsperada.getBytes(StandardCharsets.UTF_8),
                    firmaRecibida.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e){
            // eroor idc
            return false;
        }
    }
    private String calcularFirma(String bodyRaw) throws Exception{
        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec keySpec = new SecretKeySpec(
                propiedades.getClientSecret()
                        .getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        mac.init(keySpec);

        byte [] hashBytes = mac.doFinal(
                bodyRaw.getBytes(StandardCharsets.UTF_8)
        );

        return "sha256=" + bytesToHex(hashBytes);
    }

    private String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
