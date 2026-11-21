package cine.persistencia;

import cine.excepciones.PersistenciaException;

public class GestorEstado {

    private EstadoCine estado;
    private PersistenciaCine persistencia;
    private boolean estadoPerdido;

    public GestorEstado(PersistenciaCine persistencia) {
        this.persistencia = persistencia;
        this.estadoPerdido = false;
    }

    // Si el archivo existe pero no se puede leer, el error se propaga y el archivo no se toca.
    public void iniciar() throws PersistenciaException {
        if (persistencia.existeArchivo()) {
            this.estado = persistencia.cargar();
        } else {
            // Los datos iniciales se guardan enseguida para que siempre haya en disco
            // un último estado bueno al que volver si falla un guardado posterior.
            EstadoCine inicial = DatosIniciales.crearEstadoDemo();
            persistencia.guardar(inicial);
            this.estado = inicial;
        }
    }

    public EstadoCine getEstado() {
        return estado;
    }

    // Si el guardado falla, se descartan todos los cambios en memoria y se vuelve
    // al último estado guardado, en lugar de deshacer operación por operación.
    public void guardarCambios() throws PersistenciaException {
        try {
            persistencia.guardar(estado);
        } catch (PersistenciaException errorGuardado) {
            try {
                recargar();
            } catch (PersistenciaException errorRecarga) {
                this.estadoPerdido = true;
                throw new PersistenciaException("No se pudieron guardar los cambios y tampoco se pudo volver "
                        + "a leer el último estado guardado. " + errorRecarga.getMessage()
                        + " Los datos en memoria ya no son confiables y la aplicación se tiene que cerrar.",
                        errorRecarga);
            }
            throw new PersistenciaException("No se pudieron guardar los cambios. " + errorGuardado.getMessage()
                    + " Se descartaron los cambios y se volvió al último estado guardado.", errorGuardado);
        }
    }

    public boolean isEstadoPerdido() {
        return estadoPerdido;
    }

    private void recargar() throws PersistenciaException {
        this.estado = persistencia.cargar();
    }
}
