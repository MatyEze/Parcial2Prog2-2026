# Plan de trabajo

Dos partes: las decisiones que tenemos que cerrar entre los dos y la ruta por etapas.

## Decisiones pendientes

Cada una tiene una propuesta para arrancar la discusión. Al cerrarla, marcar la casilla y anotar lo decidido: varias van después al informe.

### Persistencia y archivos

- [ ] **Dónde se guarda el archivo serializado.**
  Pendiente: consultar al profesor en la próxima clase (ubicación y extensión del archivo).
  Propuesta: `datos/cine.dat`, relativo a la carpeta del proyecto. Es fácil de mostrar en la defensa y de documentar. La alternativa es la carpeta del usuario (`user.home`), que no depende de desde dónde se ejecute pero queda escondida.
- [x] **Si el archivo de datos se sube al repo.**
  Decidido: no. Los datos de demostración los genera el programa en el primer inicio, así todos arrancan con el mismo estado.
- [x] **Cuándo se guarda.**
  Decidido: después de cada operación que cambia algo (alta, modificación, confirmación, cancelación). Guardar solo al cerrar pierde todo si el programa se cae.
- [x] **Qué pasa si falla el guardado.**
  Decidido: avisar en pantalla, no mostrar éxito y deshacer el cambio en memoria, para que lo que se ve coincida con lo guardado. Los datos del formulario se conservan para reintentar.
- [x] **Qué pasa si el archivo existe pero no se puede leer.**
  Decidido: avisar con la ruta del archivo y cerrar la aplicación, sin tocarlo. La consigna prohíbe sobrescribirlo automáticamente. Para volver a usar el programa hay que mover o borrar el archivo a mano.
- [x] **Formato del CSV.**
  Decidido: una fila por reserva, con encabezados y separador `;`. Columnas: código, estado, cliente, identificación, película, sala, fecha, hora, butacas (juntas en una celda) e importe total. Incluye las reservas canceladas. El usuario elige dónde guardarlo con un diálogo.

### Reglas de negocio

- [x] **Descuento de estudiante.** Decidido: 20 % fijo, el valor de los wireframes. Va como constante en la entrada de estudiante y hay que documentarlo en el informe.
- [x] **Formato del código de reserva.** Decidido: `CIN-001`, secuencial, a partir de un contador guardado en el estado del cine. Los tres dígitos son relleno, no un tope: después de `CIN-999` sigue `CIN-1000`. Si una reserva se deshace por un fallo de guardado, el número se devuelve.
- [x] **Identificación del cliente.** Decidido: el cliente tiene nombre e identificación, los dos obligatorios. La identificación acepta solo dígitos, de 7 u 8 de largo. Si la misma identificación llega con otro nombre, se reutiliza el cliente ya registrado y se actualiza su nombre. Las reservas anteriores no cambian: cada reserva guarda el nombre del cliente tal como estaba al confirmarla, igual que guarda los precios.
- [x] **Desactivar una película con funciones futuras.** Decidido: si alguna función futura tiene reservas confirmadas, la desactivación se rechaza y un cartel muestra las funciones y reservas que la impiden; esas reservas se cancelan a mano, una por una. Si no hay ninguna, la película se desactiva, no se puede usar para funciones nuevas y sus funciones dejan de verse en cartelera. No se borra nada y las funciones pasadas no se tocan.
- [ ] **Reactivar una película desactivada.** ¿Se permite?
- [ ] **Editar la duración de una película con funciones programadas.** Propuesta: rechazar si el nuevo horario de fin se superpone con otra función.
- [ ] **Salas y funciones: ¿se modifican o eliminan?** La consigna solo pide registrarlas. Propuesta: no se eliminan; de una función solo se puede cambiar el precio.
- [ ] **Margen entre funciones.** Propuesta: ninguno; una función puede empezar cuando termina la anterior.
- [x] **Tipo de dato para el dinero.** Decidido: `double`. Es una simplificación consciente porque el importe es informativo; al mostrarlo se redondea a dos decimales. Va al informe.

### Proyecto y forma de trabajo

- [ ] **Cómo se arma la interfaz.** FXML con Scene Builder o JavaFX por código: usar la modalidad vista en clase.
- [ ] **Versión de Java y de JavaFX**, y cómo se agrega la librería al proyecto de NetBeans. Los dos tienen que tener lo mismo.
- [ ] **Nombres de los paquetes.** Propuesta: `modelo`, `servicios`, `persistencia`, `excepciones`, `ui`.
- [ ] **Reparto de tareas.** Quién toma qué en cada etapa.
- [ ] **Evidencias de verificación.** Propuesta: tabla de casos con capturas de pantalla.

## Ruta

El sistema como recorrido: tenemos una película, la programamos en una sala, se elige una función, se reservan butacas y esa información se conserva.

Cada etapa termina cuando se cumple su "Listo cuando". No se pasa a la siguiente antes.

### Etapa 1 — Comprender el problema

- Distinguir película, sala, butaca, función, cliente, entrada y reserva.
- Repasar las reglas de negocio, sin hablar de código.

**Listo cuando:** cada uno puede explicar una reserva de principio a fin con sus palabras.

### Etapa 2 — Definir el modelo

- Clases, responsabilidades, atributos y relaciones.
- Cerrar las decisiones de reglas de negocio.
- Revisar `diagrama-clases.uxf` (ya hay una primera versión) y ajustarlo.

**Listo cuando:** cada uno puede explicar qué representa cada clase y por qué se relaciona con las demás.

### Etapa 3 — Operaciones básicas

- Crear el proyecto en NetBeans y los paquetes.
- Registrar películas y salas, generar butacas, programar funciones.
- Colecciones, validaciones y excepciones de negocio.

**Listo cuando:** se crea una función válida y se rechaza una superposición de horarios.

### Etapa 4 — La reserva

- Consultar disponibilidad por función.
- Seleccionar butacas, asociar cliente, calcular precios con los tipos de entrada.
- Confirmar la reserva.

**Listo cuando:** una reserva se confirma y esas butacas dejan de estar disponibles para esa función, pero siguen libres en otra.

### Etapa 5 — Persistencia

- Guardar y recuperar el estado completo por serialización.
- Primer inicio con datos de demostración: 2 salas, 3 películas, 6 funciones y reservas.
- Manejo de errores de lectura y escritura.

**Listo cuando:** se cierra y se abre el programa y las reservas y la disponibilidad se conservan.

### Etapa 6 — JavaFX

- Pantallas y controladores según los wireframes.
- Los controladores delegan en los servicios; las reglas no van en los botones.
- Errores informados en pantalla, sin perder lo que se había cargado.

**Listo cuando:** el recorrido de reserva se completa desde la interfaz.

### Etapa 7 — Completar funcionalidades

- Consulta por código o identificación.
- Cancelación y liberación de butacas.
- Comprobante.
- Exportación a CSV.

**Listo cuando:** están todos los módulos de la consigna.

### Etapa 8 — Verificar y entregar

- Recorrer los diez casos mínimos de verificación y guardar las evidencias.
- Instrucciones de ejecución, informe y diagrama actualizado.
- Preparar la defensa: seguir una operación desde la interfaz hasta el archivo.

**Listo cuando:** otra persona puede ejecutar el proyecto y cada uno puede explicar una operación completa.
