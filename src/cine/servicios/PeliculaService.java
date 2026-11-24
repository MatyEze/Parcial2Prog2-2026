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
        Pelicula otra = buscarOtraActivaConTitulo(tituloLimpio, null);
        if (otra != null) {
            throw new ReglaNegocioException("Ya existe una película activa llamada \"" + otra.getTitulo() + "\".");
        }
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
        // El título es el que ven los clientes que ya reservaron y la duración define el horario
        // de fin de las funciones, por eso ninguno de los dos se cambia mientras haya funciones
        // futuras vigentes. El título se compara tal cual: corregir mayúsculas también es un cambio.
        boolean cambiaTitulo = !tituloLimpio.equals(pelicula.getTitulo());
        boolean cambiaDuracion = duracionMinutos != pelicula.getDuracionMinutos();
        if (cambiaTitulo || cambiaDuracion) {
            String dato;
            String cierre;
            if (cambiaTitulo && cambiaDuracion) {
                dato = "el título ni la duración";
                cierre = "el título y la duración se pueden modificar.";
            } else if (cambiaTitulo) {
                dato = "el título";
                cierre = "el título se puede modificar.";
            } else {
                dato = "la duración";
                cierre = "la duración se puede modificar.";
            }
            int cantidad = funcionService.futurasVigentesDePelicula(id).size();
            if (cantidad == 1) {
                throw new ReglaNegocioException("No se puede cambiar " + dato + " porque la película tiene "
                        + "1 función futura vigente. Si se cancela esa función, " + cierre);
            }
            if (cantidad > 1) {
                throw new ReglaNegocioException("No se puede cambiar " + dato + " porque la película tiene "
                        + cantidad + " funciones futuras vigentes. Si se cancelan esas funciones, " + cierre);
            }
        }
        // Una película desactivada puede repetir el título de otra; la regla se revisa al activarla.
        if (pelicula.isActiva()) {
            Pelicula otra = buscarOtraActivaConTitulo(tituloLimpio, pelicula);
            if (otra != null) {
                throw new ReglaNegocioException("Ya existe otra película activa llamada \""
                        + otra.getTitulo() + "\".");
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
        Pelicula otra = buscarOtraActivaConTitulo(pelicula.getTitulo(), pelicula);
        if (otra != null) {
            throw new ReglaNegocioException("No se puede activar la película porque ya existe otra película "
                    + "activa llamada \"" + otra.getTitulo() + "\". Si se cambia el título de esta película "
                    + "o se desactiva la otra, se puede activar.");
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

    // Dos títulos son iguales aunque difieran en mayúsculas o espacios de los extremos;
    // los acentos sí los distinguen. La película excluida es la que se está modificando
    // o activando (null al registrar).
    private Pelicula buscarOtraActivaConTitulo(String titulo, Pelicula excluida) {
        for (Pelicula pelicula : listarActivas()) {
            if (!pelicula.equals(excluida) && pelicula.getTitulo().trim().equalsIgnoreCase(titulo.trim())) {
                return pelicula;
            }
        }
        return null;
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
