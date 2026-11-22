package cine;

import cine.excepciones.PersistenciaException;
import cine.persistencia.ExportadorReservas;
import cine.persistencia.GestorEstado;
import cine.persistencia.PersistenciaCine;
import cine.servicios.FuncionService;
import cine.servicios.PeliculaService;
import cine.servicios.ReservaService;
import cine.servicios.SalaService;
import cine.ui.VentanaPrincipal;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class CineApp extends Application {

    private GestorEstado gestor;
    private FuncionService funcionService;
    private PeliculaService peliculaService;
    private SalaService salaService;
    private ReservaService reservaService;

    @Override
    public void start(Stage ventana) {
        // El archivo de datos se guarda en la carpeta "datos" dentro de la carpeta del proyecto.
        gestor = new GestorEstado(new PersistenciaCine(Paths.get("datos", "cine.dat")));
        try {
            gestor.iniciar();
        } catch (PersistenciaException e) {
            mostrarErrorYCerrar(e.getMessage());
            return;
        }
        funcionService = new FuncionService(gestor);
        peliculaService = new PeliculaService(gestor, funcionService);
        salaService = new SalaService(gestor, funcionService);
        reservaService = new ReservaService(gestor, new ExportadorReservas());

        VentanaPrincipal principal = new VentanaPrincipal(peliculaService, salaService, funcionService,
                reservaService, gestor);
        ventana.setTitle("Reservas de cine");
        ventana.setScene(new Scene(principal.crearVista(), 1050, 680));
        ventana.show();
    }

    // La aplicación se cierra sin escribir nada: el archivo queda como estaba para poder revisarlo.
    private void mostrarErrorYCerrar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Reservas de cine");
        alerta.setHeaderText("No se pudieron cargar los datos del sistema.");
        alerta.setContentText(mensaje + "\n\nLa aplicación se va a cerrar sin modificar el archivo. "
                + "Si el archivo está dañado, hay que moverlo o borrarlo para volver a empezar "
                + "con los datos de demostración.");
        alerta.showAndWait();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
