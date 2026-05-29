SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE movimientos;
TRUNCATE TABLE pokemons;
TRUNCATE TABLE evoluciones;
TRUNCATE TABLE estados;
TRUNCATE TABLE record_juego;

SET FOREIGN_KEY_CHECKS = 1;

LOAD DATA LOCAL INFILE 'src/files/estados'
INTO TABLE estados
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
(nombre, unicode_icon, prob_moverte, potencia, prob_irse, max_turnos);

LOAD DATA LOCAL INFILE 'src/files/movimientos'
INTO TABLE movimientos
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
(
    nombre, tipo, categoria, prioridad, precision_valor, potencia, curar,
    subir_vel, subir_atk, subir_def, subir_spa, subir_spd, buffs_al_ejecutador,
    estado_nombre, prob_estado, estado_al_ejecutador, tipos_compatibles, nivel_minimo
);

LOAD DATA LOCAL INFILE 'src/files/pokemons'
INTO TABLE pokemons
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
(nombre, tipos, velocidad, vida, atk, def, spa, spd);

LOAD DATA LOCAL INFILE 'src/files/evoluciones'
INTO TABLE evoluciones
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
(
    preevolucion, nivel_evolucion, evolucion, sum_vel, sum_vida,
    sum_atk, sum_def, sum_spa, sum_spd, tipos
);

LOAD DATA LOCAL INFILE 'src/files/record'
INTO TABLE record_juego
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
(@record_victorias)
SET id = 1, record_victorias = @record_victorias;
