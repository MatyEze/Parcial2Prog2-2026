package cine.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Sala implements Identificable<Integer>, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private int filas;
    private int butacasPorFila;
    private List<Butaca> butacas;
    private boolean habilitada;

    public Sala(int id, String nombre, int filas, int butacasPorFila) {
        this.id = id;
        this.nombre = nombre;
        this.filas = filas;
        this.butacasPorFila = butacasPorFila;
        this.butacas = new ArrayList<>();
        this.habilitada = true;
        generarButacas();
    }

    // Las filas se nombran con letras desde la 'A' y las butacas se numeran desde 1.
    private void generarButacas() {
        for (int i = 0; i < filas; i++) {
            char fila = (char) ('A' + i);
            for (int numero = 1; numero <= butacasPorFila; numero++) {
                butacas.add(new Butaca(fila, numero));
            }
        }
    }

    public void deshabilitar() {
        this.habilitada = false;
    }

    public void habilitar() {
        this.habilitada = true;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public int getCapacidad() {
        return butacas.size();
    }

    // Vista de solo lectura: las butacas de una sala no se agregan ni se quitan desde afuera.
    public List<Butaca> getButacas() {
        return Collections.unmodifiableList(butacas);
    }

    public Butaca buscarButaca(char fila, int numero) {
        for (Butaca butaca : butacas) {
            if (butaca.getFila() == fila && butaca.getNumero() == numero) {
                return butaca;
            }
        }
        return null;
    }

    public boolean contiene(Butaca butaca) {
        return butacas.contains(butaca);
    }

    @Override
    public Integer getId() {
        return id;
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
        Sala otra = (Sala) o;
        return id == otra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
