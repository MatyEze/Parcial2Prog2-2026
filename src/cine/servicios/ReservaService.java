package cine.servicios;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Butaca;
import cine.modelo.Cliente;
import cine.modelo.Entrada;
import cine.modelo.EstadoFuncion;
import cine.modelo.Funcion;
import cine.modelo.Reserva;
import cine.modelo.TipoEntrada;
import cine.persistencia.EstadoCine;
import cine.persistencia.ExportadorReservas;
import cine.persistencia.GestorEstado;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReservaService {

    private static final int MINIMO_DIGITOS_IDENTIFICACION = 7;
    private static final int MAXIMO_DIGITOS_IDENTIFICACION = 8;

    private static final Comparator<Reserva> POR_CODIGO = new PorCodigo();

    private GestorEstado gestor;
    private ExportadorReservas exportador;

    public ReservaService(GestorEstado gestor, ExportadorReservas exportador) {
        this.gestor = gestor;
        this.exportador = exportador;
    }

    // La ocupación se calcula a partir de las reservas confirmadas de la función:
    // ni la butaca ni la función guardan si están ocupadas.
    public Set<Butaca> butacasOcupadas(Funcion funcion) {
        Set<Butaca> ocupadas = new HashSet<>();
        for (Reserva reserva : gestor.getEstado().getReservas().listar()) {
            if (reserva.estaConfirmada() && reserva.getFuncion().equals(funcion)) {
                ocupadas.addAll(reserva.getButacas());
            }
        }
        return ocupadas;
    }

    public Reserva confirmar(Funcion funcion, String nombre, String identificacion,
            Map<Butaca, TipoEntrada> tipos) throws ReglaNegocioException, PersistenciaException {
        EstadoCine estado = gestor.getEstado();
        Funcion registrada = validarFuncion(funcion);
        validarButacas(registrada, tipos);
        String nombreLimpio = validarNombre(nombre);
        String identificacionLimpia = validarIdentificacion(identificacion);

        // El cliente se actualiza antes de crear la reserva para que esta copie el nombre vigente;
        // las reservas anteriores conservan el nombre que tenían.
        Cliente cliente = estado.getClientes().buscar(identificacionLimpia);
        if (cliente == null) {
            cliente = new Cliente(identificacionLimpia, nombreLimpio);
            estado.getClientes().agregar(cliente);
        } else {
            cliente.cambiarNombre(nombreLimpio);
        }

        List<Entrada> entradas = new ArrayList<>();
        for (Map.Entry<Butaca, TipoEntrada> par : tipos.entrySet()) {
            entradas.add(Entrada.crear(par.getValue(), par.getKey(), registrada.getPrecio()));
        }

        Reserva reserva = new Reserva(estado.siguienteCodigoReserva(), cliente, registrada, entradas,
                LocalDateTime.now());
        estado.getReservas().agregar(reserva);
        gestor.guardarCambios();
        return reserva;
    }

    // La reserva no se borra: queda cancelada en el historial y sus butacas vuelven a estar
    // libres, porque la ocupación solo cuenta las reservas confirmadas.
    public void cancelar(String codigo) throws ReglaNegocioException, PersistenciaException {
        Reserva reserva = null;
        if (codigo != null) {
            reserva = gestor.getEstado().getReservas().buscar(codigo.trim());
        }
        if (reserva == null) {
            throw new ReglaNegocioException("No existe la reserva seleccionada.");
        }
        if (!reserva.estaConfirmada()) {
            throw new ReglaNegocioException("La reserva " + reserva.getId() + " ya está cancelada.");
        }
        EstadoFuncion estadoFuncion = reserva.getFuncion().getEstado(LocalDateTime.now());
        if (estadoFuncion == EstadoFuncion.EN_EMISION) {
            throw new ReglaNegocioException("La función está en emisión y la reserva " + reserva.getId()
                    + " no se puede cancelar por separado. Solo se puede cancelar la función completa,"
                    + " junto con todas sus reservas.");
        }
        if (estadoFuncion == EstadoFuncion.FINALIZADA) {
            throw new ReglaNegocioException("La función ya finalizó y la reserva " + reserva.getId()
                    + " no se puede cancelar. Queda confirmada en el historial.");
        }
        reserva.cancelar();
        gestor.guardarCambios();
    }

    // Sin texto devuelve todas las reservas. Con texto busca el código exacto, sin importar
    // mayúsculas, o la identificación exacta del cliente.
    public List<Reserva> buscar(String texto) {
        String buscado = "";
        if (texto != null) {
            buscado = texto.trim();
        }
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva reserva : gestor.getEstado().getReservas().listar()) {
            if (buscado.isEmpty() || reserva.getId().equalsIgnoreCase(buscado)
                    || reserva.getCliente().getId().equals(buscado)) {
                resultado.add(reserva);
            }
        }
        Collections.sort(resultado, POR_CODIGO);
        return resultado;
    }

    public List<Reserva> listar() {
        return gestor.getEstado().getReservas().listar();
    }

    // Se exportan todas las reservas, también las canceladas; no se modifica ni se guarda el estado.
    public void exportarCsv(Path destino) throws PersistenciaException {
        exportador.exportarCsv(buscar(""), destino);
    }

    private Funcion validarFuncion(Funcion funcion) throws ReglaNegocioException {
        if (funcion == null) {
            throw new ReglaNegocioException("Hay que seleccionar una función.");
        }
        Funcion registrada = gestor.getEstado().getFunciones().buscar(funcion.getId());
        if (registrada == null) {
            throw new ReglaNegocioException("No existe la función seleccionada.");
        }
        if (registrada.isCancelada()) {
            throw new ReglaNegocioException("La función está cancelada y no admite reservas.");
        }
        if (registrada.yaComenzo(LocalDateTime.now())) {
            throw new ReglaNegocioException("La función ya comenzó y no admite reservas.");
        }
        return registrada;
    }

    // La disponibilidad se vuelve a revisar aquí aunque la pantalla ya la haya mostrado,
    // porque otra reserva pudo ocupar la butaca mientras tanto.
    private void validarButacas(Funcion funcion, Map<Butaca, TipoEntrada> tipos)
            throws ReglaNegocioException {
        if (tipos == null || tipos.isEmpty()) {
            throw new ReglaNegocioException("Hay que seleccionar al menos una butaca.");
        }
        Set<Butaca> ocupadas = butacasOcupadas(funcion);
        for (Map.Entry<Butaca, TipoEntrada> par : tipos.entrySet()) {
            Butaca butaca = par.getKey();
            if (butaca == null) {
                throw new ReglaNegocioException("Una de las butacas seleccionadas no es válida.");
            }
            if (!funcion.getSala().contiene(butaca)) {
                throw new ReglaNegocioException("La butaca " + butaca.getCodigo()
                        + " no pertenece a la sala \"" + funcion.getSala().getNombre() + "\".");
            }
            if (par.getValue() == null) {
                throw new ReglaNegocioException("Hay que indicar el tipo de entrada de la butaca "
                        + butaca.getCodigo() + ".");
            }
            if (ocupadas.contains(butaca)) {
                throw new ReglaNegocioException("La butaca " + butaca.getCodigo()
                        + " ya está ocupada en esta función.");
            }
        }
    }

    private String validarNombre(String nombre) throws ReglaNegocioException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaNegocioException("El nombre del cliente no puede estar vacío.");
        }
        return nombre.trim();
    }

    private String validarIdentificacion(String identificacion) throws ReglaNegocioException {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new ReglaNegocioException("La identificación del cliente no puede estar vacía.");
        }
        String limpia = identificacion.trim();
        boolean soloDigitos = true;
        for (int i = 0; i < limpia.length(); i++) {
            char c = limpia.charAt(i);
            // Se compara con el rango ASCII porque Character.isDigit acepta dígitos de otros alfabetos.
            if (c < '0' || c > '9') {
                soloDigitos = false;
            }
        }
        if (!soloDigitos || limpia.length() < MINIMO_DIGITOS_IDENTIFICACION
                || limpia.length() > MAXIMO_DIGITOS_IDENTIFICACION) {
            throw new ReglaNegocioException("La identificación tiene que tener "
                    + MINIMO_DIGITOS_IDENTIFICACION + " u " + MAXIMO_DIGITOS_IDENTIFICACION
                    + " dígitos, sin puntos ni espacios.");
        }
        return limpia;
    }

    private static class PorCodigo implements Comparator<Reserva> {

        // Se compara primero el largo para que CIN-1000 quede después de CIN-999;
        // comparando solo el texto quedaría antes.
        @Override
        public int compare(Reserva a, Reserva b) {
            int porLargo = Integer.compare(a.getId().length(), b.getId().length());
            if (porLargo != 0) {
                return porLargo;
            }
            return a.getId().compareTo(b.getId());
        }
    }
}
