package cine.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Funcion implements Identificable<Integer>, Serializable {

    private static final long serialVersionUID = 1L;

    // Tiempo de limpieza entre funciones de una misma sala.
    public static final int MARGEN_MINUTOS = 15;

    private int id;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime inicio;
    private double precio;
    private boolean cancelada;

    public Funcion(int id, Pelicula pelicula, Sala sala, LocalDateTime inicio, double precio) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.inicio = inicio;
        this.precio = precio;
        this.cancelada = false;
    }

    public LocalDateTime getFin() {
        return inicio.plusMinutes(pelicula.getDuracionMinutos());
    }

    public LocalDateTime getSalaLibreDesde() {
        return getFin().plusMinutes(MARGEN_MINUTOS);
    }

    // Cada funcion ocupa la sala en [inicio, salaLibreDesde). Si una empieza justo
    // cuando la otra libera la sala, no se superponen.
    public boolean seSuperponeCon(Funcion otra) {
        if (!sala.equals(otra.getSala())) {
            return false;
        }
        if (cancelada || otra.isCancelada()) {
            return false;
        }
        return inicio.isBefore(otra.getSalaLibreDesde())
                && otra.getInicio().isBefore(getSalaLibreDesde());
    }

    public boolean yaComenzo(LocalDateTime ahora) {
        return !ahora.isBefore(inicio);
    }

    // El estado se calcula en cada consulta y no se guarda, porque depende de la hora actual.
    public EstadoFuncion getEstado(LocalDateTime ahora) {
        if (cancelada) {
            return EstadoFuncion.CANCELADA;
        }
        if (yaComenzo(ahora)) {
            return EstadoFuncion.FINALIZADA;
        }
        return EstadoFuncion.VIGENTE;
    }

    public boolean estaVigente(LocalDateTime ahora) {
        return getEstado(ahora) == EstadoFuncion.VIGENTE;
    }

    public void cambiarPrecio(double precio) {
        this.precio = precio;
    }

    public void cancelar() {
        this.cancelada = true;
    }

    public boolean isCancelada() {
        return cancelada;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public double getPrecio() {
        return precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Funcion otra = (Funcion) o;
        return id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
