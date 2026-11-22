package cine.persistencia;

import cine.excepciones.PersistenciaException;
import cine.modelo.Butaca;
import cine.modelo.EstadoReserva;
import cine.modelo.Funcion;
import cine.modelo.Reserva;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorReservas {

    private static final String SEPARADOR = ";";
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    // Se escribe en UTF-8 con la marca BOM al principio para que Excel reconozca los acentos,
    // y con punto y coma porque es el separador que espera Excel con configuración en español.
    public void exportarCsv(List<Reserva> reservas, Path destino) throws PersistenciaException {
        try (BufferedWriter salida = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            salida.write('﻿');
            salida.write("Código;Estado;Cliente;Identificación;Película;Sala;Fecha;Hora;Butacas;Importe total");
            salida.newLine();
            for (Reserva reserva : reservas) {
                salida.write(aLineaCsv(reserva));
                salida.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo escribir el archivo de exportación \""
                    + destino.toAbsolutePath() + "\".", e);
        }
    }

    private String aLineaCsv(Reserva reserva) {
        Funcion funcion = reserva.getFuncion();

        String estado;
        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            estado = "Confirmada";
        } else {
            estado = "Cancelada";
        }

        StringBuilder butacas = new StringBuilder();
        for (Butaca butaca : reserva.getButacas()) {
            if (butacas.length() > 0) {
                butacas.append(", ");
            }
            butacas.append(butaca.getCodigo());
        }

        // Importe sin signo ni separador de miles para que la planilla lo lea como número.
        long centavos = Math.round(reserva.getImporteTotal() * 100);
        String importe = String.valueOf(centavos / 100);
        if (centavos % 100 != 0) {
            long resto = centavos % 100;
            if (resto < 10) {
                importe = importe + ",0" + resto;
            } else {
                importe = importe + "," + resto;
            }
        }

        String[] celdas = {
            reserva.getId(),
            estado,
            reserva.getNombreCliente(),
            reserva.getCliente().getId(),
            funcion.getPelicula().getTitulo(),
            funcion.getSala().getNombre(),
            funcion.getInicio().format(FECHA),
            funcion.getInicio().format(HORA),
            butacas.toString(),
            importe
        };

        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < celdas.length; i++) {
            if (i > 0) {
                linea.append(SEPARADOR);
            }
            String celda = celdas[i];
            // Una celda con separador, comillas o salto de línea va entre comillas,
            // y las comillas de adentro se duplican, como pide el formato CSV.
            if (celda.contains(SEPARADOR) || celda.contains("\"") || celda.contains("\n") || celda.contains("\r")) {
                celda = "\"" + celda.replace("\"", "\"\"") + "\"";
            }
            linea.append(celda);
        }
        return linea.toString();
    }
}
