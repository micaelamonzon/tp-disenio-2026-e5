package ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV;


import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Tipo;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.TipoDeNotificacion;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Data;

import java.io.FileReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.io.IOException;

@Data
public class ImportadorDeCSV{

    ArrayList<PersonaHumana> donantesHumanos = new ArrayList<>();
    ArrayList<PersonaJuridica> donantesJuridicos = new ArrayList<>();

   public Void importarCsv(String path) throws IOException, CsvValidationException{

       CSVParser csvParser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(true).build();
       CSVReader reader = new CSVReaderBuilder(new FileReader(path)).withSkipLines(1).withCSVParser(csvParser).build();

       String [] linea;
       String esHumano = "HUMANA";
       String esJuridica = "JURIDICA";
       String esEncabezado = "TipoPersona";

       while((linea = reader.readNext()) != null){

            if(linea != null){
                if(linea[0].equals(esEncabezado)){
                    continue;
                }
                if(linea[0].equals(esHumano)){

                    String[] nombreYapellido = linea[3].split(" ");
                    String nombre = nombreYapellido[0];
                    String apellido = nombreYapellido[1];
                    Integer DNI = Integer.parseInt(linea[2]);
                    String email = linea[4];
                    String telefono = linea[5];

                    MedioDeNotificacion medioMail = new MedioDeNotificacion(TipoDeNotificacion.EMAIL, email);
                    MedioDeNotificacion medioTelefono = new MedioDeNotificacion(TipoDeNotificacion.SMS, telefono);
                    MedioDeNotificacion medioWhatsapp = new MedioDeNotificacion(TipoDeNotificacion.WHATSAPP, telefono);

                    PersonaHumana nuevoDonante = new PersonaHumana(nombre, apellido, null, DNI, null, null);

                    nuevoDonante.agregarMedioDeNotificacion(medioMail);
                    nuevoDonante.agregarMedioDeNotificacion(medioTelefono);
                    nuevoDonante.agregarMedioDeNotificacion(medioWhatsapp);
                    donantesHumanos.add(nuevoDonante);
                }
                if(linea[0].equals(esJuridica)){

                    String cuit = linea[2];
                    String razonSocial = linea[3];
                    Tipo tipoDeEmpresa = this.identificarTipo(razonSocial);
                    String email = linea[4];
                    String telefono = linea[5];

                    MedioDeNotificacion medioMail = new MedioDeNotificacion(TipoDeNotificacion.EMAIL, email);
                    MedioDeNotificacion medioTelefono = new MedioDeNotificacion(TipoDeNotificacion.SMS, telefono);
                    MedioDeNotificacion medioWhatsapp = new MedioDeNotificacion(TipoDeNotificacion.WHATSAPP, telefono);

                    PersonaJuridica nuevoDonante = new PersonaJuridica(cuit, razonSocial, tipoDeEmpresa, null);

                    nuevoDonante.agregarMedioDeNotificacion(medioMail);
                    nuevoDonante.agregarMedioDeNotificacion(medioTelefono);
                    nuevoDonante.agregarMedioDeNotificacion(medioWhatsapp);

                    donantesJuridicos.add(nuevoDonante);
                }
            }
       }

       return null;
   }

    public Tipo identificarTipo(String nombreDeLaEmpresa){
        // S.A. || S.A.S. || S.R.L -> Empresa
        // Asociación -> Gubernamental
        // cooperativa -> INSTITUCIÓN
        // fundación  ->  ONG

        String nombreNormalizado = normalizador(nombreDeLaEmpresa);

        if(nombreNormalizado.contains("s.a.") || nombreNormalizado.contains("s.a.s")|| nombreNormalizado.contains("s.r.l")){
            return Tipo.EMPRESA;
        }else if(nombreNormalizado.contains("asociacion")){
            return Tipo.GUBERNAMENTAL;
        }else if (nombreNormalizado.contains("cooperativa")){
            return Tipo.INSTITUCION;
        }else if(nombreNormalizado.contains("fundacion")){
            return Tipo.ONG;
        }
        return null;
    }

    public String normalizador(String razonSocial){

        razonSocial = Normalizer.normalize(razonSocial, Normalizer.Form.NFD);
        razonSocial = razonSocial.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        razonSocial = razonSocial.toLowerCase();

        return razonSocial;
    }

}



//    public ArrayList<Donante> importarCsv(String path) {
//
//        ArrayList<Donante> donantes = new ArrayList<>();
//
//        String linea;
//        String separador = ",";
//        String esHumano = "HUMANA";
//        String esJuridica = "JURIDICA";
//        String esEncabezado = "TipoPersona";
//
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//
//            while ((linea = br.readLine()) != null) { // HUMANA, DNI, Ana Navarro,45924197 ,anna@mail , +1121747299
//
//                String[] datos = linea.split(separador);  // datos es un vector/array que tiene los datos del registro. En donde
//                //cada valor de cada celda está guardada en un sub-indice del array/vector
//                //[HUMANA, DNI, Ana Navarro,45924197 ,anna@mail , +1121747299]
//
//                if(datos[0].equals(esEncabezado)){
//                    continue;
//                }
//                if (datos[0].equals(esHumano)) {
//
//                    String[] nombreYapellido = datos[3].split(" "); // [Ana, Navarro]
//                    Donante nuevoDonante = new PersonaHumana(nombreYapellido[0], nombreYapellido[1], null, null, null, null);
//
//                    MedioDeNotificacion medioEmail = new Email(datos[4]);
//                    MedioDeNotificacion medioTelefono = new SMS(datos[5]);
//
//                    nuevoDonante.mediosDeNotificaciones(medioEmail);
//                    nuevoDonante.mediosDeNotificaciones(medioTelefono);
//
//                    donantes.add(nuevoDonante);
//
//                } else if (datos[0].equals(esJuridica)) {
//                    Tipo tipoIdentificado = identificarTipo(datos[2]);
//                    Donante nuevoDonante = new PersonaJuridica(datos[2], datos[3], tipoIdentificado, null);
//
//                    MedioDeNotificacion medioEmail = new Email(datos[4]);
//                    MedioDeNotificacion medioTelefono = new SMS(datos[5]);
//
//                    nuevoDonante.mediosDeNotificaciones(medioEmail);
//                    nuevoDonante.mediosDeNotificaciones(medioTelefono);
//
//                    donantes.add(nuevoDonante);
//                }
//
//
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return donantes;
//    }
//
//


