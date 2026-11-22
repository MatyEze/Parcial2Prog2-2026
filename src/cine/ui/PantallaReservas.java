package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.modelo.Funcion;
import cine.modelo.Reserva;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

// Pantalla 06: consulta de reservas por código o identificación del cliente y por función.
public class PantallaReservas {

    private VentanaPrincipal ventana;
    private TextField texto;
    private ComboBox<String> funciones;
    // Ids de las funciones en el mismo orden que el selector; la posición 0 es "Todas las funciones".
    private List<Integer> idsFunciones;
    private VBox resultados;

    public PantallaReservas(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista(String textoInicial) {
        texto = new TextField(textoInicial);
        texto.setPrefColumnCount(18);
        texto.setOnAction(e -> buscar());

        funciones = new ComboBox<>();
        idsFunciones = new ArrayList<>();
        funciones.getItems().add("Todas las funciones");
        idsFunciones.add(null);
        // Solo se ofrecen las funciones que tienen alguna reserva, confirmada o cancelada.
        List<Reserva> reservas = ventana.getReservaService().listar();
        for (Funcion funcion : ventana.getFuncionService().listar()) {
            boolean tieneReservas = false;
            for (Reserva reserva : reservas) {
                if (reserva.getFuncion().equals(funcion)) {
                    tieneReservas = true;
                }
            }
            if (tieneReservas) {
                funciones.getItems().add(funcion.getPelicula().getTitulo() + " · " + Formato.funcion(funcion));
                idsFunciones.add(funcion.getId());
            }
        }
        funciones.getSelectionModel().selectFirst();

        Button buscar = Componentes.botonPrincipal("Buscar");
        buscar.setOnAction(e -> buscar());
        Button limpiar = new Button("Limpiar");
        limpiar.setOnAction(e -> limpiar());
        Button exportar = new Button("Exportar CSV");
        exportar.setOnAction(e -> exportar());

        HBox filtroTexto = new HBox(10, new Label("Código o identificación del cliente"), texto);
        filtroTexto.setAlignment(Pos.CENTER_LEFT);
        HBox filtroFuncion = new HBox(10, new Label("Función"), funciones, buscar, limpiar, exportar);
        filtroFuncion.setAlignment(Pos.CENTER_LEFT);

        resultados = new VBox();

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo("Reservas"), filtroTexto, filtroFuncion, resultados);
        buscar();
        return pantalla;
    }

    // El servicio busca por texto y el filtro por función se aplica aquí sobre ese resultado.
    private void buscar() {
        Integer idFuncion = idsFunciones.get(funciones.getSelectionModel().getSelectedIndex());
        List<Reserva> reservas = new ArrayList<>();
        for (Reserva reserva : ventana.getReservaService().buscar(texto.getText())) {
            if (idFuncion == null || idFuncion.equals(reserva.getFuncion().getId())) {
                reservas.add(reserva);
            }
        }

        resultados.getChildren().clear();
        if (reservas.isEmpty()) {
            resultados.getChildren().add(Componentes.nota("No hay reservas para estos filtros"));
            return;
        }
        GridPane tabla = Componentes.tabla("Código", "Cliente", "Estado", "Acción");
        for (Reserva reserva : reservas) {
            String codigo = reserva.getId();
            Button verDetalle = new Button("Ver detalle →");
            verDetalle.setOnAction(e -> ventana.mostrarDetalleReserva(codigo));
            Componentes.agregarFila(tabla,
                    new Label(codigo),
                    new Label(reserva.getNombreCliente()),
                    new Label(Formato.estadoReserva(reserva.getEstado())),
                    verDetalle);
        }
        resultados.getChildren().add(tabla);
    }

    private void limpiar() {
        texto.setText("");
        funciones.getSelectionModel().selectFirst();
        buscar();
    }

    // Un error aquí es de escritura del archivo exportado: el estado del sistema no se tocó,
    // por eso no pasa por informarErrorPersistencia, que puede cerrar la aplicación.
    private void exportar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Exportar reservas");
        selector.setInitialFileName("reservas.csv");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
        File archivo = selector.showSaveDialog(ventana.getEscenario());
        if (archivo == null) {
            return;
        }
        try {
            ventana.getReservaService().exportarCsv(archivo.toPath());
            Dialogos.mostrarInformacion("Reservas exportadas",
                    "Se guardaron las reservas en \"" + archivo.getAbsolutePath() + "\".");
        } catch (PersistenciaException e) {
            Dialogos.mostrarErrorDatos(e.getMessage());
        }
    }
}
