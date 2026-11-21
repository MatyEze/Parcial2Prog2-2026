package cine.persistencia;

import cine.excepciones.PersistenciaException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PersistenciaCine {

    private Path archivo;

    public PersistenciaCine(Path archivo) {
        this.archivo = archivo;
    }

    public boolean existeArchivo() {
        return Files.exists(archivo);
    }

    // Se lee un único objeto raíz para que las referencias compartidas entre reservas,
    // funciones, salas y películas sigan apuntando a las mismas instancias.
    public EstadoCine cargar() throws PersistenciaException {
        Object leido;
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            leido = entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("No se pudo leer el archivo de datos \"" + getRuta() + "\".", e);
        }
        if (!(leido instanceof EstadoCine)) {
            throw new PersistenciaException("El archivo \"" + getRuta()
                    + "\" no contiene datos del sistema de reservas.", null);
        }
        return (EstadoCine) leido;
    }

    // Se escribe primero en un archivo temporal y después se reemplaza el archivo real,
    // así una escritura que falla a mitad de camino no deja truncado el último estado bueno.
    public void guardar(EstadoCine estado) throws PersistenciaException {
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        try {
            Path carpeta = archivo.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(temporal))) {
                salida.writeObject(estado);
            }
            reemplazarArchivo(temporal);
        } catch (IOException e) {
            borrarTemporal(temporal);
            throw new PersistenciaException("No se pudo escribir el archivo de datos \"" + getRuta() + "\".", e);
        }
    }

    private void reemplazarArchivo(Path temporal) throws IOException {
        try {
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void borrarTemporal(Path temporal) {
        try {
            Files.deleteIfExists(temporal);
        } catch (IOException e) {
            // Se informa el error de escritura original; un temporal que quedó se reemplaza al próximo guardado.
        }
    }

    private Path getRuta() {
        return archivo.toAbsolutePath();
    }
}
