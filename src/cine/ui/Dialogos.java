package cine.ui;

import javafx.scene.control.Alert;
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
