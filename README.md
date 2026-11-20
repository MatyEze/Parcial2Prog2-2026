# Parcial 2 - Programación 2

Sistema de reservas de cine. Java con JavaFX, proyecto Ant de NetBeans.

## Integrantes

- Matias Ezequiel Sellanes
- Alex Ivan Gonzalez

## Qué hace falta

- JDK 25.
- NetBeans 25.
- SDK de JavaFX 25.0.5 para Windows, descomprimido en `C:\javafx-sdk-25.0.5`.

## Preparar NetBeans

El proyecto usa una plataforma Java llamada `JDK 25`. Si NetBeans no la tiene:

1. Ir a *Tools → Java Platforms → Add Platform*.
2. Elegir la carpeta donde está instalado el JDK 25.
3. Dejar el nombre `JDK 25`.

## Si JavaFX está en otra carpeta

La ruta del SDK está en una sola línea de `nbproject/project.properties`:

```
javafx.home=C:/javafx-sdk-25.0.5
```

Para cambiarla desde NetBeans:

1. Abrir la pestaña *Files* (*Window → Files*).
2. Abrir `nbproject/project.properties`.
3. Cambiar la ruta de `javafx.home` y guardar. Va con barras `/`.

No hace falta crear una librería ni tocar las opciones de ejecución.

## Ejecutar

1. *File → Open Project* y elegir la carpeta del repositorio.
2. *Run → Run Project* (F6).

La clase principal es `cine.CineApp`.
