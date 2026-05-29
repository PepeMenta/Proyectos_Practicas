\c postgres;
DROP DATABASE IF EXISTS juegopokemon;
CREATE DATABASE juegopokemon;
\c juegopokemon;

CREATE TABLE IF NOT EXISTS estados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    unicode_icon VARCHAR(16) NOT NULL,
    prob_moverte DOUBLE PRECISION NOT NULL,
    potencia DOUBLE PRECISION NOT NULL,
    prob_irse DOUBLE PRECISION NOT NULL,
    max_turnos INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    prioridad INTEGER NOT NULL,
    precision_valor DOUBLE PRECISION NOT NULL,
    potencia DOUBLE PRECISION NOT NULL,
    curar DOUBLE PRECISION NOT NULL,
    subir_vel DOUBLE PRECISION NOT NULL,
    subir_atk DOUBLE PRECISION NOT NULL,
    subir_def DOUBLE PRECISION NOT NULL,
    subir_spa DOUBLE PRECISION NOT NULL,
    subir_spd DOUBLE PRECISION NOT NULL,
    buffs_al_ejecutador BOOLEAN NOT NULL,
    estado_nombre VARCHAR(50) NOT NULL REFERENCES estados(nombre),
    prob_estado DOUBLE PRECISION NOT NULL,
    prob_modificacion_stats DOUBLE PRECISION NOT NULL,
    estado_al_ejecutador BOOLEAN NOT NULL,
    tipos_compatibles TEXT NOT NULL,
    nivel_minimo INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS pokemons (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipos VARCHAR(80) NOT NULL,
    velocidad DOUBLE PRECISION NOT NULL,
    vida DOUBLE PRECISION NOT NULL,
    atk DOUBLE PRECISION NOT NULL,
    def DOUBLE PRECISION NOT NULL,
    spa DOUBLE PRECISION NOT NULL,
    spd DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS evoluciones (
    id BIGSERIAL PRIMARY KEY,
    preevolucion VARCHAR(100) NOT NULL,
    nivel_evolucion INTEGER NOT NULL,
    evolucion VARCHAR(100) NOT NULL,
    sum_vel DOUBLE PRECISION NOT NULL,
    sum_vida DOUBLE PRECISION NOT NULL,
    sum_atk DOUBLE PRECISION NOT NULL,
    sum_def DOUBLE PRECISION NOT NULL,
    sum_spa DOUBLE PRECISION NOT NULL,
    sum_spd DOUBLE PRECISION NOT NULL,
    tipos VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS record_juego (
    id SMALLINT PRIMARY KEY DEFAULT 1,
    record_victorias INTEGER NOT NULL CHECK (record_victorias >= 0)
);

CREATE VIEW info_mov AS SELECT nombre, tipo, categoria, precision_valor, potencia, estado_nombre, prob_estado, prob_modificacion_stats, curar FROM movimientos;
CREATE VIEW datos_pokemons AS SELECT evolucion AS nombre, sum_vida AS vida, sum_atk AS atk, sum_def AS def, sum_spa AS spa, sum_spd AS spd, sum_vel AS velocidad, (sum_vel + sum_vida + sum_atk + sum_def + sum_spa + sum_spd) AS total FROM evoluciones
UNION ALL
SELECT nombre, vida, atk, def, spa, spd, velocidad, (velocidad + vida + atk + def + spa + spd) AS total FROM pokemons ORDER BY total DESC;
