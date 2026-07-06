package ar.edu.utn.frba.ddsi.models.entities.persona;


import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionEnCurso;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Donante {
    private Long id; //id en incentivos
    private Long idEnDonaciones; //id en donaciones
    private String nombre;
    private String apellido;
    private String razonSocial;
    private LocalDateTime fechaDeRegistro;
    private List<DonacionSinSegmentar> donaciones;
    private Perfil perfil;
    private List<Mision> misiones;
    private TipoDeDonante tipoDeDonante;
    private MedioDeNotificacion medioDeNotificacionPredeterminado;
    private MisionEnCurso misionEnCurso;

    public Donante(Long id,String nombre,String apellido,String razonSocial, List<DonacionSinSegmentar> donaciones,LocalDateTime fechaDeRegistro, MedioDeNotificacion medioDeNotificacionPredeterminado, TipoDeDonante tipo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.razonSocial = razonSocial;
        this.donaciones = donaciones;
        this.fechaDeRegistro = fechaDeRegistro;
        this.medioDeNotificacionPredeterminado = medioDeNotificacionPredeterminado;
        this.tipoDeDonante = tipo;
    }

    public void agregarMision(Mision mision) {
        this.misiones.add(mision);
    }

    public Integer calcularMisionesCumplidasEn(YearMonth periodo) {
        return (int) this.misiones.stream()
                .filter(m -> m.getEstadoDeMision() == EstadoDeMision.COMPLETADA) 
                .filter(m -> m.getFechaCompletada() != null)
                .filter(m -> YearMonth.from(m.getFechaCompletada()).equals(periodo))
                .count();
    }

    public YearMonth mesDeMayorActividad() {
        return donaciones.stream()
                .collect(Collectors.groupingBy(
                        d -> YearMonth.from(d.getFechaDeIngreso().toLocalDate()),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }


    public long cantidadDonacionesEnMes(YearMonth mes) {
        return donaciones.stream()
                .filter(d -> YearMonth.from(
                        d.getFechaDeIngreso().toLocalDate()).equals(mes))
                .count();
    }


    public List<MetricaMensual> calcularEvolucionMensual() {
        return donaciones.stream()
                .collect(Collectors.groupingBy(
                        d -> YearMonth.from(d.getFechaDeIngreso().toLocalDate())
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new MetricaMensual(
                        entry.getKey(),
                        entry.getValue().size(),
                        0  // organizaciones: se completa cuando tengamos entidad en donación
                ))
                .toList();
    }

    // Total historico
    public int totalDonaciones() {
        return donaciones.size();
    }

    public int compararConMesAnterior() {
        YearMonth mesActual = YearMonth.now();
        YearMonth mesAnterior = mesActual.minusMonths(1);

        long donacionesMesActual = cantidadDonacionesEnMes(mesActual);
        long donacionesMesAnterior = cantidadDonacionesEnMes(mesAnterior);

        // Retorna la diferencia (puede ser positiva, cero o negativa)
        return (int) (donacionesMesActual - donacionesMesAnterior);
    }


    public int totalOrganizacionesAyudadas() {
        if (this.donaciones == null || this.donaciones.isEmpty()) {
            return 0;
        }

        return (int) this.donaciones.stream()
                .map(DonacionSinSegmentar::getEntidadBeneficiariaId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();
    }
    public boolean esNuevoEn(YearMonth periodo) {
        if (fechaDeRegistro == null) return false;
        return YearMonth.from(fechaDeRegistro.toLocalDate()).equals(periodo);
    }


}
