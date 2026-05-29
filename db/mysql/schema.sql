CREATE TABLE IF NOT EXISTS estados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    unicode_icon VARCHAR(16) NOT NULL,
    prob_moverte DOUBLE NOT NULL,
    potencia DOUBLE NOT NULL,
    prob_irse DOUBLE NOT NULL,
    max_turnos INT NOT NULL
);

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    prioridad INT NOT NULL,
    precision_valor DOUBLE NOT NULL,
    potencia DOUBLE NOT NULL,
    curar DOUBLE NOT NULL,
    subir_vel DOUBLE NOT NULL,
    subir_atk DOUBLE NOT NULL,
    subir_def DOUBLE NOT NULL,
    subir_spa DOUBLE NOT NULL,
    subir_spd DOUBLE NOT NULL,
    buffs_al_ejecutador BOOLEAN NOT NULL,
    estado_nombre VARCHAR(50) NOT NULL,
    prob_estado DOUBLE NOT NULL,
    estado_al_ejecutador BOOLEAN NOT NULL,
    tipos_compatibles TEXT NOT NULL,
    nivel_minimo INT NOT NULL,
    CONSTRAINT fk_movimientos_estado
        FOREIGN KEY (estado_nombre) REFERENCES estados(nombre)
);

CREATE TABLE IF NOT EXISTS pokemons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipos VARCHAR(80) NOT NULL,
    velocidad DOUBLE NOT NULL,
    vida DOUBLE NOT NULL,
    atk DOUBLE NOT NULL,
    def DOUBLE NOT NULL,
    spa DOUBLE NOT NULL,
    spd DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS evoluciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    preevolucion VARCHAR(100) NOT NULL,
    nivel_evolucion INT NOT NULL,
    evolucion VARCHAR(100) NOT NULL,
    sum_vel DOUBLE NOT NULL,
    sum_vida DOUBLE NOT NULL,
    sum_atk DOUBLE NOT NULL,
    sum_def DOUBLE NOT NULL,
    sum_spa DOUBLE NOT NULL,
    sum_spd DOUBLE NOT NULL,
    tipos VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS record_juego (
    id TINYINT PRIMARY KEY DEFAULT 1,
    record_victorias INT NOT NULL
);
