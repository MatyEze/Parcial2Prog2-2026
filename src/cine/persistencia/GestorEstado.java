package cine.persistencia;

import cine.excepciones.PersistenciaException;

public class GestorEstado {

    private EstadoCine estado;

    public GestorEstado() {
    }

    public void iniciar() {
        this.estado = new EstadoCine();
    }

    public EstadoCine getEstado() {
        return estado;
    }

    public void guardarCambios() throws PersistenciaException {
        // La escritura en archivo se agrega junto con la persistencia.
    }
}
