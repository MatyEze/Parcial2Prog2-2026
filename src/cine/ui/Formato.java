package cine.ui;

import cine.modelo.Funcion;
import cine.modelo.TipoEntrada;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formato {

    // Se fija el idioma y el país para que los importes y las fechas se vean igual
    // en cualquier computadora, sin depender de su configuración regional.
    private static final Locale LOCALE = Locale.of("es", "AR");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm", LOCALE);

    public static String dinero(double monto) {
        long centavos = Math.round(monto * 100);
        String patron;
        if (centavos % 100 == 0) {
            patron = "#,##0";
        } else {
            patron = "#,##0.00";
        }
        DecimalFormat formato = new DecimalFormat(patron, DecimalFormatSymbols.getInstance(LOCALE));
        return "$ " + formato.format(centavos / 100.0);
    }

    public static String fecha(LocalDateTime fechaHora) {
        return fechaHora.format(FECHA);
    }

    public static String hora(LocalDateTime fechaHora) {
        return fechaHora.format(HORA);
    }

    // Ejemplo: "Sala 1 · 04/12/2026 · 18:00".
    public static String funcion(Funcion funcion) {
        return funcion.getSala().getNombre() + " · " + fecha(funcion.getInicio()) + " · "
                + hora(funcion.getInicio());
    }

    public static String tipoEntrada(TipoEntrada tipo) {
        switch (tipo) {
            case GENERAL:
                return "General";
            case ESTUDIANTE:
                return "Estudiante";
            default:
                return tipo.toString();
        }
    }
}
