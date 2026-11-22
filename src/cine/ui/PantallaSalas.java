package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Funcion;
import cine.modelo.Sala;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

// Pantalla 10: listado de salas con su distribución y su estado.
public class PantallaSalas {

    private VentanaPrincipal ventana;
    private VBox listado;

    public PantallaSalas(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista() {
        Button registrar = Componentes.botonPrincipal("Registrar sala");
        registrar.setOnAction(e -> ventana.mostrarRegistroSala());

        listado = new VBox();

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo("Salas"), registrar, listado);
        actualizarListado();
        return pantalla;
    }

    private void actualizarListado() {
        List<Sala> salas = ventana.getSalaService().listar();
        listado.getChildren().clear();
        if (salas.isEmpty()) {
            listado.getChildren().add(Componentes.nota("No hay salas registradas."));
            return;
        }
        GridPane tabla = Componentes.tabla("Sala", "Filas", "Butacas / fila", "Capacidad", "Estado", "Acción");
        for (Sala sala : salas) {
            int idSala = sala.getId();
            Button distribucion = new Button("Ver distribución →");
            distribucion.setOnAction(e -> ventana.mostrarDistribucionSala(idSala));
            String estado;
            Button cambiarEstado;
            if (sala.isHabilitada()) {
                estado = "Habilitada";
                cambiarEstado = new Button("Deshabilitar");
                cambiarEstado.setOnAction(e -> deshabilitar(idSala));
            } else {
                estado = "Deshabilitada";
                cambiarEstado = new Button("Habilitar");
                cambiarEstado.setOnAction(e -> habilitar(idSala));
            }
            Componentes.agregarFila(tabla,
                    new Label(sala.getNombre()),
                    new Label(String.valueOf(sala.getFilas())),
                    new Label(String.valueOf(sala.getButacasPorFila())),
                    new Label(sala.getCapacidad() + " butacas"),
                    new Label(estado),
                    Componentes.filaBotones(distribucion, cambiarEstado));
        }
        listado.getChildren().add(tabla);
    }

    // Antes de deshabilitar se muestran las funciones futuras que el servicio va a cancelar.
    private void deshabilitar(int idSala) {
        Sala sala = ventana.buscarSala(idSala);
        if (sala == null) {
            actualizarListado();
            return;
        }
        List<Funcion> afectadas = ventana.getFuncionService().futurasVigentesDeSala(idSala);
        String mensaje;
        if (afectadas.isEmpty()) {
            mensaje = "La sala no tiene funciones futuras vigentes: no se cancela ninguna función.";
        } else {
            mensaje = "Se van a cancelar estas funciones futuras y sus reservas confirmadas:\n\n"
                    + ventana.describirFuncionesAfectadas(afectadas, true)
                    + "\nLas cancelaciones son definitivas: si después se vuelve a habilitar la sala, "
                    + "las funciones y las reservas canceladas no se recuperan.";
        }
        boolean aceptado = Dialogos.confirmar("¿Deshabilitar la sala \"" + sala.getNombre() + "\"?",
                mensaje, "Sí, deshabilitar", "No, volver");
        if (!aceptado) {
            return;
        }
        try {
            ventana.getSalaService().deshabilitar(idSala);
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
        actualizarListado();
    }

    private void habilitar(int idSala) {
        try {
            ventana.getSalaService().habilitar(idSala);
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
        actualizarListado();
    }
}
