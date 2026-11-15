# Cómo trabajamos en equipo

## Antes de empezar

1. Clonar el repo y abrirlo en NetBeans.
2. Pedir por fuera de git los PDFs de la consigna y el resumen: la carpeta `no-git-docs/` no se sube.

## Flujo de trabajo

1. Actualizar `main` antes de arrancar: `git pull`.
2. Crear una rama por tarea: `git switch -c reserva-service`.
3. Hacer commits chicos, cada uno con un cambio que compile.
4. Subir la rama y abrir un pull request.
5. El otro lo lee y lo aprueba. Recién ahí se mergea a `main`.
6. Borrar la rama y volver al paso 1.

Nadie sube directo a `main`. La defensa es individual: revisar el código del otro es la forma de entender todo el proyecto.

## Commits

- En español, minúsculas, cortos, sin prefijos ni punto final.
- Describen qué se hizo: `clase abstracta entrada`, `validar superposicion de funciones`.

## Para no pisarnos

- Avisar qué tarea toma cada uno antes de empezar.
- No trabajar los dos sobre la misma clase al mismo tiempo.
- `diagrama-clases.uxf` lo edita uno solo por vez: git no puede combinar dos versiones de ese archivo.
- Si cambia el diseño, actualizar el diagrama en el mismo pull request que el código.
- No subir `build/`, `dist/` ni `nbproject/private/` (ya están en el `.gitignore`).

## Si hay conflicto

1. Traer `main` a la rama: `git pull origin main`.
2. Resolver el conflicto hablando con quien tocó ese código.
3. Probar que compila y corre antes de subir.
