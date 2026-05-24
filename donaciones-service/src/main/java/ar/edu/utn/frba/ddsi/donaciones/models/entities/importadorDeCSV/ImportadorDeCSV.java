package ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV;

import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Donante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Email;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.SMS;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Tipo;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;



public class ImportadorDeCSV {

   public ArrayList<Donante> importarCsv(String path) throws IOException, CsvValidationException {

       CSVReader reader = new CSVReaderBuilder(new FileReader(path)).build();
       String [] nextLine;
       while ((nextLine = reader.readNext()) != null) {
           // nextLine[] is an array of values from the line
           System.out.println(nextLine[0] + nextLine[1] + "etc...");
       }

       return null;
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
//    public Tipo identificarTipo(String nombreDeLaEmpresa){
//        // S.A. || S.A.S. || S.R.L -> Empresa
//        // Asociación -> Gubernamental
//        // cooperativa -> INSTITUCIÓN
//         // fundación  ->  ONG
//        if(nombreDeLaEmpresa.contains("S.A.") || nombreDeLaEmpresa.contains("S.A.S")|| nombreDeLaEmpresa.contains("S.R.L")){
//            return Tipo.EMPRESA;
//        }else if(nombreDeLaEmpresa.contains("Asociación")){
//            return Tipo.GUBERNAMENTAL;
//        }else if (nombreDeLaEmpresa.contains("cooperativa")){
//            return Tipo.INSTITUCION;
//        }else if(nombreDeLaEmpresa.contains("fundación")){
//            return Tipo.ONG;
//        }
//        return null;
//    }


