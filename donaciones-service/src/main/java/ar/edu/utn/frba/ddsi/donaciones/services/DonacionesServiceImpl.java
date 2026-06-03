package ar.edu.utn.frba.ddsi.donaciones.services;
import ar.edu.utn.frba.ddsi.donaciones.config.NotificacionesProperties;
import ar.edu.utn.frba.ddsi.donaciones.dto.BienDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.CategoriaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.DonacionSinSegmentarDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.MisionDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaDonanteDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.PersonaHumanaDTO;
import ar.edu.utn.frba.ddsi.donaciones.dto.SubcategoriaDTO;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.CategoriaDeDonante;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaHumana;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.Donante.PersonaJuridica;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Bien;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Categoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.bien.Subcategoria;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mediosDeNotificacion.MedioDeNotificacion;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.mision.Mision;
import ar.edu.utn.frba.ddsi.donaciones.models.entities.segmentador.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.donaciones.repositories.DonantesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ar.edu.utn.frba.ddsi.donaciones.dto.MedioDeNotificacionDTO;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonacionesServiceImpl implements DonacionesService {
    private final DonantesRepository donantesRepository;

    // Cliente HTTP para llamar al servicio de notificaciones
    private final RestTemplate restTemplate;
    private final NotificacionesProperties notificacionesProperties;

    public DonacionesServiceImpl(DonantesRepository donantesRepository,
                                 RestTemplate restTemplate,
                                 NotificacionesProperties notificacionesProperties) {
        this.donantesRepository = donantesRepository;
        this.restTemplate = restTemplate;
        this.notificacionesProperties = notificacionesProperties;
    }

    @Override
   public List<PersonaDonanteDTO> obtenerTodosHumanos(){
        List<PersonaHumana> humanos = this.donantesRepository.findAllHumanos();

        return humanos.stream().map(persona -> {

            List<DonacionSinSegmentarDTO> donacionesDTO = this.obtenerDonacionesSinSegmentarDTO(persona.getDonaciones());
            List<MisionDTO> misionesDTO = this.obtenerMisionesDTO(persona.getMisiones());
            return new PersonaDonanteDTO(
                    persona.getId(),
                    persona.getNombre(),
                    persona.getApellido(),
                    persona.getNumeroDeDocumento(),
                    persona.getGenero(),
                    persona.getEdad(),
                    persona.getDireccion(),
                    null,
                    donacionesDTO,
                    null,
                    null,
                    misionesDTO,
                    persona.getCategoria()
            );

        }).toList();
    }


    @Override
    public List<PersonaDonanteDTO> obtenerTodosJuridicos(){

        List<PersonaJuridica> juridicos = this.donantesRepository.findAllJuridicos();
        //transformar a dto
        return juridicos.stream().map(persona -> {

            List<DonacionSinSegmentarDTO> donacionesDTO = this.obtenerDonacionesSinSegmentarDTO(persona.getDonaciones());
            List<MisionDTO> misionesDTO = this.obtenerMisionesDTO(persona.getMisiones());

            return new PersonaDonanteDTO(
                    null,
                   null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    persona.getId(),
                    donacionesDTO,
                    persona.getCuit(),
                    persona.getRazonSocial(),
                    misionesDTO,
                    persona.getCategoria()
            );

        }).toList();

    }

    @Override
    public PersonaDonanteDTO obtenerDonacionesDeHumano(Long id) {
        PersonaHumana personaHumana = this.donantesRepository.humanoFindById(id);
        if (personaHumana != null) {
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTOS = this.obtenerDonacionesSinSegmentarDTO(personaHumana.getDonaciones());
            List<MisionDTO> misionesDTO = this.obtenerMisionesDTO(personaHumana.getMisiones());

        PersonaDonanteDTO personaDTO = new PersonaDonanteDTO(
                                        personaHumana.getId(),
                                        personaHumana.getNombre(),
                                        personaHumana.getApellido(),
                                        personaHumana.getNumeroDeDocumento(),
                                        personaHumana.getGenero(),
                                        personaHumana.getEdad(),
                                        personaHumana.getDireccion(),
                                        null,
                                        donacionSinSegmentarDTOS,
                                        null,
                                        null,
                                        misionesDTO,
                                        personaHumana.getCategoria()
                                        );

            System.out.println(personaDTO);
            return personaDTO;

        }
        throw new RuntimeException("Persona humana no encontrada");
    }

    @Override
    public PersonaDonanteDTO obtenerDonacionesDeJurico(Long id) {
        PersonaJuridica personaJuridica = this.donantesRepository.juridicaFindById(id);
        if (personaJuridica != null) {
            List<DonacionSinSegmentarDTO> donacionSinSegmentarDTOS = this.obtenerDonacionesSinSegmentarDTO(personaJuridica.getDonaciones());
            List<MisionDTO> misionesDTO = this.obtenerMisionesDTO(personaJuridica.getMisiones());
            PersonaDonanteDTO personaDTO = new PersonaDonanteDTO(
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            personaJuridica.getId(),
                                            donacionSinSegmentarDTOS,
                                            personaJuridica.getCuit(),
                                            personaJuridica.getRazonSocial(),
                                            misionesDTO,
                                            personaJuridica.getCategoria()
                                            );

            return personaDTO;
        }
        throw new RuntimeException("Persona juridica no encontrada");
    }

    @Override
    public PersonaHumanaDTO crearDonanteHumano(PersonaHumanaDTO body){

        PersonaHumana nuevaPersona = new PersonaHumana(
                body.nombre(),
                body.apellido(),
                body.edad(),
                body.DNI(),
                body.genero(),
                body.direccion()
                );

        List<DonacionSinSegmentar> donaciones = this.convertirDonacionesDTO(body.donaciones());
        nuevaPersona.setDonaciones(donaciones);

        CategoriaDeDonante nuevaCategoria = body.categoria();
        nuevaPersona.setCategoria(nuevaCategoria);

        List<Mision> misiones = this.convertirMisionesDTO(body.misiones());
        nuevaPersona.setMisiones(misiones);

        // Mapper de DTO a entidad para el medio de notificación predeterminado
        if (body.medioDeNotificacionPredeterminado() != null) {
            MedioDeNotificacion medio = new MedioDeNotificacion(
                    body.medioDeNotificacionPredeterminado().tipoDeNotificacion(),
                    body.medioDeNotificacionPredeterminado().datoDeContacto()
            );
            nuevaPersona.setMedioDeNotificacionPredeterminado(medio);
        }
        System.out.println(nuevaPersona);

        PersonaHumana humano= this.donantesRepository.saveHumana(nuevaPersona);

        // Mapper de entidad a DTO para el medio de notificación predeterminado
        MedioDeNotificacionDTO medioDTO = null;
        if (humano.getMedioDeNotificacionPredeterminado() != null) {
            medioDTO = new MedioDeNotificacionDTO(
                    humano.getMedioDeNotificacionPredeterminado().getTipoDeNotificacion(),
                    humano.getMedioDeNotificacionPredeterminado().getDatoDeContacto()
            );
        }

        List<DonacionSinSegmentarDTO> donacionesDTO = obtenerDonacionesSinSegmentarDTO(humano.getDonaciones());
        List<MisionDTO> misionesDTO = this.obtenerMisionesDTO(humano.getMisiones());
        PersonaHumanaDTO nuevaPersonaDTO = new PersonaHumanaDTO(
                humano.getId(),
                humano.getNombre(),
                humano.getApellido(),
                humano.getNumeroDeDocumento(),
                humano.getGenero(),
                humano.getEdad(),
                humano.getDireccion(),
                donacionesDTO,
                misionesDTO,
                body.categoria(),
                medioDTO
                );

        return nuevaPersonaDTO;
    }

    @Override
    public DonacionSinSegmentarDTO crearDonacionDeJuridico(DonacionSinSegmentarDTO body, Long id){

        PersonaJuridica personaJuridica = this.donantesRepository.juridicaFindById(id);
        if (personaJuridica == null) {
            throw new RuntimeException("Persona juridica no encontrada");
        }

        List<Bien> bienes = this.convertirBienesDTO(body.bienes());
        DonacionSinSegmentar nuevaDonacion = new DonacionSinSegmentar(bienes, LocalDateTime.now());

        personaJuridica.agregarDonacion(nuevaDonacion);

        // Notificar al representante de la persona jurídica usando el primer medio disponible
        if (!personaJuridica.getMediosDeNotificacion().isEmpty()) {
            notificar(
                    personaJuridica.getMediosDeNotificacion().get(0).getDatoDeContacto(),
                    "Tu+donacion+fue+registrada+exitosamente",
                    personaJuridica.getMediosDeNotificacion().get(0).getTipoDeNotificacion().name()
            );
        }

        List<BienDTO> bienesDTO = this.obtenerBienesDTO(nuevaDonacion.getBienes());
        return new DonacionSinSegmentarDTO(bienesDTO, nuevaDonacion.getFechaDeIngreso(), nuevaDonacion.getDonacionEntregada());

    }
    @Override
    public DonacionSinSegmentarDTO crearDonacionDeHumano(DonacionSinSegmentarDTO body, Long id){

        PersonaHumana personaHumana = this.donantesRepository.humanoFindById(id);

        if (personaHumana == null) {
            throw new RuntimeException("Persona humana no encontrada");
        }

        List<Bien> bienes = this.convertirBienesDTO(body.bienes());
        DonacionSinSegmentar nuevaDonacion = new DonacionSinSegmentar(bienes, LocalDateTime.now());

        personaHumana.agregarDonacion(nuevaDonacion);

        // Notificar al donante que su donación fue registrada
        if (personaHumana.getMedioDeNotificacionPredeterminado() != null) {
            notificar(
                    personaHumana.getMedioDeNotificacionPredeterminado().getDatoDeContacto(),
                    "Tu+donacion+fue+registrada+exitosamente",
                    personaHumana.getMedioDeNotificacionPredeterminado().getTipoDeNotificacion().name()
            );
        }

        List<BienDTO> bienesDTO = this.obtenerBienesDTO(nuevaDonacion.getBienes());
        return new DonacionSinSegmentarDTO(bienesDTO, nuevaDonacion.getFechaDeIngreso(), nuevaDonacion.getDonacionEntregada());
    }
    @Override
    public List<DonacionSinSegmentarDTO> modificarDonacionDeHumano(DonacionSinSegmentarDTO body, Long idHumano, Long idDonacion, Long idBien){
        PersonaHumana personaHumana = this.donantesRepository.humanoFindById(idHumano);
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

        PersonaJuridica personaJuridica = this.donantesRepository.juridicaFindById(idJuridica);
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
        PersonaHumana personaHumana = this.donantesRepository.humanoFindById(idHumano);
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
        PersonaJuridica personaJuridica = this.donantesRepository.juridicaFindById(idJuridico);
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
    public void modificarDonacion(DonacionSinSegmentar donacionAModificada,DonacionSinSegmentarDTO body, Long idBien){

        if(body.fechaDeIngreso() != null){
            donacionAModificada.setFechaDeIngreso(body.fechaDeIngreso());
        }
        if(body.donacionEntregada() != null){
            throw new RuntimeException("No se puede modificar el estado de entrega de una donacion");
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


    public List<DonacionSinSegmentarDTO> obtenerDonacionesSinSegmentarDTO(List<DonacionSinSegmentar> donacionesSinSegmentar){

        List<DonacionSinSegmentarDTO> donaciones = donacionesSinSegmentar.stream().map(
                donacion -> {

                    List<BienDTO> bienesDTO = this.obtenerBienesDTO(donacion.getBienes());


                    return new DonacionSinSegmentarDTO(
                            bienesDTO,
                            donacion.getFechaDeIngreso(),
                            donacion.getDonacionEntregada()
                    );
                }).toList();

        return donaciones;
    }
    public List<DonacionSinSegmentar> convertirDonacionesDTO(List<DonacionSinSegmentarDTO> donacionesDTO){

        List<DonacionSinSegmentar> donaciones = donacionesDTO.stream().map(
                donacion -> {
                    List<Bien> bienes = this.convertirBienesDTO(donacion.bienes());

                    return new DonacionSinSegmentar(
                            bienes,
                            donacion.fechaDeIngreso()
                    );
                }).toList();

        return donaciones;
    }
    public List<MisionDTO> obtenerMisionesDTO(List<Mision> misiones){
        List<MisionDTO> misionesDTO = misiones.stream().map(m -> new MisionDTO(m.getNombre(), m.getEstadoDeMision())).toList();
        return misionesDTO;
    }
    public List<Mision> convertirMisionesDTO(List<MisionDTO> misionesDTO){
        List<Mision> misiones = misionesDTO.stream().map(m -> new Mision(m.nombre(), m.estadoDeMision())).toList();
        return misiones;
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
    public List<Bien> convertirBienesDTO(List<BienDTO> bienesDTO){
        List<Bien> bienes = bienesDTO.stream().map(b -> new Bien(
                b.nombre(),
                b.descripcion(),
                null,
                new Subcategoria(
                        b.subcategoria().nombre(),
                        b.subcategoria().esPerecedero(),
                        new Categoria(
                                b.subcategoria().categoria().nombre()
                        )
                ),
                b.fechaDeVencimiento(),
                b.esUsado(),
                b.tipoUnidad(),
                b.cantidad()
        )).toList();
        return bienes;
    }

    // Método para llamar al servicio de notificaciones
    // URL resultante: http://localhost:8081/servicioDeNotificaciones/notificar?destinatario=X&mensaje=Y&medio=Z
    private void notificar(String destinatario, String mensaje, String medio) {
        try {
            String url = notificacionesProperties.getUrl() +
                    "/servicioDeNotificaciones/notificar" +
                    "?destinatario=" + destinatario +
                    "&mensaje=" + mensaje +
                    "&medio=" + medio;
            restTemplate.postForObject(url, null, String.class);
        } catch (Exception e) {
            System.out.println("Error al notificar: " + e.getMessage());
        }
    }
}

