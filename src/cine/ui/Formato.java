package cine.ui;

import cine.modelo.EstadoFuncion;
import cine.modelo.EstadoReserva;
import cine.modelo.Funcion;
import cine.modelo.TipoEntrada;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class Formato {

    // Se fija el idioma y el país para que los importes y las fechas se vean igual
    // en cualquier computadora, sin depender de su configuración regional.
    private static final Locale LOCALE = Locale.of("es", "AR");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", LOCALE);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm", LOCALE);
    // Al leer se usa el modo estricto para que "24:00" o "18:75" no se acepten corridos.
    private static final DateTimeFormatter LECTURA_HORA = DateTimeFormatter.ofPattern("HH:mm", LOCALE)
            .withResolverStyle(ResolverStyle.STRICT);

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

    public static String fechaHora(LocalDateTime fechaHora) {
        return fecha(fechaHora) + " " + hora(fechaHora);
    }

    // Número para editar en un campo de texto: "5000" o "5000,50", sin separador de miles.
    public static String numero(double valor) {
        long centavos = Math.round(valor * 100);
        if (centavos % 100 == 0) {
            return String.valueOf(centavos / 100);
        }
        DecimalFormat formato = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(LOCALE));
        return formato.format(centavos / 100.0);
    }

    public static String reservasConfirmadas(int cantidad) {
        if (cantidad == 1) {
            return "1 reserva confirmada";
        }
        return cantidad + " reservas confirmadas";
    }

    // Las conversiones de texto devuelven null si el texto no tiene el formato esperado.
    // Solo convierten: si el valor es aceptable o no lo decide el servicio.
    public static Integer leerEntero(String texto) {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Solo se aceptan dígitos y, si hace falta, una coma con uno o dos decimales.
    // El punto se rechaza porque "5.000" se leería como 5 en lugar de cinco mil.
    public static Double leerDecimal(String texto) {
        String limpio = texto.trim();
        if (!limpio.matches("[0-9]+(,[0-9]{1,2})?")) {
            return null;
        }
        try {
            return Double.valueOf(limpio.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static LocalTime leerHora(String texto) {
        try {
            return LocalTime.parse(texto.trim(), LECTURA_HORA);
        } catch (DateTimeParseException e) {
            return null;
        }
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

    public static String estadoFuncion(EstadoFuncion estado) {
        switch (estado) {
            case VIGENTE:
                return "Vigente";
            case FINALIZADA:
                return "Finalizada";
            case CANCELADA:
                return "Cancelada";
            default:
                return estado.toString();
        }
    }

    public static String estadoReserva(EstadoReserva estado) {
        switch (estado) {
            case CONFIRMADA:
                return "Confirmada";
            case CANCELADA:
                return "Cancelada";
            default:
                return estado.toString();
        }
    }
}
