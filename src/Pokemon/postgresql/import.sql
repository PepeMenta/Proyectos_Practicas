BEGIN;

TRUNCATE TABLE movimientos, pokemons, evoluciones, estados, record_juego RESTART IDENTITY CASCADE;

CREATE TEMP TABLE staging_estados (linea TEXT);
CREATE TEMP TABLE staging_movimientos (linea TEXT);
CREATE TEMP TABLE staging_pokemons (linea TEXT);
CREATE TEMP TABLE staging_evoluciones (linea TEXT);
CREATE TEMP TABLE staging_record (linea TEXT);

\copy staging_estados FROM '../files/estados'
\copy staging_movimientos FROM '../files/movimientos'
\copy staging_pokemons FROM '../files/pokemons'
\copy staging_evoluciones FROM '../files/evoluciones'
\copy staging_record FROM '../files/record'

INSERT INTO estados (nombre, unicode_icon, prob_moverte, potencia, prob_irse, max_turnos)
SELECT
    split_part(linea, ';', 1),
    split_part(linea, ';', 2),
    split_part(linea, ';', 3)::DOUBLE PRECISION,
    split_part(linea, ';', 4)::DOUBLE PRECISION,
    split_part(linea, ';', 5)::DOUBLE PRECISION,
    split_part(linea, ';', 6)::INTEGER
FROM staging_estados;

INSERT INTO movimientos (
    nombre, tipo, categoria, prioridad, precision_valor, potencia, curar,
    subir_vel, subir_atk, subir_def, subir_spa, subir_spd, buffs_al_ejecutador,
    estado_nombre, prob_estado, prob_modificacion_stats, estado_al_ejecutador, tipos_compatibles, nivel_minimo
)
SELECT
    split_part(linea, ';', 1),
    split_part(linea, ';', 2),
    split_part(linea, ';', 3),
    split_part(linea, ';', 4)::INTEGER,
    split_part(linea, ';', 5)::DOUBLE PRECISION,
    split_part(linea, ';', 6)::DOUBLE PRECISION,
    split_part(linea, ';', 7)::DOUBLE PRECISION,
    split_part(linea, ';', 8)::DOUBLE PRECISION,
    split_part(linea, ';', 9)::DOUBLE PRECISION,
    split_part(linea, ';', 10)::DOUBLE PRECISION,
    split_part(linea, ';', 11)::DOUBLE PRECISION,
    split_part(linea, ';', 12)::DOUBLE PRECISION,
    split_part(linea, ';', 13)::BOOLEAN,
    split_part(linea, ';', 14),
    split_part(linea, ';', 15)::DOUBLE PRECISION,
    split_part(linea, ';', 16)::DOUBLE PRECISION,
    split_part(linea, ';', 17)::BOOLEAN,
    split_part(linea, ';', 18),
    split_part(linea, ';', 19)::INTEGER
FROM staging_movimientos;

INSERT INTO pokemons (nombre, tipos, velocidad, vida, atk, def, spa, spd)
SELECT
    split_part(linea, ';', 1),
    split_part(linea, ';', 2),
    split_part(linea, ';', 3)::DOUBLE PRECISION,
    split_part(linea, ';', 4)::DOUBLE PRECISION,
    split_part(linea, ';', 5)::DOUBLE PRECISION,
    split_part(linea, ';', 6)::DOUBLE PRECISION,
    split_part(linea, ';', 7)::DOUBLE PRECISION,
    split_part(linea, ';', 8)::DOUBLE PRECISION
FROM staging_pokemons;

INSERT INTO evoluciones (
    preevolucion, nivel_evolucion, evolucion, sum_vel, sum_vida,
    sum_atk, sum_def, sum_spa, sum_spd, tipos
)
SELECT
    split_part(linea, ';', 1),
    split_part(linea, ';', 2)::INTEGER,
    split_part(linea, ';', 3),
    split_part(linea, ';', 4)::DOUBLE PRECISION,
    split_part(linea, ';', 5)::DOUBLE PRECISION,
    split_part(linea, ';', 6)::DOUBLE PRECISION,
    split_part(linea, ';', 7)::DOUBLE PRECISION,
    split_part(linea, ';', 8)::DOUBLE PRECISION,
    split_part(linea, ';', 9)::DOUBLE PRECISION,
    split_part(linea, ';', 10)
FROM staging_evoluciones;

INSERT INTO record_juego (id, record_victorias)
SELECT 1, trim(linea)::INTEGER
FROM staging_record;

COMMIT;
