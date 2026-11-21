package cine.modelo;

import java.io.Serializable;

public abstract class Entrada implements Serializable {

    private static final long serialVersionUID = 1L;

    private Butaca butaca;
    private double precioConfirmado;

    // El precio se fija al crear la entrada, así un cambio posterior del precio
    // de la función no modifica lo que ya se reservó.
    protected Entrada(Butaca butaca, double precioBase) {
        this.butaca = butaca;
        this.precioConfirmado = calcularPrecio(precioBase);
    }

    public static Entrada crear(TipoEntrada tipo, Butaca butaca, double precioBase) {
        switch (tipo) {
            case GENERAL:
                return new EntradaGeneral(butaca, precioBase);
            case ESTUDIANTE:
                return new EntradaEstudiante(butaca, precioBase);
            default:
                throw new IllegalArgumentException("Tipo de entrada desconocido: " + tipo + ".");
        }
    }

    // Se llama desde el constructor: las subclases solo pueden usar el precio recibido
    // y sus constantes, porque sus atributos todavía no están inicializados.
    protected abstract double calcularPrecio(double precioBase);

    public abstract TipoEntrada getTipo();

    public Butaca getButaca() {
        return butaca;
    }

    public double getPrecioConfirmado() {
        return precioConfirmado;
    }
}
