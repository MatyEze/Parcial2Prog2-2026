package cine.ui;

import cine.modelo.Butaca;
import cine.modelo.Funcion;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

// Pantalla 02: grilla de butacas de la sala con su estado para la función elegida.
public class PantallaSeleccionButacas {

    // Los tres estados se distinguen por el relleno, el grosor del borde y el tipo de letra,
    // no solo por el color: disponible es blanca, seleccionada oscura y en negrita,
    // ocupada queda deshabilitada y con el código tachado. El relleno interno compensa
    // el grosor del borde para que todas las butacas midan lo mismo.
    private static final String ESTILO_DISPONIBLE = "-fx-background-color: white; -fx-text-fill: black;"
            + " -fx-border-color: #555555; -fx-border-width: 1; -fx-padding: 6 8 6 8;";
    private static final String ESTILO_SELECCIONADA = "-fx-background-color: #2b2b2b; -fx-text-fill: white;"
            + " -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 3; -fx-padding: 4 6 4 6;";
    private static final String ESTILO_OCUPADA = "-fx-background-color: #cccccc;"
            + " -fx-border-color: #999999; -fx-border-width: 1; -fx-padding: 6 8 6 8;";
    private static final double ANCHO_BUTACA = 52;

    private VentanaPrincipal ventana;
    private ReservaEnCurso reserva;
    private Label butacas;
    private Label cantidad;
    private Button continuar;

    public PantallaSeleccionButacas(VentanaPrincipal ventana, ReservaEnCurso reserva) {
        this.ventana = ventana;
        this.reserva = reserva;
    }

    public Parent crearVista(Funcion funcion) {
        Set<Butaca> ocupadas = ventana.getReservaService().butacasOcupadas(funcion);
        quitarSeleccionadasOcupadas(ocupadas);

        VBox sala = new VBox(14, Componentes.indicadorPantalla(), crearGrilla(funcion, ocupadas), crearLeyenda());

        BorderPane contenido = new BorderPane();
        contenido.setCenter(sala);
        contenido.setRight(crearResumen(funcion));
        BorderPane.setMargin(contenido.getRight(), new Insets(0, 0, 0, 24));

        Button volver = new Button("← Volver");
        volver.setOnAction(e -> volver());

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(
                Componentes.titulo("Selección de butacas"),
                Componentes.subtitulo(funcion.getPelicula().getTitulo() + " · " + Formato.funcion(funcion)),
                contenido,
                volver);
        actualizarResumen();
        return pantalla;
    }

    // Una butaca que el usuario había elegido pudo ocuparse mientras tanto (por ejemplo,
    // si la confirmación fue rechazada); se quita de la selección y se avisa.
    private void quitarSeleccionadasOcupadas(Set<Butaca> ocupadas) {
        List<String> quitadas = new ArrayList<>();
        for (Butaca butaca : ocupadas) {
            if (reserva.estaSeleccionada(butaca.getCodigo())) {
                reserva.quitar(butaca.getCodigo());
                quitadas.add(butaca.getCodigo());
            }
        }
        if (!quitadas.isEmpty()) {
            Dialogos.mostrarInformacion("Butacas ya ocupadas",
                    "Estas butacas ya no están disponibles y se quitaron de la selección: "
                    + String.join(", ", quitadas) + ".");
        }
    }

    private GridPane crearGrilla(Funcion funcion, Set<Butaca> ocupadas) {
        GridPane grilla = new GridPane();
        grilla.setHgap(6);
        grilla.setVgap(6);
        grilla.setAlignment(Pos.CENTER);
        int fila = -1;
        char filaActual = 0;
        for (Butaca butaca : funcion.getSala().getButacas()) {
            if (butaca.getFila() != filaActual) {
                filaActual = butaca.getFila();
                fila++;
                Label letra = new Label(String.valueOf(filaActual));
                letra.setStyle("-fx-font-weight: bold;");
                letra.setMinWidth(20);
                grilla.add(letra, 0, fila);
            }
            grilla.add(crearBotonButaca(butaca, ocupadas.contains(butaca)), butaca.getNumero(), fila);
        }
        return grilla;
    }

    private Button crearBotonButaca(Butaca butaca, boolean ocupada) {
        String codigo = butaca.getCodigo();
        Button boton = new Button();
        boton.setMinWidth(ANCHO_BUTACA);
        boton.setPrefWidth(ANCHO_BUTACA);
        if (ocupada) {
            // El texto tachado se arma con un Text porque un botón no permite tachar su propio texto.
            Text tachado = new Text(codigo);
            tachado.setStrikethrough(true);
            boton.setGraphic(tachado);
            boton.setStyle(ESTILO_OCUPADA);
            boton.setDisable(true);
        } else {
            boton.setText(codigo);
            boton.setOnAction(e -> alternarButaca(codigo, boton));
            actualizarBoton(codigo, boton);
        }
        return boton;
    }

    private void volver() {
        if (reserva.isAbiertaDesdeFunciones()) {
            ventana.mostrarFunciones();
        } else {
            ventana.mostrarCartelera();
        }
    }

    private void alternarButaca(String codigo, Button boton) {
        if (reserva.estaSeleccionada(codigo)) {
            reserva.quitar(codigo);
        } else {
            reserva.seleccionar(codigo);
        }
        actualizarBoton(codigo, boton);
        actualizarResumen();
    }

    private void actualizarBoton(String codigo, Button boton) {
        if (reserva.estaSeleccionada(codigo)) {
            boton.setStyle(ESTILO_SELECCIONADA);
        } else {
            boton.setStyle(ESTILO_DISPONIBLE);
        }
    }

    private HBox crearLeyenda() {
        Button disponible = new Button("A1");
        disponible.setStyle(ESTILO_DISPONIBLE);
        Button seleccionada = new Button("A1");
        seleccionada.setStyle(ESTILO_SELECCIONADA);
        Text tachado = new Text("A1");
        tachado.setStrikethrough(true);
        Button ocupada = new Button();
        ocupada.setGraphic(tachado);
        ocupada.setStyle(ESTILO_OCUPADA);
        // Los botones de la leyenda son solo una muestra y no responden al mouse.
        disponible.setMouseTransparent(true);
        seleccionada.setMouseTransparent(true);
        ocupada.setMouseTransparent(true);
        disponible.setFocusTraversable(false);
        seleccionada.setFocusTraversable(false);
        ocupada.setFocusTraversable(false);

        HBox leyenda = new HBox(8, disponible, new Label("Blanca: disponible"),
                seleccionada, new Label("Oscura: seleccionada"),
                ocupada, new Label("Tachada: ocupada"));
        leyenda.setAlignment(Pos.CENTER);
        return leyenda;
    }

    private VBox crearResumen(Funcion funcion) {
        butacas = new Label();
        butacas.setWrapText(true);
        cantidad = new Label();
        continuar = Componentes.botonPrincipal("Continuar →");
        continuar.setOnAction(e -> ventana.mostrarClienteEntradas(reserva));

        VBox resumen = new VBox(10,
                Componentes.resaltado("Resumen de selección"),
                butacas,
                cantidad,
                new Label("Precio general: " + Formato.dinero(funcion.getPrecio())),
                continuar,
                Componentes.nota("Se reserva al confirmar, no al seleccionar."));
        resumen.setPadding(new Insets(12));
        resumen.setPrefWidth(230);
        resumen.setStyle("-fx-border-color: #bbbbbb; -fx-border-radius: 4;");
        return resumen;
    }

    private void actualizarResumen() {
        List<String> codigos = reserva.getCodigos();
        if (codigos.isEmpty()) {
            butacas.setText("Butacas: ninguna");
        } else {
            butacas.setText("Butacas: " + String.join(", ", codigos));
        }
        cantidad.setText("Cantidad: " + codigos.size());
        // Es un control de pantalla: sin butacas no hay nada que continuar.
        continuar.setDisable(codigos.isEmpty());
    }
}
