import java.lang.ref.Cleaner.Cleanable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Objects.Horizon.*;

public class Horizon_BoardGame {
    static Random random = new Random();
    static Scanner sc = new Scanner(System.in);
    static final String DB_URL = System.getenv().getOrDefault("HORIZON_DB_URL", "jdbc:postgresql://localhost:5432/horizon");
    static final String DB_USER = System.getenv().getOrDefault("HORIZON_DB_USER", "alex");
    static final String DB_PASSWORD = System.getenv().getOrDefault("HORIZON_DB_PASSWORD", "1234");
    static final String N8N_WEBHOOK_URL = System.getenv().getOrDefault("N8N_WEBHOOK_URL", "http://127.0.0.1:5678/webhook-test/horizon-ia");
    static final int N8N_TIMEOUT_SECONDS = Integer.parseInt(System.getenv().getOrDefault("N8N_TIMEOUT_SECONDS", "60"));
    static final int COSTES = 10;
    static final int REFUERZO_MOV = 5;
    static final int REFUERZO_ATA = 125;
    static final int REFUERZO_ELI = 2500;
    public static void main(String[] args) throws SQLException {
        List<Partida> partidas;
        List <Pieza> piezas = new ArrayList<>();
        cargarPiezas(piezas);
        Equipo jugador = new Equipo(piezas);
        Equipo rival = new Equipo(piezas);
        rival.add(piezas.get(4));
        partidas = generarPartidas(piezas);
        for (int i = 0; i < partidas.size(); i++) {
            System.out.println("NIVEL: "+(i+1));
            partidas.get(i).mostrarPartida();
        }
        sc.nextLine();

        comenzarPartidas(partidas, jugador, piezas);
    }
    public static void comenzarPartidas (List<Partida> partidas, Equipo jugador, List<Pieza> piezas) {
        boolean victoria = false;
        int derrotas = 0;

        for (int i = 0; i < partidas.size(); i++) {
            clear();
            System.out.println("NIVEL "+(i+1));
            sc.nextLine();
            jugador.inventario.maxRubies = partidas.get(i).maxRubies;
            if (17 <= i)victoria = partida(partidas.get(i), jugador, piezas);
            else victoria = true;
            if (victoria) {
                System.out.println("¡VICTORIA!\n¡"+(partidas.get(i).piezaQueDesbloquea != null ? "DESBLOQUEAS A "+partidas.get(i).piezaQueDesbloquea.nombre+" "+partidas.get(i).piezaQueDesbloquea.unicode+"!" : ""));
                if (partidas.get(i).piezaQueDesbloquea != null)jugador.addInventario(new Pieza(partidas.get(i).piezaQueDesbloquea));
                sc.nextLine();
            }
            else {
                System.out.println("HAS PERDIDO...");
                derrotas++;
                partidas.get(i).reiniciar();
                i--;
            }
        }

        System.out.println("TE LO HAS PASADO EN "+derrotas+" DERROTAS");
    }
    public static boolean partida (Partida partida, Equipo jugador, List<Pieza> piezas) {
        Tablero tablero = partida.tablero;
        Equipo rival = partida.equipo;
        jugador = seleccionPiezas(jugador.inventario);

        // colocarPiezasAleatorio(jugador, tablero, true);
        colocarPiezasAleatorio(rival, tablero, false);

        while (jugador.numPiezasVivas() > 0 && rival.numPiezasVivas() > 0) {
            clear();
            menuJugador(tablero, jugador, rival);
            // turnoRival(tablero, rival, jugador, true);
            if (rival.numPiezasVivas() > 0) turnoRival(tablero, jugador, rival, false);
            
            tablero.recuperarAccionesPiezas();
        }

        return rival.numPiezasVivas() == 0;
    }
    private static void colocarPiezasAleatorio (Equipo rival, Tablero tablero, boolean jugador) {
        int f;
        int c;
        int colocados = 0;
        int sumJug = jugador ? 6 : 0;

        while(colocados != rival.size()) {
            f = random.nextInt(0,2) + sumJug;
            c = random.nextInt(tablero.tablero.length);
            if (tablero.piezaPorCoordenadas(f, c) == null) {
                tablero.entrarPieza(rival.get(colocados), f, c);
                colocados++;
            }
        }

        for (int i = 0; i < rival.size(); i++) {
            rival.get(i).aliado = jugador;
        }
    }
    private static void colocarPiezasEnTablero (Equipo jugador, Tablero tablero) {
        String coordenada = "";
        int [] coor = new int[2];
        int colocados = 0;
        do{
            clear();
            tablero.mostrarTablero();
            System.out.println("\n");
            jugador.mostrarEquipoPorColocar();

            System.out.print("Coordenada:");
            coordenada = sc.nextLine();
            coor = tablero.procesarCoordenadas(coordenada);

            if (tablero.colocarCorrecto(coordenada) && jugador.get(colocados).c == -1 && tablero.piezaPorCoordenadas(coor[0], coor[1]) == null) {
                tablero.entrarPieza(jugador.get(colocados), coor[0], coor[1]);
                colocados++;
            }

        }while(colocados != jugador.size());
    }
    private static List<DecisionRival> accionesDe1Pieza (int numP, List<DecisionRival> acciones) {
        List<DecisionRival> accionesDe1Pieza = new ArrayList<>();
        for (int i = 0; i < acciones.size(); i++) {
            if (acciones.get(i).pieza == numP) {
                accionesDe1Pieza.add(acciones.get(i));
            }
        }
        return accionesDe1Pieza;
    } 
    private static int refuerzoMasAlto (List<DecisionRival> acciones) {
        int max = 0;
        for (int i = 0; i < acciones.size(); i++) {
            if (max <= acciones.get(i).refuerzo) max = acciones.get(i).refuerzo;
        }
        return max;
    }
    private static DecisionRival escogerDecision (List<DecisionRival> acciones) {
        int max = refuerzoMasAlto(acciones);
        List<DecisionRival> mejoresAcciones = new ArrayList<>();

        for (int i = 0; i < acciones.size(); i++) {
            if (max == acciones.get(i).refuerzo) mejoresAcciones.add(acciones.get(i));
        }

        return mejoresAcciones.get(random.nextInt(mejoresAcciones.size()));
    }
    private static void realizarDecision (Tablero tablero, Pieza pieza, DecisionRival decision) {

        if (decision.accion.equals("mover")) {
            int [] coor = new int [2];
            coor[0] = Integer.parseInt(decision.destino.split(" ")[0]);
            coor[1] = Integer.parseInt(decision.destino.split(" ")[1]);
            tablero.mostrarMovimientosPosibles(pieza, coor);
            System.out.println(pieza.unicode+" se mueve a "+tablero.procesarCoordenadasLectura(decision.destino));

            tablero.gestionDatosTrasMover(coor, pieza);
        }
        else if (decision.accion.equals("atacar")) {
            tablero.mostrarAtaquesPosibles(pieza);
            int [] coor = new int [2];
            coor[0] = Integer.parseInt(decision.destino.split(" ")[0]);
            coor[1] = Integer.parseInt(decision.destino.split(" ")[1]);

            System.out.println(pieza.unicode+" ataca a "+tablero.piezaPorCoordenadas(decision.destino).unicode+" en "+tablero.procesarCoordenadasLectura(decision.destino));
            tablero.gestionDatosTrasAtaque(tablero.procesarCoordenadasLectura(decision.destino), pieza);
        }
        System.out.println(":"+decision.refuerzo);
    }
    private static void turnoRival (Tablero tablero, Equipo jugador, Equipo rival, boolean turno) {
        List<DecisionRival> acciones = new ArrayList<>();
        List<DecisionRival> acciones1Pieza = new ArrayList<>();
        DecisionRival decision;

        for (int i = 0; i < rival.size(); i++) {
            if (rival.get(i).estaVivo()) {
                clear();
                tablero.mostrarTablero();
                System.out.println(turno ? "\nTurno Jugador..." : "\nTurno rival...");
                if (!turno) mostrarEquipos(jugador, rival);
                else mostrarEquipos(rival, jugador);
                System.out.println(rival.get(i).unicode+" seleccionado "+tablero.procesarCoordenadasLectura(rival.get(i).f+" "+rival.get(i).c));
                while (rival.get(i).acciones > 0 && jugador.numPiezasVivas() > 0) {
                    acciones = accionesPosibles(rival, tablero);
                    acciones1Pieza = accionesDe1Pieza(i, acciones);
                    if (acciones1Pieza.isEmpty()) break;

                    decision = escogerDecision(acciones1Pieza);
                    realizarDecision(tablero, rival.get(i), decision);

                    sc.nextLine();
                }
            }
        }

        // sc.nextLine();
    }
    private static int sumRefuerzoCentroMovimiento (int f, int c) {
        if (f > 3) f = 7-f;
        if (c > 3) c = 7-c;

        return f + c;
    }   
    private static int calcularRefuerzoMovimiento (Tablero tablero, Pieza pieza, int f, int c) {
        Pieza p = new Pieza(pieza);
        Tablero tableroTemporal = new Tablero(tablero);
        List <DecisionRival> decisiones = new ArrayList<>();
        int refuerzo = 0;

        tableroTemporal.tablero[p.f][p.c].pieza = null;
        tableroTemporal.tablero[f][c].pieza = p;
        p.acciones -= tablero.distanciaDesplazada(p.f, p.c, f, c) == p.movimiento + 1 ? 2 : 1;
        p.setCoordenadas(f, c);
        p.movido = true;
        p.ataco = false;
        tableroTemporal.movimientosPosibles(p);
        tableroTemporal.ataquesPosibles(p);
        decisiones = accionesPosiblesPieza(p, tableroTemporal);
        
        for (int i = 0; i < decisiones.size(); i++) {
            // System.out.println(p.unicode+" "+decisiones.get(i).accion+" "+decisiones.get(i).destino+" "+decisiones.get(i).refuerzo);
            refuerzo += decisiones.get(i).refuerzo;
        }
        Habitat h = tablero.tablero[f][c].habitat;
        if (!decisiones.isEmpty()) refuerzo /= decisiones.size()/0.5;
        refuerzo += REFUERZO_MOV + h.altura*10 + (pieza.habitat == h ? pieza.buffHabitat : 0)*10;
        refuerzo += sumRefuerzoCentroMovimiento(f, c);

        // System.out.println(refuerzo);
        // System.out.println(f+" "+c);
        return refuerzo;
    }
    private static List<DecisionRival> accionesPosiblesPieza (Pieza pieza, Tablero tablero) {
        List <DecisionRival> decisiones = new ArrayList<>();
        Pieza recibidor;

        if (pieza.acciones <= 0 || !pieza.estaVivo()) return decisiones;
        
        for (int j = 0; j < pieza.movimientosPosibles.size(); j++) {
            if (!pieza.movido) {
                    Habitat h = tablero.tablero[pieza.movimientosPosibles.get(j)[0]][pieza.movimientosPosibles.get(j)[1]].habitat;
                    decisiones.add(new DecisionRival(1, "mover", pieza.movimientosPosibles.get(j)[0]+" "+pieza.movimientosPosibles.get(j)[1], REFUERZO_MOV + h.altura*10 + (pieza.habitat == h ? pieza.buffHabitat : 0)*10));                
            }    
        }
        for (int j = 0; j < pieza.ataquesPosibles.size(); j++) {
            recibidor = tablero.piezaPorCoordenadas(pieza.ataquesPosibles.get(j)[0], pieza.ataquesPosibles.get(j)[1]);
            if (recibidor != null && recibidor.aliado != pieza.aliado && !pieza.ataco) {
                if (puedeEliminar(pieza, recibidor, tablero)) decisiones.add(new DecisionRival(0, "atacar", pieza.ataquesPosibles.get(j)[0]+" "+pieza.ataquesPosibles.get(j)[1], REFUERZO_ELI)); 
                else decisiones.add(new DecisionRival(0, "atacar", pieza.ataquesPosibles.get(j)[0]+" "+pieza.ataquesPosibles.get(j)[1], REFUERZO_ATA*cuantoDaño(pieza, recibidor, tablero)));                
            }
        }
        return decisiones;
    }
    private static List<DecisionRival> accionesPosibles (Equipo rival, Tablero tablero) {
        List <DecisionRival> decisiones = new ArrayList<>();
        Pieza recibidor;

        for (int i = 0; i < rival.size(); i++) {
            if (rival.get(i).acciones <= 0 || !rival.get(i).estaVivo() || rival.get(i).f < 0 || rival.get(i).c < 0) continue;

            tablero.movimientosPosibles(rival.get(i));
            tablero.ataquesPosibles(rival.get(i));
            for (int j = 0; j < rival.get(i).movimientosPosibles.size(); j++) {
                if (!rival.get(i).movido) decisiones.add(new DecisionRival(i, "mover", rival.get(i).movimientosPosibles.get(j)[0]+" "+rival.get(i).movimientosPosibles.get(j)[1], calcularRefuerzoMovimiento(tablero, rival.get(i), rival.get(i).movimientosPosibles.get(j)[0], rival.get(i).movimientosPosibles.get(j)[1])));                
            }
            for (int j = 0; j < rival.get(i).ataquesPosibles.size(); j++) {
                recibidor = tablero.piezaPorCoordenadas(rival.get(i).ataquesPosibles.get(j)[0], rival.get(i).ataquesPosibles.get(j)[1]);
                // System.out.println(i+1+". "+rival.get(i).ataquesPosibles.get(j)[0]+" "+rival.get(i).ataquesPosibles.get(j)[1]+" "+(recibidor != null ? recibidor.unicode : "null"));
                if (recibidor != null && recibidor.aliado != rival.get(i).aliado && !rival.get(i).ataco) {
                    if (puedeEliminar(rival.get(i), recibidor, tablero)) decisiones.add(new DecisionRival(i, "atacar", rival.get(i).ataquesPosibles.get(j)[0]+" "+rival.get(i).ataquesPosibles.get(j)[1], REFUERZO_ELI)); 
                    else decisiones.add(new DecisionRival(i, "atacar", rival.get(i).ataquesPosibles.get(j)[0]+" "+rival.get(i).ataquesPosibles.get(j)[1], REFUERZO_ATA*cuantoDaño(rival.get(i), recibidor, tablero)));                
                }
            }
        }

        // for (int i = 0; i < decisiones.size(); i++) {
        //     System.out.println(decisiones.get(i).toString());
        // }
        // sc.nextLine();
        return decisiones;
    }
    private static boolean puedeEliminar (Pieza atacante, Pieza recibidor, Tablero tablero) {
        int daño = atacante.ataque;
        daño += tablero.tablero[atacante.f][atacante.c].habitat.altura;
        daño += atacante.afinidadHabitat(tablero.tablero[atacante.f][atacante.c].habitat);
            
        daño -= tablero.tablero[recibidor.f][recibidor.c].habitat.altura;
        daño -= recibidor.afinidadHabitat(tablero.tablero[recibidor.f][recibidor.c].habitat);
        return recibidor.vida-daño <= 0;
    }
    private static int cuantoDaño (Pieza atacante, Pieza recibidor, Tablero tablero) {
        int daño = atacante.ataque;
        daño += tablero.tablero[atacante.f][atacante.c].habitat.altura;
        daño += atacante.afinidadHabitat(tablero.tablero[atacante.f][atacante.c].habitat);
            
        daño -= tablero.tablero[recibidor.f][recibidor.c].habitat.altura;
        daño -= recibidor.afinidadHabitat(tablero.tablero[recibidor.f][recibidor.c].habitat);
        return daño < 0 ? 0 : daño;
    }
    private static void menuJugador (Tablero tablero, Equipo jugador, Equipo rival) {
        String opcion = "";
        String coordenada = "";

        while (!opcion.equals("0") && rival.numPiezasVivas() > 0) {
            clear();
            tablero.mostrarTablero();
            mostrarEquipos(jugador, rival);
            
            do{
                System.out.println("\n1. Usar Pieza\n0. Acabar Turno");
                opcion = sc.nextLine();
            }while(!opcion.equals("1") && !opcion.equals("0"));

            switch (opcion) {
                case "1":
                    if (jugador.numPiezasVivas()>1) {
                        System.out.print("Pieza: ");
                        do{
                            coordenada = sc.nextLine();
                        }while((!jugador.entradaCorrectaEquipo(coordenada) || jugador.get(Integer.parseInt(coordenada)-1).noMasAcciones() || !jugador.get(Integer.parseInt(coordenada)-1).estaVivo()) && !coordenada.equals("0"));
                    }
                    else coordenada = jugador.unicaOpcionViva();
                    
                    if (!coordenada.equals("0")) {
                        tablero.usarPieza(jugador.get(Integer.parseInt(coordenada)-1));
                        sc.nextLine();
                    }
                    else opcion = "0";
                    break;
            
                case "0":
                    break;
            }
        }
    }
    private static void equiposPersonalizados (Equipo jugador, Equipo rival, Tablero tablero, List <Pieza> piezas) {
        jugador.add(piezas.get(0));
        rival.add(piezas.get(1));
        tablero.entrarPieza(jugador.get(0), 2, 3);
        tablero.entrarPieza(rival.get(0), 4, 4);
        jugador.get(0).aliado=true;
        rival.get(0).aliado=false;
    }
    private static Equipo seleccionAleatorio (List <Pieza> piezas) {
        Equipo equipoAleatorio = new Equipo();
        int rubies = COSTES;
        List <Integer> piezasPosibles;
        equipoAleatorio.inventario.todasPiezas(piezas);;
        do{
            piezasPosibles = piezasPosibles(piezas, rubies);
            rubies = equipoAleatorio.add(""+piezasPosibles.get(random.nextInt(piezasPosibles.size())), piezas, rubies);
        }while(rubies > 0);

        return equipoAleatorio;
    }
    private static List <Integer> piezasPosibles (List <Pieza> piezas, int rubies) {
        List <Integer> piezasPosibles = new ArrayList<>();
        
        for (int i = 0; i < piezas.size(); i++) {
            if (piezas.get(i).coste <= rubies) piezasPosibles.add(i+1);
        }

        return piezasPosibles;
    }
    private static Equipo seleccionPiezas (Inventario inventario) {
        List<Pieza> piezas = inventario.piezasDesbloqueadas;
        piezas.sort((a, b) -> a.coste - b.coste);
        String opcion = "";
        Equipo jugador = new Equipo();
        jugador.inventario = inventario;
        jugador.equipo.clear();
        do {
            clear();
            for (int i = 0; i < piezas.size(); i++) {
                System.out.print(i+1+"."+" ".repeat(3-(int)Math.floor(Math.log10(i+1))));
                piezas.get(i).mostrarPiezaDetallada();
            }
            System.out.println("\nQue Piezas quieres usar:");
            System.out.println(inventario.maxRubies+"🔶");
            do {
                opcion = sc.nextLine();
            }while(!jugador.entradaCorrectaInventario(opcion));
            inventario.maxRubies = jugador.add(opcion, piezas, inventario.maxRubies);
            jugador.get(jugador.size()-1).aliado = true;
        }while(!opcion.equals("0") && inventario.maxRubies > 0);

        return jugador;
    }
    private static void mostrarEquipos (Equipo jugador, Equipo rival) {
        System.out.println("\n🔵");
        jugador.mostrarEquipo();
        System.out.println("\n🔴");
        rival.mostrarEquipo();
    }
    private static void clear() {
        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void cargarPiezas (List <Pieza> piezas) throws SQLException {
        String sql = "SELECT nombre, unicode, habitat, buffHabitat, movimiento, coste, vida, ataque, alcanceAtaque, saltoAtaque, adyacenteAtaque, acciones FROM pieza ORDER BY id";

        try (Connection conn = obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            int id = 0;
            while (rs.next()) {
                piezas.add(new Pieza(
                    id,
                    rs.getString("nombre"),
                    rs.getString("unicode"),
                    Habitat.valueOf(rs.getString("habitat")),
                    rs.getInt("buffHabitat"),
                    rs.getInt("movimiento"),
                    rs.getInt("coste"),
                    rs.getInt("vida"),
                    rs.getInt("ataque"),
                    rs.getInt("alcanceAtaque"),
                    rs.getInt("saltoAtaque"),
                    rs.getInt("adyacenteAtaque"),
                    rs.getInt("acciones")
                ));
                id++;
            }
        }
    }
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    private static List<Partida> generarPartidas (List<Pieza> piezas) {
        List<Partida> partidas = new ArrayList<>();
        int [][][] indexs = {
            {{18,18}/*Piezas combate*/,{18}/*Pieza Desbloquea*/,{4}/*Max Rubies*/},
            {{2,3,4}/*Piezas combate*/,{4}/*Pieza Desbloquea*/,{6}/*Max Rubies*/},
            {{7,7,1}/*Piezas combate*/,{7}/*Pieza Desbloquea*/,{6}/*Max Rubies*/},
            {{5,5}/*Piezas combate*/,{5}/*Pieza Desbloquea*/,{7}/*Max Rubies*/},
            {{6,1,6}/*Piezas combate*/,{6}/*Pieza Desbloquea*/,{7}/*Max Rubies*/},
            {{8,3,7,3}/*Piezas combate*/,{8}/*Pieza Desbloquea*/,{8}/*Max Rubies*/},
            {{16,4,0}/*Piezas combate*/,{16}/*Pieza Desbloquea*/,{6}/*Max Rubies*/},
            {{15,15,5,3}/*Piezas combate*/,{15}/*Pieza Desbloquea*/,{7}/*Max Rubies*/},
            {{17,17,2}/*Piezas combate*/,{17}/*Pieza Desbloquea*/,{7}/*Max Rubies*/},
            {{9,9,4}/*Piezas combate*/,{9}/*Pieza Desbloquea*/,{9}/*Max Rubies*/},
            {{10,7}/*Piezas combate*/,{10}/*Pieza Desbloquea*/,{5}/*Max Rubies*/},
            {{19,19,5}/*Piezas combate*/,{19}/*Pieza Desbloquea*/,{8}/*Max Rubies*/},
            {{11,11,11}/*Piezas combate*/,{11}/*Pieza Desbloquea*/,{10}/*Max Rubies*/},
            {{13,2,2}/*Piezas combate*/,{13}/*Pieza Desbloquea*/,{10}/*Max Rubies*/},
            {{14,4,4}/*Piezas combate*/,{14}/*Pieza Desbloquea*/,{12}/*Max Rubies*/},
            {{12,7,7}/*Piezas combate*/,{12}/*Pieza Desbloquea*/,{13}/*Max Rubies*/},
            {{13,14,12}/*Piezas combate*/,{-1}/*Pieza Desbloquea*/,{18}/*Max Rubies*/},
            {{13,14,12,17,16,15,5}/*Piezas combate*/,{-1}/*Pieza Desbloquea*/,{30}/*Max Rubies*/},

        };
        
        for (int i = 0; i < indexs.length; i++) {
            Partida partida = new Partida();
            Equipo eq = new Equipo();
            for (int j = 0; j < indexs[i].length; j++) {
                for (int k = 0; k < indexs[i][j].length; k++) {
                    if (j == 0) eq.add(new Pieza (piezas.get(indexs[i][j][k])));
                    else if (j == 1) partida.piezaQueDesbloquea = indexs[i][j][k] != -1 ? piezas.get(indexs[i][j][k]) : null;
                    else if (j == 2) partida.maxRubies = indexs[i][j][k];
                }
                if (j == 0) partida.equipo = eq;
            }

            partidas.add(partida);
        }

        return partidas;
    }

}
