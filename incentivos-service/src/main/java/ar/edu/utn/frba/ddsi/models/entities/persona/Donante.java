package ar.edu.utn.frba.ddsi.models.entities.persona;

import ar.edu.utn.frba.ddsi.models.entities.categorias.CategoriaDeDonante;
import ar.edu.utn.frba.ddsi.models.entities.donaciones.DonacionSinSegmentar;
import ar.edu.utn.frba.ddsi.models.entities.misiones.EstadoDeMision;
import ar.edu.utn.frba.ddsi.models.entities.misiones.Mision;
import lombok.Data;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Donante {
    private Long id;

    private String cuit;
    private String razonSocial;

    private List<DonacionSinSegmentar> donaciones;
    private Perfil perfil;
    private List<Mision> misiones;
    private CategoriaDeDonante categoria;

    private String nombre;
    private String apellido;
    private Integer edad;
    private Integer DNI;
    private String genero;
    private String direccion;

    public Donante(String cuit,String razonSocial,String nombre,String apellido,Integer edad,Integer DNI,String genero,String direccion,List<DonacionSinSegmentar> donaciones, List<Mision> misiones, CategoriaDeDonante categoria) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.DNI = DNI;
        this.genero=genero;
        this.direccion=direccion;
        this.donaciones = donaciones;
        this.misiones = misiones;
        this.categoria = categoria;

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
                .map(DonacionSinSegmentar::getOrganizacionId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();
    }

}
