\c postgres;
DROP DATABASE IF EXISTS cardwars;
CREATE DATABASE cardwars;

\c cardwars;

-- FLOOP
CREATE TABLE floop (
    id SERIAL PRIMARY KEY,
    tipo text,
    coste INT
);

-- EFECTO
CREATE TABLE efecto (
    id SERIAL PRIMARY KEY,
    valor int,
    proveedor text,
    objetivo int[],
    aliados boolean[]
);

-- TABLA INTERMEDIA FLOOP - EFECTO
CREATE TABLE floop_efecto (
    id SERIAL PRIMARY KEY,
    floop_id INT,
    efecto_id INT,
    FOREIGN KEY (floop_id) REFERENCES floop(id),
    FOREIGN KEY (efecto_id) REFERENCES efecto(id)
);


CREATE TABLE terreno (
    id SERIAL PRIMARY KEY,
    nombre text,
    unicode text,
    coste int
);

INSERT INTO terreno (nombre, unicode, coste) VALUES
('LLANURA', '🌾', 0),
('DESIERTO', '🏜️', 0),
('MAIZ', '🌽', 0),
('ARCOIRIS', '🌈', 0),
('PANTANO', '🏞️', 0),
('BUENAS_TIERRAS', '🏞️', 0);

CREATE TABLE hechizo (
    id SERIAL PRIMARY KEY,
    nombre text,
    unicode text,
    coste int
     
);

-- CRIATURA
CREATE TABLE criatura (
    id SERIAL PRIMARY KEY,
    nombre TEXT,
    unicode TEXT,
    terreno_id INT,
    coste int,
    vida INT,
    ataque INT,
    floop_id INT,
    FOREIGN KEY (terreno_id) REFERENCES terreno(id),
    FOREIGN KEY (floop_id) REFERENCES floop(id)
);

CREATE VIEW floops AS SELECT floop.tipo, floop.coste, efecto.valor, efecto.proveedor, efecto.objetivo, efecto.aliados FROM floop_efecto, floop, efecto WHERE floop.id = floop_efecto.floop_id AND floop_efecto.efecto_id = efecto.id;

-- INSERTS CARTAS REALES
-- Tipos de floop usados:
-- FloopDaño, FloopCura, FloopModificadorAtaque, FloopModificadorVida
-- Convencion usada en efecto:
-- proveedor = 'fijo', 'cartas_mano' o 'criatura_floopeada'
-- objetivo = posiciones relativas respecto a la criatura que usa el floop:
--            0 mismo carril, -1 un carril a la izquierda, 1 un carril a la derecha, etc.
--            como solo hay 4 carriles, el rango valido posible es de -3 a 3
-- aliados = true para el mismo bando, false para el enemigo

-- Husker Knight: criaturas adyacentes ganan +2 ataque.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopModificadorAtaque', 3) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (2, 'fijo', ARRAY[-1, 1], ARRAY[true, true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Husker Knight', '/images/Husker_Knight.webp', (SELECT id FROM terreno WHERE nombre = 'MAIZ'), 1, 3, 6, currval('floop_id_seq'));

-- Earl: criaturas adyacentes ganan +3 ataque.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopModificadorAtaque', 1) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (3, 'fijo', ARRAY[-1, 1], ARRAY[true, true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Earl', '/images/Earl.webp', (SELECT id FROM terreno WHERE nombre = 'ARCOIRIS'), 3, 5, 10, currval('floop_id_seq'));

-- Fummy: esta criatura y las adyacentes ganan +6 defensa.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopModificadorVida', 2) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (6, 'fijo', ARRAY[-1, 0, 1], ARRAY[true, true, true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Fummy', '/images/Fummy.webp', (SELECT id FROM terreno WHERE nombre = 'DESIERTO'), 4, 24, 10, currval('floop_id_seq'));

-- Field Reaper: baja en 4 el ataque de todas las criaturas enemigas.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopModificadorAtaque', 3) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (-4, 'fijo', ARRAY[-3, -2, -1, 0, 1, 2, 3], ARRAY[false, false, false, false, false, false, false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Field Reaper', '/images/Field_Reaper.webp', (SELECT id FROM terreno WHERE nombre = 'MAIZ'), 4, 13, 25, currval('floop_id_seq'));

-- Hot Eyebat: hace 4 de dano a cualquier criatura enemiga.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 2) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (4, 'fijo', ARRAY[-3, -2, -1, 0, 1, 2, 3], ARRAY[false, false, false, false, false, false, false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Hot Eyebat', '/images/Hot_Eyebat.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 2, 9, 8, currval('floop_id_seq'));

-- Banshe Princess: hace 4 de dano a la criatura del carril opuesto.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 3) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (4, 'fijo', ARRAY[0], ARRAY[false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Banshe Princess', '/images/Banshe_Princess.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 1, 2, 7, currval('floop_id_seq'));

-- Bog Bum: hace 5 de dano a la criatura del carril opuesto.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 2) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (5, 'fijo', ARRAY[0], ARRAY[false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Bog Bum', '/images/Bog_Bum.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 2, 10, 8, currval('floop_id_seq'));

-- Gray Eyebat: hace 2 de dano a cualquier criatura enemiga.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 1) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (2, 'fijo', ARRAY[-3, -2, -1, 0, 1, 2, 3], ARRAY[false, false, false, false, false, false, false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Gray Eyebat', '/images/Gray_Eyebat.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 1, 5, 2, currval('floop_id_seq'));

-- Herculeye: hace 2 de dano por cada carta en tu mano a la criatura del carril opuesto.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 1) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (2, 'cartas_mano', ARRAY[0], ARRAY[false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Herculeye', '/images/Herculeye.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 2, 9, 11, currval('floop_id_seq'));

-- Bog BanShe Angel: hace 4 de dano por cada carta en tu mano a la criatura del carril opuesto.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 3) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (4, 'cartas_mano', ARRAY[0], ARRAY[false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Bog BanShe Angel', '/images/Bog_BanShe_Angel.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 4, 15, 24, currval('floop_id_seq'));

-- Nicelands Eye Bat: cura 4 por cada carta en tu mano a una criatura aliada.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopCura', 3) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (4, 'cartas_mano', ARRAY[-3, -2, -1, 0, 1, 2, 3], ARRAY[true, true, true, true, true, true, true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Nicelands Eye Bat', '/images/Nicelands_Eye_Bat.png', (SELECT id FROM terreno WHERE nombre = 'LLANURA'), 2, 10, 7, currval('floop_id_seq'));

-- Chest Burster: hace 2 de dano al heroe enemigo por cada carta en tu mano.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 2) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (2, 'cartas_mano', ARRAY[]::int[], ARRAY[]::boolean[])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Chest Burster', '/images/Chest_Burster.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 4, 8, 5, currval('floop_id_seq'));

-- Log Knight: gana +4 ataque por cada criatura floopeada este turno.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopModificadorAtaque', 2) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (4, 'criatura_floopeada', ARRAY[0], ARRAY[true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Log Knight', '/images/Log_Knight.webp', (SELECT id FROM terreno WHERE nombre = 'MAIZ'), 0, 20, 10, currval('floop_id_seq'));

-- Bald Man's Throne: hace 5 de dano por cada criatura floopeada este turno a la criatura enemiga opuesta.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopDaño', 2) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (5, 'criatura_floopeada', ARRAY[0], ARRAY[false])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Bald Man''s Throne', '/images/Bald_Man''s_Throne.webp', (SELECT id FROM terreno WHERE nombre = 'PANTANO'), 0, 15, 15, currval('floop_id_seq'));

-- Fatapillar: cura 5 por cada criatura floopeada este turno a una criatura.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopCura', 1) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (5, 'criatura_floopeada', ARRAY[-3, -2, -1, 0, 1, 2, 3], ARRAY[true, true, true, true, true, true, true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Fatapillar', '/images/Fatapillar.webp', (SELECT id FROM terreno WHERE nombre = 'LLANURA'), 0, 27, 3, currval('floop_id_seq'));

-- Green Party Ogre: sube la defensa 4 por cada criatura floopeada este turno a una criatura aliada.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopModificadorVida', 2) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (4, 'criatura_floopeada', ARRAY[-3, -2, -1, 0, 1, 2, 3], ARRAY[true, true, true, true, true, true, true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Green Party Ogre', '/images/Green_Party_Ogre.webp', (SELECT id FROM terreno WHERE nombre = 'DESIERTO'), 0, 19, 11, currval('floop_id_seq'));

-- Music Mallard: imagen descargada desde Fandom en src/main/resources/images/Music_Mallard.png.
WITH nuevo_floop AS (
    INSERT INTO floop (tipo, coste) VALUES ('FloopCura', 3) RETURNING id
), nuevo_efecto AS (
    INSERT INTO efecto (valor, proveedor, objetivo, aliados)
    VALUES (5, 'fijo', ARRAY[-1, 0, 1], ARRAY[true, true, true])
    RETURNING id
)
INSERT INTO floop_efecto (floop_id, efecto_id)
SELECT nuevo_floop.id, nuevo_efecto.id FROM nuevo_floop, nuevo_efecto;

INSERT INTO criatura (nombre, unicode, terreno_id, coste, vida, ataque, floop_id)
VALUES ('Music Mallard', '/images/Music_Mallard.webp', (SELECT id FROM terreno WHERE nombre = 'BUENAS_TIERRAS'), 2, 4, 12, currval('floop_id_seq'));
