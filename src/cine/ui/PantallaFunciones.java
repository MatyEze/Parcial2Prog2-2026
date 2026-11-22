package cine.ui;

import cine.excepciones.PersistenciaException;
import cine.excepciones.ReglaNegocioException;
import cine.modelo.Funcion;
import java.time.LocalDateTime;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

// Pantalla 12: todas las funciones, incluidas las canceladas y las que ya comenzaron.
public class PantallaFunciones {

    private VentanaPrincipal ventana;
    private VBox listado;

    public PantallaFunciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
    }

    public Parent crearVista() {
        Button registrar = Componentes.botonPrincipal("Registrar función");
        registrar.setOnAction(e -> ventana.mostrarRegistroFuncion());

        listado = new VBox();

        VBox pantalla = Componentes.pantalla();
        pantalla.getChildren().addAll(Componentes.titulo("Funciones"), registrar, listado);
        actualizarListado();
        return pantalla;
    }

    private void actualizarListado() {
        List<Funcion> funciones = ventana.getFuncionService().listar();
        listado.getChildren().clear();
        if (funciones.isEmpty()) {
            listado.getChildren().add(Componentes.nota("No hay funciones registradas."));
            return;
        }
        LocalDateTime ahora = LocalDateTime.now();
        GridPane tabla = Componentes.tabla("Película", "Sala", "Fecha", "Hora", "Precio", "Estado", "Acción");
        for (Funcion funcion : funciones) {
            Componentes.agregarFila(tabla,
                    new Label(funcion.getPelicula().getTitulo()),
                    new Label(funcion.getSala().getNombre()),
                    new Label(Formato.fecha(funcion.getInicio())),
                    new Label(Formato.hora(funcion.getInicio())),
                    new Label(Formato.dinero(funcion.getPrecio())),
                    new Label(describirEstado(funcion, ahora)),
                    crearAcciones(funcion, ahora));
        }
        listado.getChildren().add(tabla);
    }

    private String describirEstado(Funcion funcion, LocalDateTime ahora) {
        if (funcion.isCancelada()) {
            return "Cancelada";
        }
        if (funcion.yaComenzo(ahora)) {
            return "Finalizada";
        }
        return "Vigente";
    }

    // Solo las funciones vigentes tienen acciones; las demás ya no se pueden modificar.
    private Node crearAcciones(Funcion funcion, LocalDateTime ahora) {
        if (!funcion.estaVigente(ahora)) {
            return new Label("");
        }
        int idFuncion = funcion.getId();
        Button verButacas = new Button("Ver butacas →");
        verButacas.setOnAction(e -> verButacas(idFuncion));
        Button cambiarPrecio = new Button("Cambiar precio");
        cambiarPrecio.setOnAction(e -> cambiarPrecio(idFuncion));
        Button cancelar = new Button("Cancelar función");
        cancelar.setOnAction(e -> cancelar(idFuncion));
        return Componentes.filaBotones(verButacas, cambiarPrecio, cancelar);
    }

    private void verButacas(int idFuncion) {
        ReservaEnCurso reserva = new ReservaEnCurso(idFuncion);
        reserva.marcarAbiertaDesdeFunciones();
        ventana.mostrarSeleccionButacas(reserva);
    }

    // Si el texto no es un número o el servicio rechaza el precio, se vuelve a pedir
    // con lo que el usuario escribió, hasta que lo guarde o cancele.
    private void cambiarPrecio(int idFuncion) {
        Funcion funcion = ventana.getFuncionService().buscar(idFuncion);
        if (funcion == null) {
            actualizarListado();
            return;
        }
        String encabezado = "Cambiar el precio de " + funcion.getPelicula().getTitulo() + " · "
                + Formato.funcion(funcion) + "\nPrecio actual: " + Formato.dinero(funcion.getPrecio());
        String texto = Formato.numero(funcion.getPrecio());
        boolean terminado = false;
        while (!terminado) {
            texto = Dialogos.pedirTexto(encabezado, "Precio general", texto);
            if (texto == null) {
                return;
            }
            Double precio = Formato.leerDecimal(texto);
            if (precio == null) {
                Dialogos.mostrarErrorNegocio("El precio tiene que ser un número, por ejemplo 5000 o 5000,50 "
                        + "(sin puntos y con coma para los decimales).");
                continue;
            }
            try {
                ventana.getFuncionService().cambiarPrecio(idFuncion, precio);
                terminado = true;
            } catch (ReglaNegocioException e) {
                Dialogos.mostrarErrorNegocio(e.getMessage());
            } catch (PersistenciaException e) {
                ventana.informarErrorPersistencia(e);
                terminado = true;
            }
        }
        actualizarListado();
    }

    private void cancelar(int idFuncion) {
        Funcion funcion = ventana.getFuncionService().buscar(idFuncion);
        if (funcion == null) {
            actualizarListado();
            return;
        }
        int reservas = ventana.getFuncionService().cantidadReservasConfirmadas(funcion);
        String mensaje;
        if (reservas == 0) {
            mensaje = "La función no tiene reservas confirmadas.\n\n"
                    + "La cancelación es definitiva: la función cancelada no se recupera.";
        } else if (reservas == 1) {
            mensaje = "La función tiene 1 reserva confirmada que también se va a cancelar.\n\n"
                    + "La cancelación es definitiva: la función y la reserva cancelada no se recuperan.";
        } else {
            mensaje = "La función tiene " + reservas + " reservas confirmadas que también se van a cancelar.\n\n"
                    + "La cancelación es definitiva: la función y las reservas canceladas no se recuperan.";
        }
        boolean aceptado = Dialogos.confirmar("¿Cancelar la función de " + funcion.getPelicula().getTitulo()
                + " · " + Formato.funcion(funcion) + "?", mensaje, "Sí, cancelar función", "Conservar función");
        if (!aceptado) {
            return;
        }
        try {
            ventana.getFuncionService().cancelar(idFuncion);
        } catch (ReglaNegocioException e) {
            Dialogos.mostrarErrorNegocio(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.informarErrorPersistencia(e);
        }
        actualizarListado();
    }
}
