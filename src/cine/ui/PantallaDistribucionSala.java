package cine.ui;

import cine.modelo.Butaca;
import cine.modelo.Sala;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

// Distribución de la sala (desde la pantalla 10): códigos de las butacas por fila y número.
// Es una vista estructural, no muestra ocupación porque no depende de ninguna función.
public class PantallaDistribucionSala {

    private static final String ESTILO_BUTACA = "-fx-border-color: #555555; -fx-border-width: 1;"
            + " -fx-padding: 6 8 6 8;";
    private static final double ANCHO_BUTACA = 52;

    private VentanaPrincipal ventana;

    public PantallaDistribucionSala(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista(Sala sala) {
        VBox plano = new VBox(14, Componentes.indicadorPantalla(), crearGrilla(sala));
        plano.setMaxWidth(Region.USE_PREF_SIZE);

        Button volver = new Button("← Volver a salas");
        volver.setOnAction(e -> ventana.mostrarSalas());

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(
                Componentes.titulo("Distribución · " + sala.getNombre()),
                Componentes.subtitulo(sala.getFilas() + " filas · " + sala.getButacasPorFila()
                        + " butacas por fila · Capacidad: " + sala.getCapacidad() + " butacas"),
                plano,
                Componentes.nota("Vista de la ubicación de las butacas; no muestra la ocupación de ninguna función."),
                volver);
        return pantalla;
    }

    private GridPane crearGrilla(Sala sala) {
        GridPane grilla = new GridPane();
        grilla.setHgap(6);
        grilla.setVgap(6);
        grilla.setAlignment(Pos.CENTER);
        int fila = -1;
        char filaActual = 0;
        for (Butaca butaca : sala.getButacas()) {
            if (butaca.getFila() != filaActual) {
                filaActual = butaca.getFila();
                fila++;
                Label letra = new Label(String.valueOf(filaActual));
                letra.setStyle("-fx-font-weight: bold;");
                letra.setMinWidth(20);
                grilla.add(letra, 0, fila);
            }
            Label codigo = new Label(butaca.getCodigo());
            codigo.setStyle(ESTILO_BUTACA);
            codigo.setMinWidth(ANCHO_BUTACA);
            codigo.setAlignment(Pos.CENTER);
            grilla.add(codigo, butaca.getNumero(), fila);
        }
        return grilla;
    }
}
