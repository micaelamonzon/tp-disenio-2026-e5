package ar.edu.utn.frba.ddsi.models.entities.persona;


import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSegmentada;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.MisionParticular;
import ar.edu.utn.frba.ddsi.models.entities.persistente.Persistente;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "Donante")
public class Donante extends Persistente {

    @Column(name = "id_en_donaciones")
    private Long idEnDonaciones; //id en donaciones

    @Enumerated(EnumType.STRING)
    private TipoDeDonante tipoDeDonante;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "fechaDeRegistro")
    private LocalDateTime fechaDeRegistro;

    @OneToMany(mappedBy = "donante", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DonacionSegmentada> donaciones = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "perfil_id", referencedColumnName = "id")
    private Perfil perfil;


    @OneToMany(mappedBy = "donante", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<MisionParticular> misiones = new ArrayList<>();; //historial de misiones tanto en curso como pasadas

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "mision_en_curso_id", referencedColumnName = "id")
    private MisionParticular misionEnCurso;

    @OneToMany(mappedBy = "donante")
    private List<RankingPosicion> posicionesEnRankings = new ArrayList<>();

    public Donante(Long idEnDonaciones,String nombre, String apellido, String razonSocial, LocalDateTime fechaDeRegistro, List<DonacionSegmentada> donaciones ,TipoDeDonante tipo) {
        this.idEnDonaciones = idEnDonaciones;
        this.nombre = nombre;
        this.apellido = apellido;
        this.razonSocial = razonSocial;
        this.donaciones = donaciones;
        this.fechaDeRegistro = fechaDeRegistro;
        this.tipoDeDonante = tipo;
    }

    public Donante() {

    }

    public void agregarMision(MisionParticular mision) {
        this.misiones.add(mision);
    }


    public Integer calcularMisionesCumplidasEn(YearMonth periodo) {
        return (int) this.misiones.stream()
                .filter(m -> m.getEstado() == EstadoDeMision.COMPLETADA)
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


    public List<MisionParticular> obtenerMisionesCompletadas(){
        return this.misiones.stream().filter(m -> m.getEstado() == EstadoDeMision.COMPLETADA).collect(Collectors.toList()) ;
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
                .map(DonacionSegmentada::getEntidadBeneficiariaId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();
    }
    public boolean esNuevoEn(YearMonth periodo) {
        if (fechaDeRegistro == null) return false;
        return YearMonth.from(fechaDeRegistro.toLocalDate()).equals(periodo);
    }

    public void agregarDonacion(DonacionSegmentada donacion) {
        if (donacion == null) {
           return;
        }
        this.donaciones.add(donacion);
    }

}
