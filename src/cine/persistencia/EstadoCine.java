package cine.persistencia;

import cine.modelo.Cliente;
import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import cine.modelo.Reserva;
import cine.modelo.Sala;
import java.io.Serializable;

public class EstadoCine implements Serializable {

    private static final long serialVersionUID = 1L;

    private Repositorio<Integer, Pelicula> peliculas;
    private Repositorio<Integer, Sala> salas;
    private Repositorio<Integer, Funcion> funciones;
    private Repositorio<String, Cliente> clientes;
    private Repositorio<String, Reserva> reservas;
    private int ultimoIdPelicula;
    private int ultimoIdSala;
    private int ultimoIdFuncion;
    private int ultimoNumeroReserva;

    public EstadoCine() {
        this.peliculas = new Repositorio<>();
        this.salas = new Repositorio<>();
        this.funciones = new Repositorio<>();
        this.clientes = new Repositorio<>();
        this.reservas = new Repositorio<>();
        this.ultimoIdPelicula = 0;
        this.ultimoIdSala = 0;
        this.ultimoIdFuncion = 0;
        this.ultimoNumeroReserva = 0;
    }

    public Repositorio<Integer, Pelicula> getPeliculas() {
        return peliculas;
    }

    public Repositorio<Integer, Sala> getSalas() {
        return salas;
    }

    public Repositorio<Integer, Funcion> getFunciones() {
        return funciones;
    }

    public Repositorio<String, Cliente> getClientes() {
        return clientes;
    }

    public Repositorio<String, Reserva> getReservas() {
        return reservas;
    }

    public int siguienteIdPelicula() {
        ultimoIdPelicula++;
        return ultimoIdPelicula;
    }

    public int siguienteIdSala() {
        ultimoIdSala++;
        return ultimoIdSala;
    }

    public int siguienteIdFuncion() {
        ultimoIdFuncion++;
        return ultimoIdFuncion;
    }

    // Los tres dígitos son relleno, no un tope: después de CIN-999 sigue CIN-1000.
    public String siguienteCodigoReserva() {
        ultimoNumeroReserva++;
        return String.format("CIN-%03d", ultimoNumeroReserva);
    }
}
