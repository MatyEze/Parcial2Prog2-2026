package cine.servicios;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import cine.persistencia.EstadoCine;
import cine.persistencia.GestorEstado;
import java.util.ArrayList;
import java.util.List;

public class PeliculaService {

    private GestorEstado gestor;
    private FuncionService funcionService;

    public PeliculaService(GestorEstado gestor, FuncionService funcionService) {
        this.gestor = gestor;
        this.funcionService = funcionService;
    }

    public Pelicula registrar(String titulo, int duracionMinutos)
            throws ReglaNegocioException, PersistenciaException {
        String tituloLimpio = validarTitulo(titulo);
        validarDuracion(duracionMinutos);
        EstadoCine estado = gestor.getEstado();
        Pelicula pelicula = new Pelicula(estado.siguienteIdPelicula(), tituloLimpio, duracionMinutos);
        estado.getPeliculas().agregar(pelicula);
        gestor.guardarCambios();
        return pelicula;
    }

    public void modificar(int id, String titulo, int duracionMinutos)
            throws ReglaNegocioException, PersistenciaException {
        Pelicula pelicula = buscarExistente(id);
        String tituloLimpio = validarTitulo(titulo);
        validarDuracion(duracionMinutos);
        // La duración define el horario de fin de sus funciones, por eso no se cambia
        // mientras haya funciones futuras vigentes.
        if (duracionMinutos != pelicula.getDuracionMinutos()) {
            int cantidad = funcionService.futurasVigentesDePelicula(id).size();
            if (cantidad == 1) {
                throw new ReglaNegocioException("No se puede cambiar la duración porque la película tiene "
                        + "1 función futura vigente. Si se cancela esa función, "
                        + "la duración se puede modificar.");
            }
            if (cantidad > 1) {
                throw new ReglaNegocioException("No se puede cambiar la duración porque la película tiene "
                        + cantidad + " funciones futuras vigentes. Si se cancelan esas funciones, "
                        + "la duración se puede modificar.");
            }
        }
        pelicula.cambiarTitulo(tituloLimpio);
        pelicula.cambiarDuracion(duracionMinutos);
        gestor.guardarCambios();
    }

    public void desactivar(int id) throws ReglaNegocioException, PersistenciaException {
        Pelicula pelicula = buscarExistente(id);
        if (!pelicula.isActiva()) {
            throw new ReglaNegocioException("La película ya está desactivada.");
        }
        for (Funcion funcion : funcionService.futurasVigentesDePelicula(id)) {
            funcionService.cancelarEnCascada(funcion);
        }
        pelicula.desactivar();
        gestor.guardarCambios();
    }

    public void activar(int id) throws ReglaNegocioException, PersistenciaException {
        Pelicula pelicula = buscarExistente(id);
        if (pelicula.isActiva()) {
            throw new ReglaNegocioException("La película ya está activa.");
        }
        pelicula.activar();
        gestor.guardarCambios();
    }

    public List<Pelicula> listar() {
        return gestor.getEstado().getPeliculas().listar();
    }

    public List<Pelicula> listarActivas() {
        List<Pelicula> resultado = new ArrayList<>();
        for (Pelicula pelicula : listar()) {
            if (pelicula.isActiva()) {
                resultado.add(pelicula);
            }
        }
        return resultado;
    }

    private Pelicula buscarExistente(int id) throws ReglaNegocioException {
        Pelicula pelicula = gestor.getEstado().getPeliculas().buscar(id);
        if (pelicula == null) {
            throw new ReglaNegocioException("No existe la película seleccionada.");
        }
        return pelicula;
    }

    private String validarTitulo(String titulo) throws ReglaNegocioException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ReglaNegocioException("El título de la película no puede estar vacío.");
        }
        return titulo.trim();
    }

    private void validarDuracion(int duracionMinutos) throws ReglaNegocioException {
        if (duracionMinutos <= 0) {
            throw new ReglaNegocioException("La duración tiene que ser mayor que cero minutos.");
        }
    }
}
