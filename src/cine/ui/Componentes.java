package cine.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class Componentes {

    public static Label titulo(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        return etiqueta;
    }

    public static Label subtitulo(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-size: 14px; -fx-text-fill: #444444;");
        return etiqueta;
    }

    public static Label nota(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;");
        etiqueta.setWrapText(true);
        return etiqueta;
    }

    public static Label resaltado(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        return etiqueta;
    }

    public static Button botonPrincipal(String texto) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-font-weight: bold;");
        return boton;
    }

    // Contenedor común de las pantallas: columna con margen y separación entre elementos.
    public static VBox pantalla() {
        VBox caja = new VBox(12);
        caja.setPadding(new Insets(20));
        caja.setAlignment(Pos.TOP_LEFT);
        return caja;
    }

    // Las tablas se arman con una grilla: una fila de encabezados y una fila por elemento.
    public static GridPane tabla(String... encabezados) {
        GridPane tabla = new GridPane();
        tabla.setHgap(24);
        tabla.setVgap(8);
        for (int i = 0; i < encabezados.length; i++) {
            Label encabezado = new Label(encabezados[i]);
            encabezado.setStyle("-fx-font-weight: bold; -fx-border-color: transparent transparent #888888 transparent;"
                    + " -fx-padding: 0 0 4 0;");
            encabezado.setMaxWidth(Double.MAX_VALUE);
            tabla.add(encabezado, i, 0);
        }
        return tabla;
    }

    public static void agregarFila(GridPane tabla, Node... celdas) {
        int fila = tabla.getRowCount();
        for (int i = 0; i < celdas.length; i++) {
            GridPane.setValignment(celdas[i], VPos.CENTER);
            tabla.add(celdas[i], i, fila);
        }
    }
}
