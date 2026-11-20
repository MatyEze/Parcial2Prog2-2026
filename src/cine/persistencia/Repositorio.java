package cine.persistencia;

import cine.modelo.Identificable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Repositorio<K, T extends Identificable<K>> implements Serializable {

    private static final long serialVersionUID = 1L;

    // LinkedHashMap conserva el orden de alta, que es el orden en que se listan.
    private Map<K, T> elementos;

    public Repositorio() {
        this.elementos = new LinkedHashMap<>();
    }

    public void agregar(T elemento) {
        elementos.put(elemento.getId(), elemento);
    }

    public T buscar(K id) {
        return elementos.get(id);
    }

    public boolean existe(K id) {
        return elementos.containsKey(id);
    }

    public List<T> listar() {
        return new ArrayList<>(elementos.values());
    }

    public int cantidad() {
        return elementos.size();
    }
}
