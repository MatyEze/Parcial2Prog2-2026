package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.modelo.Funcion;
import cine.modelo.Reserva;
import cine.persistencia.GestorEstado;
import cine.servicios.FuncionService;
import cine.servicios.PeliculaService;
import cine.servicios.ReservaService;
import cine.servicios.SalaService;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Ventana con el encabezado, el menú lateral y la pantalla actual en el centro.
// Las pantallas le piden a esta clase que muestre la pantalla siguiente.
public class VentanaPrincipal {

    private PeliculaService peliculaService;
    private SalaService salaService;
    private FuncionService funcionService;
    private ReservaService reservaService;
    // Solo se usa para saber si después de un error al guardar hay que cerrar la aplicación.
    private GestorEstado gestor;
    private BorderPane raiz;

    public VentanaPrincipal(PeliculaService peliculaService, SalaService salaService,
            FuncionService funcionService, ReservaService reservaService, GestorEstado gestor) {
        this.peliculaService = peliculaService;
        this.salaService = salaService;
        this.funcionService = funcionService;
        this.reservaService = reservaService;
        this.gestor = gestor;
    }

    public Parent crearVista() {
        raiz = new BorderPane();
        raiz.setTop(crearEncabezado());
        raiz.setLeft(crearMenu());
        mostrarCartelera();
        return raiz;
    }

    public PeliculaService getPeliculaService() {
        return peliculaService;
    }

    public SalaService getSalaService() {
        return salaService;
    }

    public FuncionService getFuncionService() {
        return funcionService;
    }

    public ReservaService getReservaService() {
        return reservaService;
    }

    public void mostrarCartelera() {
        mostrar(new PantallaCartelera(this).crearVista());
    }

    public void mostrarSeleccionButacas(ReservaEnCurso reserva) {
        Funcion funcion = buscarFuncionReservable(reserva.getIdFuncion());
        if (funcion != null) {
            mostrar(new PantallaSeleccionButacas(this, reserva).crearVista(funcion));
        }
    }

    public void mostrarClienteEntradas(ReservaEnCurso reserva) {
        Funcion funcion = buscarFuncionReservable(reserva.getIdFuncion());
        if (funcion != null) {
            mostrar(new PantallaClienteEntradas(this, reserva).crearVista(funcion));
        }
    }

    public void mostrarRevision(ReservaEnCurso reserva) {
        Funcion funcion = buscarFuncionReservable(reserva.getIdFuncion());
        if (funcion != null) {
            mostrar(new PantallaRevision(this, reserva).crearVista(funcion));
        }
    }

    public void mostrarComprobante(Reserva reserva) {
        mostrar(new PantallaComprobante(this).crearVista(reserva));
    }

    // La función se vuelve a consultar cada vez porque el estado pudo recargarse
    // después de un error al guardar. Si ya no se puede reservar, se informa y se vuelve
    // a la cartelera; la validación definitiva la hace el servicio al confirmar.
    public Funcion buscarFuncionReservable(int idFuncion) {
        Funcion funcion = funcionService.buscar(idFuncion);
        if (funcion == null || !funcion.estaVigente(LocalDateTime.now())) {
            Dialogos.mostrarInformacion("La función ya no está disponible",
                    "La función seleccionada ya no existe, fue cancelada o ya comenzó. "
                    + "Se vuelve a la cartelera.");
            mostrarCartelera();
            return null;
        }
        return funcion;
    }

    // El gestor ya volvió al último estado guardado; si tampoco pudo leerlo,
    // los datos en memoria no son confiables y la aplicación se cierra.
    public void informarErrorPersistencia(PersistenciaException e) {
        if (gestor.isEstadoPerdido()) {
            Dialogos.mostrarErrorDatos(e.getMessage() + "\n\nLa aplicación se va a cerrar.");
            Platform.exit();
        } else {
            Dialogos.mostrarErrorDatos(e.getMessage());
        }
    }

    private void mostrar(Node pantalla) {
        ScrollPane desplazable = new ScrollPane(pantalla);
        desplazable.setFitToWidth(true);
        raiz.setCenter(desplazable);
    }

    private Node crearEncabezado() {
        Label encabezado = new Label("CINE | Sistema de reservas");
        encabezado.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        VBox barra = new VBox(encabezado);
        barra.setPadding(new Insets(12, 20, 12, 20));
        barra.setStyle("-fx-background-color: #2b2b2b;");
        return barra;
    }

    // Las opciones de pantallas que todavía no existen se muestran deshabilitadas.
    private Node crearMenu() {
        Button cartelera = crearOpcionMenu("Cartelera");
        cartelera.setOnAction(e -> mostrarCartelera());
        Button reservas = crearOpcionMenu("Reservas");
        reservas.setDisable(true);
        Button peliculas = crearOpcionMenu("Películas");
        peliculas.setDisable(true);
        Button salas = crearOpcionMenu("Salas");
        salas.setDisable(true);
        Button funciones = crearOpcionMenu("Funciones");
        funciones.setDisable(true);

        VBox menu = new VBox(8, cartelera, reservas, peliculas, salas, funciones);
        menu.setPadding(new Insets(16));
        menu.setPrefWidth(170);
        menu.setStyle("-fx-background-color: #eeeeee;");
        return menu;
    }

    private Button crearOpcionMenu(String texto) {
        Button opcion = new Button(texto);
        opcion.setMaxWidth(Double.MAX_VALUE);
        return opcion;
    }
}
