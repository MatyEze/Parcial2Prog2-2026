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

La única librería externa es JavaFX; no hay base de datos ni servicios externos.

## Dónde se guardan los datos

Todo el estado del cine (películas, salas, funciones, clientes y reservas) se guarda en un solo archivo serializado:

```
datos/cine.dat
```

La carpeta `datos/` se crea dentro de la carpeta del proyecto, porque NetBeans ejecuta el programa desde ahí. No se sube al repositorio: cada uno genera la suya en el primer inicio. El archivo se actualiza después de cada operación que cambia algo. Mientras se guarda puede aparecer por un momento `datos/cine.dat.tmp`.

## Primer inicio: datos de demostración

Si `datos/cine.dat` no existe, el programa crea un estado de demostración y lo guarda enseguida. Las fechas se calculan desde el día del primer inicio: "mañana" y "pasado mañana" son relativos a ese día.

**Películas:** Órbita (120 min), El último viaje (105 min) y Bosque de papel (90 min).

**Salas:** Sala 1 (4 filas de 6 butacas, 24 lugares) y Sala 2 (3 filas de 6, 18 lugares).

**Funciones (8):**

| Película | Sala | Cuándo | Precio | Estado al primer inicio |
|---|---|---|---|---|
| Órbita | Sala 1 | Mañana 18:00 | $ 5.000 | Vigente |
| Órbita | Sala 1 | Mañana 21:00 | $ 5.000 | Vigente |
| El último viaje | Sala 2 | Mañana 19:00 | $ 4.500 | Vigente |
| Bosque de papel | Sala 2 | Mañana 21:15 | $ 4.000 | Vigente |
| Bosque de papel | Sala 1 | Pasado mañana 17:00 | $ 4.000 | Vigente |
| El último viaje | Sala 2 | Pasado mañana 20:00 | $ 4.500 | Vigente |
| El último viaje | Sala 2 | Ayer 20:00 | $ 4.500 | Finalizada |
| Órbita | Sala 1 | Empezó 20 minutos antes del primer inicio | $ 5.000 | En emisión (pasa a finalizada cuando termina la película) |

**Reservas (5):**

| Código | Cliente | Identificación | Función | Butacas | Importe | Estado |
|---|---|---|---|---|---|---|
| CIN-001 | Lucía Pérez | 30125478 | El último viaje, mañana 19:00 | A3 y A4 general | $ 9.000 | Confirmada |
| CIN-002 | Martín Gómez | 28456913 | Órbita, mañana 18:00 | B3 general y B4 estudiante | $ 9.000 | Confirmada |
| CIN-003 | Ana López | 35872046 | Órbita, mañana 18:00 | C5 estudiante | $ 4.000 | Cancelada |
| CIN-004 | Martín Gómez | 28456913 | El último viaje, ayer 20:00 | B2 general | $ 4.500 | Confirmada |
| CIN-005 | Diego Fernández | 32904517 | Órbita en emisión | D1 y D2 general | $ 10.000 | Confirmada |

La entrada de estudiante tiene un 20 % de descuento sobre el precio de la función.

## Volver a los datos de demostración

Cerrar el programa y borrar `datos/cine.dat`. En el próximo inicio se vuelven a crear los datos de demostración, con fechas calculadas desde ese día. Conviene hacerlo antes de una demostración si pasaron días desde el primer inicio, porque las funciones de "mañana" ya habrán quedado finalizadas.

## Si el archivo no se puede leer

Si `datos/cine.dat` existe pero está dañado o no es un archivo del sistema, el programa muestra un aviso con la ruta del archivo y se cierra sin modificarlo. Para volver a usarlo hay que mover o borrar ese archivo a mano; en el siguiente inicio arranca con los datos de demostración.

Si en cambio falla un guardado durante el uso, el programa avisa, descarta el cambio y vuelve al último estado guardado. Si tampoco puede volver a leer el archivo, avisa y se cierra.

## Exportar reservas

En *Reservas*, el botón *Exportar CSV* abre un cuadro para elegir dónde guardar el archivo (el nombre sugerido es `reservas.csv`). El archivo queda donde elija el usuario, no en la carpeta del proyecto. Tiene una fila por reserva, incluidas las canceladas, separadas por `;`, en UTF-8, pensado para abrirse con Excel en español. Exportar no modifica las reservas.

## Documentos del repositorio

| Documento | Contenido |
|---|---|
| `informe.md` | Informe de la entrega: organización del código, decisiones de diseño, conceptos aplicados y el recorrido de una reserva desde la pantalla hasta el archivo. |
| `objetos-y-reglas.md` | Los objetos del problema y las reglas de negocio, sin código. |
| `plan-de-trabajo.md` | Las decisiones que tomamos y las etapas del trabajo. |
| `diagrama-clases.uxf` | Diagrama de clases (se abre con UMLet o UMLetino). |
| `trabajo-en-equipo.md` | Cómo trabajamos con git y pull requests. |
| `verificacion.md` y `evidencias/` | Casos de verificación con sus resultados y capturas de pantalla. |
