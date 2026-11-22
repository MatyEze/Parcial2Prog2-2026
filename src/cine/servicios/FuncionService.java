package cine.servicios;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.EstadoFuncion;
import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import cine.modelo.Reserva;
import cine.modelo.Sala;
import cine.persistencia.EstadoCine;
import cine.persistencia.GestorEstado;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FuncionService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Comparator<Funcion> POR_INICIO = new PorInicio();

    private GestorEstado gestor;

    public FuncionService(GestorEstado gestor) {
        this.gestor = gestor;
    }

    public Funcion programar(int idPelicula, int idSala, LocalDateTime inicio, double precio)
            throws ReglaNegocioException, PersistenciaException {
        EstadoCine estado = gestor.getEstado();
        Pelicula pelicula = estado.getPeliculas().buscar(idPelicula);
        if (pelicula == null) {
            throw new ReglaNegocioException("No existe la película seleccionada.");
        }
        if (!pelicula.isActiva()) {
            throw new ReglaNegocioException("La película \"" + pelicula.getTitulo()
                    + "\" está desactivada y no se pueden programar funciones nuevas.");
        }
        Sala sala = estado.getSalas().buscar(idSala);
        if (sala == null) {
            throw new ReglaNegocioException("No existe la sala seleccionada.");
        }
        if (!sala.isHabilitada()) {
            throw new ReglaNegocioException("La sala \"" + sala.getNombre()
                    + "\" está deshabilitada y no se pueden programar funciones nuevas.");
        }
        if (inicio == null) {
            throw new ReglaNegocioException("Hay que indicar la fecha y hora de inicio.");
        }
        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha y hora de inicio tiene que ser futura.");
        }
        validarPrecio(precio);

        // Se valida con un id provisorio para no consumir un id si la función se rechaza.
        validarSuperposicion(new Funcion(0, pelicula, sala, inicio, precio));

        Funcion funcion = new Funcion(estado.siguienteIdFuncion(), pelicula, sala, inicio, precio);
        estado.getFunciones().agregar(funcion);
        gestor.guardarCambios();
        return funcion;
    }

    public void cambiarPrecio(int id, double precio) throws ReglaNegocioException, PersistenciaException {
        Funcion funcion = buscarExistente(id);
        // La pantalla oculta la acción en estos casos, pero el listado pudo quedar abierto
        // mientras pasaba la hora de inicio.
        EstadoFuncion estado = funcion.getEstado(LocalDateTime.now());
        if (estado == EstadoFuncion.CANCELADA) {
            throw new ReglaNegocioException("La función está cancelada y no se puede cambiar el precio.");
        }
        if (estado == EstadoFuncion.EN_EMISION) {
            throw new ReglaNegocioException("La función está en emisión y no se puede cambiar el precio.");
        }
        if (estado == EstadoFuncion.FINALIZADA) {
            throw new ReglaNegocioException("La función ya finalizó y no se puede cambiar el precio.");
        }
        validarPrecio(precio);
        funcion.cambiarPrecio(precio);
        gestor.guardarCambios();
    }

    public void cancelar(int id) throws ReglaNegocioException, PersistenciaException {
        Funcion funcion = buscarExistente(id);
        EstadoFuncion estado = funcion.getEstado(LocalDateTime.now());
        if (estado == EstadoFuncion.CANCELADA) {
            throw new ReglaNegocioException("La función ya está cancelada.");
        }
        if (estado == EstadoFuncion.FINALIZADA) {
            throw new ReglaNegocioException("La función ya finalizó y no se puede cancelar.");
        }
        cancelarEnCascada(funcion);
        gestor.guardarCambios();
    }

    void cancelarEnCascada(Funcion funcion) {
        // Las reservas no se borran: quedan canceladas y sus butacas vuelven a estar libres.
        for (Reserva reserva : gestor.getEstado().getReservas().listar()) {
            if (reserva.estaConfirmada() && reserva.getFuncion().equals(funcion)) {
                reserva.cancelar();
            }
        }
        funcion.cancelar();
    }

    public int cantidadReservasConfirmadas(Funcion funcion) {
        int cantidad = 0;
        for (Reserva reserva : gestor.getEstado().getReservas().listar()) {
            if (reserva.estaConfirmada() && reserva.getFuncion().equals(funcion)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public List<Funcion> futurasVigentesDePelicula(int idPelicula) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion funcion : gestor.getEstado().getFunciones().listar()) {
            if (funcion.getPelicula().getId() == idPelicula && funcion.estaVigente(ahora)) {
                resultado.add(funcion);
            }
        }
        Collections.sort(resultado, POR_INICIO);
        return resultado;
    }

    public List<Funcion> futurasVigentesDeSala(int idSala) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion funcion : gestor.getEstado().getFunciones().listar()) {
            if (funcion.getSala().getId() == idSala && funcion.estaVigente(ahora)) {
                resultado.add(funcion);
            }
        }
        Collections.sort(resultado, POR_INICIO);
        return resultado;
    }

    // Devuelve null si no existe, igual que la búsqueda de los repositorios.
    public Funcion buscar(int id) {
        return gestor.getEstado().getFunciones().buscar(id);
    }

    public List<Funcion> listar() {
        List<Funcion> resultado = gestor.getEstado().getFunciones().listar();
        Collections.sort(resultado, POR_INICIO);
        return resultado;
    }

    public List<Funcion> buscarEnCartelera(Integer idPelicula, LocalDate fecha) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion funcion : gestor.getEstado().getFunciones().listar()) {
            if (!funcion.estaVigente(ahora)) {
                continue;
            }
            if (idPelicula != null && !idPelicula.equals(funcion.getPelicula().getId())) {
                continue;
            }
            if (fecha != null && !fecha.equals(funcion.getInicio().toLocalDate())) {
                continue;
            }
            resultado.add(funcion);
        }
        Collections.sort(resultado, POR_INICIO);
        return resultado;
    }

    // Distingue un choque real de horarios de un choque que solo existe por el margen,
    // para que el mensaje explique cuál de los dos casos es.
    private void validarSuperposicion(Funcion nueva) throws ReglaNegocioException {
        for (Funcion existente : listar()) {
            if (!nueva.seSuperponeCon(existente)) {
                continue;
            }
            if (nueva.getInicio().isBefore(existente.getFin())
                    && existente.getInicio().isBefore(nueva.getFin())) {
                throw new ReglaNegocioException("El horario se superpone con la función de \""
                        + existente.getPelicula().getTitulo() + "\" de "
                        + existente.getInicio().format(HORA) + " a "
                        + existente.getFin().format(HORA) + " en esa sala.");
            }
            if (!existente.getFin().isAfter(nueva.getInicio())) {
                throw new ReglaNegocioException("La sala queda libre a las "
                        + existente.getSalaLibreDesde().format(HORA)
                        + ": la función anterior termina a las "
                        + existente.getFin().format(HORA) + " y se dejan "
                        + Funcion.MARGEN_MINUTOS + " minutos de margen.");
            }
            throw new ReglaNegocioException("La función siguiente de la sala empieza a las "
                    + existente.getInicio().format(HORA) + ": se necesitan "
                    + Funcion.MARGEN_MINUTOS + " minutos de margen después del fin de la nueva función ("
                    + nueva.getFin().format(HORA) + ").");
        }
    }

    private Funcion buscarExistente(int id) throws ReglaNegocioException {
        Funcion funcion = gestor.getEstado().getFunciones().buscar(id);
        if (funcion == null) {
            throw new ReglaNegocioException("No existe la función seleccionada.");
        }
        return funcion;
    }

    private void validarPrecio(double precio) throws ReglaNegocioException {
        // Se compara con "mayor que" para rechazar también NaN, que no es mayor que ningún número.
        if (!(precio > 0) || Double.isInfinite(precio)) {
            throw new ReglaNegocioException("El precio tiene que ser un número mayor que cero.");
        }
    }

    private static class PorInicio implements Comparator<Funcion> {

        @Override
        public int compare(Funcion a, Funcion b) {
            return a.getInicio().compareTo(b.getInicio());
        }
    }
}
