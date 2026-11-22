package cine.persistencia;

import cine.modelo.Cliente;
import cine.modelo.Entrada;
import cine.modelo.Funcion;
import cine.modelo.Pelicula;
import cine.modelo.Reserva;
import cine.modelo.Sala;
import cine.modelo.TipoEntrada;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatosIniciales {

    // Los objetos se crean directamente y no con los servicios, porque los servicios
    // guardan después de cada operación y no permiten programar funciones pasadas.
    // Las fechas se calculan a partir de hoy para que el estado de cada función de
    // demostración sea siempre el mismo en el primer inicio.
    public static EstadoCine crearEstadoDemo() {
        EstadoCine estado = new EstadoCine();

        Pelicula orbita = agregarPelicula(estado, "Órbita", 120);
        Pelicula ultimoViaje = agregarPelicula(estado, "El último viaje", 105);
        Pelicula bosque = agregarPelicula(estado, "Bosque de papel", 90);

        Sala sala1 = agregarSala(estado, "Sala 1", 4, 6);
        Sala sala2 = agregarSala(estado, "Sala 2", 3, 6);

        LocalDate manana = LocalDate.now().plusDays(1);
        LocalDate pasadoManana = LocalDate.now().plusDays(2);

        Funcion orbitaTemprano = agregarFuncion(estado, orbita, sala1, manana.atTime(18, 0), 5000);
        agregarFuncion(estado, orbita, sala1, manana.atTime(21, 0), 5000);
        Funcion ultimoViajeManana = agregarFuncion(estado, ultimoViaje, sala2, manana.atTime(19, 0), 4500);
        agregarFuncion(estado, bosque, sala2, manana.atTime(21, 15), 4000);
        agregarFuncion(estado, bosque, sala1, pasadoManana.atTime(17, 0), 4000);
        agregarFuncion(estado, ultimoViaje, sala2, pasadoManana.atTime(20, 0), 4500);

        Cliente lucia = agregarCliente(estado, "30125478", "Lucía Pérez");
        Cliente martin = agregarCliente(estado, "28456913", "Martín Gómez");
        Cliente ana = agregarCliente(estado, "35872046", "Ana López");

        List<Entrada> entradasLucia = new ArrayList<>();
        entradasLucia.add(crearEntrada(ultimoViajeManana, TipoEntrada.GENERAL, 'A', 3));
        entradasLucia.add(crearEntrada(ultimoViajeManana, TipoEntrada.GENERAL, 'A', 4));
        agregarReserva(estado, lucia, ultimoViajeManana, entradasLucia);

        List<Entrada> entradasMartin = new ArrayList<>();
        entradasMartin.add(crearEntrada(orbitaTemprano, TipoEntrada.GENERAL, 'B', 3));
        entradasMartin.add(crearEntrada(orbitaTemprano, TipoEntrada.ESTUDIANTE, 'B', 4));
        agregarReserva(estado, martin, orbitaTemprano, entradasMartin);

        List<Entrada> entradasAna = new ArrayList<>();
        entradasAna.add(crearEntrada(orbitaTemprano, TipoEntrada.ESTUDIANTE, 'C', 5));
        Reserva reservaAna = agregarReserva(estado, ana, orbitaTemprano, entradasAna);
        reservaAna.cancelar();

        // Una función de ayer, ya finalizada, y otra que empezó hace 20 minutos, para que al
        // abrir el programa por primera vez esté en emisión. Esta pasa sola a finalizada
        // cuando termina la película (dos horas después de su inicio). Están en salas
        // distintas y lejos de las funciones de mañana, así que no se superponen con nada.
        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
        Funcion ultimoViajeAyer = agregarFuncion(estado, ultimoViaje, sala2,
                LocalDate.now().minusDays(1).atTime(20, 0), 4500);
        Funcion orbitaEnEmision = agregarFuncion(estado, orbita, sala1, ahora.minusMinutes(20), 5000);

        List<Entrada> entradasMartinAyer = new ArrayList<>();
        entradasMartinAyer.add(crearEntrada(ultimoViajeAyer, TipoEntrada.GENERAL, 'B', 2));
        agregarReserva(estado, martin, ultimoViajeAyer, entradasMartinAyer);

        Cliente diego = agregarCliente(estado, "32904517", "Diego Fernández");
        List<Entrada> entradasDiego = new ArrayList<>();
        entradasDiego.add(crearEntrada(orbitaEnEmision, TipoEntrada.GENERAL, 'D', 1));
        entradasDiego.add(crearEntrada(orbitaEnEmision, TipoEntrada.GENERAL, 'D', 2));
        agregarReserva(estado, diego, orbitaEnEmision, entradasDiego);

        return estado;
    }

    private static Pelicula agregarPelicula(EstadoCine estado, String titulo, int duracionMinutos) {
        Pelicula pelicula = new Pelicula(estado.siguienteIdPelicula(), titulo, duracionMinutos);
        estado.getPeliculas().agregar(pelicula);
        return pelicula;
    }

    private static Sala agregarSala(EstadoCine estado, String nombre, int filas, int butacasPorFila) {
        Sala sala = new Sala(estado.siguienteIdSala(), nombre, filas, butacasPorFila);
        estado.getSalas().agregar(sala);
        return sala;
    }

    private static Funcion agregarFuncion(EstadoCine estado, Pelicula pelicula, Sala sala,
            LocalDateTime inicio, double precio) {
        Funcion funcion = new Funcion(estado.siguienteIdFuncion(), pelicula, sala, inicio, precio);
        estado.getFunciones().agregar(funcion);
        return funcion;
    }

    private static Cliente agregarCliente(EstadoCine estado, String identificacion, String nombre) {
        Cliente cliente = new Cliente(identificacion, nombre);
        estado.getClientes().agregar(cliente);
        return cliente;
    }

    private static Entrada crearEntrada(Funcion funcion, TipoEntrada tipo, char fila, int numero) {
        return Entrada.crear(tipo, funcion.getSala().buscarButaca(fila, numero), funcion.getPrecio());
    }

    private static Reserva agregarReserva(EstadoCine estado, Cliente cliente, Funcion funcion,
            List<Entrada> entradas) {
        Reserva reserva = new Reserva(estado.siguienteCodigoReserva(), cliente, funcion, entradas,
                LocalDateTime.now());
        estado.getReservas().agregar(reserva);
        return reserva;
    }
}
