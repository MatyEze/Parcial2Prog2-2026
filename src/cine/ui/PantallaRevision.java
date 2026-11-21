package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Butaca;
import cine.modelo.Entrada;
import cine.modelo.Funcion;
import cine.modelo.Reserva;
import cine.modelo.TipoEntrada;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Pantalla 04: resumen de la reserva antes de confirmarla.
public class PantallaRevision {

    private VentanaPrincipal ventana;
    private ReservaEnCurso reserva;

    public PantallaRevision(VentanaPrincipal ventana, ReservaEnCurso reserva) {
        this.ventana = ventana;
        this.reserva = reserva;
    }

    public Parent crearVista(Funcion funcion) {
        GridPane tabla = Componentes.tabla("Butaca", "Entrada", "Precio confirmado");
        double suma = 0;
        for (Map.Entry<Butaca, TipoEntrada> par : reserva.crearTiposPorButaca(funcion.getSala()).entrySet()) {
            Entrada entrada = Entrada.crear(par.getValue(), par.getKey(), funcion.getPrecio());
            suma += entrada.getPrecioConfirmado();
            Componentes.agregarFila(tabla,
                    new Label(par.getKey().getCodigo()),
                    new Label(Formato.tipoEntrada(entrada.getTipo())),
                    new Label(Formato.dinero(entrada.getPrecioConfirmado())));
        }

        Button corregir = new Button("← Corregir datos");
        corregir.setOnAction(e -> ventana.mostrarClienteEntradas(reserva));
        Button confirmar = Componentes.botonPrincipal("Confirmar reserva");
        confirmar.setOnAction(e -> confirmar());
        HBox botones = new HBox(10, corregir, confirmar);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(
                Componentes.titulo("Revisar antes de confirmar"),
                Componentes.subtitulo(funcion.getPelicula().getTitulo() + " · " + Formato.funcion(funcion)),
                new Label("Cliente: " + reserva.getNombre().trim()
                        + " · Identificación: " + reserva.getIdentificacion().trim()),
                tabla,
                Componentes.resaltado("Total: " + Formato.dinero(suma)),
                Componentes.nota("La disponibilidad se verifica nuevamente antes de guardar."),
                botones);
        return pantalla;
    }

    // Solo se pasa al comprobante si el servicio guardó la reserva. Ante cualquier error
    // la pantalla queda como está y los datos cargados se conservan.
    private void confirmar() {
        Funcion funcion = ventana.buscarFuncionReservable(reserva.getIdFuncion());
        if (funcion == null) {
            return;
        }
        try {
            Reserva confirmada = ventana.getReservaService().confirmar(funcion, reserva.getNombre(),
                    reserva.getIdentificacion(), reserva.crearTiposPorButaca(funcion.getSala()));
            ventana.mostrarComprobante(confirmada);
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
    }
}
