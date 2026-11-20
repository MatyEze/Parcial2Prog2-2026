package cine;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CineApp extends Application {

    @Override
    public void start(Stage ventana) {
        StackPane raiz = new StackPane(new Label("Reservas de cine"));
        ventana.setTitle("Reservas de cine");
        ventana.setScene(new Scene(raiz, 800, 600));
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
