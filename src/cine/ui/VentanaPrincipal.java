package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import cine.modelo.Reserva;
import cine.modelo.Sala;
import cine.persistencia.GestorEstado;
import cine.servicios.FuncionService;
import cine.servicios.PeliculaService;
import cine.servicios.ReservaService;
import cine.servicios.SalaService;
import java.time.LocalDateTime;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Ventana con el encabezado, el menú lateral y la pantalla actual en el centro.
// Las pantallas le piden a esta clase que muestre la pantalla siguiente.
public class VentanaPrincipal {

    // Se guarda para que los cuadros de diálogo de archivos queden asociados a la ventana.
    private Stage escenario;
    private PeliculaService peliculaService;
    private SalaService salaService;
    private FuncionService funcionService;
    private ReservaService reservaService;
    // Solo se usa para saber si después de un error al guardar hay que cerrar la aplicación.
    private GestorEstado gestor;
    private BorderPane raiz;

    public VentanaPrincipal(Stage escenario, PeliculaService peliculaService, SalaService salaService,
            FuncionService funcionService, ReservaService reservaService, GestorEstado gestor) {
        this.escenario = escenario;
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

    public Stage getEscenario() {
        return escenario;
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

    public void mostrarReservas() {
        mostrarReservas("");
    }

    // Abre la consulta con el texto ya escrito y buscado, por ejemplo el código de la
    // reserva recién confirmada.
    public void mostrarReservas(String textoInicial) {
        mostrar(new PantallaReservas(this).crearVista(textoInicial));
    }

    // La reserva se vuelve a consultar porque el estado pudo recargarse después de un error al guardar.
    public void mostrarDetalleReserva(String codigo) {
        Reserva reserva = buscarReserva(codigo);
        if (reserva == null) {
            Dialogos.mostrarInformacion("La reserva ya no existe",
                    "La reserva seleccionada ya no existe. Se vuelve al listado de reservas.");
            mostrarReservas();
            return;
        }
        mostrar(new PantallaDetalleReserva(this, codigo).crearVista(reserva));
    }

    public void mostrarPeliculas() {
        mostrar(new PantallaPeliculas(this).crearVista());
    }

    public void mostrarRegistroPelicula() {
        mostrar(new PantallaFormularioPelicula(this, null).crearVista(null));
    }

    // La película se vuelve a consultar porque el estado pudo recargarse después de un error al guardar.
    public void mostrarModificacionPelicula(int idPelicula) {
        Pelicula pelicula = buscarPelicula(idPelicula);
        if (pelicula == null) {
            Dialogos.mostrarInformacion("La película ya no existe",
                    "La película seleccionada ya no existe. Se vuelve al listado de películas.");
            mostrarPeliculas();
            return;
        }
        mostrar(new PantallaFormularioPelicula(this, idPelicula).crearVista(pelicula));
    }

    public void mostrarSalas() {
        mostrar(new PantallaSalas(this).crearVista());
    }

    public void mostrarRegistroSala() {
        mostrar(new PantallaRegistroSala(this).crearVista());
    }

    public void mostrarDistribucionSala(int idSala) {
        Sala sala = buscarSala(idSala);
        if (sala == null) {
            Dialogos.mostrarInformacion("La sala ya no existe",
                    "La sala seleccionada ya no existe. Se vuelve al listado de salas.");
            mostrarSalas();
            return;
        }
        mostrar(new PantallaDistribucionSala(this).crearVista(sala));
    }

    public void mostrarFunciones() {
        mostrar(new PantallaFunciones(this).crearVista());
    }

    public void mostrarRegistroFuncion() {
        mostrar(new PantallaFormularioFuncion(this).crearVista());
    }

    // Los servicios no tienen búsqueda por id de películas y salas; se recorre el listado
    // completo. Devuelven null si no existe.
    public Pelicula buscarPelicula(int idPelicula) {
        for (Pelicula pelicula : peliculaService.listar()) {
            if (pelicula.getId() == idPelicula) {
                return pelicula;
            }
        }
        return null;
    }

    public Sala buscarSala(int idSala) {
        for (Sala sala : salaService.listar()) {
            if (sala.getId() == idSala) {
                return sala;
            }
        }
        return null;
    }

    public Reserva buscarReserva(String codigo) {
        for (Reserva reserva : reservaService.listar()) {
            if (reserva.getId().equals(codigo)) {
                return reserva;
            }
        }
        return null;
    }

    // Una línea por función con fecha, hora, el nombre de la sala o el título de la película
    // y la cantidad de reservas confirmadas que se cancelarían con ella.
    public String describirFuncionesAfectadas(List<Funcion> funciones, boolean nombrarPelicula) {
        StringBuilder texto = new StringBuilder();
        for (Funcion funcion : funciones) {
            String nombre;
            if (nombrarPelicula) {
                nombre = funcion.getPelicula().getTitulo();
            } else {
                nombre = funcion.getSala().getNombre();
            }
            texto.append("• ").append(Formato.fechaHora(funcion.getInicio())).append(" · ").append(nombre)
                    .append(" · ").append(Formato.reservasConfirmadas(funcionService.cantidadReservasConfirmadas(funcion)))
                    .append("\n");
        }
        return texto.toString();
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
        // Sin esto el panel dibuja un marco de foco alrededor de toda la pantalla al hacer clic en ella.
        desplazable.setStyle("-fx-background-color: transparent;");
        raiz.setCenter(desplazable);
        // Al cambiar de pantalla el foco pasaba al primer botón del menú y parecía que
        // la sección elegida era "Cartelera"; se lo deja en la pantalla que se acaba de mostrar.
        desplazable.requestFocus();
    }

    private Node crearEncabezado() {
        Label encabezado = new Label("CINE | Sistema de reservas");
        encabezado.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        VBox barra = new VBox(encabezado);
        barra.setPadding(new Insets(12, 20, 12, 20));
        barra.setStyle("-fx-background-color: #2b2b2b;");
        return barra;
    }

    private Node crearMenu() {
        Button cartelera = crearOpcionMenu("Cartelera");
        cartelera.setOnAction(e -> mostrarCartelera());
        Button reservas = crearOpcionMenu("Reservas");
        reservas.setOnAction(e -> mostrarReservas());
        Button peliculas = crearOpcionMenu("Películas");
        peliculas.setOnAction(e -> mostrarPeliculas());
        Button salas = crearOpcionMenu("Salas");
        salas.setOnAction(e -> mostrarSalas());
        Button funciones = crearOpcionMenu("Funciones");
        funciones.setOnAction(e -> mostrarFunciones());

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
