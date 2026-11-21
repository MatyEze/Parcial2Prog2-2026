package cine.modelo;

public class EntradaEstudiante extends Entrada {

    private static final long serialVersionUID = 1L;

    private static final double DESCUENTO = 0.20;

    public EntradaEstudiante(Butaca butaca, double precioBase) {
        super(butaca, precioBase);
    }

    @Override
    protected double calcularPrecio(double precioBase) {
        return precioBase * (1 - DESCUENTO);
    }

    @Override
    public TipoEntrada getTipo() {
        return TipoEntrada.ESTUDIANTE;
    }
}
