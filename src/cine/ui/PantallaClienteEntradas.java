package cine.ui;

import cine.modelo.Butaca;
import cine.modelo.Entrada;
import cine.modelo.Funcion;
import cine.modelo.TipoEntrada;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Pantalla 03: datos del cliente y tipo de entrada de cada butaca seleccionada.
public class PantallaClienteEntradas {

    private VentanaPrincipal ventana;
    private ReservaEnCurso reserva;
    private TextField identificacion;
    private TextField nombre;
    // Etiqueta de importe de cada fila, por código de butaca.
    private Map<String, Label> importes;
    private Label total;

    public PantallaClienteEntradas(VentanaPrincipal ventana, ReservaEnCurso reserva) {
        this.ventana = ventana;
        this.reserva = reserva;
    }

    public Parent crearVista(Funcion funcion) {
        identificacion = new TextField(reserva.getIdentificacion());
        identificacion.setPromptText("Solo números, sin puntos");
        nombre = new TextField(reserva.getNombre());
        nombre.setPrefColumnCount(24);

        GridPane datos = new GridPane();
        datos.setHgap(10);
        datos.setVgap(8);
        datos.add(new Label("Identificación"), 0, 0);
        datos.add(identificacion, 1, 0);
        datos.add(new Label("Nombre y apellido"), 0, 1);
        datos.add(nombre, 1, 1);

        importes = new HashMap<>();
        GridPane tabla = Componentes.tabla("Butaca", "Tipo de entrada", "Importe");
        for (String codigo : reserva.getCodigos()) {
            ComboBox<String> tipo = new ComboBox<>();
            // Las opciones siguen el orden de TipoEntrada, así la posición elegida indica el tipo.
            for (TipoEntrada valor : TipoEntrada.values()) {
                tipo.getItems().add(Formato.tipoEntrada(valor));
            }
            tipo.getSelectionModel().select(reserva.getTipo(codigo).ordinal());
            tipo.setOnAction(e -> cambiarTipo(codigo, tipo, funcion));
            Label importe = new Label();
            importes.put(codigo, importe);
            Componentes.agregarFila(tabla, new Label(codigo), tipo, importe);
        }

        total = Componentes.resaltado("");
        actualizarImportes(funcion);

        Button butacas = new Button("← Butacas");
        butacas.setOnAction(e -> volverAButacas());
        Button revisar = Componentes.botonPrincipal("Revisar reserva →");
        revisar.setOnAction(e -> revisar());
        HBox botones = new HBox(10, butacas, revisar);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(
                Componentes.titulo("Cliente y entradas"),
                Componentes.subtitulo(funcion.getPelicula().getTitulo() + " · " + Formato.funcion(funcion)),
                datos,
                tabla,
                Componentes.nota("Estudiante: descuento ilustrativo del 20 %."),
                total,
                botones);
        return pantalla;
    }

    private void cambiarTipo(String codigo, ComboBox<String> tipo, Funcion funcion) {
        reserva.cambiarTipo(codigo, TipoEntrada.values()[tipo.getSelectionModel().getSelectedIndex()]);
        actualizarImportes(funcion);
    }

    // Los importes se obtienen creando la entrada que se va a confirmar, así el descuento
    // queda definido en un solo lugar del modelo.
    private void actualizarImportes(Funcion funcion) {
        double suma = 0;
        for (Butaca butaca : funcion.getSala().getButacas()) {
            String codigo = butaca.getCodigo();
            if (reserva.estaSeleccionada(codigo)) {
                double precio = Entrada.crear(reserva.getTipo(codigo), butaca, funcion.getPrecio())
                        .getPrecioConfirmado();
                importes.get(codigo).setText(Formato.dinero(precio));
                suma += precio;
            }
        }
        total.setText("Total: " + Formato.dinero(suma));
    }

    private void guardarDatosCliente() {
        reserva.setIdentificacion(identificacion.getText());
        reserva.setNombre(nombre.getText());
    }

    private void volverAButacas() {
        guardarDatosCliente();
        ventana.mostrarSeleccionButacas(reserva);
    }

    private void revisar() {
        guardarDatosCliente();
        ventana.mostrarRevision(reserva);
    }
}
