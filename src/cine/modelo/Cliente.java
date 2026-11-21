package cine.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Cliente implements Identificable<String>, Serializable {

    private static final long serialVersionUID = 1L;

    private String identificacion;
    private String nombre;

    public Cliente(String identificacion, String nombre) {
        this.identificacion = identificacion;
        this.nombre = nombre;
    }

    public void cambiarNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getId() {
        return identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cliente otro = (Cliente) o;
        return Objects.equals(identificacion, otro.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }
}
