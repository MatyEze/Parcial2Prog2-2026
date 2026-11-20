package cine.servicios;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Funcion;
import cine.modelo.Sala;
import cine.persistencia.EstadoCine;
import cine.persistencia.GestorEstado;
import java.util.ArrayList;
import java.util.List;

public class SalaService {

    // Las filas se nombran con letras de la A a la Z.
    private static final int MAXIMO_FILAS = 26;
    private static final int MAXIMO_BUTACAS_POR_FILA = 40;

    private GestorEstado gestor;
    private FuncionService funcionService;

    public SalaService(GestorEstado gestor, FuncionService funcionService) {
        this.gestor = gestor;
        this.funcionService = funcionService;
    }

    public Sala registrar(String nombre, int filas, int butacasPorFila)
            throws ReglaNegocioException, PersistenciaException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre de la sala no puede estar vacío.");
        }
        String nombreLimpio = nombre.trim();
        if (existeNombre(nombreLimpio)) {
            throw new ReglaNegocioException("Ya existe una sala llamada \"" + nombreLimpio + "\".");
        }
        if (filas < 1 || filas > MAXIMO_FILAS) {
            throw new ReglaNegocioException("La cantidad de filas tiene que estar entre 1 y "
                    + MAXIMO_FILAS + " (las filas se nombran de la A a la Z).");
        }
        if (butacasPorFila < 1 || butacasPorFila > MAXIMO_BUTACAS_POR_FILA) {
            throw new ReglaNegocioException("La cantidad de butacas por fila tiene que estar entre 1 y "
                    + MAXIMO_BUTACAS_POR_FILA + ".");
        }
        EstadoCine estado = gestor.getEstado();
        Sala sala = new Sala(estado.siguienteIdSala(), nombreLimpio, filas, butacasPorFila);
        estado.getSalas().agregar(sala);
        gestor.guardarCambios();
        return sala;
    }

    public void deshabilitar(int id) throws ReglaNegocioException, PersistenciaException {
        Sala sala = buscarExistente(id);
        if (!sala.isHabilitada()) {
            throw new ReglaNegocioException("La sala ya está deshabilitada.");
        }
        for (Funcion funcion : funcionService.futurasVigentesDeSala(id)) {
            funcionService.cancelarEnCascada(funcion);
        }
        sala.deshabilitar();
        gestor.guardarCambios();
    }

    public void habilitar(int id) throws ReglaNegocioException, PersistenciaException {
        Sala sala = buscarExistente(id);
        if (sala.isHabilitada()) {
            throw new ReglaNegocioException("La sala ya está habilitada.");
        }
        sala.habilitar();
        gestor.guardarCambios();
    }

    public List<Sala> listar() {
        return gestor.getEstado().getSalas().listar();
    }

    public List<Sala> listarHabilitadas() {
        List<Sala> resultado = new ArrayList<>();
        for (Sala sala : listar()) {
            if (sala.isHabilitada()) {
                resultado.add(sala);
            }
        }
        return resultado;
    }

    private boolean existeNombre(String nombre) {
        for (Sala sala : listar()) {
            if (sala.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
                return true;
            }
        }
        return false;
    }

    private Sala buscarExistente(int id) throws ReglaNegocioException {
        Sala sala = gestor.getEstado().getSalas().buscar(id);
        if (sala == null) {
            throw new ReglaNegocioException("No existe la sala seleccionada.");
        }
        return sala;
    }
}
