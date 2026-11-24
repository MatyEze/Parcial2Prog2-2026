# Objetos y reglas de negocio

Etapa 1 del plan: entender el problema antes de escribir código.

El recorrido: tenemos una película, la programamos en una sala, alguien elige una función, reserva butacas y esa información se conserva.

## Los objetos

### Existen aunque nadie reserve

| Objeto | Qué es | Qué no es |
|---|---|---|
| **Película** | La obra: título y duración. | No tiene horarios ni sala. |
| **Sala** | El lugar físico, con su distribución de filas y butacas. | No sabe qué se proyecta. |
| **Butaca** | Una ubicación fija dentro de una sala, identificada por fila y número (B3). | No sabe si está ocupada. |

### Une todo

| Objeto | Qué es |
|---|---|
| **Función** | Una película proyectada en una sala, en una fecha y hora, a un precio. Es el evento concreto. |

Órbita a las 18:00 y Órbita a las 21:00 son la misma película, pero dos funciones distintas.

### Aparecen cuando alguien reserva

| Objeto | Qué es |
|---|---|
| **Cliente** | Quien reserva. Se lo reconoce por su identificación. |
| **Entrada** | Una butaca de esa función, con un tipo (general o estudiante) y el precio que se cobró. Hay una entrada por butaca. |
| **Reserva** | La operación completa: un cliente, una función, una o más entradas, un código único y un estado (confirmada o cancelada). |

## Cómo se relacionan

- Una sala **tiene** butacas. Las butacas no existen sin su sala.
- Una función **es de** una película y **ocurre en** una sala.
- Una reserva **es de** un cliente y **es para** una función.
- Una reserva **tiene** entradas. Cada entrada **corresponde a** una butaca de la sala de esa función.

## Las tres ideas centrales

1. **La disponibilidad es por función, no por butaca.**
   B3 no "está ocupada": está ocupada para la función de las 18:00 y puede estar libre para la de las 21:00.
2. **Seleccionar no es reservar.**
   Mientras se eligen butacas en pantalla no pasó nada. La reserva existe recién al confirmar, y en ese momento se vuelve a verificar que sigan libres.
3. **Lo confirmado no cambia.**
   Si después cambia el precio de la función, la reserva conserva lo que se cobró. Cancelar tampoco borra: cambia el estado, libera las butacas y la reserva queda en el historial.

## Reglas de negocio

### Películas

- Dos películas activas no pueden tener el mismo título. No importan las mayúsculas ni los espacios al principio o al final; los acentos sí. Las películas desactivadas pueden repetir títulos, y para activar una tiene que tener un título que no use otra activa.
- El título y la duración no se cambian mientras la película tenga funciones futuras vigentes. Si esas funciones se cancelan, se pueden modificar.

### Funciones

- Una función necesita una película y una sala que existan.
- En una misma sala no puede haber dos funciones superpuestas. La hora de fin es la hora de inicio más la duración de la película.
- Una función está vigente (todavía no empezó), en emisión (ya empezó y la película todavía no terminó), finalizada (la película ya terminó) o cancelada. El estado no se guarda: se calcula con la hora actual, y cancelada tiene prioridad sobre los demás.
- Solo a una función vigente se le puede cambiar el precio.
- Una función vigente o en emisión se puede cancelar, y con ella se cancelan sus reservas confirmadas. Una función finalizada o cancelada no se puede cancelar.
- Una reserva se puede cancelar por separado solo mientras su función está vigente. Si la función está en emisión o finalizada, la reserva ya no se cancela sola.

### Reservas

- Solo se puede reservar una función que todavía no empezó.
- Una reserva tiene al menos una butaca.
- Todas las butacas tienen que ser de la sala de esa función.
- Una butaca no puede estar en dos reservas confirmadas de la misma función.
- Cada reserva tiene un código único.
- El importe y el precio de cada entrada quedan fijos al confirmar.

### Cancelación

- Una reserva cancelada libera sus butacas para esa función.
- Conserva toda su información.
- No se puede cancelar dos veces.

### Precios

- Hay al menos dos tipos de entrada, general y estudiante, con reglas de precio distintas.
- Cada tipo de entrada sabe calcular su propio precio a partir del precio de la función.

### Datos que no se pueden perder

- No se elimina nada que deje una reserva sin película, sala, función o cliente.
- Cuando algo deja de usarse se desactiva o se rechaza la operación; no se borra.

### Validaciones

- Datos obligatorios completos.
- Precio y duración con valores válidos.
- Fechas válidas.
- Identificación del cliente válida.
- Al menos una butaca seleccionada.

Ante un error se informa en pantalla y se puede corregir sin volver a cargar todo.

## Un caso para probar si se entendió

Lucía reserva B3 y B4 para Órbita a las 18:00.

- **Martín quiere B3 para Órbita a las 21:00.** Puede: es otra función, y la reserva de Lucía no la afecta.
- **Ana quiere B4 para Órbita a las 18:00.** No puede: B4 ya está en una reserva confirmada de esa misma función.
- **Lucía cancela.** Su reserva queda en el historial como cancelada, y ahora Ana sí puede reservar B4 para las 18:00.
