# Plan de trabajo

Dos partes: las decisiones que tenemos que cerrar entre los dos y la ruta por etapas.

## Decisiones pendientes

Cada una tiene una propuesta para arrancar la discusión. Al cerrarla, marcar la casilla y anotar lo decidido: varias van después al informe.

### Persistencia y archivos

- [ ] **Dónde se guarda el archivo serializado.**
  Propuesta: `datos/cine.dat`, relativo a la carpeta del proyecto. Es fácil de mostrar en la defensa y de documentar. La alternativa es la carpeta del usuario (`user.home`), que no depende de desde dónde se ejecute pero queda escondida.
- [x] **Si el archivo de datos se sube al repo.**
  Decidido: no. Los datos de demostración los genera el programa en el primer inicio, así todos arrancan con el mismo estado.
- [ ] **Cuándo se guarda.**
  Propuesta: después de cada operación que cambia algo (alta, modificación, confirmación, cancelación). Guardar solo al cerrar pierde todo si el programa se cae.
- [ ] **Qué pasa si falla el guardado.**
  Propuesta: avisar en pantalla, no mostrar éxito y deshacer el cambio en memoria, para que lo que se ve coincida con lo guardado.
- [ ] **Qué pasa si el archivo existe pero no se puede leer.**
  Propuesta: avisar y no abrir la aplicación con datos vacíos sobre ese archivo. La consigna prohíbe sobrescribirlo automáticamente.
- [ ] **Formato del CSV.**
  Propuesta: una fila por reserva, separador `;`, con código, estado, cliente, identificación, película, sala, fecha, hora, butacas e importe. El usuario elige dónde guardarlo con un diálogo.

### Reglas de negocio

- [ ] **Descuento de estudiante.** Propuesta: 20 %, el valor de los wireframes.
- [ ] **Formato del código de reserva.** Propuesta: `CIN-001`, secuencial.
- [ ] **Identificación del cliente.** Qué se acepta (¿solo dígitos?, ¿largo?) y qué pasa si la misma identificación llega con otro nombre. Propuesta: solo dígitos, y se reutiliza el cliente ya registrado.
- [ ] **Desactivar una película con funciones futuras.** ¿Se permite? ¿Se puede reactivar? Propuesta: se permite, deja de ofrecerse para funciones nuevas y las ya programadas siguen.
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
