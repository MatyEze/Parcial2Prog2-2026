package cine.ui;

import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Pantalla 01: funciones vigentes con filtros por película y fecha.
public class PantallaCartelera {

    private VentanaPrincipal ventana;
    private ComboBox<String> peliculas;
    // Ids de las películas en el mismo orden que el selector; la posición 0 es "Todas las películas".
    private List<Integer> idsPeliculas;
    private DatePicker fecha;
    private VBox resultados;

    public PantallaCartelera(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista() {
        peliculas = new ComboBox<>();
        idsPeliculas = new ArrayList<>();
        peliculas.getItems().add("Todas las películas");
        idsPeliculas.add(null);
        for (Pelicula pelicula : ventana.getPeliculaService().listarActivas()) {
            peliculas.getItems().add(pelicula.getTitulo());
            idsPeliculas.add(pelicula.getId());
        }
        peliculas.getSelectionModel().selectFirst();

        fecha = new DatePicker();
        fecha.setPromptText("Cualquier fecha");

        Button buscar = Componentes.botonPrincipal("Buscar");
        buscar.setOnAction(e -> buscar());
        Button limpiar = new Button("Limpiar");
        limpiar.setOnAction(e -> limpiar());

        HBox filtros = new HBox(10, new Label("Película"), peliculas, new Label("Fecha"), fecha, buscar, limpiar);
        filtros.setAlignment(Pos.CENTER_LEFT);

        resultados = new VBox();

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo("Cartelera"), filtros, resultados);
        buscar();
        return pantalla;
    }

    private void buscar() {
        Integer idPelicula = idsPeliculas.get(peliculas.getSelectionModel().getSelectedIndex());
        LocalDate dia = fecha.getValue();
        List<Funcion> funciones = ventana.getFuncionService().buscarEnCartelera(idPelicula, dia);

        resultados.getChildren().clear();
        if (funciones.isEmpty()) {
            resultados.getChildren().add(Componentes.nota("No hay funciones para estos filtros"));
            return;
        }
        GridPane tabla = Componentes.tabla("Película", "Sala", "Horario", "Precio", "Acción");
        for (Funcion funcion : funciones) {
            int idFuncion = funcion.getId();
            Button reservar = new Button("Reservar →");
            reservar.setOnAction(e -> reservar(idFuncion));
            Componentes.agregarFila(tabla,
                    new Label(funcion.getPelicula().getTitulo()),
                    new Label(funcion.getSala().getNombre()),
                    new Label(Formato.fecha(funcion.getInicio()) + " " + Formato.hora(funcion.getInicio())),
                    new Label(Formato.dinero(funcion.getPrecio())),
                    reservar);
        }
        resultados.getChildren().add(tabla);
    }

    private void limpiar() {
        peliculas.getSelectionModel().selectFirst();
        fecha.setValue(null);
        buscar();
    }

    private void reservar(int idFuncion) {
        ventana.mostrarSeleccionButacas(new ReservaEnCurso(idFuncion));
    }
}
