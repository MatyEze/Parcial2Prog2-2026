package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Butaca;
import cine.modelo.Entrada;
import cine.modelo.EstadoFuncion;
import cine.modelo.Funcion;
import cine.modelo.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Pantalla 07: detalle de una reserva, con la opción de cancelarla si todavía se puede.
public class PantallaDetalleReserva {

    private VentanaPrincipal ventana;
    private String codigo;

    public PantallaDetalleReserva(VentanaPrincipal ventana, String codigo) {
        this.ventana = ventana;
        this.codigo = codigo;
    }

    public Parent crearVista(Reserva reserva) {
        Funcion funcion = reserva.getFuncion();
        VBox entradas = new VBox(4);
        for (Entrada entrada : reserva.getEntradas()) {
            entradas.getChildren().add(new Label(entrada.getButaca().getCodigo() + " · "
                    + Formato.tipoEntrada(entrada.getTipo()) + " · "
                    + Formato.dinero(entrada.getPrecioConfirmado())));
        }

        Button volver = new Button("← Volver a reservas");
        volver.setOnAction(e -> ventana.mostrarReservas());
        HBox botones = Componentes.filaBotones(volver);
        Label aviso = null;
        if (reserva.estaConfirmada()) {
            EstadoFuncion estadoFuncion = funcion.getEstado(LocalDateTime.now());
            if (estadoFuncion == EstadoFuncion.VIGENTE) {
                Button cancelar = new Button("Cancelar reserva");
                cancelar.setOnAction(e -> cancelar());
                botones.getChildren().add(0, cancelar);
            } else if (estadoFuncion == EstadoFuncion.EN_EMISION) {
                aviso = Componentes.nota("La función está en emisión: la reserva no se puede cancelar "
                        + "por separado, solo cancelando la función completa.");
            } else if (estadoFuncion == EstadoFuncion.FINALIZADA) {
                aviso = Componentes.nota("La función ya finalizó: la reserva no se puede cancelar.");
            }
        }

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(
                Componentes.titulo(reserva.getId() + " · " + Formato.estadoReserva(reserva.getEstado())),
                new Label("Cliente: " + reserva.getNombreCliente()
                        + " · Identificación: " + reserva.getCliente().getId()),
                new Label("Película: " + funcion.getPelicula().getTitulo()),
                new Label("Función: " + Formato.funcion(funcion)),
                entradas,
                Componentes.resaltado("Importe confirmado: " + Formato.dinero(reserva.getImporteTotal())));
        if (aviso != null) {
            pantalla.getChildren().add(aviso);
        }
        pantalla.getChildren().add(botones);
        return pantalla;
    }

    private void cancelar() {
        Reserva reserva = ventana.buscarReserva(codigo);
        if (reserva == null) {
            ventana.mostrarDetalleReserva(codigo);
            return;
        }
        String mensaje = describirLiberacion(reserva.getButacas()) + " La reserva quedará en el historial.";
        boolean aceptado = Dialogos.confirmar("¿Cancelar la reserva " + codigo + "?", mensaje,
                "Sí, cancelar", "Conservar reserva");
        if (!aceptado) {
            return;
        }
        try {
            ventana.getReservaService().cancelar(codigo);
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
        ventana.mostrarDetalleReserva(codigo);
    }

    // Ejemplos: "Se liberará B3." o "Se liberarán B2, B3 y B4."
    private String describirLiberacion(List<Butaca> butacas) {
        if (butacas.size() == 1) {
            return "Se liberará " + butacas.get(0).getCodigo() + ".";
        }
        StringBuilder texto = new StringBuilder("Se liberarán ");
        for (int i = 0; i < butacas.size(); i++) {
            if (i > 0 && i == butacas.size() - 1) {
                texto.append(" y ");
            } else if (i > 0) {
                texto.append(", ");
            }
            texto.append(butacas.get(i).getCodigo());
        }
        texto.append(".");
        return texto.toString();
    }
}
