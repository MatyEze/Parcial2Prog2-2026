package cine.persistencia;

import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import cine.modelo.Sala;
import java.io.Serializable;

public class EstadoCine implements Serializable {

    private static final long serialVersionUID = 1L;

    private Repositorio<Integer, Pelicula> peliculas;
    private Repositorio<Integer, Sala> salas;
    private Repositorio<Integer, Funcion> funciones;
    private int ultimoIdPelicula;
    private int ultimoIdSala;
    private int ultimoIdFuncion;

    public EstadoCine() {
        this.peliculas = new Repositorio<>();
        this.salas = new Repositorio<>();
        this.funciones = new Repositorio<>();
        this.ultimoIdPelicula = 0;
        this.ultimoIdSala = 0;
        this.ultimoIdFuncion = 0;
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
}
