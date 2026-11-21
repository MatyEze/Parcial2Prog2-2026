package cine.modelo;

public class EntradaGeneral extends Entrada {

    private static final long serialVersionUID = 1L;

    public EntradaGeneral(Butaca butaca, double precioBase) {
        super(butaca, precioBase);
    }

    @Override
    protected double calcularPrecio(double precioBase) {
        return precioBase;
    }

    @Override
    public TipoEntrada getTipo() {
        return TipoEntrada.GENERAL;
    }
}
