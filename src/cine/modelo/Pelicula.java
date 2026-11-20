package cine.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Pelicula implements Identificable<Integer>, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String titulo;
    private int duracionMinutos;
    private boolean activa;

    public Pelicula(int id, String titulo, int duracionMinutos) {
        this.id = id;
        this.titulo = titulo;
        this.duracionMinutos = duracionMinutos;
        this.activa = true;
    }

    public void cambiarTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void cambiarDuracion(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public void desactivar() {
        this.activa = false;
    }

    public void activar() {
        this.activa = true;
    }

    public boolean isActiva() {
        return activa;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pelicula otra = (Pelicula) o;
        return id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
