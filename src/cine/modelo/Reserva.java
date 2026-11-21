package cine.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Reserva implements Identificable<String>, Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private Cliente cliente;
    // Se guarda el nombre del momento de la confirmación porque el cliente
    // puede cambiar de nombre en una reserva posterior.
    private String nombreCliente;
    private Funcion funcion;
    private List<Entrada> entradas;
    private EstadoReserva estado;
    private LocalDateTime fechaCreacion;

    public Reserva(String codigo, Cliente cliente, Funcion funcion, List<Entrada> entradas,
            LocalDateTime fechaCreacion) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.nombreCliente = cliente.getNombre();
        this.funcion = funcion;
        this.entradas = new ArrayList<>(entradas);
        this.estado = EstadoReserva.CONFIRMADA;
        this.fechaCreacion = fechaCreacion;
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean estaConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    public boolean ocupa(Butaca butaca) {
        for (Entrada entrada : entradas) {
            if (entrada.getButaca().equals(butaca)) {
                return true;
            }
        }
        return false;
    }

    public List<Butaca> getButacas() {
        List<Butaca> butacas = new ArrayList<>();
        for (Entrada entrada : entradas) {
            butacas.add(entrada.getButaca());
        }
        return Collections.unmodifiableList(butacas);
    }

    public double getImporteTotal() {
        double total = 0;
        for (Entrada entrada : entradas) {
            total += entrada.getPrecioConfirmado();
        }
        return total;
    }

    @Override
    public String getId() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reserva otra = (Reserva) o;
        return Objects.equals(codigo, otra.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
