# Migracion de `src/files` a base de datos

He dejado dos opciones listas:

- PostgreSQL: `db/postgresql/schema.sql` y `db/postgresql/import.sql`
- MySQL: `db/mysql/schema.sql` y `db/mysql/import.sql`

La idea es mantener el formato actual de tus archivos para que puedas migrar ya, sin cambiar todavia el juego.

## Que se guarda

- `src/files/estados` -> tabla `estados`
- `src/files/movimientos` -> tabla `movimientos`
- `src/files/pokemons` -> tabla `pokemons`
- `src/files/evoluciones` -> tabla `evoluciones`
- `src/files/record` -> tabla `record_juego`

## PostgreSQL

1. Crea la base de datos.
2. Ejecuta el esquema:

```bash
psql -d tu_basedatos -f db/postgresql/schema.sql
```

3. Ejecuta la importacion desde la raiz del proyecto:

```bash
psql -d tu_basedatos -f db/postgresql/import.sql
```

Nota: `import.sql` usa `\copy`, asi que debes lanzarlo con `psql`.

## MySQL

1. Crea la base de datos.
2. Ejecuta el esquema:

```bash
mysql tu_basedatos < db/mysql/schema.sql
```

3. Ejecuta la importacion:

```bash
mysql --local-infile=1 tu_basedatos < db/mysql/import.sql
```

Si MySQL no encuentra los archivos, cambia las rutas `src/files/...` por rutas absolutas del proyecto.

## Recomendacion

Para este proyecto te recomiendo PostgreSQL:

- Tiene importacion con `\copy` muy comoda para estos archivos planos.
- Suele dar menos guerra que `LOAD DATA LOCAL INFILE`.
- El SQL queda un poco mas limpio para futuras consultas.

## Siguiente paso natural

Ahora mismo el juego sigue leyendo desde archivos en [src/JuegoRPG.java](/home/alex/Documentos/project/src/JuegoRPG.java:17) y en los metodos `cargarPokemons`, `cargarEvoluciones`, `cargarEstados` y `cargarMovimientos`.

El siguiente paso seria cambiar esas cargas para leer desde JDBC en vez de `Scanner + File`.
