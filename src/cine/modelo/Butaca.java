package cine.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Butaca implements Serializable {

    private static final long serialVersionUID = 1L;

    private char fila;
    private int numero;

    public Butaca(char fila, int numero) {
        this.fila = fila;
        this.numero = numero;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public String getCodigo() {
        return fila + String.valueOf(numero);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Butaca otra = (Butaca) o;
        return fila == otra.fila && numero == otra.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, numero);
    }
}
