package cine.ui;

import cine.modelo.Entrada;
import cine.modelo.Funcion;
import cine.modelo.Reserva;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Pantalla 05: comprobante de la reserva recién confirmada.
public class PantallaComprobante {

    private VentanaPrincipal ventana;

    public PantallaComprobante(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista(Reserva reserva) {
        Funcion funcion = reserva.getFuncion();
        VBox entradas = new VBox(4);
        for (Entrada entrada : reserva.getEntradas()) {
            entradas.getChildren().add(new Label(entrada.getButaca().getCodigo() + " · "
                    + Formato.tipoEntrada(entrada.getTipo()) + " · "
                    + Formato.dinero(entrada.getPrecioConfirmado())));
        }

        Button nueva = Componentes.botonPrincipal("Nueva reserva");
        nueva.setOnAction(e -> ventana.mostrarCartelera());
        // Se habilita cuando exista la consulta de reservas.
        Button verReservas = new Button("Ver reservas");
        verReservas.setDisable(true);
        HBox botones = new HBox(10, nueva, verReservas);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(
                Componentes.titulo("Reserva confirmada · " + reserva.getId()),
                new Label("Película: " + funcion.getPelicula().getTitulo()),
                new Label("Función: " + Formato.funcion(funcion)),
                new Label("Cliente: " + reserva.getNombreCliente()
                        + " · Identificación: " + reserva.getCliente().getId()),
                entradas,
                Componentes.resaltado("Importe confirmado: " + Formato.dinero(reserva.getImporteTotal())),
                botones);
        return pantalla;
    }
}
