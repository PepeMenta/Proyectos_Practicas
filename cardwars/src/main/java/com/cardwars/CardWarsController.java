package com.cardwars;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cardwars.CardWars.Carta;
import com.cardwars.CardWars.ContextoPartida;
import com.cardwars.CardWars.Criatura;
import com.cardwars.CardWars.CualquierObjetivo;
import com.cardwars.CardWars.Edificio;
import com.cardwars.CardWars.Efecto;
import com.cardwars.CardWars.Floop;
import com.cardwars.CardWars.FloopCura;
import com.cardwars.CardWars.FloopDaño;
import com.cardwars.CardWars.FloopModificadorAtaque;
import com.cardwars.CardWars.FloopModificadorVida;
import com.cardwars.CardWars.Hechizo;
import com.cardwars.CardWars.Jugador;
import com.cardwars.CardWars.ObjetivoFloop;
import com.cardwars.CardWars.ProveedorValor;
import com.cardwars.CardWars.Terreno;
import com.cardwars.CardWars.TipoTerreno;
import com.cardwars.CardWars.ValorFijo;
import com.cardwars.CardWars.ValorPorCartasMano;
import com.cardwars.CardWars.ValorPorCriaturaFloopeada;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;

public class CardWarsController {

    static final String DB_URL =
            System.getenv().getOrDefault(
                    "CARDWARS_DB_URL",
                    "jdbc:postgresql://localhost:5432/cardwars"
            );

    static final String DB_USER =
            System.getenv().getOrDefault(
                    "CARDWARS_DB_USER",
                    "alex"
            );

    static final String DB_PASSWORD =
            System.getenv().getOrDefault(
                    "CARDWARS_DB_PASSWORD",
                    "1234"
            );

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD
        );
    }

static void cargarCartas(
        List<Criatura> criaturas,
        List<Terreno> terrenos,
        List<Hechizo> hechizos,
        List<Edificio> edificios
) {
    cargarCriaturas(criaturas);
    cargarEdificios(edificios);
    cargarHechizos(hechizos);
    cargarTerrenos(terrenos);
}

static void cargarTerrenos(List<Terreno> terrenos) {

}

static void cargarHechizos(List<Hechizo> hechizos) {

}

static void cargarEdificios(List<Edificio> edificios) {

}

static void cargarCriaturas(List<Criatura> criaturas) {

    String sql = """
        SELECT
            criatura.id AS criatura_id,
            criatura.nombre,
            criatura.unicode,
            terreno.nombre AS terreno,
            criatura.coste AS coste_criatura,
            criatura.vida,
            criatura.ataque,

            floop.id AS floop_id,
            floop.tipo,
            floop.coste AS coste_floop,

            efecto.valor,
            efecto.proveedor,
            efecto.objetivo,
            efecto.aliados

        FROM criatura

        JOIN terreno
            ON criatura.terreno_id = terreno.id

        LEFT JOIN floop
            ON criatura.floop_id = floop.id

        LEFT JOIN floop_efecto
            ON floop.id = floop_efecto.floop_id

        LEFT JOIN efecto
            ON floop_efecto.efecto_id = efecto.id

        ORDER BY criatura.id, efecto.id
        """;

    try (
            Connection conn = obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
    ) {

        int criaturaActualId = -1;

        Criatura criaturaActual = null;

        List<Efecto> habilidades = null;

        int costeFloop = 0;

        while (rs.next()) {

            int criaturaId = rs.getInt("criatura_id");

            if (criaturaActual == null || criaturaActualId != criaturaId) {

                if (criaturaActual != null) {

                    criaturaActual.floop = habilidades.isEmpty()
                            ? null
                            : new Floop(costeFloop, habilidades);

                    criaturas.add(criaturaActual);
                }

                criaturaActualId = criaturaId;

                habilidades = new ArrayList<>();

                costeFloop = rs.getInt("coste_floop");

                criaturaActual = new Criatura(
                        rs.getString("nombre"),
                        rs.getString("unicode"),
                        TipoTerreno.valueOf(rs.getString("terreno")),
                        rs.getInt("vida"),
                        rs.getInt("ataque"),
                        null,
                        0,
                        true,
                        rs.getInt("coste_criatura")
                );

                criaturaActual.vidaMax = criaturaActual.vida;
            }

            String tipo = rs.getString("tipo");

            if (tipo != null) {

                habilidades.add(
                        crearHabilidadFloop(
                                tipo,
                                rs.getInt("valor"),
                                rs.getString("proveedor"),
                                toIntArray(rs.getArray("objetivo")),
                                toBooleanArray(rs.getArray("aliados"))
                        )
                );
            }
        }

        if (criaturaActual != null) {

            criaturaActual.floop = habilidades.isEmpty()
                    ? null
                    : new Floop(costeFloop, habilidades);

            criaturas.add(criaturaActual);
        }

    } catch (SQLException e) {

        throw new RuntimeException(
                "No se pudieron cargar las criaturas de CardWars",
                e
        );
    }
}
private static int[] toIntArray(java.sql.Array sqlArray)
        throws SQLException {

    if (sqlArray == null) {
        return new int[0];
    }

    Integer[] array = (Integer[]) sqlArray.getArray();

    return Arrays.stream(array)
            .mapToInt(Integer::intValue)
            .toArray();
}
private static boolean[] toBooleanArray(java.sql.Array sqlArray)
        throws SQLException {

    if (sqlArray == null) {
        return new boolean[0];
    }

    Boolean[] array = (Boolean[]) sqlArray.getArray();

    boolean[] booleanArray = new boolean[array.length];

    for (int i = 0; i < array.length; i++) {
        booleanArray[i] = array[i];
    }

    return booleanArray;
}

static boolean carrilCorrecto(String posicion) {

    boolean correcto = true;

    try {

        int pos = Integer.parseInt(posicion) - 1;

        if (!(0 <= pos && pos < 4)) {
            correcto = false;
        }

    } catch (Exception e) {
        correcto = false;
    }

    return correcto;
}
private static Efecto crearHabilidadFloop(
        String tipo,
        int valor,
        String proveedor,
        int[] posiciones,
        boolean[] aliados
) {

    ProveedorValor proveedorValor =
            crearProveedorValor(valor, proveedor);

    ObjetivoFloop objetivo =
            new CualquierObjetivo(posiciones, aliados);

    switch (tipo) {

        case "FloopDaño":

            FloopDaño daño =
                    new FloopDaño(proveedorValor);

            daño.objetivo = objetivo;

            return daño;

        case "FloopCura":

            FloopCura cura =
                    new FloopCura(proveedorValor);

            cura.objetivo = objetivo;

            return cura;

        case "FloopModificadorAtaque":
        case "FloopModificadorDaño":

            FloopModificadorAtaque modificadorAtaque =
                    new FloopModificadorAtaque();

            modificadorAtaque.modificacion = proveedorValor;

            modificadorAtaque.objetivo = objetivo;

            return modificadorAtaque;

        case "FloopModificadorVida":

            FloopModificadorVida modificadorVida =
                    new FloopModificadorVida();

            modificadorVida.modificacion = proveedorValor;

            modificadorVida.objetivo = objetivo;

            return modificadorVida;

        default:

            throw new IllegalArgumentException(
                    "Tipo de floop desconocido: " + tipo
            );
    }
}

private static ProveedorValor crearProveedorValor(
        int valor,
        String proveedor
) {

    switch (proveedor) {

        case "fijo":
            return new ValorFijo(valor);

        case "cartas_mano":
            return new ValorPorCartasMano(valor);

        case "criatura_floopeada":
            return new ValorPorCriaturaFloopeada(valor);

        default:
            throw new IllegalArgumentException(
                    "Proveedor de valor desconocido: " + proveedor
            );
    }
}
    @FXML
    private HBox BoxMano;

    @FXML
    private HBox BoxManoBaraja;

    @FXML
    private VBox Tablero;

    private ArrayList<Carta> cartas = new ArrayList<>(); 
    private ArrayList<Criatura> criaturas = new ArrayList<>(); 
    private ArrayList<Terreno> terrenos = new ArrayList<>(); 
    private ArrayList<Hechizo> hechizos = new ArrayList<>(); 
    private ArrayList<Edificio> edificios = new ArrayList<>(); 
//     private Jugador jugador = new Jugador();
    private ContextoPartida partida = new ContextoPartida("Finn","Jake");

    public void initialize() {
        System.out.println("ADasdadsad");
        cargarCartas(criaturas, terrenos, hechizos, edificios);
        System.out.println("gola");
        partida.primeraMano();
        System.out.println(partida.jugadores.get(0).mano.size());
        for (int i = 0; i < partida.jugadores.get(0).mano.size(); i++) {
                System.out.println("ass"+partida.jugadores.get(0).mano.get(i).nombre);
                BoxMano.getChildren().add(((Criatura)partida.jugadores.get(0).mano.get(i)).getImageView());
        }
     }

}