package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

// Pantalla 11: registro de una sala nueva.
public class PantallaRegistroSala {

    // Las filas se nombran con una letra; si hay más filas que letras no se puede mostrar
    // el rango de filas, y el servicio rechaza la sala al guardar.
    private static final int LETRAS_DE_FILA = 'Z' - 'A' + 1;

    private VentanaPrincipal ventana;
    private TextField nombre;
    private TextField filas;
    private TextField butacasPorFila;
    private Label capacidad;

    public PantallaRegistroSala(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista() {
        nombre = new TextField();
        nombre.setPrefColumnCount(20);
        filas = new TextField();
        filas.setPromptText("Por ejemplo, 4");
        butacasPorFila = new TextField();
        butacasPorFila.setPromptText("Por ejemplo, 6");
        filas.textProperty().addListener((observable, anterior, nuevo) -> actualizarCapacidad());
        butacasPorFila.textProperty().addListener((observable, anterior, nuevo) -> actualizarCapacidad());

        GridPane datos = Componentes.formulario();
        Componentes.agregarCampo(datos, "Nombre", nombre);
        Componentes.agregarCampo(datos, "Cantidad de filas", filas);
        Componentes.agregarCampo(datos, "Butacas por fila", butacasPorFila);

        capacidad = Componentes.resaltado("");
        actualizarCapacidad();

        Button cancelar = new Button("Cancelar");
        cancelar.setOnAction(e -> ventana.mostrarSalas());
        Button guardar = Componentes.botonPrincipal("Guardar sala");
        guardar.setOnAction(e -> guardar());

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo("Registrar sala"), datos, capacidad,
                Componentes.filaBotones(cancelar, guardar));
        return pantalla;
    }

    // Es solo una ayuda visual mientras se escribe; los límites los valida el servicio al guardar.
    private void actualizarCapacidad() {
        Integer cantidadFilas = Formato.leerEntero(filas.getText());
        Integer porFila = Formato.leerEntero(butacasPorFila.getText());
        if (cantidadFilas == null || porFila == null || cantidadFilas <= 0 || porFila <= 0) {
            capacidad.setText("");
            capacidad.setVisible(false);
            return;
        }
        long total = (long) cantidadFilas * porFila;
        String texto = "Capacidad resultante: " + total + " butacas";
        if (cantidadFilas <= LETRAS_DE_FILA) {
            texto += " · Filas A a " + (char) ('A' + cantidadFilas - 1);
        }
        texto += " · Números 1 a " + porFila;
        capacidad.setText(texto);
        capacidad.setVisible(true);
    }

    private void guardar() {
        Integer cantidadFilas = Formato.leerEntero(filas.getText());
        if (cantidadFilas == null) {
            Dialogos.mostrarErrorNegocio("La cantidad de filas tiene que ser un número entero, por ejemplo 4.");
            return;
        }
        Integer porFila = Formato.leerEntero(butacasPorFila.getText());
        if (porFila == null) {
            Dialogos.mostrarErrorNegocio("La cantidad de butacas por fila tiene que ser un número entero, "
                    + "por ejemplo 6.");
            return;
        }
        try {
            ventana.getSalaService().registrar(nombre.getText(), cantidadFilas, porFila);
            ventana.mostrarSalas();
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
    }
}
