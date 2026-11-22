package cine.ui;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;

public class Dialogos {

    private static final String TITULO = "Reservas de cine";

    // Un error de negocio es una operación rechazada por una regla del sistema:
    // los datos quedan como estaban y el usuario puede corregirlos.
    public static void mostrarErrorNegocio(String mensaje) {
        mostrar(Alert.AlertType.WARNING, "No se puede realizar la operación", mensaje);
    }

    // Un error de datos es una falla al leer o escribir el archivo, no un error del usuario.
    public static void mostrarErrorDatos(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "Error al guardar o leer los datos", mensaje);
    }

    public static void mostrarInformacion(String encabezado, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, encabezado, mensaje);
    }

    // Devuelve true solo si el usuario elige la opción de aceptar. La opción de rechazar
    // queda como botón por defecto porque las confirmaciones son para operaciones definitivas.
    public static boolean confirmar(String encabezado, String mensaje, String textoAceptar, String textoRechazar) {
        ButtonType aceptar = new ButtonType(textoAceptar, ButtonBar.ButtonData.OK_DONE);
        ButtonType rechazar = new ButtonType(textoRechazar, ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, aceptar, rechazar);
        alerta.setTitle(TITULO);
        alerta.setHeaderText(encabezado);
        alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ((Button) alerta.getDialogPane().lookupButton(aceptar)).setDefaultButton(false);
        ((Button) alerta.getDialogPane().lookupButton(rechazar)).setDefaultButton(true);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == aceptar;
    }

    // Devuelve el texto escrito, o null si el usuario cancela.
    public static String pedirTexto(String encabezado, String etiqueta, String valorInicial) {
        TextInputDialog dialogo = new TextInputDialog(valorInicial);
        dialogo.setTitle(TITULO);
        dialogo.setHeaderText(encabezado);
        dialogo.setContentText(etiqueta);
        Optional<String> respuesta = dialogo.showAndWait();
        if (respuesta.isPresent()) {
            return respuesta.get();
        }
        return null;
    }

    private static void mostrar(Alert.AlertType tipo, String encabezado, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(TITULO);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);
        // Sin esto los mensajes largos se cortan en lugar de agrandar el cuadro.
        alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alerta.showAndWait();
    }
}
