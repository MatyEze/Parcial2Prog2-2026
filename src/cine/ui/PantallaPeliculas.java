package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Pantalla 08: listado de películas con sus acciones de modificación y de estado.
public class PantallaPeliculas {

    private VentanaPrincipal ventana;
    private VBox listado;

    public PantallaPeliculas(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista() {
        Button registrar = Componentes.botonPrincipal("Registrar película");
        registrar.setOnAction(e -> ventana.mostrarRegistroPelicula());

        listado = new VBox();

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo("Películas"), registrar, listado);
        actualizarListado();
        return pantalla;
    }

    // El listado se arma siempre con los datos actuales del servicio, también después
    // de un error al guardar, porque el estado pudo volver al último guardado.
    private void actualizarListado() {
        List<Pelicula> peliculas = ventana.getPeliculaService().listar();
        listado.getChildren().clear();
        if (peliculas.isEmpty()) {
            listado.getChildren().add(Componentes.nota("No hay películas registradas."));
            return;
        }
        GridPane tabla = Componentes.tabla("Título", "Duración", "Estado", "Acciones");
        for (Pelicula pelicula : peliculas) {
            int idPelicula = pelicula.getId();
            HBox acciones;
            String estado;
            if (pelicula.isActiva()) {
                estado = "Activa";
                Button editar = new Button("Editar");
                editar.setOnAction(e -> ventana.mostrarModificacionPelicula(idPelicula));
                Button desactivar = new Button("Desactivar");
                desactivar.setOnAction(e -> desactivar(idPelicula));
                acciones = Componentes.filaBotones(editar, desactivar);
            } else {
                estado = "Inactiva";
                Button activar = new Button("Activar");
                activar.setOnAction(e -> activar(idPelicula));
                acciones = Componentes.filaBotones(activar);
            }
            Componentes.agregarFila(tabla,
                    new Label(pelicula.getTitulo()),
                    new Label(pelicula.getDuracionMinutos() + " min"),
                    new Label(estado),
                    acciones);
        }
        listado.getChildren().add(tabla);
    }

    // Antes de desactivar se muestran las funciones futuras que el servicio va a cancelar.
    private void desactivar(int idPelicula) {
        Pelicula pelicula = ventana.buscarPelicula(idPelicula);
        if (pelicula == null) {
            actualizarListado();
            return;
        }
        List<Funcion> afectadas = ventana.getFuncionService().futurasVigentesDePelicula(idPelicula);
        String mensaje;
        if (afectadas.isEmpty()) {
            mensaje = "La película no tiene funciones futuras vigentes: no se cancela ninguna función.";
        } else {
            mensaje = "Se van a cancelar estas funciones futuras y sus reservas confirmadas:\n\n"
                    + ventana.describirFuncionesAfectadas(afectadas, false)
                    + "\nLas cancelaciones son definitivas: si después se vuelve a activar la película, "
                    + "las funciones y las reservas canceladas no se recuperan.";
        }
        boolean aceptado = Dialogos.confirmar("¿Desactivar la película \"" + pelicula.getTitulo() + "\"?",
                mensaje, "Sí, desactivar", "No, volver");
        if (!aceptado) {
            return;
        }
        try {
            ventana.getPeliculaService().desactivar(idPelicula);
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
        actualizarListado();
    }

    private void activar(int idPelicula) {
        try {
            ventana.getPeliculaService().activar(idPelicula);
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
        actualizarListado();
    }
}
