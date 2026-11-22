package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import cine.modelo.Sala;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

// Pantalla 13: programación de una función nueva.
public class PantallaFormularioFuncion {

    private VentanaPrincipal ventana;
    private ComboBox<String> peliculas;
    // Ids en el mismo orden que las opciones de cada selector.
    private List<Integer> idsPeliculas;
    private ComboBox<String> salas;
    private List<Integer> idsSalas;
    private DatePicker fecha;
    private TextField inicio;
    private TextField precio;
    private Label finalizacion;
    private Label salaLibre;
    private Label ayuda;

    public PantallaFormularioFuncion(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista() {
        peliculas = new ComboBox<>();
        idsPeliculas = new ArrayList<>();
        for (Pelicula pelicula : ventana.getPeliculaService().listarActivas()) {
            peliculas.getItems().add(pelicula.getTitulo());
            idsPeliculas.add(pelicula.getId());
        }
        peliculas.setPromptText("Elegir película");
        salas = new ComboBox<>();
        idsSalas = new ArrayList<>();
        for (Sala sala : ventana.getSalaService().listarHabilitadas()) {
            salas.getItems().add(sala.getNombre());
            idsSalas.add(sala.getId());
        }
        salas.setPromptText("Elegir sala");
        fecha = new DatePicker();
        // La fecha se elige con el calendario para no tener que interpretar texto escrito a mano.
        fecha.setEditable(false);
        inicio = new TextField();
        inicio.setPromptText("HH:mm, por ejemplo 18:00");
        precio = new TextField();
        precio.setPromptText("Por ejemplo, 5000 o 5000,50");

        peliculas.setOnAction(e -> actualizarFinalizacion());
        salas.setOnAction(e -> actualizarFinalizacion());
        fecha.valueProperty().addListener((observable, anterior, nuevo) -> actualizarFinalizacion());
        inicio.textProperty().addListener((observable, anterior, nuevo) -> actualizarFinalizacion());

        GridPane datos = Componentes.formulario();
        Componentes.agregarCampo(datos, "Película", peliculas);
        Componentes.agregarCampo(datos, "Sala", salas);
        Componentes.agregarCampo(datos, "Fecha", fecha);
        Componentes.agregarCampo(datos, "Inicio", inicio);
        Componentes.agregarCampo(datos, "Precio general", precio);

        finalizacion = Componentes.resaltado("");
        salaLibre = new Label();
        ayuda = Componentes.nota("Elija película, sala, fecha e inicio para ver la finalización calculada.");
        actualizarFinalizacion();

        Button cancelar = new Button("Cancelar");
        cancelar.setOnAction(e -> ventana.mostrarFunciones());
        Button guardar = Componentes.botonPrincipal("Guardar función");
        guardar.setOnAction(e -> guardar());

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo("Registrar función"), datos, finalizacion, salaLibre,
                ayuda, Componentes.filaBotones(cancelar, guardar));
        return pantalla;
    }

    // El fin y el momento en que la sala queda libre se calculan con una función provisoria
    // del modelo, para no repetir en la pantalla la duración ni el margen de limpieza.
    private void actualizarFinalizacion() {
        Pelicula pelicula = peliculaElegida();
        Sala sala = salaElegida();
        LocalDate dia = fecha.getValue();
        LocalTime hora = Formato.leerHora(inicio.getText());
        boolean completo = pelicula != null && sala != null && dia != null && hora != null;
        finalizacion.setVisible(completo);
        salaLibre.setVisible(completo);
        ayuda.setVisible(!completo);
        if (!completo) {
            finalizacion.setText("");
            salaLibre.setText("");
            return;
        }
        Funcion provisoria = new Funcion(0, pelicula, sala, LocalDateTime.of(dia, hora), 0);
        finalizacion.setText("Finalización calculada: " + describirMomento(provisoria.getFin(), dia)
                + " · Duración: " + pelicula.getDuracionMinutos() + " minutos");
        salaLibre.setText("La sala queda libre desde las " + describirMomento(provisoria.getSalaLibreDesde(), dia)
                + ".");
    }

    // Si el momento cae otro día que el inicio, se agrega la fecha.
    private String describirMomento(LocalDateTime momento, LocalDate diaInicio) {
        if (momento.toLocalDate().equals(diaInicio)) {
            return Formato.hora(momento);
        }
        return Formato.hora(momento) + " del " + Formato.fecha(momento);
    }

    // Los objetos se buscan de nuevo por id porque el estado pudo recargarse.
    private Pelicula peliculaElegida() {
        int posicion = peliculas.getSelectionModel().getSelectedIndex();
        if (posicion < 0) {
            return null;
        }
        return ventana.buscarPelicula(idsPeliculas.get(posicion));
    }

    private Sala salaElegida() {
        int posicion = salas.getSelectionModel().getSelectedIndex();
        if (posicion < 0) {
            return null;
        }
        return ventana.buscarSala(idsSalas.get(posicion));
    }

    // Solo se controla que cada dato esté elegido o tenga el formato esperado; las reglas
    // (fecha futura, superposición, precio positivo) las valida el servicio.
    private void guardar() {
        int posicionPelicula = peliculas.getSelectionModel().getSelectedIndex();
        if (posicionPelicula < 0) {
            Dialogos.mostrarErrorNegocio("Elija la película de la función.");
            return;
        }
        int posicionSala = salas.getSelectionModel().getSelectedIndex();
        if (posicionSala < 0) {
            Dialogos.mostrarErrorNegocio("Elija la sala de la función.");
            return;
        }
        LocalDate dia = fecha.getValue();
        if (dia == null) {
            Dialogos.mostrarErrorNegocio("Elija la fecha de la función.");
            return;
        }
        LocalTime hora = Formato.leerHora(inicio.getText());
        if (hora == null) {
            Dialogos.mostrarErrorNegocio("El inicio tiene que tener el formato HH:mm, por ejemplo 18:00.");
            return;
        }
        Double precioGeneral = Formato.leerDecimal(precio.getText());
        if (precioGeneral == null) {
            Dialogos.mostrarErrorNegocio("El precio general tiene que ser un número, por ejemplo 5000 o 5000,50 "
                    + "(sin puntos y con coma para los decimales).");
            return;
        }
        try {
            ventana.getFuncionService().programar(idsPeliculas.get(posicionPelicula), idsSalas.get(posicionSala),
                    LocalDateTime.of(dia, hora), precioGeneral);
            ventana.mostrarFunciones();
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
    }
}
