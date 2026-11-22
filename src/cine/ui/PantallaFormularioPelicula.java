package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Pelicula;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

// Pantalla 09: registro de una película nueva o modificación de una existente.
public class PantallaFormularioPelicula {

    private VentanaPrincipal ventana;
    // Null al registrar; al modificar se guarda el id y no el objeto, porque el estado
    // puede recargarse después de un error al guardar.
    private Integer idPelicula;
    private TextField titulo;
    private TextField duracion;

    public PantallaFormularioPelicula(VentanaPrincipal ventana, Integer idPelicula) {
        this.ventana = ventana;
        this.idPelicula = idPelicula;
    }

    // Recibe la película recién consultada cuando se modifica, o null cuando se registra.
    public Parent crearVista(Pelicula pelicula) {
        titulo = new TextField();
        titulo.setPrefColumnCount(24);
        duracion = new TextField();
        duracion.setPromptText("Por ejemplo, 120");
        String textoTitulo = "Registrar película";
        if (pelicula != null) {
            textoTitulo = "Modificar película";
            titulo.setText(pelicula.getTitulo());
            duracion.setText(String.valueOf(pelicula.getDuracionMinutos()));
        }

        GridPane datos = Componentes.formulario();
        Componentes.agregarCampo(datos, "Título", titulo);
        Componentes.agregarCampo(datos, "Duración (minutos)", duracion);

        Button guardar = Componentes.botonPrincipal("Guardar");
        guardar.setOnAction(e -> guardar());
        Button cancelar = new Button("Cancelar");
        cancelar.setOnAction(e -> ventana.mostrarPeliculas());

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo(textoTitulo), datos,
                Componentes.filaBotones(guardar, cancelar));
        return pantalla;
    }

    // Ante cualquier error se queda en el formulario con lo que el usuario escribió.
    private void guardar() {
        Integer minutos = Formato.leerEntero(duracion.getText());
        if (minutos == null) {
            Dialogos.mostrarErrorNegocio("La duración tiene que ser un número entero de minutos, por ejemplo 120.");
            return;
        }
        try {
            if (idPelicula == null) {
                ventana.getPeliculaService().registrar(titulo.getText(), minutos);
            } else {
                ventana.getPeliculaService().modificar(idPelicula, titulo.getText(), minutos);
            }
            ventana.mostrarPeliculas();
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
    }
}
