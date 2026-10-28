package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.config.NotificacionesProperties;
import ar.edu.utn.frba.ddsi.donaciones.dto.*;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.Representante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.TipoDonante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Categoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV.ImportadorDeCSV;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.importadorDeCSV.ResultadoImportacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.EstadoDonacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.Segmentador;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonacionesRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonacionesSinSegmentarRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonantesHumanosRepository;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonantesJuridicosRepository;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;

@Service
public class DonacionesServiceImpl implements DonacionesService {
    private final DonantesHumanosRepository donantesHumanosRepository;
    private final DonacionesRepository donacionesRepository;
    private final DonantesJuridicosRepository donantesJuridicosRepository;
    private final DonacionesSinSegmentarRepository donacionesSinSegmentarRepository;

    // Cliente HTTP para llamar al servicio de notificaciones
    private final RestTemplate restTemplate;

    // API token con el que donaciones se identifica ante notificaciones
    @Value("${api.token}")
    private String apiToken;
    private final NotificacionesProperties notificacionesProperties;


    //segmentador
    private final Segmentador segmentador;
    private final CatalogoSubcategoriasService catalogoSubcategorias;
    private final ServiceWebHook webhookAIncentivos;

    public DonacionesServiceImpl(DonantesHumanosRepository donantesRepository,
                                 DonantesJuridicosRepository donantesJuridicosRepository,
                                 DonacionesSinSegmentarRepository donacionesSinSegmentarRepository,
                                 RestTemplate restTemplate,
                                 NotificacionesProperties notificacionesProperties,
                                 DonacionesRepository donacionesRepository,
                                 Segmentador segmentador,
                                 CatalogoSubcategoriasService catalogoSubcategorias,
                                 ServiceWebHook webhookAIncentivos) {
        this.segmentador = segmentador;
        this.donantesHumanosRepository = donantesRepository;
        this.restTemplate = restTemplate;
        this.notificacionesProperties = notificacionesProperties;
        this.donacionesRepository = donacionesRepository;
        this.donantesJuridicosRepository = donantesJuridicosRepository;
        this.catalogoSubcategorias = catalogoSubcategorias;
        this.webhookAIncentivos = webhookAIncentivos;
        this.donacionesSinSegmentarRepository=donacionesSinSegmentarRepository;
    }



    @Override
    public void arrancarImportacionDeCSV(String file){
        ImportadorDeCSV importador = new ImportadorDeCSV();
        String home = System.getProperty("user.home"); /* user.home te da automáticamente C:\\Users\\<usuario-actual>*/
        String ruta = home + file; /*file sería la ubicación exacta, ej: "\\Desktop\\donantes_import_20000_UTF8_BOM.csv"*/
        try {
            ResultadoImportacion resultado = importador.importarCsv(ruta); /*ruta: "C:\\Users\\Analía\\Desktop\\donantes_import_20000_UTF8_BOM.csv"*/
            this.donantesHumanosRepository.saveAll(resultado.humanos);
            webhookAIncentivos.notificarNuevosDonantes(
                    resultado.humanos.stream().map(h -> new PersonaDonanteDTO(
                            h.getId(),
                            h.getNombre(),
                            h.getApellido(),
                            null,
                            null,
                            h.getFechaDeRegistro(),
                            this.obtenerDonacionesIncentivosDTO( h.getId(), h.getDonaciones())
                    )).collect(Collectors.toList())
            );
            this.donantesJuridicosRepository.saveAll(resultado.juridicos);
            webhookAIncentivos.notificarNuevosDonantes(
                    resultado.juridicos.stream().map(j -> new PersonaDonanteDTO(
                            null,
                           null,
                          null,
                            j.getId(),
                            j.getRazonSocial(),
                            j.getFechaDeRegistro(),
                            this.obtenerDonacionesIncentivosDTO(j.getId(),j.getDonaciones())
                    )).collect(Collectors.toList())
            );
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<PersonaDonanteDTO> obtenerTodosLosDonantesUnificados() {
        List<PersonaHumana> humanas = donantesHumanosRepository.findAll();
        List<PersonaJuridica> juridicas = donantesJuridicosRepository.findAll();

//        System.out.println("Cantidad humanos aca: " + humanas.size());
//        System.out.println("Cantidad juridicas aca: " + juridicas.size());

        List<PersonaDonanteDTO> donantes= new ArrayList<>();

        humanas.forEach(h -> donantes.add(
                        new PersonaDonanteDTO(
                                h.getId(),
                                h.getNombre(),
                                h.getApellido(),
                                null,
                                null,
                                h.getFechaDeRegistro(),
                                this.obtenerDonacionesIncentivosDTO(h.getId(),h.getDonaciones())
                                )
                        )
        );




        juridicas.forEach(j -> donantes.add(
                        new PersonaDonanteDTO(
                                null,
                                null,
                                null,
                                j.getId(),
                                j.getRazonSocial(),
                                j.getFechaDeRegistro(),
                                this.obtenerDonacionesIncentivosDTO(j.getId(), j.getDonaciones())
                                )
                        )
        );


        return donantes;
    }


    public List<DonacionIncentivosDTO> obtenerDonacionesIncentivosDTO(Long donanteId, List<DonacionSinSegmentar> donaciones){
        return donaciones.stream()
                .flatMap(donacionSinSegmentar -> donacionSinSegmentar.getDonacionesSegmentadas().stream())
                .map(donacionSegmentada ->
                        new DonacionIncentivosDTO(
                                donacionSegmentada.getId(),
                                donacionSegmentada.getDonacionEntregada(),
                                // Una donación recién cargada todavía no tiene entidad (la asigna el matchmaking)
                                donacionSegmentada.getEntidadBeneficiaria() != null
                                        ? donacionSegmentada.getEntidadBeneficiaria().getId() : null,
                                donacionSegmentada.getFechaDeIngreso(),
                                donacionSegmentada.getBienesDelMismoTipo().size(),
                                this.categoriaDe(donacionSegmentada)
                        ))
                .collect(Collectors.toList());
    }

    // Categoría de la donación, null si no se puede saber
    private String categoriaDe(DonacionSegmentada donacion) {
        if (donacion.getBienesDelMismoTipo() == null || donacion.getBienesDelMismoTipo().isEmpty()) {
            return null;
        }
        var subcategoria = donacion.getBienesDelMismoTipo().get(0).getSubcategoria();
        if (subcategoria == null || subcategoria.getCategoria() == null) {
            return null;
        }
        return subcategoria.getCategoria().getNombre();
    }

    @Override
   public List<PersonaHumanaDTO> obtenerTodosHumanos(){
        List<PersonaHumana> humanos = this.donantesHumanosRepository.findAll();

        return humanos.stream().map(persona -> {

            List<DonacionSinSegmentarDTO> donacionesDTO = this.obtenerDonacionesSinSegmentarDTO(persona.getDonaciones());
            List<MedioDeNotificacionDTO> mediosDTO = this.obtenerMediosDTO(persona.getMediosDeNotificacion());
            MedioDeNotificacionDTO medioDTO = null;
            if(persona.getMedioDeNotificacionPredeterminado()!=null){
                medioDTO = this.obtenerAMedioDTO(persona.getMedioDeNotificacionPredeterminado());
            }

            return new PersonaHumanaDTO(
                    persona.getId(),
                    persona.getNombre(),
                    persona.getApellido(),
                    persona.getEdad(),
                    persona.getDNI(),
                    persona.getGenero(),
                    persona.getFechaDeNaciemiento(),
                    donacionesDTO,
                    mediosDTO,
                    medioDTO

            );

        }).toList();
    }

    @Override
    public List<PersonaJuridicaDTO> obtenerTodosJuridicos(){

        List<PersonaJuridica> juridicos = this.donantesJuridicosRepository.findAll();
        //transformar a dto
        return juridicos.stream().map(persona -> {

            List<DonacionSinSegmentarDTO> donacionesDTO = this.obtenerDonacionesSinSegmentarDTO(persona.getDonaciones());
            List<MedioDeNotificacionDTO> mediosDTO = this.obtenerMediosDTO(persona.getMediosDeNotificacion());
            List<RepresentanteDTO> representantesDTO = this.obtenerRepresentantesDTO(persona.getPersonasRepresentantes());

            return new PersonaJuridicaDTO(
                    persona.getId(),
                    persona.getCuit(),
                    persona.getRazonSocial(),
                    persona.getRubro(),
                    persona.getTipo(),
                    representantesDTO,
                    mediosDTO,
                    donacionesDTO
            );

        }).toList();

    }


    @Override
    public  List<DonacionSegmentadaDTO> obtenerDonacionesDeHumano(Long id) {
        PersonaHumana personaHumana = this.donantesHumanosRepository.findById(id).orElseThrow();
        if (personaHumana != null) {
            List<DonacionSegmentadaDTO> donacionesSegmentadasDTOS = this.obtenerDonacionesSegmentadasDTO(personaHumana.getId(),TipoDonante.HUMANO);
            return donacionesSegmentadasDTOS;

        }
        throw new RuntimeException("Persona humana no encontrada");
    }

    @Override
    public List<DonacionSegmentadaDTO> obtenerDonacionesDeJuridico(Long id) {
        PersonaJuridica personaJuridica = this.donantesJuridicosRepository.findById(id).orElseThrow();
        if (personaJuridica != null) {
            List<DonacionSegmentadaDTO> donacionesSegmentadasDTOS = this.obtenerDonacionesSegmentadasDTO(personaJuridica.getId(),TipoDonante.JURIDICO);

            return donacionesSegmentadasDTOS;
        }
        throw new RuntimeException("Persona juridica no encontrada");
    }

    public  List<DonacionSegmentadaDTO> obtenerDonacionesSegmentadasDTO(Long idDeDonante, TipoDonante tipoDonante){
        List<DonacionSegmentada> donacionesSegmentadas = null;
            if(tipoDonante.equals(TipoDonante.HUMANO)){
                donacionesSegmentadas = this.donacionesRepository.findDonacionesPorIdHumano(idDeDonante);
            }else{
                donacionesSegmentadas = this.donacionesRepository.findDonacionesPorIdJuridico(idDeDonante);
            }


            return this.convertirDonacionesSegADTO(donacionesSegmentadas);
    }

    @Override
    public List<DonacionSegmentadaDTO> obtenerDonacionesEntregadas() {
        return this.convertirDonacionesSegADTO(
                this.donacionesRepository.findByEstado(EstadoDonacion.ENTREGADA));
    }


    @Override
    public PersonaHumanaDTO obtenerHumanoPorId(Long id) {
        PersonaHumana personaHumana = donantesHumanosRepository.findById(id).orElseThrow();
        if (personaHumana == null) {
            throw new RuntimeException("Persona humana no encontrada");
        }
        List<DonacionSinSegmentarDTO> donacionesDTO = obtenerDonacionesSinSegmentarDTO(personaHumana.getDonaciones());
        List<MedioDeNotificacionDTO> mediosDTO = this.obtenerMediosDTO(personaHumana.getMediosDeNotificacion());
        MedioDeNotificacionDTO medioDTO = null;
        if(personaHumana.getMedioDeNotificacionPredeterminado()!=null){
            medioDTO = this.obtenerAMedioDTO(personaHumana.getMedioDeNotificacionPredeterminado());
        }


        return new PersonaHumanaDTO(
                personaHumana.getId(),
                personaHumana.getNombre(),
                personaHumana.getApellido(),
                personaHumana.getEdad(),
                personaHumana.getDNI(),
                personaHumana.getGenero(),
                null,
                donacionesDTO,
                mediosDTO,
                medioDTO
        );
    }

    @Override
    public PersonaJuridicaDTO obtenerJuridicoPorId(Long id) {
        PersonaJuridica personaJuridica = donantesJuridicosRepository.findById(id).orElse(null);
        if (personaJuridica == null) {
            throw new RuntimeException("Persona juridica no encontrada");
        }
        List<DonacionSinSegmentarDTO> donacionesDTO = obtenerDonacionesSinSegmentarDTO(personaJuridica.getDonaciones());
        List<MedioDeNotificacionDTO> mediosDTO = this.obtenerMediosDTO(personaJuridica.getMediosDeNotificacion());
        List<RepresentanteDTO> representantesDTO = this.obtenerRepresentantesDTO(personaJuridica.getPersonasRepresentantes());

        return new PersonaJuridicaDTO(
                personaJuridica.getId(),
                personaJuridica.getCuit(),
                personaJuridica.getRazonSocial(),
                personaJuridica.getRubro(),
                personaJuridica.getTipo(),
                representantesDTO,
                mediosDTO,
                donacionesDTO
        );
    }



    @Override
    public PersonaHumanaDTO crearDonanteHumano(PersonaHumanaDTO body){

        PersonaHumana nuevaPersona = new PersonaHumana(
                body.nombre(),
                body.apellido(),
                body.DNI(),
                body.genero(),
                body.fechaDeNacimiento()
                );

            //se encraga de setear la edad de la persona humana a partir de su fecha de nacimiento
            nuevaPersona.calcularEdad();

        // Mapper de DTO a entidad para el medio de notificación predeterminado y para la lista de medios
        this.setearMediosDeNotificacion(body.mediosDeNotificacion(),nuevaPersona.getMediosDeNotificacion());

        if (body.medioDeNotificacionPredeterminado() != null) {
            MedioDeNotificacion medioP = nuevaPersona.getMediosDeNotificacion().stream().filter( m -> m.getTipoDeNotificacion().equals(body.medioDeNotificacionPredeterminado().tipoDeNotificacion())).findFirst().get();
            nuevaPersona.setMedioDeNotificacionPredeterminado(medioP);
        }
        System.out.println(nuevaPersona);

        PersonaHumana humano= this.donantesHumanosRepository.save(nuevaPersona);

        webhookAIncentivos.notificarNuevoDonante(
                new PersonaDonanteDTO(
                        humano.getId(),
                        humano.getNombre(),
                        humano.getApellido(),
                        null,
                        null,
                        humano.getFechaDeRegistro(),
                        this.obtenerDonacionesIncentivosDTO(humano.getId(),humano.getDonaciones()
                ))
        );

        // Mapper de entidad a DTO para el medio de notificación predeterminado y los medios

        MedioDeNotificacionDTO medioDTO = null;

        if (humano.getMedioDeNotificacionPredeterminado() != null) {
            medioDTO = new MedioDeNotificacionDTO(
                    humano.getMedioDeNotificacionPredeterminado().getTipoDeNotificacion(),
                    humano.getMedioDeNotificacionPredeterminado().getContacto()
            );
        }

        List<MedioDeNotificacionDTO> mediosDTO = this.obtenerMediosDTO(humano.getMediosDeNotificacion());



        List<DonacionSinSegmentarDTO> donacionesDTO = obtenerDonacionesSinSegmentarDTO(humano.getDonaciones());

        PersonaHumanaDTO nuevaPersonaDTO = new PersonaHumanaDTO(
                humano.getId(),
                humano.getNombre(),
                humano.getApellido(),
                humano.getEdad(),
                humano.getDNI(),
                humano.getGenero(),
                humano.getFechaDeNaciemiento(),
                donacionesDTO,
                mediosDTO,
                medioDTO
                );

        return nuevaPersonaDTO;
    }

    @Override
    public PersonaJuridicaDTO crearDonanteJuridico(PersonaJuridicaDTO request) {

        PersonaJuridica nuevaPersona = new PersonaJuridica(
                request.cuit(),
                request.razonSocial(),
                request.tipo(),
                request.rubro()
        );
        this.setearMediosDeNotificacion(request.mediosDeNotificacion(), nuevaPersona.getMediosDeNotificacion());
        this.setearRepresentantes(request.representantes(), nuevaPersona.getPersonasRepresentantes());

        PersonaJuridica juridica = this.donantesJuridicosRepository.save(nuevaPersona);

        webhookAIncentivos.notificarNuevoDonante(
                new PersonaDonanteDTO(
                        null,
                        null,
                       null,
                        juridica.getId(),
                        juridica.getRazonSocial(),
                        juridica.getFechaDeRegistro(),
                        this.obtenerDonacionesIncentivosDTO(juridica.getId(),juridica.getDonaciones()
                        ))
        );

        List<DonacionSinSegmentarDTO> donacionesDTO = obtenerDonacionesSinSegmentarDTO(
                juridica.getDonaciones());
        List<MedioDeNotificacionDTO> mediosDTO = obtenerMediosDTO(juridica.getMediosDeNotificacion());
        List<RepresentanteDTO> representantesDTO = obtenerRepresentantesDTO(juridica.getPersonasRepresentantes());

        return new PersonaJuridicaDTO(
                juridica.getId(),
                juridica.getCuit(),
                juridica.getRazonSocial(),
                juridica.getRubro(),
                juridica.getTipo(),
                representantesDTO,
                mediosDTO,
                donacionesDTO
        );
    }


    @Override
    @Transactional
    public List<DonacionSegmentadaDTO> crearDonacionDeHumano(DonacionSinSegmentarDTO body, Long id) {

        PersonaHumana personaHumana = this.donantesHumanosRepository.findById(id).orElse(null);

        if (personaHumana == null) {
            throw new RuntimeException("Persona humana no encontrada");
        }

        List<Bien> bienes = this.convertirBienesDTO(body.bienes());
        DonacionSinSegmentar nuevaDonacionSinSegmentar = new DonacionSinSegmentar(body.descripcionGeneral(), LocalDateTime.now());
        bienes.forEach(nuevaDonacionSinSegmentar::agregarBien);
        nuevaDonacionSinSegmentar.setDonanteHumano(personaHumana);
        personaHumana.agregarDonacion(nuevaDonacionSinSegmentar);


        //segmentamos
        List<DonacionSegmentada> donacionesSegmentadas = this.segmentador.segmentar(nuevaDonacionSinSegmentar);

        donacionesSegmentadas.forEach(d -> d.setDonanteHumano(personaHumana));



        this.donacionesRepository.saveAll(donacionesSegmentadas);

        this.donacionesSinSegmentarRepository.save(nuevaDonacionSinSegmentar);

        webhookAIncentivos.notificarNuevasDonaciones(
            donacionesSegmentadas.stream().map( d ->
                 new DonacionIncentivosDTO(
                      d.getId(),
                      d.getDonacionEntregada(),
                      d.getEntidadBeneficiaria() != null ? d.getEntidadBeneficiaria().getId() : null,
                      d.getFechaDeIngreso(),
                      d.calcularCantidadDeBienes(),
                      d.getBienesDelMismoTipo().getFirst().getSubcategoria().getCategoria().getNombre()
                 )).collect(Collectors.toList()),TipoDonante.HUMANO, id
        );

        // Notificar al donante que su donación fue registrada
//        if (personaHumana.getMedioDeNotificacionPredeterminado() != null) {
//            this.notificar(
//                    personaHumana.getMedioDeNotificacionPredeterminado().getContacto(),
//                    "Tu+donacion+fue+registrada+exitosamente",
//                    personaHumana.getMedioDeNotificacionPredeterminado().getTipoDeNotificacion().name()
//            );
//        }

        return this.convertirADonacionesSegmentadasDTO(donacionesSegmentadas);
    }

    @Override
    @Transactional
    public List<DonacionSegmentadaDTO> crearDonacionDeJuridico(DonacionSinSegmentarDTO body, Long id) {

        PersonaJuridica personaJuridica = this.donantesJuridicosRepository.findById(id).orElse(null);

        if (personaJuridica == null) {
            throw new RuntimeException("Persona juridica no encontrada");
        }

        List<Bien> bienes = this.convertirBienesDTO(body.bienes());
        DonacionSinSegmentar nuevaDonacion = new DonacionSinSegmentar(body.descripcionGeneral(), LocalDateTime.now());
        bienes.forEach(nuevaDonacion::agregarBien);
        nuevaDonacion.setDonanteJuridico(personaJuridica);
        personaJuridica.agregarDonacion(nuevaDonacion);

        //segmentamos
        List<DonacionSegmentada> donacionesSegmentadas = this.segmentador.segmentar(nuevaDonacion);
        donacionesSegmentadas.forEach(d -> {d.setDonanteJuridico(personaJuridica);} );



        this.donacionesRepository.saveAll(donacionesSegmentadas);
        this.donacionesSinSegmentarRepository.save(nuevaDonacion);


        webhookAIncentivos.notificarNuevasDonaciones(
                donacionesSegmentadas.stream().map( d ->
                        new DonacionIncentivosDTO(
                                d.getId(),
                                d.getDonacionEntregada(),
                                d.getEntidadBeneficiaria() != null ? d.getEntidadBeneficiaria().getId(): null,
                                d.getFechaDeIngreso(),
                                d.calcularCantidadDeBienes(),
                                d.getBienesDelMismoTipo().getFirst().getSubcategoria().getCategoria().getNombre()
                        )).collect(Collectors.toList()),TipoDonante.JURIDICO, id
        );
        // Notificar al representante de la persona jurídica usando el primer medio disponible
        if (!personaJuridica.getMediosDeNotificacion().isEmpty()) {
            notificar(
                    personaJuridica.getMediosDeNotificacion().get(0).getContacto(),
                    "Tu+donacion+fue+registrada+exitosamente",
                    personaJuridica.getMediosDeNotificacion().get(0).getTipoDeNotificacion().name()
            );
        }

        return this.convertirADonacionesSegmentadasDTO(donacionesSegmentadas);

    }


    @Override
    public List<DonacionSinSegmentarDTO> modificarDonacionDeHumano(DonacionSinSegmentarDTO body, Long idHumano, Long idDonacion, Long idBien){
        PersonaHumana personaHumana = this.donantesHumanosRepository.findById(idHumano).orElse(null);
        if(personaHumana == null) {
            throw new RuntimeException("Persona humano no encontrada");
        }
        try{
            DonacionSinSegmentar donacionAModificada = personaHumana.getDonaciones().get(idDonacion.intValue());
            this.modificarDonacion(donacionAModificada,body,idBien);
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTO = this.obtenerDonacionesSinSegmentarDTO(personaHumana.getDonaciones()); //voy a ver todas las donaciones del humano para reutilizar la funcion

            return donacionSinSegmentarDTO;
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Donacion no encontrada");

        }
    }

    @Override
    public List<DonacionSinSegmentarDTO> modificarDonacionDeJuridica(DonacionSinSegmentarDTO body, Long idJuridica, Long idDonacion, Long idBien){

        PersonaJuridica personaJuridica = this.donantesJuridicosRepository.findById(idJuridica).orElseThrow();
        try{
            DonacionSinSegmentar donacionAModificada = personaJuridica.getDonaciones().get(idDonacion.intValue());
            this.modificarDonacion(donacionAModificada,body,idBien);
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTO = this.obtenerDonacionesSinSegmentarDTO(personaJuridica.getDonaciones()); //voy a ver todas las donaciones del humano para reutilizar la funcion

            return donacionSinSegmentarDTO;
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Donacion no encontrada");

        }
    }


    @Override
    public List<DonacionSinSegmentarDTO> eliminarDonacionDeHumano(Long idHumano, Long idDonacion){
        PersonaHumana personaHumana = this.donantesHumanosRepository.findById(idHumano).orElseThrow();
        try{
            if(!personaHumana.getDonaciones().isEmpty() && idDonacion < personaHumana.getDonaciones().size()){
                personaHumana.getDonaciones().remove(idDonacion.intValue());
                List<DonacionSinSegmentarDTO> donacionesDTO = this.obtenerDonacionesSinSegmentarDTO(personaHumana.getDonaciones());
                return donacionesDTO;
            }
            return null;
        }
        catch (RuntimeException e){
            throw new RuntimeException("Donacion no encontrada para eliminar");
        }
    }
    @Override
    public List<DonacionSinSegmentarDTO> eliminarDonacionDeJuridico(Long idJuridico, Long idDonacion){
        PersonaJuridica personaJuridica = this.donantesJuridicosRepository.findById(idJuridico).orElseThrow();
        try{
            if(!personaJuridica.getDonaciones().isEmpty() && idDonacion < personaJuridica.getDonaciones().size()){
                personaJuridica.getDonaciones().remove(idDonacion.intValue());
                List<DonacionSinSegmentarDTO> donacionesDTO = this.obtenerDonacionesSinSegmentarDTO(personaJuridica.getDonaciones());
                return donacionesDTO;
            }
            return null;
        }
        catch (RuntimeException e){
            throw new RuntimeException("Donacion no encontrada para eliminar");
        }
    }



    @Override
    public void eliminarDonanteHumano(Long id) {
        PersonaHumana personaHumana = donantesHumanosRepository.findById(id).orElseThrow();
        if (personaHumana == null) {
            throw new RuntimeException("Persona humana no encontrada con id: " + id);
        }
        donantesHumanosRepository.delete(personaHumana);
    }

    @Override
    public void eliminarDonanteJuridico(Long id) {
        PersonaJuridica personaJuridica = donantesJuridicosRepository.findById(id).orElseThrow(null);
        if (personaJuridica == null) {
            throw new RuntimeException("Persona juridica no encontrada con id: " + id);
        }
        donantesJuridicosRepository.delete(personaJuridica);
    }



    @Override
    public PersonaHumanaDTO modificarDonanteHumano(Long id, PersonaHumanaDTO request) {
        PersonaHumana personaHumana = donantesHumanosRepository.findById(id).orElse(null);
        if (personaHumana == null) {
            throw new RuntimeException("Persona humana no encontrada con id: " + id);
        }

        // Solo actualizamos los campos que vienen con valor
        if (request.nombre() != null) personaHumana.setNombre(request.nombre());
        if (request.apellido() != null) personaHumana.setApellido(request.apellido());
        if (request.edad() != null) personaHumana.setEdad(request.edad());
        if (request.DNI() != null) personaHumana.setDNI(request.DNI());
        if (request.genero() != null) personaHumana.setGenero(request.genero());
        if(request.mediosDeNotificacion() != null && !request.mediosDeNotificacion().isEmpty()){
            this.cambiarMediosDeNotificacion(request.mediosDeNotificacion(),personaHumana.getMediosDeNotificacion());
        }
        List<DonacionSinSegmentarDTO> donacionesDTO = obtenerDonacionesSinSegmentarDTO(
                personaHumana.getDonaciones());

        List<MedioDeNotificacionDTO> mediosDTO = this.obtenerMediosDTO(personaHumana.getMediosDeNotificacion());

        MedioDeNotificacionDTO medioDTO = null;
        if (personaHumana.getMedioDeNotificacionPredeterminado() != null) {
            medioDTO = new MedioDeNotificacionDTO(
                    personaHumana.getMedioDeNotificacionPredeterminado().getTipoDeNotificacion(),
                    personaHumana.getMedioDeNotificacionPredeterminado().getContacto()
            );
        }

        return new PersonaHumanaDTO(
                personaHumana.getId(),
                personaHumana.getNombre(),
                personaHumana.getApellido(),
                personaHumana.getEdad(),
                personaHumana.getDNI(),
                personaHumana.getGenero(),
                personaHumana.getFechaDeNaciemiento(),
                donacionesDTO,
                mediosDTO,
                medioDTO
        );
    }

    @Override
    public PersonaJuridicaDTO modificarDonanteJuridico(Long id, PersonaJuridicaDTO request) {
        PersonaJuridica personaJuridica = donantesJuridicosRepository.findById(id).orElse(null);
        if (personaJuridica == null) {
            throw new RuntimeException("Persona juridica no encontrada con id: " + id);
        }

        if (request.cuit() != null) personaJuridica.setCuit(request.cuit());
        if (request.razonSocial() != null) personaJuridica.setRazonSocial(request.razonSocial());
        if (request.rubro() != null) personaJuridica.setRubro(request.rubro());
        if (request.tipo() != null) personaJuridica.setTipo(request.tipo());

        List<DonacionSinSegmentarDTO> donacionesDTO = obtenerDonacionesSinSegmentarDTO(
                personaJuridica.getDonaciones());
        List<MedioDeNotificacionDTO> mediosDTO = this.obtenerMediosDTO(personaJuridica.getMediosDeNotificacion());
        List<RepresentanteDTO> representantesDTO = this.obtenerRepresentantesDTO(personaJuridica.getPersonasRepresentantes());


        return new PersonaJuridicaDTO(
                personaJuridica.getId(),
                personaJuridica.getCuit(),
                personaJuridica.getRazonSocial(),
                personaJuridica.getRubro(),
                personaJuridica.getTipo(),
                representantesDTO,
                mediosDTO,
                donacionesDTO
        );
    }



    public void modificarDonacion(DonacionSinSegmentar donacionAModificada,DonacionSinSegmentarDTO body, Long idBien){

        if(body.descripcionGeneral() != null){
            donacionAModificada.setDescripcionGeneral(body.descripcionGeneral());
        }

        if(body.fechaDeIngreso() != null){
            donacionAModificada.setFechaDeIngreso(body.fechaDeIngreso());
        }else {
            donacionAModificada.setFechaDeIngreso(LocalDateTime.now());
        }

        if(body.bienes() != null){
            Bien bienAModificar = donacionAModificada.getBienes().get(idBien.intValue());
            if(bienAModificar != null){
                if (body.bienes().get(idBien.intValue()) != null) {
                    BienDTO bienDTO = body.bienes().get(idBien.intValue());
                    if(bienDTO.nombre() != null){
                        bienAModificar.setNombre(bienDTO.nombre());
                    }
                    if(bienDTO.descripcion() != null){
                        bienAModificar.setDescripcion(bienDTO.descripcion());
                    }
                    if(bienDTO.subcategoria() != null){
                        SubcategoriaDTO subcategoriaDTO = bienDTO.subcategoria();
                        if(subcategoriaDTO.nombre() != null){
                            bienAModificar.getSubcategoria().setNombre(subcategoriaDTO.nombre());
                        }
                        if(subcategoriaDTO.esPerecedero() != null){
                            bienAModificar.getSubcategoria().setEsPerecedero(subcategoriaDTO.esPerecedero());
                        }
                        if(subcategoriaDTO.categoria() != null){
                            CategoriaDTO categoriaDTO = subcategoriaDTO.categoria();
                            if(categoriaDTO.nombre() != null){
                                bienAModificar.getSubcategoria().getCategoria().setNombre(categoriaDTO.nombre());
                            }
                        }
                        if(bienDTO.fechaDeVencimiento() != null){
                            bienAModificar.setFechaDeVencimiento(bienDTO.fechaDeVencimiento());
                        }
                        if (bienDTO.esUsado() != null) {
                            bienAModificar.setEsUsado(bienDTO.esUsado());
                        }
                        if (bienDTO.tipoUnidad() != null) {
                            bienAModificar.setTipoUnidad(bienDTO.tipoUnidad());
                        }
                        if (bienDTO.cantidad() != null) {
                            bienAModificar.setCantidad(bienDTO.cantidad());
                        }
                    }
                }
            }else {
                throw new RuntimeException("No existe ese bien en la lista de bienes");
            }
        }
    }

    public void setearMediosDeNotificacion(List<MedioDeNotificacionDTO> mediosDTO, List<MedioDeNotificacion> medios){
        if (mediosDTO == null) {
            return;
        }

        mediosDTO.forEach(dto ->
                medios.add(new MedioDeNotificacion(
                        dto.tipoDeNotificacion(),
                        dto.datoDeContacto()
                ))
        );
    }

    public List<DonacionSinSegmentarDTO> obtenerDonacionesSinSegmentarDTO(List<DonacionSinSegmentar> donacionesSinSegmentar){
        List<DonacionSinSegmentarDTO> donaciones = donacionesSinSegmentar.stream().map(
                donacion -> {

                    List<BienDTO> bienesDTO = this.obtenerBienesDTO(donacion.getBienes());


                    return new DonacionSinSegmentarDTO(
                            donacion.getDescripcionGeneral(),
                            bienesDTO,
                            donacion.getFechaDeIngreso()
                    );
                }).toList();

        return donaciones;
    }
    public List<DonacionSinSegmentar> convertirDonacionesDTO(List<DonacionSinSegmentarDTO> donacionesDTO){

        List<DonacionSinSegmentar> donaciones = donacionesDTO.stream().map(
                donacion -> {
                    List<Bien> bienes = this.convertirBienesDTO(donacion.bienes());

                    DonacionSinSegmentar nuevaDonacion = new DonacionSinSegmentar(
                            donacion.descripcionGeneral(),
                            donacion.fechaDeIngreso()
                    );
                    bienes.forEach(nuevaDonacion::agregarBien);

                    return nuevaDonacion;
                }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        return donaciones;
    }

    public List<BienDTO> obtenerBienesDTO(List<Bien> bienes){
        List<BienDTO> bienesDTO = bienes.stream()
                .map(b -> new BienDTO(
                        b.getNombre(),
                        b.getDescripcion(),
                        new SubcategoriaDTO(
                                b.getSubcategoria().getNombre(),
                                b.getSubcategoria().getEsPerecedero(),
                                new CategoriaDTO(
                                        b.getSubcategoria()
                                                .getCategoria()
                                                .getNombre()
                                )
                        ),
                        b.getFechaDeVencimiento(),
                        b.getEsUsado(),
                        b.getTipoUnidad(),
                        b.getCantidad()
                ))
                .toList();

        return bienesDTO;
    }
    public List<Bien> convertirBienesDTO(List<BienDTO> bienesDTO) {

        Map<String, Subcategoria> resueltas = new HashMap<>();

        return bienesDTO.stream().map(b -> new Bien(
                b.nombre(),
                b.descripcion(),
                resueltas.computeIfAbsent(
                        b.subcategoria().nombre().trim().toLowerCase(),
                        k -> catalogoSubcategorias.obtenerOCrear(
                                b.subcategoria().nombre(),
                                b.subcategoria().esPerecedero(),
                                b.subcategoria().categoria() != null
                                        ? b.subcategoria().categoria().nombre() : null)),
                b.fechaDeVencimiento(),
                b.esUsado(),
                b.tipoUnidad(),
                b.cantidad(),
                null
        )).collect(Collectors.toCollection(ArrayList::new));
    }


    public List<MedioDeNotificacionDTO> obtenerMediosDTO(List<MedioDeNotificacion> mediosDeNotificacion){
        List<MedioDeNotificacionDTO> mediosDTO = new ArrayList<>();
        mediosDeNotificacion.forEach(m -> mediosDTO.add(this.obtenerAMedioDTO(m)));
        return mediosDTO;
    }
    public MedioDeNotificacionDTO obtenerAMedioDTO(MedioDeNotificacion medio){
        if(medio == null) return null;
        return new MedioDeNotificacionDTO(medio.getTipoDeNotificacion(), medio.getContacto());
    }

    public List<RepresentanteDTO> obtenerRepresentantesDTO(List<Representante> representantes){
        return representantes.stream()
                .map(r -> new RepresentanteDTO(
                        r.getNombre(),
                        r.getApellido(),
                        r.getDNI(),
                        this.obtenerMediosDTO(r.getMediosDeNotificacion())
                ))
                .toList();
    }

    public void setearRepresentantes(List<RepresentanteDTO> representantesDTO, List<Representante> representantes){
        if (representantesDTO == null) return;
        representantesDTO.forEach(dto -> {
            Representante nuevoRepresentante = new Representante(
                    dto.nombre(),
                    dto.apellido(),
                    dto.numeroDeDocumento()
            );
            this.setearMediosDeNotificacion(dto.mediosDeNotificacion(), nuevoRepresentante.getMediosDeNotificacion());
            representantes.add(nuevoRepresentante);
        });
    }

    public void cambiarMediosDeNotificacion(List<MedioDeNotificacionDTO> nuevos, List<MedioDeNotificacion> viejos){
        nuevos.forEach(dto -> {
            viejos.stream()
                    .filter(viejo -> viejo.getTipoDeNotificacion().equals(dto.tipoDeNotificacion()))
                    .findFirst()
                    .ifPresent(medioViejo -> medioViejo.setContacto(dto.datoDeContacto()));
        });
    }

    public List<DonacionSegmentadaDTO> convertirADonacionesSegmentadasDTO(List<DonacionSegmentada> donacionesSegmentadas){
        List<DonacionSegmentadaDTO> dtos = new ArrayList<>();

        donacionesSegmentadas.forEach(d -> {dtos.add(
                new DonacionSegmentadaDTO(
                        d.getId(),
                        d.getEstadoActual().toString(),
                        null,
                        this.obtenerBienesDTO(d.getBienesDelMismoTipo()),
                        d.getFechaDeIngreso(),
                        d.getFechaDeAsignacion(),
                        d.getDonacionEntregada(),
                        d.getEntidadBeneficiaria() != null ? d.getEntidadBeneficiaria().getId() : null,
                        d.getFotoPath()
                        ));});
        return dtos;
    }

    // Método para llamar al servicio de notificaciones
    // URL resultante: http://localhost:8081/servicioDeNotificaciones/notificar?destinatario=X&mensaje=Y&medio=Z

    private void notificar(String destinatario, String mensaje, String medio) {
        try {

            System.out.println("Destinatario original: [" + destinatario + "]");
            System.out.println("Largo original: " + destinatario.length());

            String url = notificacionesProperties.getUrl() +
                    "/servicioDeNotificaciones/notificar" +
                    "?destinatario=" + URLEncoder.encode(destinatario, StandardCharsets.UTF_8) +
                    "&mensaje=" + mensaje +
                    "&medio=" + medio;

            System.out.println("URL enviada: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Token", apiToken);
            String respuesta = restTemplate.postForObject(url, new HttpEntity<>(headers), String.class);

            System.out.println("Respuesta: " + respuesta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DonacionAsignadaDTO> obtenerDonacionesAsiganadasAUnaEntidad(Long idEntidad){
        List<DonacionSegmentada> donacionesASignadas = this.donacionesRepository.findByEntidadBeneficiariaId(idEntidad);

        return  this.convertirADonacionesAsignadasDTO(donacionesASignadas);
    }

    public List<DonacionAsignadaDTO> convertirADonacionesAsignadasDTO(List<DonacionSegmentada> donacionesASignadas){
        List<DonacionAsignadaDTO> dtos = new ArrayList<>();
        donacionesASignadas.forEach(d -> {dtos.add(
                new DonacionAsignadaDTO(
                  d.getId(),
                  d.getEstadoActual().toString(),
                  d.calcularCantidadDeBienes(),
                  d.getFechaDeAsignacion(),
                  d.getSubcategoria().getNombre(),
                  d.calcularDescripcion(),
                  d.getDonacionEntregada()
        ));});
        return dtos;
    }

    public List<DonacionSegmentadaDTO> convertirDonacionesSegADTO(List<DonacionSegmentada> donacionesSegmentadas){

        List<DonacionSegmentadaDTO> dtos = new ArrayList<>();

        donacionesSegmentadas.forEach(d -> {
                    dtos.add(
                            new DonacionSegmentadaDTO(
                                    d.getId(),
                                    d.getEstadoActual().toString(),
                                    null,
                                    this.obtenerBienesDTO(d.getBienesDelMismoTipo()),
                                    d.getFechaDeIngreso(),
                                    d.getFechaDeAsignacion(),
                                    d.getDonacionEntregada(),
                                    d.getEntidadBeneficiaria() != null ? d.getEntidadBeneficiaria().getId() : null,
                                    d.getFotoPath()
                            )
                    );
                }
        );
        return dtos;
    }
}

