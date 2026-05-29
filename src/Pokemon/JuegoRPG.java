import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class JuegoRPG {
    static Scanner sc = new Scanner (System.in);
    static Random random = new Random();
    static int MAX_POKEMONS = 6;
    static final String DB_URL = System.getenv().getOrDefault("JUEGORPG_DB_URL", "jdbc:postgresql://localhost:5432/juegopokemon");
    static final String DB_USER = System.getenv().getOrDefault("JUEGORPG_DB_USER", "alex");
    static final String DB_PASSWORD = System.getenv().getOrDefault("JUEGORPG_DB_PASSWORD", "1234");
    public static void main (String [] args) throws FileNotFoundException, SQLException {
        File f = new File("files/record");
        ArrayList <Movimiento> movimientos = new ArrayList<>();
        ArrayList <Estado> estados = new ArrayList<>();
        ArrayList <Pokemon> pokemons = new ArrayList<>();
        ArrayList <Evolucion> evoluciones = new ArrayList<>();
        Equipo tresPokemons;
        String opcion;
        int RecordVictorias = Integer.parseInt(new Scanner(f).nextLine());
        int combatesGanados = 0;
        // int [] nivelRivales = {10,15,23,30,37,44,50,60};
        boolean ganado = true;

        cargarEvoluciones(evoluciones);
        cargarEstados(estados);
        cargarMovimientos(movimientos, estados);
        cargarPokemons(pokemons, estados, movimientos, evoluciones);

        Equipo pok = equipoAleatorio(pokemons, movimientos, 3, evoluciones, 25, false);
        Equipo pok2 = equipoAleatorio(pokemons, movimientos, 3, evoluciones, 25, false);
        pok.equipo[0].movimientos[0]=movimientos.get(15);
        pok2.equipo[0].movimientos[1]=movimientos.get(68);

        // for (int i = 0; i < pokemons.size(); i++) {
        //     System.out.println("\n");
        //     pokemons.get(i).generarNaturaleza();
        //     pokemons.get(i).IVs.generarIVs();
        //     pokemons.get(i).setNivel(50);pokemons.get(i).evolucionar(false, evoluciones);pokemons.get(i).evolucionar(false, evoluciones);
        //         // System.out.println(pokemons.get(i).toString());            
        // }
        ordenarPokemonsPorStats(pokemons);

        for (int i = 0; i < pokemons.size(); i++) {System.out.println(i+". "+pokemons.get(i).toString()+" "+pokemons.get(i).fortalezaPokemon());}System.out.println();
        for (int i = 0; i < movimientos.size(); i++) {if (i%20 == 0) System.out.println("\n"+String.format("%-30s", "")+"\t\tPri\tPre\tPot\tCur\tVel\tAtk\tDef\tSpA\tSpD");System.out.println(i+"."+movimientos.get(i).toString());} 
        sc.nextLine();
        // ganado = combate(pok, pok2, false, movimientos, evoluciones);
        // ganado = combate(equipoAleatorio(pokemons, movimientos, 3, evoluciones, 2, true), equipoAleatorio(pokemons, movimientos, 3, evoluciones, 2, true), false, movimientos, evoluciones);

        // Equipo pokemonsTu = new Equipo(new Pokemon[]{escogerPrimerPokemon(pokemons, movimientos, evoluciones, 10)});
        Equipo pokemonsTu = equipoAleatorio(pokemons, movimientos, MAX_POKEMONS, evoluciones, 10, false);
        for (int i = 0; i < pokemonsTu.equipo[0].movimientos.length; i++) {
            pokemonsTu.equipo[0].aprenderMovimiento(movimientos);
        }
        // sc.nextLine();
        // Equipo pokemonsTu = new Equipo(new Pokemon[]{
        //     pokemons.get(508),
        //     pokemons.get(347),
        //     pokemons.get(392)
        // });
        // for (int i = 0; i < 3; i++) {
        //     pokemonsTu.equipo[i].setNivel(10);
        //     for (int j = 0; j < 4; j++) {
        //         pokemonsTu.equipo[i].aprenderMovimiento(movimientos);
        //     }
        // }

        while (ganado) {
            // for (int j = 0; j < pokemonsTu.length; j++) {System.out.println(pokemonsTu[j].toString());}  sc.nextLine();
            do {
                clear();
                System.out.println("Ir a capturar, ir a por el siguiente rival, menu equipo c/n/v: ");
                opcion = sc.nextLine().toUpperCase();
                if (opcion.equals("V")) {
                    pokemonsTu.menuEquipo(movimientos, evoluciones);
                }
            }while(!opcion.equals("C") && !opcion.equals("N")); 

            if (opcion.equals("C")) {
                pokemonsTu = capturar(movimientos, pokemons, evoluciones, pokemonsTu);
                
                if (pokemonsTu.equipoDañado())pokemonsTu.curarEquipoPokemon();
            }
            tresPokemons = pokemonsTu.escogerTresCombate();
            System.out.println("Combate contra el rival "+(combatesGanados+1));
            sc.nextLine();
            // ganado = combate(equipoPersonalizado(pokemons, evoluciones, movimientos, 15), equipoPersonalizado(pokemons, evoluciones, movimientos, 15), ganado, movimientos, evoluciones);
            ganado = combateRival(tresPokemons, equipoAleatorio(pokemons, movimientos, MAX_POKEMONS, evoluciones, pokemonsTu.mediaNivelEquipo(), true), movimientos, evoluciones);
            // ganado = true;
            // if (random.nextBoolean()) ganado = true;
            // else ganado = false;
            if (ganado) {
                combatesGanados++;
                System.out.println("🪙 +5\n"+combatesGanados+" victorias");
                pokemonsTu.curarEquipoPokemon();
                pokemonsTu.subirNivelEquipoPokemon(movimientos, evoluciones);
                pokemonsTu.monedas += 5;
            }
            else ganado = false;
        }

        System.out.println(combatesGanados+" VICTORIAS "+ (combatesGanados > RecordVictorias ? "RECORD DE "+RecordVictorias+" SUPERADO!!!" : ""));
                    
        if (combatesGanados > RecordVictorias) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("record", false))) {
                bw.write(String.valueOf(combatesGanados));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    static Equipo capturar (ArrayList <Movimiento> movimientos, ArrayList <Pokemon> pokemons, ArrayList <Evolucion> evoluciones, Equipo equipoJug) {
        String opcion="";
        boolean acabar = false;
        Equipo pokemonSalvaje;

        while (!acabar) {
            do {
                clear();
                System.out.println("ZONA DE CAPTURA");
                System.out.print(equipoJug.energia+"🔋 "+equipoJug.pokeballs+"🔴\n'B' Buscar Pokemons\n'C' Curar al Equipo\n'N' Ir a por el siguiente Rival\n'V' Ver Equipo Pokemon\n'T' Tienda\n:");
                opcion = sc.nextLine().toUpperCase();
                if (opcion.equals("V")) {
                    equipoJug.menuEquipo(movimientos, evoluciones);
                }
            }while((!opcion.equals("B") || !equipoJug.energiaSuficiente()) && !opcion.equals("N") && (!opcion.equals("C") || !equipoJug.energiaSuficiente()) && !opcion.equals("T"));

            if (opcion.equals("B")) {
                pokemonSalvaje = equipoAleatorio(pokemons, movimientos, 1, evoluciones, equipoJug.mediaNivelEquipo() + (random.nextInt(0,3) * (random.nextBoolean() ? -1 : 1)), true);
                System.out.println("Buscando...");
                try {
                    Thread.sleep(random.nextInt(1500,4000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(pokemonSalvaje.equipo[0].nombre+" salvaje ha aparecido!!!");
                sc.nextLine();
                acabar = !combate(equipoJug, pokemonSalvaje, true, movimientos, evoluciones);
            }
            else if (opcion.equals("C")) equipoJug.curarEquipoPokemon();
            else if (opcion.equals("T")) equipoJug.menuTienda();
            else acabar = true;
            if (opcion.equals("B") || opcion.equals("C")) equipoJug.energia--;
        }
        if (!acabar) {
            System.out.println("Has salido de la zona de captura");       
            sc.nextLine();
        }
        
        return equipoJug;
    }
    static boolean huir (Pokemon tu, Pokemon rival, Movimiento movTu) {
        boolean escapado = false;
        movTu = new Movimiento(999);
        if (random.nextInt(0,100) < (75 * tu.getVelocidad() / rival.getVelocidad())) {
            System.out.println("Has huido sin problemas");
            escapado = true;
        }
        else {
            System.out.println("No has podido escaparte");
            sc.nextLine();
            clear();
        }

        return escapado;
    }
    static void add(List<Pokemon> pokemonsUsadosParaLv, Pokemon pok) {
        boolean yaEsta = false;
        int x = 0;

        while (!yaEsta && x < pokemonsUsadosParaLv.size()) {
            if (pokemonsUsadosParaLv.get(x).nombre.equals(pok.nombre)) {
                yaEsta = true;
            }
            else x++;
        }

        if (!yaEsta) pokemonsUsadosParaLv.add(pok);
    }
    static void subirNivelPokemonsPorDerrotar (List<Pokemon> pokemonsUsadosParaLv, Pokemon rival, ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
        int subidaNivel; 
        for (int i = 0; i < pokemonsUsadosParaLv.size(); i++) {
            clear();
            subidaNivel = random.nextInt(1,3)+((rival.nivel-pokemonsUsadosParaLv.get(i).nivel < 0 ? 0 : rival.nivel-pokemonsUsadosParaLv.get(i).nivel)/2);
            pokemonsUsadosParaLv.get(i).subirNivel(subidaNivel, movimientos, evoluciones);
        }
    }
    static boolean combate (Equipo pokemonsTu, Equipo pokemonsRival, boolean capturando, ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
        Pokemon tu = pokemonsTu.primerPokemonDisponible();
        Pokemon rival = pokemonsRival.equipo[0];
        List<Pokemon> pokemonsUsadosParaLv = new ArrayList<>();
        String opcionTu = "";
        Movimiento movTu = null;
        Movimiento movRival = null;
        boolean jugando = true;
        boolean ganado = false;
        Pokemon auxTu = null;
        pokemonsUsadosParaLv.add(tu);
        while (jugando) {
            clear();
            mostrarCombate(pokemonsTu, pokemonsRival, tu, rival, capturando);
            if (movTu == null || movTu.turnosNecesarios == 0) {
                do{
                    clear();
                    mostrarCombate(pokemonsTu, pokemonsRival, tu, rival, capturando);

                    opcionTu = sc.nextLine().toUpperCase();
                    if (opcionTu.equals("C")) {
                        auxTu = pokemonsTu.cambiarPokemon(false);
                    }
                }while((((!opcionTu.equals("C") || auxTu == null) && !tu.movimientoDisponible(opcionTu)) && (!capturando) || (capturando && ((!opcionTu.equals("C") || auxTu == null) && !tu.movimientoDisponible(opcionTu) && !opcionTu.equals("H") && (!opcionTu.equals("L") || !pokemonsTu.tienePokeballs())))));

                if (movTu == null || movTu.turnosNecesarios == 0) movTu = (!opcionTu.equals("C") && !opcionTu.equals("H") && !opcionTu.equals("L")) ? new Movimiento(tu.movimientos[Integer.parseInt(opcionTu)-1]) : new Movimiento(999);
                if (movRival == null || movRival.turnosNecesarios == 0) movRival = new Movimiento(rival.movimientos[decisionRival(rival, tu)]);

                if (opcionTu.equals("C")) {
                    tu.limpiarEstadoTemporalMovimiento();
                    tu = auxTu;
                    movTu = new Movimiento(999);
                    add(pokemonsUsadosParaLv, tu);
                    clear();
                }
                else if (opcionTu.equals("H") && huir(tu, rival, movTu)) {
                    jugando = false;
                    ganado = true;
                }
                else if (opcionTu.equals("L") && pokemonsTu.lanzarPokeball(tu, rival, movTu)) {
                    jugando = false;
                    ganado = true;
                }
            }

            if (jugando) {
                if (tu.getVelocidad() > rival.getVelocidad() || movTu.prioridad > movRival.prioridad) {
                    if (movTu.prioridad != 999) realizarMovimiento(tu, rival, movTu);
                    mostrarCombate(pokemonsTu, pokemonsRival, tu, rival, capturando);
                    if (rival.sigueVivo()) realizarMovimiento(rival, tu, movRival);
                }
                else {
                    realizarMovimiento(rival, tu, movRival);
                    mostrarCombate(pokemonsTu, pokemonsRival, tu, rival, capturando);
                    if (tu.sigueVivo()) realizarMovimiento(tu, rival, movTu); 
                }

                if (!tu.sigueVivo()) {
                    System.out.println(tu.nombre+" esta fuera de combate");
                    pokemonsUsadosParaLv.remove(tu);
                    sc.nextLine();
                    if (pokemonsTu.pokemonsDisponibles().size() != 0) {
                        tu.limpiarEstadoTemporalMovimiento();
                        tu = pokemonsTu.cambiarPokemon(true);
                        add(pokemonsUsadosParaLv, tu);
                        movTu = null;
                        if (rival.vidaActual < rival.vida * 0.5 && random.nextDouble(0,1) < 0.33 && pokemonsRival.pokemonsDisponibles().size() > 1) {
                            rival.limpiarEstadoTemporalMovimiento();
                            rival = pokemonsRival.cambioEfectivo(tu);
                            movRival = null;
                        }
                    }
                }
                if (!rival.sigueVivo()) {
                    System.out.println(rival.nombre+" esta fuera de combate");
                    if (capturando) {
                        subirNivelPokemonsPorDerrotar(pokemonsUsadosParaLv, rival, movimientos, evoluciones);               
                    }
                    sc.nextLine();
                    if (pokemonsRival.pokemonsDisponibles().size() != 0) {
                        rival.limpiarEstadoTemporalMovimiento();
                        rival = pokemonsRival.cambioEfectivo(tu);
                        movRival = null;

                        System.out.println("Va a entrar "+rival.nombre);
                        if (pokemonsTu.pokemonsDisponibles().size()>1 && capturando) {
                            System.out.println("Quieres cambiar de Pokemon? c/n");
                            do{
                                opcionTu = sc.nextLine().toUpperCase();
                            }while(!opcionTu.equals("C") && !opcionTu.equals("N"));
                            if (opcionTu.equals("C")) {
                                tu.limpiarEstadoTemporalMovimiento();
                                tu = pokemonsTu.cambiarPokemon(false);
                                movTu = null;
                            }
                        }
                    }
                }

                //REVISAR SI YA HA ACABADO EL COMBATE
                if (pokemonsTu.pokemonsDisponibles().size() == 0) {
                    jugando = false;
                    if (!capturando) System.out.println("TE HAS QUEDADO SIN POKEMONS DISPONIBLES GANA EL RIVAL");
                    else System.out.println("TE HAS QUEDADO SIN POKEMONS ABANDONAS LA CAPTURA");
                }
                else if (pokemonsRival.pokemonsDisponibles().size() == 0) {
                    jugando = false;
                    ganado = true;
                    if (!capturando) {
                        System.out.println("EL RIVAL SE HA QUEDADO SIN POKEMONS HAS GANADO\n");   
                    }
                    
                }
            }
            if (movTu != null)movTu.turnosNecesarios--;
            if (movRival != null)movRival.turnosNecesarios--;
        }
        
        sc.nextLine();
        clear();
        return ganado;
    }
    static boolean combateRival (Equipo pokemonsTu, Equipo pokemonsRival, ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
        pokemonsRival.setNivelEquipoPokemon(50);
        pokemonsTu.setNivelEquipoPokemon(50);
        return combate(pokemonsTu, pokemonsRival, false, movimientos, evoluciones);
    }
    static int decisionRival (Pokemon rival, Pokemon jug) {
        int decision = 0;
        Double maxDaño = 0.0;

        if (random.nextDouble(0,1) < 0.85) {
            for (int i = 0; i < rival.numMovimientos(); i++) {
                if (maxDaño < formulaDaño(rival.movimientos[i], rival, jug, 1.0)) {
                    maxDaño = formulaDaño(rival.movimientos[i], rival, jug, 1.0);
                    decision = i;
                }
            }
            if (!(jug.vida - maxDaño <= 0) && rival.tieneHabilidadCurativa() != -1 && rival.vidaActual < rival.vida*0.4 && random.nextDouble(0,1) < 0.35) {
                decision = rival.tieneHabilidadCurativa();
            }
        }
        else {
            decision = random.nextInt(rival.numMovimientos());
            System.err.println(decision);
        }
        
        return decision;
    }
    static double formulaDaño (Movimiento mov, Pokemon atacando, Pokemon atacado, Double ataCritico) {
        double daño = 0; //atacando.equalsTipo(mov.tipo) ? (40 * atacando.ataque)*ataCritico : (40 * (atacando.ataque * 0.85))*ataCritico;
        if (mov.potencia > 1){ // Si no es porcentaje
            double categoriaAtaque = 0; 
            if (mov.categoria.equals(Categoria.FISICO)) {
                categoriaAtaque = atacando.getAtk();
                daño = ((((2*atacando.nivel)/5)+2)*((mov.potencia*categoriaAtaque)/atacado.getDef())/50+2) * (ataCritico*atacado.calcularEfectividad(mov));
            }
            else if (mov.categoria.equals(Categoria.ESPECIAL)) {
                categoriaAtaque = atacando.getSpA();
                daño = ((((2*atacando.nivel)/5)+2)*((mov.potencia*categoriaAtaque)/atacado.getSpD())/50+2) * (ataCritico*atacado.calcularEfectividad(mov));
            }
            if (atacando.equalsTipo(mov.tipo)) daño *= 1.5;            
        }
        else {
            daño = mov.potencia * atacado.vida;
        }

        return daño;
    }
    static void ordenarPokemonsPorStats (ArrayList <Pokemon> pokemons) {
        pokemons.sort((p1, p2) -> p1.valorStats() - p2.valorStats());
    }
    static Equipo equipoPersonalizado(ArrayList <Pokemon> pokemons, ArrayList<Evolucion> evoluciones, ArrayList<Movimiento> movimientos, int nivelInicio) {
        int [] idsPokemons = {142,200};
        Equipo equipoPokemons = new Equipo(idsPokemons.length);

        for (int i = 0; i < equipoPokemons.equipo.length; i++) {
            equipoPokemons.equipo[i] = new Pokemon(pokemons.get(idsPokemons[i]));
            equipoPokemons.equipo[i].IVs.generarIVs();
            equipoPokemons.equipo[i].generarNaturaleza();
            equipoPokemons.equipo[i].setNivel(random.nextInt(nivelInicio, nivelInicio + 5));
            equipoPokemons.equipo[i].evolucionar(false, evoluciones);
            for (int j = 0; j < 4; j++) {
                equipoPokemons.equipo[i].aprenderMovimiento(movimientos);
            }
        }

        return equipoPokemons;
    }
    static double critico () {
        return random.nextInt(0,16) == 0 ? 2.0 : 1.0;        
    }
    static Pokemon escogerPrimerPokemon (ArrayList <Pokemon> pokemons, ArrayList <Movimiento> movimientos, ArrayList<Evolucion> evoluciones, int nivel) {
        Pokemon [] tresPokemons = new Pokemon [3];
        Pokemon pokemon;
        String opcion = "";
        clear();
        System.out.println("Escoge tu primer pokemon!");
        for (int i = 0; i < tresPokemons.length; i++) {
            tresPokemons[i] = equipoAleatorio(pokemons, movimientos, 1, evoluciones, nivel, false).equipo[0];
            System.out.print((i+1)+". "+tresPokemons[i].nombre+" "+tresPokemons[i].tipoToString()+"\t\t");
        }
        System.out.println();
        do{
            opcion = sc.nextLine();
        }while(!opcion.equals("1") && !opcion.equals("2") && !opcion.equals("3"));
        
        pokemon = tresPokemons[Integer.parseInt(opcion)-1];
        pokemon.setNivel(nivel + random.nextInt(5));
        pokemon.evolucionar(false, evoluciones);
        for (int j = 0; j < 4; j++) {
            pokemon.aprenderMovimiento(movimientos);
        }
        System.out.println("Has elegido a "+pokemon.nombre);
        
        return tresPokemons[Integer.parseInt(opcion)-1];
    }
    static Equipo equipoAleatorio(ArrayList <Pokemon> pokemons, ArrayList <Movimiento> movimientos, int numPok, ArrayList<Evolucion> evoluciones, int nivel, boolean preciso) {
        Equipo equipoPokemons = new Equipo (numPok);
        int maxPok;

        for (int i = 0; i < numPok; i++) {
            maxPok = (random.nextDouble(0,1) < 0.1 ? random.nextInt(pokemons.size()) : (int)random.nextDouble(pokemons.size()*0.77))+1;
            equipoPokemons.equipo[i] = new Pokemon(pokemons.get(random.nextInt(maxPok)));
            equipoPokemons.equipo[i].IVs.generarIVs();
            equipoPokemons.equipo[i].generarNaturaleza();
            equipoPokemons.equipo[i].setNivel(nivel + (preciso ? 0 : random.nextInt(5)));
            equipoPokemons.equipo[i].evolucionar(false, evoluciones);
            equipoPokemons.equipo[i].evolucionar(false, evoluciones);
            for (int j = 0; j < 4; j++) {
                equipoPokemons.equipo[i].aprenderMovimiento(movimientos);
            }
        }
        return equipoPokemons;
    }
    static void realizarMovimiento (Pokemon atacando, Pokemon atacado, Movimiento mov) {

        if (random.nextDouble(0,1) < atacando.estado.probMoverte) {
            System.out.println(atacando.nombre+" ha usado "+mov.nombre);

            if (random.nextDouble(0,1) < mov.precision) {//VER SI FALLA
                Double ataCritico = critico();
                if (atacando.estado.nombre.equals("CONFUSION") && random.nextDouble(0,1) < 0.33) { //SE GOLPEA A SI MISMO CON CONFUSION
                    mov = new Movimiento();
                    atacando.recibirMovimiento(mov, formulaDaño(mov, atacando, atacando, ataCritico), true);                    
                    System.out.println(atacando.nombre+" esta tan confundido que se golpea a si mismo");
                } //GESTIONAR CONFUSION
                else aplicarMovimientoDos(atacando, atacado, mov, ataCritico);
                    
                //IMPRIMIR MENSAJES DEL MOVIMIENTO
                mensajesMovimiento(atacando, atacado, mov, ataCritico); //MOVIMIENTO DE ESTADO
            }
            else System.out.println(atacando.nombre+" ha fallado...");
            if (mov.turnosNecesarios == 1) atacando.limpiarEstadoTemporalMovimiento();
        }
        else System.out.println("El estado "+atacando.estado.nombre+" no deja mover a "+atacando.nombre);
    
        if (atacando.aplicarEstado()) {
            System.out.println(atacando.nombre+" se ha curado del estado");
        }
        
        sc.nextLine();
        clear();
    }
    static void aplicarMovimientoDos(Pokemon atacando, Pokemon atacado, Movimiento mov, Double ataCritico) {
        if (!(mov.estado.nombre.equals("DESAPARECER") && mov.turnosNecesarios == 1)) atacando.recibirMovimiento(mov, 0.0, false);
        if (mov.turnosNecesarios == 1) atacado.recibirMovimiento(mov, !atacado.estado.nombre.equals("DESAPARECER") ? formulaDaño(mov, atacando, atacado, ataCritico) : 0, true);
    }
    static void mensajesMovimiento(Pokemon atacando, Pokemon atacado, Movimiento mov, Double critico) {
        if (atacando.estado.nombre.equals("DESAPARECER") && mov.turnosNecesarios == 2) System.out.println(atacando.nombre+" ha desaparecido del campo de batalla!");
        else if (atacando.estado.nombre.equals("CARGANDO") && mov.turnosNecesarios == 2) System.out.println(atacando.nombre+" esta cargando "+mov.nombre+"!");
        else if (mov.potencia > 0) {
            double efectividad;
            efectividad = atacado.calcularEfectividad(mov);
            if (efectividad == 2.0) System.out.println("Movimiento efectivo!");
            else if (efectividad == 0.5) System.out.println("Movimiento poco efectivo...");
            else if (efectividad == 0.25) System.out.println("Movimiento muy poco efectivo...");
            else if (efectividad == 0) System.out.println(mov.nombre+" no afecta a "+atacado.nombre);
            else if (efectividad == 4) System.out.println("Movimiento hiperefectivo!!!");
            if (atacado.estado.equals(mov.estado) && !mov.estado.nombre.equals("NINGUNO")) System.out.println(mov.estado.nombre+" aplicada");
            if (critico == 2) System.out.println("Critico!!");
            if (mov.curar != 0)  System.out.println((mov.buffsAlEjecutador ? atacando.nombre : atacado.nombre)+ " se ha "+(mov.curar > 0 ? "curado" : "dañado"));
            if (mov.potencia > 0 && atacado.estado.nombre.equals("DESAPARECER")) System.out.println(atacado.nombre+" ha evitado el ataque");
        } 
    }
    static void mostrarCombate(Equipo equipo, Equipo equipoRival, Pokemon tu, Pokemon rival, boolean capturando) {
        String cambio = "Pokemons 🔄 'c'";
        String huir = capturando ? "Huir 🏃 'h'" : "";
        String pokeball = capturando ? "Capturar 🫳 🔴 'l' ("+equipo.pokeballs+")" : "";
        String [] pokemonsRestantes = new String[equipoRival.equipo.length];
        for (int i = 0; i < pokemonsRestantes.length; i++) {
            if (!capturando) pokemonsRestantes[i] = equipoRival.equipo[i].sigueVivo() ? "🔴" : "🔘";
            else pokemonsRestantes[i] = "";
        }
        
        System.out.printf("\t%-40s %-40s", tu.nombre + " Lv." + tu.nivel + " " + tu.tipoToString() + " " + tu.estado.unicode, rival.nombre + " Lv." +rival.nivel+ " " + rival.tipoToString() + " " + rival.estado.unicode);
        for (int i = 0; i < pokemonsRestantes.length; i++) {
            System.out.print(pokemonsRestantes[i]+" ");
        }
        System.out.println();
        System.out.printf("\t%-40s %-40s%n",(int)Math.ceil(tu.vidaActual) + "/" + (int)Math.ceil(tu.vida) + " HP", (int)Math.ceil(rival.vidaActual) + "/" + (int)Math.ceil(rival.vida) + " HP");
        imprimir_pokemons(tu, rival, 37);
        System.out.println("\n\n\n");
        for (int i = 0; i < tu.movimientos.length; i += 2) {
            String mov1 = "\t"+(i+1)+". "+(tu.movimientos[i] == null ? "None" : tu.movimientos[i].nombre) + " " + (tu.movimientos[i] == null ? "" : tu.movimientos[i].tipo.getEmoji());
            String mov2 = "\t"+(i+2)+". "+(tu.movimientos[i + 1] == null ? "None" : tu.movimientos[i + 1].nombre) + " " + (tu.movimientos[i + 1] == null ? "" : tu.movimientos[i + 1].tipo.getEmoji());
            System.out.printf("%-30s %-30s%n", mov1, mov2);
        }
        System.out.printf("%-27s %-27s %-27s\n", cambio, huir, pokeball);

    }
    static void imprimir_pokemons_datos (String url1, String url2, int tamaño) {
        imprimirImagenesPokemon(Arrays.asList(url1, url2), tamaño, 70);
    }
    static void imprimir_pokemons (Pokemon pok1, Pokemon pok2, int tamaño) {
        boolean desaparece1 = pok1.estado.nombre.equals("DESAPARECER");
        boolean desaparece2 = pok2.estado.nombre.equals("DESAPARECER");

        imprimirImagenesPokemon(Arrays.asList(
            desaparece1 ? null : pok1.urlImagen(),
            desaparece2 ? null : pok2.urlImagen()
        ), tamaño, 4);
    }
    static void clear() {
        try {
            if (esWindows()) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
    static boolean esWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    static void imprimirImagenesPokemon(List<String> urls, int tamaño, int espaciosEntreImagenes) {
        ArrayList<List<String>> imagenes = new ArrayList<>();
        int altoMaximo = 0;

        for (String url : urls) {
            List<String> imagen = renderizarImagenPokemon(url, tamaño);
            imagenes.add(imagen);
            altoMaximo = Math.max(altoMaximo, imagen.size());
        }

        String separacion = " ".repeat(espaciosEntreImagenes);
        for (int fila = 0; fila < altoMaximo; fila++) {
            StringBuilder linea = new StringBuilder();
            for (int i = 0; i < imagenes.size(); i++) {
                List<String> imagen = imagenes.get(i);
                linea.append(fila < imagen.size() ? imagen.get(fila) : " ".repeat(tamaño));
                if (i < imagenes.size() - 1) linea.append(separacion);
            }
            System.out.println(linea);
        }
    }
    static List<String> renderizarImagenPokemon(String url, int tamaño) {
        ArrayList<String> lineas = new ArrayList<>();

        if (url == null) {
            for (int i = 0; i < (tamaño + 1) / 2; i++) lineas.add(" ".repeat(tamaño));
            return lineas;
        }

        try {
            List<String> lineasChafa = renderizarConChafa(url, tamaño);
            if (!lineasChafa.isEmpty()) return lineasChafa;

            BufferedImage imagen = ImageIO.read(new File(url));
            if (imagen == null) throw new IOException("No se pudo leer la imagen " + url);

            Rectangle zonaVisible = zonaVisible(imagen);
            int altoCaracteres = (tamaño + 1) / 2;
            for (int y = 0; y < tamaño; y += 2) {
                StringBuilder linea = new StringBuilder();
                for (int x = 0; x < tamaño; x++) {
                    Color arriba = colorPromedio(imagen, zonaVisible, x, y, tamaño);
                    Color abajo = colorPromedio(imagen, zonaVisible, x, y + 1, tamaño);
                    linea.append(pixelTerminal(arriba, abajo));
                }
                linea.append("\033[0m");
                lineas.add(linea.toString());
            }

            while (lineas.size() < altoCaracteres) lineas.add(" ".repeat(tamaño));
        } catch (IOException e) {
            lineas.add("[Imagen no encontrada: " + url + "]");
        }

        return lineas;
    }
    static List<String> renderizarConChafa(String url, int tamaño) {
        ArrayList<String> lineas = new ArrayList<>();

        try {
            ProcessBuilder pb = new ProcessBuilder("chafa", "-s", tamaño + "x" + tamaño, url);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String linea;

            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }

            if (process.waitFor() == 0) return lineas;
        } catch (Exception e) {
            lineas.clear();
        }

        return lineas;
    }
    static Rectangle zonaVisible(BufferedImage imagen) {
        int minX = imagen.getWidth();
        int minY = imagen.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                int alpha = (imagen.getRGB(x, y) >>> 24) & 0xff;
                if (alpha > 20) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX == -1) return new Rectangle(0, 0, imagen.getWidth(), imagen.getHeight());

        int margen = 2;
        minX = Math.max(0, minX - margen);
        minY = Math.max(0, minY - margen);
        maxX = Math.min(imagen.getWidth() - 1, maxX + margen);
        maxY = Math.min(imagen.getHeight() - 1, maxY + margen);

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
    static Color colorPromedio(BufferedImage imagen, Rectangle zonaVisible, int x, int y, int tamaño) {
        if (y >= tamaño) return null;

        double escala = Math.min((double)tamaño / zonaVisible.width, (double)tamaño / zonaVisible.height);
        int anchoDibujado = Math.max(1, (int)Math.round(zonaVisible.width * escala));
        int altoDibujado = Math.max(1, (int)Math.round(zonaVisible.height * escala));
        int offsetX = (tamaño - anchoDibujado) / 2;
        int offsetY = (tamaño - altoDibujado) / 2;

        if (x < offsetX || x >= offsetX + anchoDibujado || y < offsetY || y >= offsetY + altoDibujado) return null;

        double srcX1 = zonaVisible.x + ((double)(x - offsetX) / anchoDibujado) * zonaVisible.width;
        double srcY1 = zonaVisible.y + ((double)(y - offsetY) / altoDibujado) * zonaVisible.height;
        double srcX2 = zonaVisible.x + ((double)(x - offsetX + 1) / anchoDibujado) * zonaVisible.width;
        double srcY2 = zonaVisible.y + ((double)(y - offsetY + 1) / altoDibujado) * zonaVisible.height;

        int inicioX = Math.max(zonaVisible.x, (int)Math.floor(srcX1));
        int inicioY = Math.max(zonaVisible.y, (int)Math.floor(srcY1));
        int finX = Math.min(zonaVisible.x + zonaVisible.width, (int)Math.ceil(srcX2));
        int finY = Math.min(zonaVisible.y + zonaVisible.height, (int)Math.ceil(srcY2));

        double rojo = 0;
        double verde = 0;
        double azul = 0;
        double alphaTotal = 0;
        int muestras = Math.max(1, (finX - inicioX) * (finY - inicioY));

        for (int imgY = inicioY; imgY < finY; imgY++) {
            for (int imgX = inicioX; imgX < finX; imgX++) {
                Color color = new Color(imagen.getRGB(imgX, imgY), true);
                int alpha = color.getAlpha();
                rojo += color.getRed() * alpha;
                verde += color.getGreen() * alpha;
                azul += color.getBlue() * alpha;
                alphaTotal += alpha;
            }
        }

        if (alphaTotal / muestras < 18) return null;

        return new Color(
            (int)Math.round(rojo / alphaTotal),
            (int)Math.round(verde / alphaTotal),
            (int)Math.round(azul / alphaTotal)
        );
    }
    static String pixelTerminal(Color arriba, Color abajo) {
        if (arriba == null && abajo == null) return " ";
        if (arriba == null) return colorTexto(abajo) + "▄";
        if (abajo == null) return colorTexto(arriba) + "▀";
        return colorTexto(arriba) + colorFondo(abajo) + "▀";
    }
    static String colorTexto(Color color) {
        return "\033[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }
    static String colorFondo(Color color) {
        return "\033[48;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }
    static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    static Estado buscarEstadoPorNombre(ArrayList<Estado> estados, String nombreEstado) {
        for (int i = 0; i < estados.size(); i++) {
            if (estados.get(i).nombre.equals(nombreEstado)) {
                return estados.get(i);
            }
        }
        return null;
    }
    static Evolucion buscarEvolucionPorPreevolucion(ArrayList<Evolucion> evoluciones, String nombrePokemon) {
        for (int i = 0; i < evoluciones.size(); i++) {
            if (evoluciones.get(i).preevolucion.equals(nombrePokemon)) {
                return evoluciones.get(i);
            }
        }
        return null;
    }
    static void cargarPokemons (ArrayList <Pokemon> pokemons, ArrayList <Estado> estados, ArrayList <Movimiento> movimientos, ArrayList <Evolucion> evoluciones) throws SQLException {
        String sql = "SELECT nombre, tipos, velocidad, vida, atk, def, spa, spd FROM pokemons ORDER BY id";

        try (Connection conn = obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
            Pokemon pokemon = new Pokemon();

            pokemon.nombre = rs.getString("nombre");
            String [] tipos = rs.getString("tipos").split(":");
            for (int i = 0; i < tipos.length; i++) {
                pokemon.tipo.add(Tipo.valueOf(tipos[i]));
            }

            pokemon.velocidad = rs.getDouble("velocidad");
            pokemon.vida = rs.getDouble("vida");
            pokemon.vidaActual = rs.getDouble("vida");
            pokemon.Atk = rs.getDouble("atk");
            pokemon.Def = rs.getDouble("def");
            pokemon.SpA = rs.getDouble("spa");
            pokemon.SpD = rs.getDouble("spd");
            pokemon.statsBase.modificarStatsBase(pokemon.velocidad, pokemon.vida, pokemon.Atk, pokemon.Def, pokemon.SpA, pokemon.SpD);
            // pokemon.IVs.generarIVs();
            // pokemon.generarNaturaleza();

            pokemon.evolucion = buscarEvolucionPorPreevolucion(evoluciones, pokemon.nombre);
            if (pokemon.evolucion == null) pokemon.evolucion = new Evolucion();

            pokemons.add(pokemon);
        }
        }
    }
    static void cargarEvoluciones (ArrayList <Evolucion> evoluciones) throws SQLException {
        String sql = "SELECT preevolucion, nivel_evolucion, evolucion, sum_vel, sum_vida, sum_atk, sum_def, sum_spa, sum_spd, tipos FROM evoluciones ORDER BY id";

        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
            Evolucion evolucion = new Evolucion();

            evolucion.preevolucion = rs.getString("preevolucion");
            evolucion.nivelEvolucion = rs.getInt("nivel_evolucion");
            evolucion.evolucion = rs.getString("evolucion");
            evolucion.sumVel = rs.getDouble("sum_vel");
            evolucion.sumVida = rs.getDouble("sum_vida");
            evolucion.sumAtk = rs.getDouble("sum_atk");
            evolucion.sumDef = rs.getDouble("sum_def");
            evolucion.sumSpA = rs.getDouble("sum_spa");
            evolucion.sumSpD = rs.getDouble("sum_spd");
            String [] tipo = rs.getString("tipos").split(":");
            for (int i = 0; i < tipo.length; i++) {
                evolucion.tipo.add(Tipo.valueOf(tipo[i]));
            }

            evoluciones.add(evolucion);
        }
        }
    }

    static void cargarEstados (ArrayList <Estado> estados) throws SQLException {
        String sql = "SELECT nombre, unicode_icon, prob_moverte, potencia, prob_irse, max_turnos FROM estados ORDER BY id";

        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
            Estado estado = new Estado();

            estado.nombre = rs.getString("nombre");
            estado.unicode = rs.getString("unicode_icon");
            estado.probMoverte = rs.getDouble("prob_moverte");
            estado.potencia = rs.getDouble("potencia");
            estado.probIrse = rs.getDouble("prob_irse");
            estado.maxTurnos = rs.getInt("max_turnos");
            estados.add(estado);
        }
        }
    }
    static void cargarMovimientos (ArrayList <Movimiento> movimientos, ArrayList <Estado> estados) throws SQLException {
        String sql = "SELECT nombre, tipo, categoria, prioridad, precision_valor, potencia, curar, subir_vel, subir_atk, subir_def, subir_spa, subir_spd, buffs_al_ejecutador, estado_nombre, prob_estado, prob_modificacion_stats, estado_al_ejecutador, tipos_compatibles, nivel_minimo FROM movimientos ORDER BY id";

        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
            Movimiento movimiento = new Movimiento();

            movimiento.nombre = rs.getString("nombre");
            movimiento.tipo = Tipo.valueOf(rs.getString("tipo")); 
            movimiento.categoria = Categoria.valueOf(rs.getString("categoria"));
            movimiento.prioridad = rs.getInt("prioridad");
            movimiento.precision = rs.getDouble("precision_valor");
            movimiento.potencia = rs.getDouble("potencia");
            movimiento.curar = rs.getDouble("curar");
            movimiento.subirVel = rs.getDouble("subir_vel");
            movimiento.subirAtk = rs.getDouble("subir_atk");
            movimiento.subirDef = rs.getDouble("subir_def");
            movimiento.subirSpA = rs.getDouble("subir_spa");
            movimiento.subirSpD = rs.getDouble("subir_spd");
            movimiento.buffsAlEjecutador = rs.getBoolean("buffs_al_ejecutador");
            movimiento.estado = buscarEstadoPorNombre(estados, rs.getString("estado_nombre"));
            if (movimiento.estado == null) {
                throw new SQLException("No existe el estado '" + rs.getString("estado_nombre") + "' para el movimiento '" + movimiento.nombre + "'");
            }
            if (movimiento.estado.nombre.equals("DESAPARECER") || movimiento.estado.nombre.equals("CARGANDO")) movimiento.turnosNecesarios = 2;
            movimiento.probEstado = rs.getDouble("prob_estado");        
            movimiento.probModificacionStats = rs.getDouble("prob_modificacion_stats");
            movimiento.estadoAlEjecutador = rs.getBoolean("estado_al_ejecutador");
            String [] tiposCompatibles = rs.getString("tipos_compatibles").split(":");
            for (int i = 0; i < tiposCompatibles.length; i++) {
                movimiento.tiposCompatibles.add(Tipo.valueOf(tiposCompatibles[i]));
            }
            movimiento.nivelMinimo = rs.getInt("nivel_minimo");
            movimientos.add(movimiento);
        }
        }
    }
    static public class Equipo {
        private Pokemon [] equipo;
        private List<Pokemon> pc = new ArrayList<>();
        private int energia = 8;
        private int monedas = 0;
        private int pokeballs = 8;
        private int caramelosRaros = 0;
        
        public Equipo() {}

        public Equipo(Pokemon [] equipo) {
            this.equipo = equipo;
        }
        public Equipo(int numPokemons) {
            this.equipo = new Pokemon[numPokemons];
        }
        public boolean equipoDañado () {
            boolean noDañados = false;
            int i = 0;
            
            while (!noDañados && i < equipo.length) {
                if (equipo[i].vidaActual < equipo[i].vida) noDañados = true;
                else i++;
            }

            return noDañados;
        }
        public Pokemon cambioEfectivo (Pokemon jug) {
            boolean cambio = false;
            int x = 0;
            Movimiento mov = new Movimiento();
            Pokemon pok = null;
            Equipo equipo = equipoDisponible();

            for (int i = 0; i < equipo.equipo.length; i++) {
                System.out.println(equipo.equipo[i].nombre);
            }

            while (!cambio && x < equipo.equipo.length) {
                for (int i = 0; i < equipo.equipo[x].tipo.size(); i++) {
                    mov.tipo = equipo.equipo[x].tipo.get(i);
                    if (jug.calcularEfectividad(mov) == 4 || jug.calcularEfectividad(mov) == 2) {
                        cambio = true;
                        pok = equipo.equipo[x];
                    }
                }
                if (!cambio) x++;
            }

            if (!cambio) {
                x = 0;
                while (!cambio && x < equipo.equipo.length) {
                    for (int i = 0; i < equipo.equipo[x].tipo.size(); i++) {
                        mov.tipo = equipo.equipo[x].tipo.get(i);
                        if (jug.calcularEfectividad(mov) == 1) {
                            cambio = true;
                            pok = equipo.equipo[x];
                        }
                    }
                    if (!cambio) x++;
                }
            }

            if (pok == null)  {
                pok = equipo.equipo[equipo.equipo.length-1];
            }
            pok.limpiarEstadoTemporalMovimiento();

            return pok;
        }
        public boolean energiaSuficiente () {
            if (energia == 0) {
                System.out.println("No tienes energia suficiente");
                sc.nextLine();
            } 
            return energia > 0;
        }
        private Equipo escogerTresCombate () {
            String opcion = "";
            int[] pokemons = new int [MAX_POKEMONS];

            clear();
            mostrarPokemonsDisponibles();
            if (MAX_POKEMONS != 6) {
                System.out.print("Escoge "+MAX_POKEMONS+" pokemons para el combate: ");
                do {
                    opcion = sc.nextLine();
                }while(!comprobarEscogerTres(opcion));
            }
            else opcion = "1 2 3 4 5 6";
            for (int i = 0; i < MAX_POKEMONS; i++) {
                pokemons[i] = Integer.parseInt(opcion.split(" ")[i])-1;
            }

            clear();
            Equipo eq = new Equipo();
            Pokemon [] pokemons2 = new Pokemon[MAX_POKEMONS];
            for (int i = 0; i < pokemons2.length; i++) {
                pokemons2[i] = new Pokemon(equipo[pokemons[i]]);
            }
            eq.equipo = pokemons2;
            return eq;
        }
        private boolean comprobarEscogerTres (String opcion) {
            boolean correcto = true;
            try {
                String [] spliter;

                spliter = opcion.split(" ");
                int x = 0;
                if (spliter.length != MAX_POKEMONS) correcto = false;
                while (correcto && x < spliter.length) {
                    System.out.println(spliter[x]);
                    if (!hayPokemon(spliter[x])) correcto = false;
                    else x++;
                }

                x = 0;
                int y = 0;
                while (correcto && x < spliter.length - 1) {
                    y = x;
                    while (correcto && y < spliter.length) {
                        if (spliter[x].equals(spliter[y]) && x != y) correcto = false;
                        else y++;
                    } 
                    x++;
                } 
            } catch (Exception e) {
                correcto = false;
            }

            return correcto;
        }
        private void comprarComida () {
            clear();
            String opcion = "";
            final int PRECIO = 2;
            final int VALOR = 3;
            int cantidad = 0;


            System.out.print("Cuanta comida 🍎 quieres comprar\nPrecio: "+PRECIO+"🪙\n"+infoObjetos()+"\n:");
            do {
                opcion = sc.nextLine().toUpperCase();
            }while((!esNumero(opcion) || monedasInsuficientes(Integer.parseInt(opcion), PRECIO)) && !opcion.equals("S"));
           
            if (!opcion.equals("S")) {
                cantidad = Integer.parseInt(opcion);
                monedas -= PRECIO * cantidad;
                energia += cantidad * VALOR;
                System.out.println("+"+(cantidad * VALOR)+"🔋");
            }

            sc.nextLine();
        }
        private boolean monedasInsuficientes (int cantidad, int precio) {   
            if (cantidad <= 0) return true;
            else return cantidad * precio > monedas;
        }
        public boolean tienePokeballs () {
            if (pokeballs == 0) System.out.println("No te quedan pokeballs");
            return (pokeballs > 0);
        }
        private boolean esNumero (String txt) {
            boolean correcto = true;
            try {
                Integer.parseInt(txt);
            } catch (Exception e) {
                correcto = false;
            }
            return correcto;
        }
        public String infoObjetos () {
            return monedas+"🪙  "+energia+"🔋 "+pokeballs+"🔴"+ " "+caramelosRaros+" 🍬";
        }
        public void comprarCaramelos () {
            clear();
            String opcion = "";
            final int PRECIO = 1;
            final int VALOR = 2;
            int cantidad;

            System.out.print("Cuantos caramelos 🍬 quieres comprar\nPrecio: "+PRECIO+"🪙\n"+infoObjetos()+"\n:");
            do {
                opcion = sc.nextLine().toUpperCase();
            }while((!esNumero(opcion) || monedasInsuficientes(Integer.parseInt(opcion), PRECIO)) && !opcion.equals("S"));
            
            if (!opcion.equals("S")) {
                cantidad = Integer.parseInt(opcion);
                monedas -= PRECIO * cantidad;
                caramelosRaros += cantidad * VALOR;
                System.out.println("+"+(cantidad * VALOR)+"🍬");
            }
            sc.nextLine();
        }
        public void comprarPokeballs () {
            clear();
            String opcion = "";
            final int PRECIO = 1;
            final int VALOR = 2;
            int cantidad;

            System.out.print("Cuantas pokeballs 🔴 quieres comprar\nPrecio: "+PRECIO+"🪙\n"+infoObjetos()+"\n:");
            do {
                opcion = sc.nextLine().toUpperCase();
            }while((!esNumero(opcion) || monedasInsuficientes(Integer.parseInt(opcion), PRECIO)) && !opcion.equals("S"));
            
            if (!opcion.equals("S")) {
                cantidad = Integer.parseInt(opcion);
                monedas -= PRECIO * cantidad;
                pokeballs += cantidad * VALOR;
                System.out.println("+"+(cantidad * VALOR)+"🔴");
            }
            sc.nextLine();
        }
        public void menuTienda () {
            String opcion;
            boolean salir = false;

            while(!salir) {
                clear();
                System.out.println("🛒 MENU TIENDA 🛒\n"+infoObjetos());
                System.out.print("'P' Comprar Pokeballs 🔴\n'C' Comprar comida 🍎\n'R' Comprar Caramelo Raro 🍬\n'S' Salir\n:");
                do{
                    opcion = sc.nextLine().toUpperCase();
                }while(!opcion.equals("C") && !opcion.equals("P") && !opcion.equals("R") && !opcion.equals("S"));

                switch (opcion) {
                    case "C":
                        comprarComida();
                        break;
                
                    case "P":
                        comprarPokeballs();
                        break;

                    case "R":
                        comprarCaramelos();

                    case "S":
                        salir = true;
                        break;  
                }
            }   
        } 
        public void menuEquipo (ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
            String opcion;
            boolean salir = false;

            while (!salir) {
                clear();
                System.out.println("MENU EQUIPO POKEMON");
                mostrarPokemonsDisponibles();
                System.out.println("\n'm' Mover\n'd' Ver Datos de un Pokemon\n'r' Usar Caramelos Raros\n'p' PC\n's' Salir");

                do{
                    opcion = sc.nextLine().toUpperCase();
                }while(!opcion.equals("M") && !opcion.equals("D") && !opcion.equals("R") && !opcion.equals("P") && !opcion.equals("S"));

                switch (opcion) {
                    case "M":
                        moverPokemon();                        
                        break;
                    case "D":
                        mostrarDatosPokemon(equipo);
                        break;
                    case "R":
                        usarCaramelosRaros(movimientos, evoluciones);
                        break;
                    case "P":
                        usarPc(movimientos, evoluciones);
                        break;
                    case "S":
                        salir = true;
                        break;
                }
            }
            
        }
        private void usarPc (ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
            String opcion;
            boolean salir = false;

            while (!salir) {
                clear();
                System.out.println("MENU PC");
                mostrarPc();
                System.out.println("\n'm' Mover\n'd' Ver Datos de un Pokemon\n'r' Usar Caramelos Raros\n's' Salir");

                do{
                    opcion = sc.nextLine().toUpperCase();
                }while(!opcion.equals("M") && !opcion.equals("D") && !opcion.equals("R") && !opcion.equals("S"));

                switch (opcion) {
                    case "M":
                        moverPokemonPc();
                    break;
                    case "D":
                        Pokemon [] equipo = new Pokemon[pc.size()];
                        for (int i = 0; i < equipo.length; i++) {
                            equipo[i] = pc.get(i);
                        }
                        mostrarDatosPokemonPc(equipo);
                        break;
                    case "R":
                        usarCaramelosRarosPc(movimientos, evoluciones);
                        break;  
                    case "S":
                        salir = true;
                        break;
                }
            }
        }
        private void moverPokemonPc () {
            String idPokemon1;
            String idPokemon2;

            do {
                System.out.print("Pc:");
                idPokemon1 = sc.nextLine();
            }while(!hayPokemonPc(idPokemon1));

            for (int i = 0; i < equipo.length; i++) {
                System.out.println((i+1)+". "+equipo[i].nombre);
            }
            do {
                System.out.print("\nEquipo:");
                idPokemon2 = sc.nextLine();
            }while(!hayPokemon(idPokemon2));

            Pokemon swap;
            swap = equipo[Integer.parseInt(idPokemon2)-1];
            equipo[Integer.parseInt(idPokemon2)-1] = pc.get(Integer.parseInt(idPokemon1)-1);
            pc.set(Integer.parseInt(idPokemon1)-1, swap);
        }
        private void mostrarPc() {

            int porFila = 4;
            int tamaño = 46;

            try {

                for (int inicio = 0; inicio < pc.size(); inicio += porFila) {

                    int fin = Math.min(inicio + porFila, pc.size());

                    // =========================
                    // IMÁGENES
                    // =========================

                    ArrayList<String> urlsImagenes = new ArrayList<>();

                    for (int i = inicio; i < fin; i++) {
                        urlsImagenes.add(pc.get(i).urlImagen());
                    }

                    imprimirImagenesPokemon(urlsImagenes, tamaño, 4);

                    // =========================
                    // NOMBRES
                    // =========================

                    for (int i = inicio; i < fin; i++) {
                        // ancho fijo para alineación
                        System.out.printf("\t\t"+(i+1)+". %-35s", pc.get(i).nombre+" lv."+pc.get(i).nivel);
                    }

                    System.out.println("\n");

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }    
        private void usarCaramelosRarosPc (ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
            String idPokemon; 
            
                do {
                    if (caramelosRaros > 0) {
                        mostrarPc();
                        System.out.println("\n"+caramelosRaros+"🍬 1-"+equipo.length);

                        do {
                            System.out.print(":");
                            idPokemon = sc.nextLine().toUpperCase();
                        }while(!hayPokemonPc(idPokemon) && !idPokemon.equals("S"));

                        if (!idPokemon.equals("S")) {
                            pc.get(Integer.parseInt(idPokemon)-1).subirNivel(1, movimientos, evoluciones);
                            caramelosRaros--;
                            sc.nextLine();
                        }
                    }
                    else {
                        System.out.println("Sin Caramelos Raros");
                        idPokemon = "S";
                    }
                }while(!idPokemon.equals("S"));
            

        }
        private void usarCaramelosRaros (ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
            String idPokemon; 
            
                do {
                    if (caramelosRaros > 0) {
                        clear();
                        mostrarPokemonsDisponibles();
                        System.out.println("\n"+caramelosRaros+"🍬 1-"+equipo.length);

                        do {
                            System.out.print(":");
                            idPokemon = sc.nextLine().toUpperCase();
                        }while(!hayPokemon(idPokemon) && !idPokemon.equals("S"));

                        if (!idPokemon.equals("S")) {
                            equipo[Integer.parseInt(idPokemon)-1].subirNivel(1, movimientos, evoluciones);
                            caramelosRaros--;
                            sc.nextLine();
                        }
                    }
                    else {
                        System.out.println("Sin Caramelos Raros");
                        idPokemon = "S";
                    }
                }while(!idPokemon.equals("S"));
            

        }
        private void mostrarDatosPokemon (Pokemon[] equipo) {
            String idPokemon; 

            System.out.println("1-"+equipo.length+", 'T' todos");
            do {
                System.out.print(":");
                idPokemon = sc.nextLine().toUpperCase();
            }while(!hayPokemon(idPokemon) && !idPokemon.equals("T"));
            clear();

            if (idPokemon.equals("T")) {
                for (int i = 0; i < equipo.length; i++) {
                    System.out.println((i+1)+". "+equipo[i].toString());
                }
            }
            else {
                System.out.println("1. "+equipo[Integer.parseInt(idPokemon)-1].toString());
                System.out.println("");
                equipo[Integer.parseInt(idPokemon)-1].mostrarMovimientosDetalladamente();
            }
            sc.nextLine();
        }
        private void mostrarDatosPokemonPc (Pokemon[] equipo) {
            String idPokemon; 

            System.out.println("1-"+equipo.length+", 'T' todos");
            do {
                System.out.print(":");
                idPokemon = sc.nextLine().toUpperCase();
            }while(!hayPokemonPc(idPokemon) && !idPokemon.equals("T"));
            clear();

            if (idPokemon.equals("T")) {
                for (int i = 0; i < equipo.length; i++) {
                    System.out.println((i+1)+". "+equipo[i].toString());
                }
            }
            else {
                System.out.println("1. "+equipo[Integer.parseInt(idPokemon)-1].toString());
                System.out.println("");
                equipo[Integer.parseInt(idPokemon)-1].mostrarMovimientosDetalladamente();
            }
            sc.nextLine();
        }
        private void moverPokemon() {
            String idPokemon1;
            String idPokemon2;

            do {
                System.out.print(":");
                idPokemon1 = sc.nextLine();
            }while(!hayPokemon(idPokemon1));

            do {
                System.out.print("\n:");
                idPokemon2 = sc.nextLine();
            }while(!hayPokemon(idPokemon2));

            Pokemon swap;
            swap = equipo[Integer.parseInt(idPokemon2)-1];
            equipo[Integer.parseInt(idPokemon2)-1] = equipo[Integer.parseInt(idPokemon1)-1];
            equipo[Integer.parseInt(idPokemon1)-1] = swap;
        }
        public Pokemon primerPokemonDisponible () {
            int x = 0;
            while (!equipo[x].sigueVivo() && x < equipo.length){
                x++;
            }
            return equipo[x];
        }
        public void curarEquipoPokemon () {
            System.out.println("❤️‍🩹Curando al equipo...❤️‍🩹");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < equipo.length; i++) {
                equipo[i].curar();
                clear();
            }
        }
        public void subirNivelEquipoPokemon (ArrayList <Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
            int subidaNivel;
            Pokemon [] equipoOrdenador = Arrays.copyOf(equipo, equipo.length);
            Arrays.sort(equipoOrdenador, (p1, p2) -> p2.nivel - p1.nivel);

            for (int i = 0; i < equipo.length; i++) {
                subidaNivel = random.nextInt(1+i/2,3+i);
                equipoOrdenador[i].subirNivel(subidaNivel, movimientos, evoluciones);
                equipo[Arrays.asList(equipo).indexOf(equipoOrdenador[i])] = equipoOrdenador[i];
                sc.nextLine();
                clear();
            }

        }
        public void setNivelEquipoPokemon (int nivel) {
            for (int i = 0; i < equipo.length; i++) {
                equipo[i].setNivel(nivel);
            }
        }
        public ArrayList<Integer> pokemonsDisponibles () {
            ArrayList<Integer> pokDisponibles = new ArrayList<>();
            for (int i = 0; i < equipo.length; i++) {
                if (equipo[i].sigueVivo()) pokDisponibles.add(i);
            }

            return pokDisponibles;
        } 
        public Equipo equipoDisponible () {
            ArrayList<Integer> pokDisponibles = new ArrayList<>();
            for (int i = 0; i < equipo.length; i++) {
                if (equipo[i].sigueVivo()) pokDisponibles.add(i);
            }

            Equipo equipoDisponible = new Equipo(pokDisponibles.size());
            for (int i = 0; i < pokDisponibles.size(); i++) {
                equipoDisponible.equipo[i] = equipo[pokDisponibles.get(i)];
            }
            return equipoDisponible;
        } 
        public boolean lanzarPokeball (Pokemon tu, Pokemon rival, Movimiento movTu) {
            movTu = new Movimiento(999);
            double probabilidadCaptura = (rival.vidaActual/rival.vida - 0.1 - (rival.yaTieneEstado() ? 0.5 : 0));
            boolean capturado = false;

            System.out.println("¡Pokeball lanzada!");
            try {Thread.sleep(2000);} 
            catch (InterruptedException e) {e.printStackTrace();}
            if (probabilidadCaptura < random.nextDouble(0,1)) {
                System.out.println("¡Shk… shk… shk!");   
                try {Thread.sleep(2000);} 
                catch (InterruptedException e) {e.printStackTrace();}

                if (probabilidadCaptura < random.nextDouble(0,1)) {
                    System.out.println("¡Tink… tink!");
                    try {Thread.sleep(2000);} 
                    catch (InterruptedException e) {e.printStackTrace();}

                    if (probabilidadCaptura < random.nextDouble(0,1)) {
                        System.out.println("¡Thump!");
                        System.out.println("HAS CAPTURADO A "+rival.nombre+"!!!");
                        if (equipo.length < 6) {
                            equipo = Arrays.copyOf(equipo, equipo.length + 1);
                            equipo[equipo.length-1] = rival;
                        }
                        else {
                            pc.add(rival);
                            System.out.println("Se ha guardado a "+rival.nombre+" en el PC");                            
                        }
                        capturado = true;
                    }else System.out.println(rival.nombre+" se ha liberado");
                }else System.out.println(rival.nombre+" se ha liberado");
                        
            }else System.out.println(rival.nombre+" se ha liberado");
            pokeballs--;
            sc.nextLine();
            clear();

            return capturado;
        }
        public Pokemon cambiarPokemon (boolean obligatorio) {
            String opcPokemonStr;
            int opcPokemon;
            Pokemon cambioPokemon = null;
            
            mostrarPokemonsDisponibles();
            do{
                opcPokemonStr=sc.nextLine().toUpperCase();
            }while(!cambioPokemonCorrecto(opcPokemonStr) && (!opcPokemonStr.equals("S") || obligatorio));
            if (!opcPokemonStr.equals("S")) {
                opcPokemon = Integer.parseInt(opcPokemonStr)-1;
                
                cambioPokemon = equipo[opcPokemon];
                cambioPokemon.limpiarEstadoTemporalMovimiento();
                System.out.println("Entra "+cambioPokemon.nombre+" al combate");
            }

            sc.nextLine();
            return cambioPokemon;
        }
        private boolean hayPokemon (String opcion, Pokemon[] equipo) {
            boolean correcto=false;
            int x = 0;
            try {
                while(!correcto && x < equipo.length){
                    if (Integer.parseInt(opcion)-1 < equipo.length){
                        correcto = true;
                    }
                    else x++;
                }
            }catch (Exception e){
            }

            return correcto;
        }
        private boolean hayPokemon (String opcion) {
            boolean correcto=false;
            int x = 0;
            try {
                while(!correcto && x < equipo.length){
                    if (Integer.parseInt(opcion)-1 < equipo.length){
                        correcto = true;
                    }
                    else x++;
                }
            }catch (Exception e){
            }

            return correcto;
        }
        private boolean hayPokemonPc (String opcion) {
            boolean correcto=false;
            int x = 0;
            try {
                while(!correcto && x < pc.size()){
                    if (Integer.parseInt(opcion)-1 < pc.size()){
                        correcto = true;
                    }
                    else x++;
                }
            }catch (Exception e){
            }

            return correcto;
        }
        private boolean cambioPokemonCorrecto (String opcion) {
            boolean correcto=false;
            int x = 0;
            ArrayList<Integer> pokDisponibles = pokemonsDisponibles();
            try {
                while(!correcto && x < pokDisponibles.size()){
                    if (Integer.parseInt(opcion)-1 == pokDisponibles.get(x)){
                        correcto = true;
                    }
                    else x++;
                }
            }catch (Exception e){
            }

            return correcto;
        }
        public int mediaNivelEquipo () {
            int sum = 0; 
            for (int i = 0; i < equipo.length; i++) {
                sum += equipo[i].nivel;
            }

            return sum / equipo.length;
        }
        public void mostrarPokemonsDisponibles() {
            for (int i = 0; i < equipo.length; i += 2) {
                System.out.println("\n");

                System.out.printf(
                    "%-100s %-100s",
                    (i+1) + "." + equipo[i].nombre + " Lv." + equipo[i].nivel + " " +
                    equipo[i].tipoToString() + " " +
                    equipo[i].estado.unicode + " " +
                    (equipo[i].vidaActual != 0 ?
                    (String.format("%d", (int)Math.ceil(equipo[i].vidaActual)) + "/" +
                    String.format("%d", (int)Math.ceil(equipo[i].vida)) + " HP") : " KO"),

                    i+1 < equipo.length ?
                    (i+2) + "." + equipo[i+1].nombre + " Lv." + equipo[i+1].nivel + " " +
                    equipo[i+1].tipoToString() + " " +
                    equipo[i+1].estado.unicode + " " +
                    (equipo[i+1].vidaActual != 0 ?
                    (String.format("%d", (int)Math.ceil(equipo[i+1].vidaActual)) + "/" +
                    String.format("%d", (int)Math.ceil(equipo[i+1].vida)) + " HP") : " KO") : ""
                );

                System.out.println();
                imprimir_pokemons_datos(equipo[i].urlImagen(), i+1 < equipo.length ? equipo[i+1].urlImagen() : null, 30);
                // 👇 imprimir movimientos de ambos
                for (int j = 0; j < 4; j += 2) {
                    String mov1a = "";
                    String mov1b = "";
                    String mov2a = "";
                    String mov2b = "";

                    if (equipo[i].sigueVivo()) {
                        mov1a = "\t"+(j+1) + ". " +
                            (equipo[i].movimientos[j] == null ? "None" :
                            equipo[i].movimientos[j].nombre + " " +
                            equipo[i].movimientos[j].tipo.getEmoji());

                        mov1b = "\t"+(j+2) + ". " +
                            (equipo[i].movimientos[j+1] == null ? "None" :
                            equipo[i].movimientos[j+1].nombre + " " +
                            equipo[i].movimientos[j+1].tipo.getEmoji());
                    }
                    if (i+1 < equipo.length && equipo[i+1].sigueVivo()) {
                        mov2a = "\t"+(j+1) + ". " +
                            (equipo[i+1].movimientos[j] == null ? "None" :
                            equipo[i+1].movimientos[j].nombre + " " +
                            equipo[i+1].movimientos[j].tipo.getEmoji());

                        mov2b = "\t"+(j+2) + ". " +
                            (equipo[i+1].movimientos[j+1] == null ? "None" :
                            equipo[i+1].movimientos[j+1].nombre + " " +
                            equipo[i+1].movimientos[j+1].tipo.getEmoji());
                    }

                    System.out.printf("%-30s %-65s    %-30s %-30s%n",
                        mov1a, mov1b, mov2a, mov2b);
                }
            }
        }

    }
    static public class Evolucion {
        private int nivelEvolucion;
        private String preevolucion;
        private String evolucion;
        private List<Tipo> tipo = new ArrayList<>();
        private double sumVel;
        private double sumVida;
        private double sumAtk;
        private double sumDef;
        private double sumSpA;
        private double sumSpD;

        public Evolucion () {
            nivelEvolucion = 999;
        }

    }
    static public class Stats {
        public double velocidad = 0;
        public double vida = 0;
        public double Atk = 0;
        public double Def = 0;
        public double SpA = 0;
        public double SpD = 0;

        public Stats () {}
        public double modificacion (String stat) {
            double[] buffs = {1.5,2,2.5,3,3.5,4};
            double[] debuffs = {0.67,0.5,0.4,0.33,0.28,0.25};

            switch (stat) {
                case "vel":
                    if (velocidad > 0) return buffs[(int)velocidad-1];
                    else if (velocidad < 0) return debuffs[(int)velocidad*-1-1];
                case "vida":
                    if (vida > 0) return buffs[(int)vida-1];
                    else if (vida < 0) return debuffs[(int)vida*-1-1];
                case "atk":
                    if (Atk > 0) return buffs[(int)Atk-1];
                    else if (Atk < 0) return debuffs[(int)Atk*-1-1];
                case "def":
                    if (Def > 0) return buffs[(int)Def-1];
                    else if (Def < 0) return debuffs[(int)Def*-1-1];
                case "spa":
                    if (SpA > 0) return buffs[(int)SpA-1];
                    else if (SpA < 0) return debuffs[(int)SpA*-1-1];
                case "spd":
                    if (SpD > 0) return buffs[(int)SpD-1];
                    else if (SpD < 0) return debuffs[(int)SpD*-1-1];
            }

            return 0;
        }
        public void generarIVs () {
            this.velocidad = random.nextInt(1,32);
            this.vida = random.nextInt(1,32);
            this.Atk = random.nextInt(1,32);
            this.Def = random.nextInt(1,32);
            this.SpA = random.nextInt(1,32);
            this.SpD = random.nextInt(1,32);
        }
        public void modificarStatsBase (double velocidad, double vida, double Atk, double Def, double SpA, double SpD){
            this.velocidad = velocidad;
            this.vida = vida;
            this.Atk = Atk;
            this.Def = Def;
            this.SpA = SpA;
            this.SpD = SpD;
        }
    }
    static public class Pokemon {
        private static final String ANSI_RESET = "\u001B[0m";
        private static final String ANSI_RED = "\u001B[31m";
        private static final String ANSI_BLUE = "\u001B[34m";
        private String nombre;
        private List<Tipo> tipo = new ArrayList<>();
        private Estado estado = new Estado();
        private double velocidad;
        private double vida;
        private double vidaActual;
        private double Atk;
        private double Def;
        private double SpA;
        private double SpD;
        private String Naturaleza; 
        private Stats IVs = new Stats();
        private Stats modificacionesStats = new Stats();
        private Stats statsBase = new Stats();
        private Movimiento [] movimientos = new Movimiento[4];
        private int turnosEstado = 0;
        private int nivel = 0;
        private Evolucion evolucion;
        private List <Movimiento> movimientosOlvidados = new ArrayList<>();

        public Pokemon () {}

        public Pokemon(Pokemon otro) {
            this.nombre = otro.nombre;
            this.tipo = new ArrayList<>(otro.tipo);
            this.estado = new Estado();
            this.velocidad = otro.velocidad;
            this.vida = otro.vida;
            this.vidaActual = otro.vida;
            this.Atk = otro.Atk;
            this.Def = otro.Def;
            this.SpA = otro.SpA;
            this.SpD = otro.SpD;
            this.Naturaleza = otro.Naturaleza;
            if (otro.statsBase != null) this.statsBase = otro.statsBase;
            if (otro.movimientos != null) {
                for (int i = 0; i < movimientos.length; i++) {
                    movimientos[i] = otro.movimientos[i];
                }
            }
            else this.movimientos = new Movimiento[4];
            this.turnosEstado = 0;
            this.nivel = otro.nivel;
            this.evolucion = otro.evolucion;
        }
        public double getVelocidad() {
          if (modificacionesStats.modificacion("vel") != 0) return velocidad * modificacionesStats.modificacion("vel");
          else return velocidad;
        }
        public double getAtk() {
          if (modificacionesStats.modificacion("atk") != 0) return Atk * modificacionesStats.modificacion("atk");
          else return Atk;
        }
        public double getDef() {
          if (modificacionesStats.modificacion("def") != 0) return Def * modificacionesStats.modificacion("def");
          else return Def;
        }
        public double getSpA() {
          if (modificacionesStats.modificacion("spa") != 0) return SpA * modificacionesStats.modificacion("spa");
          else return SpA;
        }
        public double getSpD() {
          if (modificacionesStats.modificacion("spd") != 0) return SpD * modificacionesStats.modificacion("spd");
          else return SpD;
        }
        public void generarNaturaleza() {
            String[] naturalezas = {"Huraña", "Solo", "Firme", "Audaz", "Pícara", "Osada", "Dócil", "Plácida", "Agitada", "Alocada", "Miedosa", "Activa", "Seria", "Alegre", "Ingenua", "Modesta", "Afable", "Mansa", "Tímida", "Flojera", "Serena", "Gentil", "Grosera", "Cauta", "Rara"};
            this.Naturaleza = naturalezas[random.nextInt(naturalezas.length)];
        }
        public void imprimirPokemon() {
            imprimirImagenesPokemon(Arrays.asList(urlImagen()), 46, 0);
        }
        public String urlImagen () {
            String url = "images/";
            String[] nomSplited = nombre.split(" ");
            for (int i = 0; i < nomSplited.length; i++) {
                url += nomSplited[i].toLowerCase()+"-";
            }
            url = url.substring(0,url.length()-1)+".png";

            return url;
        }
        public void aplicarNaturaleza () {
            switch (Naturaleza) {
                case "Huraña":
                    Def *= 0.9;
                    Atk *= 1.1;
                    break;
                case "Solo":
                    SpD *= 0.9;
                    Atk *= 1.1;
                    break;
                case "Firme":
                    SpA *= 0.9;
                    Atk *= 1.1;
                    break;
                case "Audaz":
                    velocidad *= 0.9;
                    Atk *= 1.1;
                    break;
                case "Pícara":
                    Atk *= 0.9;
                    Def *= 1.1;
                    break;
                case "Osada":
                    SpA *= 0.9;
                    Def *= 1.1;
                    break;
                case "Plácida":
                    SpD *= 0.9;
                    Def *= 1.1;
                    break;
                case "Agitada":
                    velocidad *= 0.9;
                    Def *= 1.1;
                    break;
                case "Miedosa":
                    Atk *= 0.9;
                    velocidad *= 1.1;
                    break;
                case "Activa":
                    Def *= 0.9;
                    velocidad *= 1.1;
                    break;
                case "Alegre":
                    SpA *= 0.9;
                    velocidad *= 1.1;
                    break;
                case "Ingenua":
                    SpD *= 0.9;
                    velocidad *= 1.1;
                    break;
                case "Modesta":
                    Atk *= 0.9;
                    SpA *= 1.1;
                    break;
                case "Afable":
                    Def *= 0.9;
                    SpA *= 1.1;
                    break;
                case "Mansa":
                    SpD *= 0.9;
                    SpA *= 1.1;
                    break;
                case "Tímida":
                    velocidad *= 0.9;
                    SpA *= 1.1;
                    break;
                case "Serena":
                    Atk *= 0.9;
                    SpD *= 1.1;
                    break;
                case "Gentil":
                    Def *= 0.9;
                    SpD *= 1.1;
                    break;
                case "Grosera":
                    SpA *= 0.9;
                    SpD *= 1.1;
                    break;
                case "Cauta":
                    velocidad *= 0.9;
                    SpD *= 1.1;
                    break;
                default:
                    break;
            }

        }
        public int tieneHabilidadCurativa () {
            for (int i = 0; i < numMovimientos(); i++) {
                if (movimientos[i].curar > 0 && movimientos[i].buffsAlEjecutador) return i;
            }

            return -1;
        }
        public boolean yaTieneEstado () {
            return !estado.nombre.equals("NINGUNO");
        }
        public void limpiarEstadoTemporalMovimiento () {
            if (estado.nombre.equals("DESAPARECER") || estado.nombre.equals("CARGANDO")) {
                estado = new Estado();
                turnosEstado = 0;
            }
        }
        public void curar () {
            vidaActual = vida;
            estado = new Estado();
            turnosEstado = 0;
            limpiarStatsDeModificadores();
        }
        private void actualizarEvolucion (ArrayList<Evolucion> evoluciones) {
            boolean encontrado = false;
            int x = 0;
            while (!encontrado && x < evoluciones.size()){
                if (evoluciones.get(x).preevolucion.equals(nombre)) {
                    this.evolucion = evoluciones.get(x);
                    encontrado = true;
                }
                else x++;
            }

            if (!encontrado) evolucion = new Evolucion();
        }
        public void limpiarStatsDeModificadores () {
            this.modificacionesStats.velocidad = 0;
            this.modificacionesStats.Atk = 0;
            this.modificacionesStats.Def = 0;
            this.modificacionesStats.SpA = 0;
            this.modificacionesStats.SpD = 0;
        }
        public void subirNivel (int subidaNivel, ArrayList<Movimiento> movimientos, ArrayList<Evolucion> evoluciones) {
            this.nivel += subidaNivel;
            formulaSubidaNivel();
            System.out.println(nombre+" ha subido a nivel "+nivel+"!!");
            aprenderMovimientoNuevo(movimientos);
            evolucionar(true, evoluciones);
        }
        public void setNivel(int nuevoNivel) {
            this.nivel = nuevoNivel;
            formulaSubidaNivel();
        }
        public void formulaSubidaNivel () {
            vida = (((2 * statsBase.vida + IVs.vida + (85/4)) / 100) * nivel + nivel + 10);
            Atk = (((2 * statsBase.Atk + IVs.Atk + (85/4)) / 100) * nivel + 5) * 1;
            Def = (((2 * statsBase.Def + IVs.Def + (85/4)) / 100) * nivel + 5) * 1;
            SpA = (((2 * statsBase.SpA + IVs.SpA + (85/4)) / 100) * nivel + 5) * 1;
            SpD = (((2 * statsBase.SpD + IVs.SpD + (85/4)) / 100) * nivel + 5) * 1;
            velocidad = (((2 * statsBase.velocidad + IVs.velocidad + (85/4)) / 100) * nivel + 5) * 1;
            vidaActual = vida;
            aplicarNaturaleza();
        }
        public void evolucionar(boolean mostrar, ArrayList<Evolucion> evoluciones) {
            if (evolucion.nivelEvolucion <= nivel) {
                if (mostrar){
                    System.out.println(nombre+" esta evolucionando!!!");
                    sc.nextLine();
                    System.out.println(nombre+" ha evolucionado a \n✨✨✨"+evolucion.evolucion+"✨✨✨");
                }    
                nombre = evolucion.evolucion;
                statsBase.modificarStatsBase(
                    evolucion.sumVel,
                    evolucion.sumVida,
                    evolucion.sumAtk,
                    evolucion.sumDef,
                    evolucion.sumSpA,
                    evolucion.sumSpD
                );
                formulaSubidaNivel();
                vidaActual = vida;
                tipo.clear();
                for (int i = 0; i < evolucion.tipo.size(); i++) {
                    tipo.add(evolucion.tipo.get(i));
                }
                actualizarEvolucion(evoluciones);
                if (mostrar) imprimirPokemon();
            }

        }
        public boolean movimientoRepetido (Movimiento movimiento) {
            boolean repetido = false;
            int x = 0;

            while (!repetido && x < movimientos.length){
                try {
                    if (movimientos[x].equals(movimiento)){
                        repetido = true;
                    }
                    else x++;
                }catch (NullPointerException e) {x++;}
            }

            return repetido;
        }
        public void aprenderMovimientoNuevo (ArrayList <Movimiento> movimientos) {
            if (numMovimientos()<4){
                aprenderMovimiento(movimientos);
                System.out.println(nombre+" ha aprendido "+this.movimientos[numMovimientos()-1].nombre+"!");
            }
            else {
                Movimiento mov = aprenderMovimiento(movimientos);
                String opcion = "";
                if (mov != null) {
                    System.out.println(nombre+" "+fortalezaPokemon()+" "+tipoToString()+" quiere aprender "+mov.info()+" pero ya tiene 4 movimientos: o/a");
                    do{
                        opcion = sc.nextLine().toUpperCase();
                    }while (!opcion.equals("O") && !opcion.equals("A"));

                    switch (opcion) {
                        case "O":
                            System.out.println(nombre+" ha olvidado "+mov.nombre);
                            movimientosOlvidados.add(mov);
                            break;
                    
                        case "A":
                            mostrarMovimientosDetalladamente();
                            System.out.println("'c' para cancelar");
                            do{
                                opcion = sc.nextLine().toUpperCase();
                            }while(!opcion.equals("C") && !movimientoDisponible(opcion));

                            if (!opcion.equals("C")) {
                                movimientosOlvidados.add(this.movimientos[Integer.parseInt(opcion)-1]);
                                this.movimientos[Integer.parseInt(opcion)-1] = mov;
                                System.out.println(nombre+" ha aprendido "+mov.nombre+"!");
                            }
                            else movimientosOlvidados.add(mov);

                            break;
                    }
                }
            }
        }
        private void mostrarMovimientosDetalladamente () {
            for (int i = 0; i < movimientos.length; i += 2) {
                String mov1 = "\t"+(i+1)+". "+(movimientos[i] == null ? "None" : movimientos[i].nombre) + " " + (movimientos[i] == null ? "" : movimientos[i].tipo.getEmoji()+" "+movimientos[i].potencia+" "+movimientos[i].categoria.name());
                String mov2 = "\t"+(i+2)+". "+(movimientos[i + 1] == null ? "None" : movimientos[i + 1].nombre) + " " + (movimientos[i + 1] == null ? "" : movimientos[i + 1].tipo.getEmoji()+" "+movimientos[i + 1].potencia+" "+movimientos[i + 1].categoria.name());
                System.out.printf("%-45s %-45s%n", mov1, mov2);
            }
        }
        public Movimiento aprenderMovimiento(ArrayList <Movimiento> movimientos) {
            List <Movimiento> lmc; 
            Movimiento aprender = null;

            lmc = listaMovimientosCompatibles(movimientos);
            int movComp = 0;
            boolean aprende = false;

            while (!aprende && lmc.size() > 0){
                if (!movimientoRepetido(lmc.get(movComp)) && !movimientoOlvidado(lmc.get(movComp))) {
                    aprende=true;
                }
                else {
                    lmc.remove(movComp);
                    if (lmc.size() > 0)movComp = random.nextInt(lmc.size());
                }
            }

            if (!aprende) aprender = null;
            else aprender = lmc.get(movComp);

            if (numMovimientos() < 4) this.movimientos[numMovimientos()] = aprender;
            return aprender;
        }
        public boolean movimientoOlvidado (Movimiento mov) {
            boolean olvidado = false;
            int x = 0;

            while (!olvidado && x < movimientosOlvidados.size()) {
                if (movimientosOlvidados.get(x).equals(mov)) {
                    olvidado = true;
                }
                else x++;
            }

            return olvidado;
        }
        public List <Movimiento> listaMovimientosCompatibles (ArrayList <Movimiento> movimientos) {
            List <Movimiento> movimientosCompatibles = new ArrayList<>();
            boolean ok;
            boolean ok2;
            int x; 
            int y;


            for (int i = 0; i < movimientos.size(); i++) {
                ok2 = false;
                y = 0;
                while (!ok2 && y < movimientos.get(i).tiposCompatibles.size()) {
                    x = 0;
                    ok = false;
                    while(!ok && x < tipo.size()) {
                        if (tipo.get(x).equals(movimientos.get(i).tiposCompatibles.get(y)) && movimientos.get(i).nivelMinimo <= nivel) {
                            movimientosCompatibles.add(movimientos.get(i));
                            ok = true;
                        }
                        else x++;
                    }
                    y++;
                }
            }

            
            return movimientosCompatibles;
        }
        public boolean movimientoDisponible (String opcion) {
            int opcionInt;

            try {
                opcionInt = Integer.parseInt(opcion);
                return 0 < opcionInt && opcionInt <= numMovimientos();
            } catch (Exception e) {
                return false;
            }

        }
        public int numMovimientos () {
            int x = 0;
            for (int i = 0; i < movimientos.length; i++) {
                if (movimientos[i] != null) x++;
            }
            return x;
        }
        public String fortalezaPokemon () {
            if (Atk-SpA > Atk*0.17) return Categoria.FISICO.name();
            else if (SpA-Atk > SpA*0.17) return Categoria.ESPECIAL.name();
            else return "EQUILIBRADO";
        }
        public int valorStats () {
            return (int)(Atk+Def+SpA+SpD+vida+velocidad);
        }
        public boolean equalsTipo (Tipo tipo) {
            boolean igual =  false;
            int x=0;
            while (!igual && x < this.tipo.size()){
                if (this.tipo.get(x).equals(tipo)) igual = true;
                else x++;
            }
            return igual;
        }
        public String tipoToString () {
            String tipos = "";
            for (int i = 0; i < tipo.size(); i++) {
                tipos += tipo.get(i).getEmoji()+" ";
            }
            return tipos;
        }
        private String statSubidaNaturaleza() {
            if (Naturaleza == null) return "";

            switch (Naturaleza) {
                case "Huraña":
                case "Solo":
                case "Firme":
                case "Audaz":
                    return "Atk";
                case "Pícara":
                case "Osada":
                case "Plácida":
                case "Agitada":
                    return "Def";
                case "Miedosa":
                case "Activa":
                case "Alegre":
                case "Ingenua":
                    return "Vel";
                case "Modesta":
                case "Afable":
                case "Mansa":
                case "Tímida":
                    return "SpA";
                case "Serena":
                case "Gentil":
                case "Grosera":
                case "Cauta":
                    return "SpD";
                default:
                    return "";
            }
        }
        private String statBajadaNaturaleza() {
            if (Naturaleza == null) return "";

            switch (Naturaleza) {
                case "Pícara":
                case "Miedosa":
                case "Modesta":
                case "Serena":
                    return "Atk";
                case "Huraña":
                case "Activa":
                case "Afable":
                case "Gentil":
                    return "Def";
                case "Solo":
                case "Osada":
                case "Alegre":
                case "Grosera":
                case "Firme":
                    return "SpA";
                case "Plácida":
                case "Ingenua":
                case "Mansa":
                    return "SpD";
                case "Audaz":
                case "Agitada":
                case "Tímida":
                case "Cauta":
                    return "Vel";
                default:
                    return "";
            }
        }
        private String colorearCampoNaturaleza(String stat, String texto) {
            if (stat.equals(statSubidaNaturaleza())) return ANSI_RED + texto + ANSI_RESET;
            if (stat.equals(statBajadaNaturaleza())) return ANSI_BLUE + texto + ANSI_RESET;
            return texto;
        }
        private String rellenarDerecha(String texto, int ancho) {
            return String.format("%-" + ancho + "s", texto);
        }
        public boolean sigueVivo(){
            return vidaActual > 0;
        }
        public double calcularEfectividad(Movimiento movimiento){
            Double efectividad = 1.0;

            for (int i = 0; i < tipo.size(); i++) {
                efectividad = efectividad * calcularEfectividadUnTipo(movimiento, tipo.get(i));
            }

            return efectividad;
        }

        public double calcularEfectividadUnTipo(Movimiento movimiento, Tipo tipo) {
            Tipo mov = movimiento.tipo;

            // NORMAL
            if (mov.equals(Tipo.NORMAL)) {
                if (tipo.equals(Tipo.FANTASMA)) return 0; // inmune
                if (tipo.equals(Tipo.ROCA) || tipo.equals(Tipo.ACERO)) return 0.5;
            }

            // FUEGO
            else if (mov.equals(Tipo.FUEGO)) {
                if (tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.BICHO) || tipo.equals(Tipo.HIELO) || tipo.equals(Tipo.ACERO)) return 2;
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.AGUA) || tipo.equals(Tipo.ROCA) || tipo.equals(Tipo.DRAGON)) return 0.5;
            }

            // AGUA
            else if (mov.equals(Tipo.AGUA)) {
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.TIERRA) || tipo.equals(Tipo.ROCA)) return 2;
                if (tipo.equals(Tipo.AGUA) || tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.DRAGON)) return 0.5;
            }

            // PLANTA
            else if (mov.equals(Tipo.PLANTA)) {
                if (tipo.equals(Tipo.AGUA) || tipo.equals(Tipo.TIERRA) || tipo.equals(Tipo.ROCA)) return 2;
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.BICHO) || tipo.equals(Tipo.VOLADOR) || tipo.equals(Tipo.VENENO) || tipo.equals(Tipo.DRAGON) || tipo.equals(Tipo.ACERO)) return 0.5;
            }

            // RAYO
            else if (mov.equals(Tipo.RAYO)) {
                if (tipo.equals(Tipo.AGUA) || tipo.equals(Tipo.VOLADOR)) return 2;
                if (tipo.equals(Tipo.RAYO) || tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.DRAGON)) return 0.5;
                if (tipo.equals(Tipo.TIERRA)) return 0; // ⚠️ corregido (era 0.5)
            }

            // TIERRA
            else if (mov.equals(Tipo.TIERRA)) {
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.RAYO) || tipo.equals(Tipo.VENENO) || tipo.equals(Tipo.ROCA) || tipo.equals(Tipo.ACERO)) return 2;
                if (tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.BICHO)) return 0.5;
                if (tipo.equals(Tipo.VOLADOR)) return 0; // inmune
            }

            // HIELO
            else if (mov.equals(Tipo.HIELO)) {
                if (tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.TIERRA) || tipo.equals(Tipo.VOLADOR) || tipo.equals(Tipo.DRAGON)) return 2;
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.AGUA) || tipo.equals(Tipo.HIELO) || tipo.equals(Tipo.ACERO)) return 0.5;
            }

            // LUCHA
            else if (mov.equals(Tipo.LUCHA)) {
                if (tipo.equals(Tipo.NORMAL) || tipo.equals(Tipo.ROCA) || tipo.equals(Tipo.HIELO) || tipo.equals(Tipo.SINIESTRO) || tipo.equals(Tipo.ACERO)) return 2;
                if (tipo.equals(Tipo.PSIQUICO) || tipo.equals(Tipo.VOLADOR) || tipo.equals(Tipo.VENENO) || tipo.equals(Tipo.BICHO) || tipo.equals(Tipo.HADA)) return 0.5;
                if (tipo.equals(Tipo.FANTASMA)) return 0; // ⚠️ corregido duplicado
            }

            // VENENO
            else if (mov.equals(Tipo.VENENO)) {
                if (tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.HADA)) return 2;
                if (tipo.equals(Tipo.VENENO) || tipo.equals(Tipo.TIERRA) || tipo.equals(Tipo.ROCA) || tipo.equals(Tipo.FANTASMA)) return 0.5;
                if (tipo.equals(Tipo.ACERO)) return 0; // ⚠️ corregido (inmune)
            }

            // VOLADOR
            else if (mov.equals(Tipo.VOLADOR)) {
                if (tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.BICHO) || tipo.equals(Tipo.LUCHA)) return 2;
                if (tipo.equals(Tipo.RAYO) || tipo.equals(Tipo.ROCA) || tipo.equals(Tipo.ACERO)) return 0.5;
            }

            // PSIQUICO
            else if (mov.equals(Tipo.PSIQUICO)) {
                if (tipo.equals(Tipo.LUCHA) || tipo.equals(Tipo.VENENO)) return 2;
                if (tipo.equals(Tipo.PSIQUICO) || tipo.equals(Tipo.ACERO)) return 0.5;
                if (tipo.equals(Tipo.SINIESTRO)) return 0; // ⚠️ corregido
            }

            // BICHO
            else if (mov.equals(Tipo.BICHO)) {
                if (tipo.equals(Tipo.PSIQUICO) || tipo.equals(Tipo.PLANTA) || tipo.equals(Tipo.SINIESTRO)) return 2;
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.LUCHA) || tipo.equals(Tipo.FANTASMA) || tipo.equals(Tipo.ACERO) || tipo.equals(Tipo.VENENO) || tipo.equals(Tipo.VOLADOR) || tipo.equals(Tipo.HADA)) return 0.5;
            }

            // ROCA
            else if (mov.equals(Tipo.ROCA)) {
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.HIELO) || tipo.equals(Tipo.VOLADOR) || tipo.equals(Tipo.BICHO)) return 2;
                if (tipo.equals(Tipo.LUCHA) || tipo.equals(Tipo.TIERRA) || tipo.equals(Tipo.ACERO)) return 0.5;
            }

            // SINIESTRO
            else if (mov.equals(Tipo.SINIESTRO)) {
                if (tipo.equals(Tipo.PSIQUICO) || tipo.equals(Tipo.FANTASMA)) return 2;
                if (tipo.equals(Tipo.LUCHA) || tipo.equals(Tipo.SINIESTRO) || tipo.equals(Tipo.BICHO) || tipo.equals(Tipo.HADA)) return 0.5;
            }

            // FANTASMA
            else if (mov.equals(Tipo.FANTASMA)) {
                if (tipo.equals(Tipo.FANTASMA) || tipo.equals(Tipo.PSIQUICO)) return 2;
                if (tipo.equals(Tipo.NORMAL)) return 0; // inmune
            }

            // ACERO
            else if (mov.equals(Tipo.ACERO)) {
                if (tipo.equals(Tipo.HIELO) || tipo.equals(Tipo.ROCA) || tipo.equals(Tipo.HADA)) return 2;
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.AGUA) || tipo.equals(Tipo.RAYO) || tipo.equals(Tipo.ACERO)) return 0.5;
            }

            // DRAGÓN
            else if (mov.equals(Tipo.DRAGON)) {
                if (tipo.equals(Tipo.DRAGON)) return 2;
                if (tipo.equals(Tipo.ACERO)) return 0.5;
                if (tipo.equals(Tipo.HADA)) return 0;
            }

            // HADA
            else if (mov.equals(Tipo.HADA)) {
                if (tipo.equals(Tipo.LUCHA) || tipo.equals(Tipo.DRAGON) || tipo.equals(Tipo.SINIESTRO)) return 2;
                if (tipo.equals(Tipo.FUEGO) || tipo.equals(Tipo.VENENO) || tipo.equals(Tipo.ACERO)) return 0.5;
            }
            return 1; // neutral
        }

        public boolean aplicarEstado () {
            boolean curado = false;
            if (estado.potencia > 0) {
                vidaActual -= vida * estado.potencia;
                imprimirDañoEstado();
            }

            turnosEstado --;
            if (turnosEstado == 0 || random.nextDouble(0,1) < estado.probIrse) {
                estado = new Estado();
                curado = true;
            }

            return curado;
        }

        private void imprimirDañoEstado () {
            System.out.println(nombre+" sufre "+estado.nombre);
        }
        private void mensajesModificacion (Movimiento mov) {
            String statsSuben = "";
            String statsBajan = "";
            if (mov.subirVel > 0) statsSuben += "Velocidad, "; 
            else if (mov.subirVel < 0) statsBajan += "Velocidad, ";
            if (mov.subirAtk > 0) statsSuben += "Ataque Fisico, "; 
            else if (mov.subirAtk < 0) statsBajan += "Ataque Fisico, ";
            if (mov.subirDef > 0) statsSuben += "Defensa Fisica, "; 
            else if (mov.subirDef < 0) statsBajan += "Defensa Fisica, ";
            if (mov.subirSpA > 0) statsSuben += "Ataque Especial, "; 
            else if (mov.subirSpA < 0) statsBajan += "Ataque Especial, ";
            if (mov.subirSpD > 0) statsSuben += "Defensa Especial, "; 
            else if (mov.subirSpD < 0) statsBajan += "Defensa Especial, ";
            
            if (!statsSuben.equals("")) System.out.println("El "+statsSuben.substring(0,statsSuben.length()-2)+" de "+nombre+" ha subido");
            if (!statsBajan.equals("")) System.out.println("El "+statsBajan.substring(0,statsBajan.length()-2)+" de "+nombre+" ha bajado");

        }
        public void recibirMovimiento (Movimiento movimiento, double daño, boolean rival) {       
            if (rival && daño > 1){
                vidaActual = vidaActual - daño <= 0 ? 0 : vidaActual - daño;
            }
            else if (rival) vidaActual = vidaActual - (vida * daño) < 0 ? 0 : vidaActual - (vida * daño);

            boolean recibeEfectosPropios = (!movimiento.buffsAlEjecutador && rival) || (movimiento.buffsAlEjecutador && !rival);

            if (recibeEfectosPropios && movimiento.curar != 0) {
                if (movimiento.curar > 0){ //CURA 
                    if (movimiento.curar > 1) vidaActual = vidaActual + movimiento.curar > vida ? vida : vidaActual + movimiento.curar;
                    else vidaActual = vidaActual + (vida * movimiento.curar) > vida ? vida : vidaActual + (vida * movimiento.curar);
                }
                else{ //DAÑA 
                    if (movimiento.curar < -1) vidaActual = vidaActual + movimiento.curar < 0 ? 0 : vidaActual + movimiento.curar;
                    else vidaActual = vidaActual + (vida * movimiento.curar) < 0 ? 0 : vidaActual + (vida * movimiento.curar);
                }
            }

            if (recibeEfectosPropios && random.nextDouble(0,1) < movimiento.probModificacionStats) {
                if (modificacionesStats.velocidad + movimiento.subirVel < 7 && modificacionesStats.velocidad + movimiento.subirVel > -7) modificacionesStats.velocidad += movimiento.subirVel;
                else modificacionesStats.velocidad = modificacionesStats.velocidad > 0 ? 6 : -6;
                if (modificacionesStats.Atk + movimiento.subirAtk < 7 && modificacionesStats.Atk + movimiento.subirAtk > -7) modificacionesStats.Atk += movimiento.subirAtk;
                else modificacionesStats.Atk = modificacionesStats.Atk > 0 ? 6 : -6;
                if (modificacionesStats.Def + movimiento.subirDef < 7 && modificacionesStats.Def + movimiento.subirDef > -7) modificacionesStats.Def += movimiento.subirDef;
                else modificacionesStats.Def = modificacionesStats.Def > 0 ? 6 : -6;
                if (modificacionesStats.SpA + movimiento.subirSpA < 7 && modificacionesStats.SpA + movimiento.subirSpA > -7) modificacionesStats.SpA += movimiento.subirSpA;
                else modificacionesStats.SpA = modificacionesStats.SpA > 0 ? 6 : -6;
                if (modificacionesStats.SpD + movimiento.subirSpD < 7 && modificacionesStats.SpD + movimiento.subirSpD > -7) modificacionesStats.SpD += movimiento.subirSpD;
                else modificacionesStats.SpD = modificacionesStats.SpD > 0 ? 6 : -6;

                mensajesModificacion(movimiento);
            }

            if ((!movimiento.estadoAlEjecutador && rival && movimiento.potencia > 0 && daño == 0) || (movimiento.estadoAlEjecutador && !rival)) {
                if (random.nextDouble(0,1) < movimiento.probEstado && !yaTieneEstado()) {
                    if (movimiento.estado.nombre.equals("QUEMADURA")) modificacionesStats.Atk = -2;
                    turnosEstado = movimiento.estado.maxTurnos;
                    this.estado = movimiento.estado; 
                }
            }
            
        } 

        @Override
        public String toString() {
            return String.format(
                "%-6d %-16s %-15s\t%-10s %-12s %-18s",
                valorStats(),
                nombre,
                "| Tipo: " + tipoToString().trim(),
                "| Lv: " + nivel,
                "| " + Naturaleza,
                "| Vida: " + (int) vida + " (" + (int) IVs.vida + ")"
            ) + " " +
            colorearCampoNaturaleza("Atk", rellenarDerecha("| Atk: " + (int) Atk + " (" + (int) IVs.Atk + ")", 17)) + " " +
            colorearCampoNaturaleza("Def", rellenarDerecha("| Def: " + (int) Def + " (" + (int) IVs.Def + ")", 17)) + " " +
            colorearCampoNaturaleza("SpA", rellenarDerecha("| SpA: " + (int) SpA + " (" + (int) IVs.SpA + ")", 17)) + " " +
            colorearCampoNaturaleza("SpD", rellenarDerecha("| SpD: " + (int) SpD + " (" + (int) IVs.SpD + ")", 17)) + " " +
            colorearCampoNaturaleza("Vel", rellenarDerecha("| Vel: " + (int) velocidad + " (" + (int) IVs.velocidad + ")", 17));

        } 
    }
    static public class Movimiento {
        private String nombre = "";
        private Tipo tipo = Tipo.NORMAL;
        private Categoria categoria = Categoria.FISICO;
        private int prioridad = 0;
        private int turnosNecesarios = 1;
        private double precision = 1.0;
        private double potencia = 40;
        private double curar = 0;
        private double subirVel = 0;
        private double subirAtk = 0;
        private double subirDef = 0;
        private double subirSpA = 0;
        private double subirSpD = 0;
        private boolean buffsAlEjecutador = false;
        private Estado estado = new Estado();
        private double probEstado = 0.0;
        private double probModificacionStats = 0.0;
        private boolean estadoAlEjecutador = false;
        private List<Tipo> tiposCompatibles = new ArrayList<>();
        private int nivelMinimo = 0;

        public Movimiento () {}
        public Movimiento (int prioridad) {
            this.prioridad=prioridad;
        }
        public Movimiento (Movimiento mov) {
            this.nombre = mov.nombre;
            this.tipo = mov.tipo;
            this.categoria = mov.categoria;
            this.prioridad = mov.prioridad;
            this.turnosNecesarios = mov.turnosNecesarios;
            this.precision = mov.precision;
            this.potencia = mov.potencia;
            this.curar = mov.curar;
            this.subirVel = mov.subirVel;
            this.subirAtk = mov.subirAtk;
            this.subirDef = mov.subirDef;
            this.subirSpA = mov.subirSpA;
            this.subirSpD = mov.subirSpD;
            this.buffsAlEjecutador = mov.buffsAlEjecutador;
            this.estado = mov.estado;
            this.probEstado = mov.probEstado;
            this.probModificacionStats = mov.probModificacionStats;
            this.estadoAlEjecutador = mov.estadoAlEjecutador;
        }
        public String info () {
            String infoEstado = "";
            String infoModificacion = "";
            String infoVida = "";
            String infoPrioridad = "";

            if (!estado.nombre.equals("NINGUNO")) infoEstado += "\nTiene una probabilidad del "+(int)(probEstado*100)+"% de aplicar "+estado.nombre+" al "+(estadoAlEjecutador ? "usuario" : "rival");
            if (probModificacionStats > 0) {
                String statsModificadas = "";
                String tipoModificacion = "";
                if (subirVel > 0) statsModificadas += "aumentar "+(subirVel==2 ? "mucho " : "")+"Velocidad, "; 
                else if (subirVel < 0) statsModificadas += "disminuir "+(subirVel==-2 ? "mucho " : "")+"Velocidad, "; 
                if (subirAtk > 0) statsModificadas += "aumentar "+(subirAtk==2 ? "mucho " : "")+"Ataque Fisico, "; 
                else if (subirAtk < 0) statsModificadas += "disminuir "+(subirAtk==-2 ? "mucho " : "")+"Ataque Fisico, "; 
                if (subirDef > 0) statsModificadas += "aumentar "+(subirDef==2 ? "mucho " : "")+"Defensa Fisica, "; 
                else if (subirDef < 0) statsModificadas += "disminuir "+(subirDef==-2 ? "mucho " : "")+"Defensa Fisica, "; 
                if (subirSpA > 0) statsModificadas += "aumentar "+(subirSpA==2 ? "mucho " : "")+"Ataque Especial, "; 
                else if (subirSpA < 0) statsModificadas += "disminuir "+(subirSpA==-2 ? "mucho " : "")+"Ataque Especial, "; 
                if (subirSpD > 0) statsModificadas += "aumentar "+(subirSpD==2 ? "mucho " : "")+"Defensa Especial, "; 
                else if (subirSpD < 0) statsModificadas += "disminuir "+(subirSpD==-2 ? "mucho " : "")+"Defensa Especial, "; 

                statsModificadas = statsModificadas.substring(0, statsModificadas.length()-2);
                infoModificacion += "\nTiene una probabilidad del "+(int)(probModificacionStats*100)+"% de "+statsModificadas+" al "+(buffsAlEjecutador ? "usario" : "rival");
            }
            if (curar > 0) {
                if (curar > 1) infoVida += "\nCura "+curar+" al usuario";
                else infoVida += "\nCura "+(int)(curar*100)+"% al usuario";
            }
            else if (curar < 0 && buffsAlEjecutador) {
                if (curar < -1) infoVida += "\nTiene retroceso de "+curar+ "al usuario";
                else infoVida += "\nTiene retroceso de "+(int)(curar*100)+"% al usuario";
            }
            if (prioridad == 1) infoPrioridad = " con prioridad";
            else if (prioridad == 2) infoPrioridad = " con maxima prioridad";
            return nombre+" "+((int)potencia > 0 ? (int)potencia : "--")+"💥 "+(int)(precision*100)+"🎯 "+tipo.getEmoji()+" "+categoria.name()+infoPrioridad+infoEstado+infoModificacion+infoVida;
        }
        public String toString () {
            return String.format("%-30s", nombre+" "+tipo)+" \t"+prioridad+" \t"+precision+" \t"+potencia+" \t"+curar+" \t"+subirVel+" \t"+subirAtk+" \t"+subirDef+" \t"+subirSpA+" \t"+subirSpD+" \t"+buffsAlEjecutador+" "+estado.nombre+" "+probEstado+" "+probModificacionStats+" "+estadoAlEjecutador;
        }
    }
    static public class Estado {
        private String nombre;
        private String unicode;
        private double probMoverte;
        private double potencia;
        private double probIrse; 
        private int maxTurnos;   

        public Estado () {
            this.nombre = "NINGUNO";
            this.unicode = "";
            this.probMoverte = 1.0;
            this.potencia = 0.0;
            this.probIrse = 0.0;
            this.maxTurnos = 0;
        }

        public String toString () {
            return nombre+" "+unicode+" "+probMoverte+" "+potencia+" "+probIrse+" "+maxTurnos;
        }
    }
    static enum Categoria {
        FISICO("⚔️"),
        ESPECIAL("✨"),
        ESTADO("🧪");

        private String unicode;

        Categoria(String unicode) {
            this.unicode = unicode;
        }
    }
    static enum Tipo {
        NORMAL("\u26AA"),       // ⚪
        PLANTA("\uD83C\uDF3F"), // 🌿
        FUEGO("\uD83D\uDD25"),  // 🔥
        RAYO("\u26A1"),         // ⚡
        TIERRA("\u26F0"),       // ⛰️
        AGUA("\uD83D\uDCA7"),   // 💧
        HIELO("❄️"),            // ❄️
        LUCHA("💪"),  // 🎨 
        VENENO("\u2620"),        // ☠ 
        VOLADOR("🐦"), //  
        PSIQUICO("👁"), // 🤖 
        BICHO("\uD83D\uDC1B"),   // 🐛
        SINIESTRO("🌑"),
        FANTASMA("👻"),
        ACERO("⚙"),
        DRAGON("🐉"),
        ROCA("🪨"),
        HADA("🧚");


        private final String emoji;

        // Constructor
        Tipo(String emoji) {
            this.emoji = emoji;
        }

        // Método para obtener el emoji
        public String getEmoji() {
            return emoji;
        }
    }
}
