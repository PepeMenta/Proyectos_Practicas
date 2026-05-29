import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class poker_codex {
    private static final Scanner SC = new Scanner(System.in);
    private static final Random RANDOM = new Random();
    private static final List<Player> jugadoresMesaActual = new ArrayList<>();
    private static final int STACK_INICIAL = 500;
    private static final int CIEGA_PEQUENA = 5;
    private static final int CIEGA_GRANDE = 10;
    private static final int JUGADOR_HUMANO_ID = 1;

    public static void main(String[] args) {
        System.out.println("TEXAS HOLDEM");
        int numJugadores = leerNumeroJugadores();

        List<Player> jugadores = new ArrayList<>();
        for (int i = 1; i <= numJugadores; i++) {
            jugadores.add(new Player(i, STACK_INICIAL));
        }

        int dealerIndex = RANDOM.nextInt(jugadores.size());
        int numeroMano = 1;

        while (jugadoresConFichas(jugadores) > 1) {
            List<Card> baraja = crearBaraja();
            Collections.shuffle(baraja, RANDOM);
            jugadoresMesaActual.clear();
            jugadoresMesaActual.addAll(jugadores);
            prepararNuevaMano(jugadores, baraja);
            dealerIndex = buscarSiguienteDealer(jugadores, dealerIndex);
            int bote = ponerCiegas(jugadores, dealerIndex);

            System.out.println();
            System.out.println("================================");
            System.out.println("MANO " + numeroMano);
            System.out.println("================================");

            mostrarPreflop(jugadores);
            mostrarEstadoJugadores(jugadores, bote, dealerIndex);

            if (jugadoresActivos(jugadores) > 1) {
                int inicioPreflop = jugadoresActivos(jugadores) > 2
                    ? siguienteActivo(jugadores, dealerIndex + 3)
                    : siguienteActivo(jugadores, dealerIndex);
                bote = jugarRondaApuestas(jugadores, bote, inicioPreflop, CIEGA_GRANDE, "PREFLOP");
            }

            if (jugadoresActivos(jugadores) == 1) {
                declararGanadorPorRetirada(jugadores, bote);
                eliminarJugadoresSinFichas(jugadores);
                esperarSiguienteMano();
                numeroMano++;
                continue;
            }

            List<Card> mesa = new ArrayList<>();
            cartasMesaActuales.clear();

            pausar("Pulsa ENTER para ver el flop...");
            mesa.add(robar(baraja));
            mesa.add(robar(baraja));
            mesa.add(robar(baraja));
            sincronizarMesaActual(mesa);
            mostrarMesa("FLOP", mesa);
            limpiarApuestasDeRonda(jugadores);
            bote = jugarRondaApuestas(jugadores, bote, siguienteActivo(jugadores, dealerIndex + 1), 0, "FLOP");

            if (jugadoresActivos(jugadores) == 1) {
                declararGanadorPorRetirada(jugadores, bote);
                eliminarJugadoresSinFichas(jugadores);
                esperarSiguienteMano();
                numeroMano++;
                continue;
            }

            pausar("Pulsa ENTER para ver el turn...");
            mesa.add(robar(baraja));
            sincronizarMesaActual(mesa);
            mostrarMesa("TURN", mesa);
            limpiarApuestasDeRonda(jugadores);
            bote = jugarRondaApuestas(jugadores, bote, siguienteActivo(jugadores, dealerIndex + 1), 0, "TURN");

            if (jugadoresActivos(jugadores) == 1) {
                declararGanadorPorRetirada(jugadores, bote);
                eliminarJugadoresSinFichas(jugadores);
                esperarSiguienteMano();
                numeroMano++;
                continue;
            }

            pausar("Pulsa ENTER para ver el river...");
            mesa.add(robar(baraja));
            sincronizarMesaActual(mesa);
            mostrarMesa("RIVER", mesa);
            limpiarApuestasDeRonda(jugadores);
            bote = jugarRondaApuestas(jugadores, bote, siguienteActivo(jugadores, dealerIndex + 1), 0, "RIVER");

            if (jugadoresActivos(jugadores) == 1) {
                declararGanadorPorRetirada(jugadores, bote);
            } else {
                resolverShowdown(jugadores, mesa, bote);
            }

            eliminarJugadoresSinFichas(jugadores);
            if (jugadoresConFichas(jugadores) > 1) {
                esperarSiguienteMano();
            }
            numeroMano++;
        }

        Player campeon = jugadorConFichas(jugadores);
        System.out.println();
        System.out.println("================================");
        System.out.println("FIN DE LA PARTIDA");
        System.out.println("================================");
        System.out.println("Campeon: Jugador " + campeon.id + " con " + campeon.stack + " fichas.");
    }

    private static int leerNumeroJugadores() {
        while (true) {
            System.out.print("Numero de jugadores (2-10): ");
            String linea = SC.nextLine().trim();
            try {
                int n = Integer.parseInt(linea);
                if (n >= 2 && n <= 10) {
                    return n;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Introduce un numero valido entre 2 y 10.");
        }
    }

    private static void mostrarPreflop(List<Player> jugadores) {
        System.out.println();
        imprimirSeparador();
        System.out.println("PREFLOP");
        imprimirSeparador();
        for (Player jugador : jugadores) {
            if (jugador.esHumano()) {
                System.out.println("TUS CARTAS: " + jugador.c1 + " " + jugador.c2);
            } else {
                System.out.println("Jugador " + jugador.id + " (IA): [ocultas]");
            }
        }
    }

    private static void mostrarMesa(String fase, List<Card> mesa) {
        System.out.println();
        imprimirSeparador();
        System.out.println(fase);
        imprimirSeparador();
        System.out.println("MESA      : " + formatearCartas(mesa));
        System.out.println("TUS CARTAS: " + formatearCartas(cartasJugadorHumano()));
    }

    private static int ponerCiegas(List<Player> jugadores, int dealerIndex) {
        int activos = jugadoresActivos(jugadores);
        int sbIndex = activos == 2
            ? dealerIndex
            : siguienteActivo(jugadores, dealerIndex + 1);
        int bbIndex = siguienteActivo(jugadores, sbIndex + 1);

        int bote = 0;
        bote += apostarCantidad(jugadores.get(sbIndex), CIEGA_PEQUENA);
        bote += apostarCantidad(jugadores.get(bbIndex), CIEGA_GRANDE);

        System.out.println();
        System.out.println("Dealer: Jugador " + jugadores.get(dealerIndex).id);
        System.out.println("Ciega pequena: Jugador " + jugadores.get(sbIndex).id + " (" + CIEGA_PEQUENA + ")");
        System.out.println("Ciega grande: Jugador " + jugadores.get(bbIndex).id + " (" + CIEGA_GRANDE + ")");

        return bote;
    }

    private static int jugarRondaApuestas(List<Player> jugadores, int bote, int inicio, int apuestaInicial, String fase) {
        if (jugadoresActivos(jugadores) <= 1) {
            return bote;
        }

        int apuestaActual = apuestaInicial;
        int jugadoresPendientes = contarPendientes(jugadores);
        int index = inicio;

        System.out.println();
        System.out.println("APUESTAS " + fase);

        while (jugadoresActivos(jugadores) > 1 && jugadoresPendientes > 0) {
            Player jugador = jugadores.get(index);

            if (jugador.activo && !jugador.allIn) {
                mostrarEstadoJugadores(jugadores, bote, -1);
                int porIgualar = Math.max(0, apuestaActual - jugador.apostadoRonda);
                String accion = jugador.esHumano()
                    ? leerAccionHumana(jugador, porIgualar, apuestaActual > 0)
                    : decidirAccionIA(jugador, jugadores, index, fase, porIgualar, apuestaActual, bote);

                if ("f".equals(accion)) {
                    jugador.activo = false;
                    jugadoresPendientes--;
                    System.out.println("Jugador " + jugador.id + " se retira.");
                } else if ("c".equals(accion)) {
                    int metido = apostarCantidad(jugador, porIgualar);
                    bote += metido;
                    jugadoresPendientes--;
                    if (porIgualar == 0) {
                        System.out.println("Jugador " + jugador.id + " pasa.");
                    } else {
                        System.out.println("Jugador " + jugador.id + " iguala " + metido + ".");
                    }
                } else if ("r".equals(accion)) {
                    int subidaTotal = jugador.esHumano()
                        ? leerSubidaMinima(jugador, porIgualar)
                        : calcularSubidaIA(jugador, jugadores, index, fase, porIgualar, apuestaActual, bote);
                    int metido = apostarCantidad(jugador, porIgualar + subidaTotal);
                    apuestaActual = jugador.apostadoRonda;
                    jugadoresPendientes = contarJugadoresQuePuedenActuar(jugadores, jugador.id);
                    bote += metido;
                    System.out.println("Jugador " + jugador.id + " sube a " + apuestaActual + ".");
                }
            }

            index = (index + 1) % jugadores.size();
        }

        return bote;
    }

    private static String leerAccionHumana(Player jugador, int porIgualar, boolean hayApuesta) {
        while (true) {
            if (porIgualar == 0) {
                System.out.print("Jugador " + jugador.id + " [" + jugador.stack + "] (c=check, r=raise, f=fold): ");
            } else {
                System.out.print("Jugador " + jugador.id + " [" + jugador.stack + "] iguala " + porIgualar + " (c=call, r=raise, f=fold): ");
            }

            String accion = SC.nextLine().trim().toLowerCase();
            if ("f".equals(accion)) {
                return accion;
            }
            if ("c".equals(accion)) {
                return accion;
            }
            if ("r".equals(accion) && jugador.stack > porIgualar) {
                return accion;
            }
            if ("r".equals(accion) && jugador.stack <= porIgualar) {
                System.out.println("No tienes fichas suficientes para subir.");
                continue;
            }
            if (!hayApuesta && "c".equals(accion)) {
                return accion;
            }
            System.out.println("Accion no valida.");
        }
    }

    private static String decidirAccionIA(Player jugador, List<Player> jugadores, int index, String fase, int porIgualar, int apuestaActual, int bote) {
        AIStats stats = construirAIStats(jugador, jugadores, index, fase, porIgualar, apuestaActual, bote);

        if (porIgualar == 0) {
            if (stats.raiseScore > stats.betThreshold && jugador.stack > CIEGA_GRANDE) {
                return "r";
            }
            return "c";
        }

        if (stats.raiseScore > stats.raiseThreshold && jugador.stack > porIgualar + CIEGA_GRANDE) {
            return "r";
        }
        if (stats.callScore > stats.callThreshold || stats.potOddsBuenas) {
            return "c";
        }
        return "f";
    }

    private static double estimarFuerzaIA(Player jugador, String fase) {
        if ("PREFLOP".equals(fase)) {
            return fuerzaPreflop(jugador);
        }

        List<Card> siete = construirCartasJugadorMesa(jugador);
        HandValue mano = evaluarMejorMano(siete);
        double base = fuerzaBasePorCategoria(mano.categoria);
        double kicker = mano.desempate.isEmpty() ? 0.0 : mano.desempate.get(0) / 18.0;
        double proyecto = calcularPotencialProyecto(siete);
        double usoPrivadas = bonusUsoCartasPrivadas(jugador, mano);
        double ajusteMesa = ajusteContraMesa(jugador, mano);
        return Math.max(0.05, Math.min(1.0, base + kicker * 0.10 + proyecto + usoPrivadas + ajusteMesa));
    }

    private static int calcularSubidaIA(Player jugador, List<Player> jugadores, int index, String fase, int porIgualar, int apuestaActual, int bote) {
        AIStats stats = construirAIStats(jugador, jugadores, index, fase, porIgualar, apuestaActual, bote);
        int maximo = Math.max(1, jugador.stack - porIgualar);
        double spr = bote <= 0 ? jugador.stack : (double) jugador.stack / bote;

        if ("PREFLOP".equals(fase)) {
            int base = porIgualar == 0 ? CIEGA_GRANDE * 3 : Math.max(apuestaActual * 2, porIgualar * 2);
            double factor = 0.95 + stats.strength * 0.65 + jugador.agresividadIA * 0.35;
            int subida = (int) Math.round(base * factor);
            int tope = stats.strength > 0.96 ? jugador.stack / 3 : jugador.stack / 6;
            tope = Math.max(CIEGA_GRANDE * 3, tope);
            return Math.max(1, Math.min(subida, Math.min(maximo, tope)));
        }

        int base = Math.max(CIEGA_GRANDE, bote / 3);
        if (stats.madeStrength >= 0.85) {
            base = Math.max(base, (int) Math.round(bote * 0.90));
        } else if (stats.madeStrength >= 0.65 || stats.topPairOrBetter) {
            base = Math.max(base, (int) Math.round(bote * 0.65));
        } else if (stats.drawStrength >= 0.12) {
            base = Math.max(base, (int) Math.round(bote * 0.50));
        }

        double factor = 0.75 + jugador.agresividadIA * 0.30 + stats.positionFactor * 0.10;
        int subida = (int) Math.round(base * factor);
        int minimoCreible = Math.max(CIEGA_GRANDE, (int) Math.round(bote * 0.25));

        if (spr <= 1.2) {
            minimoCreible = Math.max(minimoCreible, (int) Math.round(jugador.stack * 0.75));
        } else if (spr <= 2.0 && (stats.madeStrength >= 0.65 || stats.drawStrength >= 0.12)) {
            minimoCreible = Math.max(minimoCreible, (int) Math.round(jugador.stack * 0.50));
        }

        int tope;
        if (spr <= 1.2 && (stats.madeStrength >= 0.55 || stats.drawStrength >= 0.12)) {
            tope = maximo;
        } else if (stats.madeStrength >= 0.95) {
            tope = (int) Math.round(jugador.stack * 0.75);
        } else if (stats.madeStrength >= 0.65 || stats.topPairOrBetter) {
            tope = (int) Math.round(jugador.stack * 0.55);
        } else if (stats.drawStrength >= 0.12) {
            tope = (int) Math.round(jugador.stack * 0.45);
        } else {
            tope = (int) Math.round(jugador.stack * 0.30);
        }

        tope = Math.max(minimoCreible, tope);
        tope = Math.max(CIEGA_GRANDE * 2, Math.min(maximo, tope));
        subida = Math.max(subida, minimoCreible);
        return Math.max(1, Math.min(subida, tope));
    }

    private static double calcularIniciativaIA(Player jugador, String fase, int bote, double fuerza) {
        if ("PREFLOP".equals(fase)) {
            if (fuerza > 0.74) {
                return 0.10 + jugador.agresividadIA * 0.08;
            }
            return 0.0;
        }

        List<Card> siete = construirCartasJugadorMesa(jugador);
        HandValue mano = evaluarMejorMano(siete);
        double proyecto = calcularPotencialProyecto(siete);
        double iniciativa = 0.0;

        if (mano.categoria >= 1) {
            iniciativa += 0.16 + jugador.agresividadIA * 0.10;
        }
        if (mano.categoria >= 2) {
            iniciativa += 0.10;
        }
        if (esTopPairOJugadaMejor(jugador)) {
            iniciativa += 0.12;
        }
        if (proyecto >= 0.10) {
            iniciativa += 0.10 + jugador.agresividadIA * 0.08;
        }
        if (bote <= CIEGA_GRANDE * 8) {
            iniciativa += 0.04;
        }

        return iniciativa;
    }

    private static AIStats construirAIStats(Player jugador, List<Player> jugadores, int index, String fase, int porIgualar, int apuestaActual, int bote) {
        int activos = contarActivosConDecision(jugadores);
        double strength = estimarFuerzaIA(jugador, fase);
        List<Card> siete = construirCartasJugadorMesa(jugador);
        HandValue mano = "PREFLOP".equals(fase) ? null : evaluarMejorMano(siete);
        double drawStrength = "PREFLOP".equals(fase) ? 0.0 : calcularPotencialProyecto(siete);
        double madeStrength = "PREFLOP".equals(fase) ? strength : fuerzaBasePorCategoria(mano.categoria);
        double positionFactor = calcularFactorPosicion(jugadores, index);
        boolean topPairOrBetter = !"PREFLOP".equals(fase) && esTopPairOJugadaMejor(jugador);
        boolean boardSeco = !"PREFLOP".equals(fase) && esBoardSeco(cartasMesaActuales);
        boolean potOddsBuenas = porIgualar > 0 && ((double) porIgualar / Math.max(1, bote + porIgualar)) < (strength * 0.42 + drawStrength * 0.85);
        double cbetPressure = (!"PREFLOP".equals(fase) && porIgualar == 0 && boardSeco) ? 0.10 : 0.0;
        double bluffCatch = (porIgualar > 0 && boardSeco && topPairOrBetter) ? 0.10 : 0.0;
        double pressure = porIgualar == 0 ? 0.0 : (double) porIgualar / Math.max(1, jugador.stack);
        double multiwayPenalty = Math.max(0.0, (activos - 3) * ("PREFLOP".equals(fase) ? 0.06 : 0.04));
        double stackCommitPenalty = porIgualar > jugador.stack * 0.28 ? 0.18 : 0.0;
        double jamPenalty = porIgualar > jugador.stack * 0.45 ? 0.30 : 0.0;
        double betThreshold = 0.64 - jugador.agresividadIA * 0.05 - positionFactor * 0.04 + multiwayPenalty;
        double raiseThreshold = ("PREFLOP".equals(fase) ? 0.92 : 0.94) + multiwayPenalty + stackCommitPenalty + jamPenalty;
        double callThreshold = ("PREFLOP".equals(fase) ? 0.48 : 0.50) + multiwayPenalty * 0.7 + stackCommitPenalty + jamPenalty * 0.8;

        double raiseScore = strength
            + madeStrength * 0.22
            + drawStrength * 0.55
            + positionFactor * 0.12
            + cbetPressure
            + jugador.agresividadIA * 0.12
            - multiwayPenalty
            - stackCommitPenalty
            - jamPenalty
            - pressure * 0.55
            + (RANDOM.nextDouble() - 0.5) * 0.10;

        double callScore = strength
            + drawStrength * 0.65
            + (topPairOrBetter ? 0.14 : 0.0)
            + bluffCatch
            + jugador.curiosidadIA * 0.08
            + jugador.solidezIA * 0.04
            - multiwayPenalty * 0.65
            - stackCommitPenalty * 0.85
            - jamPenalty
            - pressure * 0.42
            + (RANDOM.nextDouble() - 0.5) * 0.08;

        if ("PREFLOP".equals(fase) && porIgualar > 0) {
            callScore += jugador.apostadoRonda > 0 ? 0.08 : 0.0;
            if (strength > 0.83) {
                raiseScore += 0.10;
            }
        }

        if (!"PREFLOP".equals(fase)) {
            if (mano.categoria >= 2) {
                raiseScore += 0.14;
                callScore += 0.10;
            } else if (topPairOrBetter) {
                raiseScore += 0.08;
                callScore += 0.12;
            }
            if (drawStrength >= 0.10) {
                raiseScore += 0.08;
                callScore += 0.12;
            }
        }

        return new AIStats(
            strength,
            madeStrength,
            drawStrength,
            positionFactor,
            topPairOrBetter,
            potOddsBuenas,
            raiseScore,
            callScore,
            betThreshold,
            raiseThreshold,
            callThreshold
        );
    }

    private static double fuerzaPreflop(Player jugador) {
        int alta = Math.max(jugador.c1.valor, jugador.c2.valor);
        int baja = Math.min(jugador.c1.valor, jugador.c2.valor);
        boolean pareja = jugador.c1.valor == jugador.c2.valor;
        boolean suited = jugador.c1.palo == jugador.c2.palo;
        boolean conectadas = alta - baja <= 1;

        double fuerza = (alta + baja) / 28.0;
        if (pareja) {
            fuerza += 0.35 + alta / 30.0;
        }
        if (suited) {
            fuerza += 0.08;
        }
        if (conectadas) {
            fuerza += 0.07;
        }
        if (alta >= 13) {
            fuerza += 0.08;
        }
        if (alta == 14 && baja >= 10) {
            fuerza += 0.10;
        }
        return Math.min(1.0, fuerza);
    }

    private static double fuerzaBasePorCategoria(int categoria) {
        switch (categoria) {
            case 9: return 1.00;
            case 8: return 0.97;
            case 7: return 0.95;
            case 6: return 0.92;
            case 5: return 0.84;
            case 4: return 0.80;
            case 3: return 0.74;
            case 2: return 0.64;
            case 1: return 0.47;
            default: return 0.22;
        }
    }

    private static double calcularPotencialProyecto(List<Card> cartas) {
        int maxPalo = maxCartasMismoPalo(cartas);
        int mejorEscalera = mejorRachaEscalera(cartas);
        double bonus = 0.0;

        if (cartas.size() < 7) {
            if (maxPalo == 4) {
                bonus += 0.12;
            }
            if (mejorEscalera == 4) {
                bonus += 0.10;
            }
            if (tieneDosOvercards(cartas)) {
                bonus += 0.05;
            }
        }

        return bonus;
    }

    private static int maxCartasMismoPalo(List<Card> cartas) {
        int espadas = 0;
        int corazones = 0;
        int diamantes = 0;
        int treboles = 0;

        for (Card carta : cartas) {
            switch (carta.palo) {
                case '\u2660': espadas++; break;
                case '\u2665': corazones++; break;
                case '\u2666': diamantes++; break;
                default: treboles++; break;
            }
        }

        return Math.max(Math.max(espadas, corazones), Math.max(diamantes, treboles));
    }

    private static int mejorRachaEscalera(List<Card> cartas) {
        boolean[] presentes = new boolean[15];
        for (Card carta : cartas) {
            presentes[carta.valor] = true;
            if (carta.valor == 14) {
                presentes[1] = true;
            }
        }

        int mejor = 0;
        int actual = 0;
        for (int valor = 1; valor <= 14; valor++) {
            if (presentes[valor]) {
                actual++;
                mejor = Math.max(mejor, actual);
            } else {
                actual = 0;
            }
        }
        return mejor;
    }

    private static boolean tieneDosOvercards(List<Card> cartas) {
        if (cartasMesaActuales.isEmpty()) {
            return false;
        }

        int mayorMesa = 0;
        for (Card carta : cartasMesaActuales) {
            mayorMesa = Math.max(mayorMesa, carta.valor);
        }

        int overcards = 0;
        for (int i = 0; i < 2; i++) {
            if (cartas.get(i).valor > mayorMesa) {
                overcards++;
            }
        }

        return overcards == 2;
    }

    private static List<Card> construirCartasJugadorMesa(Player jugador) {
        List<Card> siete = new ArrayList<>();
        siete.add(jugador.c1);
        siete.add(jugador.c2);
        siete.addAll(cartasMesaActuales);
        return siete;
    }

    private static boolean esTopPairOJugadaMejor(Player jugador) {
        if (cartasMesaActuales.isEmpty()) {
            return false;
        }

        int valorMaxMesa = 0;
        for (Card carta : cartasMesaActuales) {
            valorMaxMesa = Math.max(valorMaxMesa, carta.valor);
        }

        if (jugador.c1.valor == valorMaxMesa || jugador.c2.valor == valorMaxMesa) {
            return true;
        }

        return evaluarMejorMano(construirCartasJugadorMesa(jugador)).categoria >= 2;
    }

    private static double bonusUsoCartasPrivadas(Player jugador, HandValue mano) {
        int usadas = contarCartasPrivadasUsadas(jugador, mano);
        if (usadas == 2) {
            return 0.12;
        }
        if (usadas == 1) {
            return 0.05;
        }
        return -0.12;
    }

    private static double ajusteContraMesa(Player jugador, HandValue manoJugador) {
        if (cartasMesaActuales.size() < 5) {
            return 0.0;
        }

        HandValue manoMesa = evaluarCincoCartas(new ArrayList<>(cartasMesaActuales));
        int comparacion = manoJugador.compareTo(manoMesa);
        int usadas = contarCartasPrivadasUsadas(jugador, manoJugador);

        if (comparacion > 0) {
            return 0.18 + usadas * 0.04;
        }
        if (comparacion == 0) {
            if (usadas == 0) {
                return -0.35;
            }
            return -0.08;
        }
        return -0.20;
    }

    private static int contarCartasPrivadasUsadas(Player jugador, HandValue mano) {
        int usadas = 0;
        for (Card carta : mano.cartasOrdenadas) {
            if (carta == jugador.c1 || carta == jugador.c2) {
                usadas++;
            }
        }
        return usadas;
    }

    private static double calcularFactorPosicion(List<Player> jugadores, int index) {
        int detras = 0;
        int activos = 0;
        for (Player jugador : jugadores) {
            if (jugador.activo && !jugador.allIn) {
                activos++;
            }
        }
        int cursor = (index + 1) % jugadores.size();
        while (cursor != index) {
            Player jugador = jugadores.get(cursor);
            if (jugador.activo && !jugador.allIn) {
                detras++;
            }
            cursor = (cursor + 1) % jugadores.size();
        }
        if (activos <= 1) {
            return 0.0;
        }
        return 1.0 - ((double) detras / Math.max(1, activos - 1));
    }

    private static boolean esBoardSeco(List<Card> mesa) {
        if (mesa.size() < 3) {
            return false;
        }
        boolean emparejado = hayParejaEnMesa(mesa);
        boolean monotono = maxCartasMismoPalo(mesa) >= 3;
        boolean muyConectado = mejorRachaEscalera(mesa) >= 3;
        return !emparejado && !monotono && !muyConectado;
    }

    private static boolean hayParejaEnMesa(List<Card> mesa) {
        int[] repeticiones = new int[15];
        for (Card carta : mesa) {
            repeticiones[carta.valor]++;
            if (repeticiones[carta.valor] >= 2) {
                return true;
            }
        }
        return false;
    }

    private static int leerSubidaMinima(Player jugador, int porIgualar) {
        while (true) {
            System.out.print("Cantidad extra a subir: ");
            String linea = SC.nextLine().trim();
            try {
                int subida = Integer.parseInt(linea);
                if (subida > 0 && subida <= jugador.stack - porIgualar) {
                    return subida;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Introduce una subida valida.");
        }
    }

    private static int apostarCantidad(Player jugador, int cantidadDeseada) {
        int cantidadReal = Math.min(cantidadDeseada, jugador.stack);
        jugador.stack -= cantidadReal;
        jugador.apostadoRonda += cantidadReal;
        if (jugador.stack == 0) {
            jugador.allIn = true;
        }
        return cantidadReal;
    }

    private static void limpiarApuestasDeRonda(List<Player> jugadores) {
        for (Player jugador : jugadores) {
            jugador.apostadoRonda = 0;
        }
    }

    private static int jugadoresActivos(List<Player> jugadores) {
        int activos = 0;
        for (Player jugador : jugadores) {
            if (jugador.activo) {
                activos++;
            }
        }
        return activos;
    }

    private static int jugadoresConFichas(List<Player> jugadores) {
        int vivos = 0;
        for (Player jugador : jugadores) {
            if (jugador.stack > 0) {
                vivos++;
            }
        }
        return vivos;
    }

    private static int contarPendientes(List<Player> jugadores) {
        int pendientes = 0;
        for (Player jugador : jugadores) {
            if (jugador.activo && !jugador.allIn) {
                pendientes++;
            }
        }
        return pendientes;
    }

    private static int contarJugadoresQuePuedenActuar(List<Player> jugadores, int ultimoQueSubeId) {
        int total = 0;
        for (Player jugador : jugadores) {
            if (jugador.activo && !jugador.allIn && jugador.id != ultimoQueSubeId) {
                total++;
            }
        }
        return total;
    }

    private static int contarActivosConDecision(List<Player> jugadores) {
        int total = 0;
        for (Player jugador : jugadores) {
            if (jugador.activo && jugador.stack > 0) {
                total++;
            }
        }
        return total;
    }

    private static int siguienteActivo(List<Player> jugadores, int desde) {
        int index = ((desde % jugadores.size()) + jugadores.size()) % jugadores.size();
        while (!jugadores.get(index).activo) {
            index = (index + 1) % jugadores.size();
        }
        return index;
    }

    private static int buscarSiguienteDealer(List<Player> jugadores, int dealerActual) {
        return siguienteConFichas(jugadores, dealerActual + 1);
    }

    private static int siguienteConFichas(List<Player> jugadores, int desde) {
        int index = ((desde % jugadores.size()) + jugadores.size()) % jugadores.size();
        while (jugadores.get(index).stack <= 0) {
            index = (index + 1) % jugadores.size();
        }
        return index;
    }

    private static void prepararNuevaMano(List<Player> jugadores, List<Card> baraja) {
        for (Player jugador : jugadores) {
            jugador.reiniciarParaNuevaMano();
            if (jugador.stack > 0) {
                jugador.c1 = robar(baraja);
                jugador.c2 = robar(baraja);
            }
        }
    }

    private static void eliminarJugadoresSinFichas(List<Player> jugadores) {
        for (Player jugador : jugadores) {
            if (jugador.stack <= 0) {
                jugador.activo = false;
            }
        }
    }

    private static void mostrarEstadoJugadores(List<Player> jugadores, int bote, int dealerIndex) {
        System.out.println();
        imprimirSeparador();
        System.out.println("Bote: " + bote);
        System.out.println("Tus cartas: " + formatearCartas(cartasJugadorHumano()));
        for (int i = 0; i < jugadores.size(); i++) {
            Player jugador = jugadores.get(i);
            if (jugador.stack <= 0) {
                continue;
            }
            String estado = jugador.activo ? "activo" : "fold";
            if (jugador.allIn && jugador.activo) {
                estado = "all-in";
            }
            String dealer = i == dealerIndex ? " D" : "";
            System.out.println(
                "Jugador " + jugador.id + (jugador.esHumano() ? " (Tú)" : " (IA)") + dealer
                    + " | stack=" + jugador.stack
                    + " | ronda=" + jugador.apostadoRonda
                    + " | " + estado
            );
        }
        imprimirSeparador();
    }

    private static void declararGanadorPorRetirada(List<Player> jugadores, int bote) {
        for (Player jugador : jugadores) {
            if (jugador.activo) {
                jugador.stack += bote;
                System.out.println();
                System.out.println("Todos se retiran. Gana Jugador " + jugador.id + " el bote de " + bote + ".");
                System.out.println("Stack final Jugador " + jugador.id + ": " + jugador.stack);
                return;
            }
        }
    }

    private static void resolverShowdown(List<Player> jugadores, List<Card> mesa, int bote) {
        System.out.println();
        imprimirSeparador();
        System.out.println("SHOWDOWN");
        imprimirSeparador();
        System.out.println("MESA      : " + formatearCartas(mesa));
        System.out.println("TUS CARTAS: " + formatearCartas(cartasJugadorHumano()));
        System.out.println();

        List<PlayerResult> resultados = new ArrayList<>();
        for (Player jugador : jugadores) {
            if (!jugador.activo) {
                continue;
            }
            List<Card> sieteCartas = new ArrayList<>();
            sieteCartas.add(jugador.c1);
            sieteCartas.add(jugador.c2);
            sieteCartas.addAll(mesa);

            HandValue mejorMano = evaluarMejorMano(sieteCartas);
            resultados.add(new PlayerResult(jugador, mejorMano));

            System.out.println(
                "Jugador " + jugador.id + (jugador.esHumano() ? " (Tú)" : " (IA)") + ": "
                    + formatearCartas(Arrays.asList(jugador.c1, jugador.c2))
            );
            System.out.println("  Combinacion: " + mejorMano.descripcion);
            System.out.println("  Cartas     : " + formatearCartas(mejorMano.cartasOrdenadas));
        }

        HandValue mejor = resultados.get(0).mano;
        for (int i = 1; i < resultados.size(); i++) {
            if (resultados.get(i).mano.compareTo(mejor) > 0) {
                mejor = resultados.get(i).mano;
            }
        }

        List<Integer> ganadores = new ArrayList<>();
        for (PlayerResult resultado : resultados) {
            if (resultado.mano.compareTo(mejor) == 0) {
                ganadores.add(resultado.jugador.id);
            }
        }

        System.out.println();
        if (ganadores.size() == 1) {
            int ganador = ganadores.get(0);
            buscarJugador(jugadores, ganador).stack += bote;
            System.out.println("RESULTADO : Gana Jugador " + ganador + " | Bote: " + bote);
        } else {
            int reparto = bote / ganadores.size();
            int resto = bote % ganadores.size();
            for (int i = 0; i < ganadores.size(); i++) {
                int premio = reparto + (i < resto ? 1 : 0);
                buscarJugador(jugadores, ganadores.get(i)).stack += premio;
            }
            System.out.println("RESULTADO : Empate entre " + ganadores + " | Bote: " + bote);
        }

        System.out.println();
        imprimirSeparador();
        System.out.println("STACKS FINALES");
        for (Player jugador : jugadores) {
            if (jugador.stack <= 0) {
                continue;
            }
            System.out.println("Jugador " + jugador.id + ": " + jugador.stack);
        }
        imprimirSeparador();
    }

    private static List<Card> cartasJugadorHumano() {
        List<Card> cartas = new ArrayList<>();
        for (Player jugador : jugadoresMesaActual) {
            if (jugador.esHumano() && jugador.c1 != null && jugador.c2 != null) {
                cartas.add(jugador.c1);
                cartas.add(jugador.c2);
                break;
            }
        }
        return cartas;
    }

    private static Player buscarJugador(List<Player> jugadores, int id) {
        for (Player jugador : jugadores) {
            if (jugador.id == id) {
                return jugador;
            }
        }
        throw new IllegalArgumentException("Jugador no encontrado: " + id);
    }

    private static Player jugadorConFichas(List<Player> jugadores) {
        for (Player jugador : jugadores) {
            if (jugador.stack > 0) {
                return jugador;
            }
        }
        throw new IllegalStateException("No queda ningun jugador con fichas.");
    }

    private static HandValue evaluarMejorMano(List<Card> sieteCartas) {
        HandValue mejor = null;

        for (int a = 0; a < sieteCartas.size() - 4; a++) {
            for (int b = a + 1; b < sieteCartas.size() - 3; b++) {
                for (int c = b + 1; c < sieteCartas.size() - 2; c++) {
                    for (int d = c + 1; d < sieteCartas.size() - 1; d++) {
                        for (int e = d + 1; e < sieteCartas.size(); e++) {
                            List<Card> cincoCartas = Arrays.asList(
                                sieteCartas.get(a),
                                sieteCartas.get(b),
                                sieteCartas.get(c),
                                sieteCartas.get(d),
                                sieteCartas.get(e)
                            );

                            HandValue actual = evaluarCincoCartas(cincoCartas);
                            if (mejor == null || actual.compareTo(mejor) > 0) {
                                mejor = actual;
                            }
                        }
                    }
                }
            }
        }

        return mejor;
    }

    private static HandValue evaluarCincoCartas(List<Card> cartasOriginales) {
        List<Card> cartas = new ArrayList<>(cartasOriginales);
        cartas.sort(Comparator.comparingInt((Card c) -> c.valor).reversed());

        boolean color = esColor(cartas);
        int escaleraAlta = valorEscalera(cartas);

        int[] repeticiones = new int[15];
        for (Card carta : cartas) {
            repeticiones[carta.valor]++;
        }

        List<Integer> grupos4 = new ArrayList<>();
        List<Integer> grupos3 = new ArrayList<>();
        List<Integer> grupos2 = new ArrayList<>();
        List<Integer> grupos1 = new ArrayList<>();

        for (int valor = 14; valor >= 2; valor--) {
            if (repeticiones[valor] == 4) {
                grupos4.add(valor);
            } else if (repeticiones[valor] == 3) {
                grupos3.add(valor);
            } else if (repeticiones[valor] == 2) {
                grupos2.add(valor);
            } else if (repeticiones[valor] == 1) {
                grupos1.add(valor);
            }
        }

        if (color && escaleraAlta > 0) {
            if (escaleraAlta == 14) {
                return new HandValue(
                    9,
                    Arrays.asList(14),
                    "Escalera real",
                    ordenarParaMostrar(cartas)
                );
            }
            return new HandValue(
                8,
                Arrays.asList(escaleraAlta),
                "Escalera de color al " + nombreValor(escaleraAlta),
                ordenarEscalera(cartas, escaleraAlta)
            );
        }

        if (!grupos4.isEmpty()) {
            int poker = grupos4.get(0);
            int kicker = grupos1.get(0);
            return new HandValue(
                7,
                Arrays.asList(poker, kicker),
                "Poker de " + nombreValorPlural(poker),
                ordenarPorValores(cartas, Arrays.asList(poker, kicker))
            );
        }

        if (!grupos3.isEmpty() && !grupos2.isEmpty()) {
            int trio = grupos3.get(0);
            int pareja = grupos2.get(0);
            return new HandValue(
                6,
                Arrays.asList(trio, pareja),
                "Full de " + nombreValorPlural(trio) + " sobre " + nombreValorPlural(pareja),
                ordenarPorValores(cartas, Arrays.asList(trio, pareja))
            );
        }

        if (color) {
            List<Integer> desempate = extraerValores(cartas);
            return new HandValue(
                5,
                desempate,
                "Color",
                ordenarParaMostrar(cartas)
            );
        }

        if (escaleraAlta > 0) {
            return new HandValue(
                4,
                Arrays.asList(escaleraAlta),
                "Escalera al " + nombreValor(escaleraAlta),
                ordenarEscalera(cartas, escaleraAlta)
            );
        }

        if (!grupos3.isEmpty()) {
            int trio = grupos3.get(0);
            List<Integer> desempate = new ArrayList<>();
            desempate.add(trio);
            desempate.addAll(grupos1);
            return new HandValue(
                3,
                desempate,
                "Trio de " + nombreValorPlural(trio),
                ordenarPorValores(cartas, Arrays.asList(trio, grupos1.get(0), grupos1.get(1)))
            );
        }

        if (grupos2.size() >= 2) {
            int parejaAlta = grupos2.get(0);
            int parejaBaja = grupos2.get(1);
            int kicker = grupos1.get(0);
            return new HandValue(
                2,
                Arrays.asList(parejaAlta, parejaBaja, kicker),
                "Doble pareja de " + nombreValorPlural(parejaAlta) + " y " + nombreValorPlural(parejaBaja),
                ordenarPorValores(cartas, Arrays.asList(parejaAlta, parejaBaja, kicker))
            );
        }

        if (grupos2.size() == 1) {
            int pareja = grupos2.get(0);
            List<Integer> desempate = new ArrayList<>();
            desempate.add(pareja);
            desempate.addAll(grupos1);
            return new HandValue(
                1,
                desempate,
                "Pareja de " + nombreValorPlural(pareja),
                ordenarPorValores(cartas, Arrays.asList(pareja, grupos1.get(0), grupos1.get(1), grupos1.get(2)))
            );
        }

        return new HandValue(
            0,
            extraerValores(cartas),
            "Carta alta " + nombreValor(cartas.get(0).valor),
            ordenarParaMostrar(cartas)
        );
    }

    private static boolean esColor(List<Card> cartas) {
        char palo = cartas.get(0).palo;
        for (int i = 1; i < cartas.size(); i++) {
            if (cartas.get(i).palo != palo) {
                return false;
            }
        }
        return true;
    }

    private static int valorEscalera(List<Card> cartas) {
        List<Integer> valores = extraerValores(cartas);

        if (valores.equals(Arrays.asList(14, 5, 4, 3, 2))) {
            return 5;
        }

        for (int i = 0; i < valores.size() - 1; i++) {
            if (valores.get(i) - 1 != valores.get(i + 1)) {
                return 0;
            }
        }
        return valores.get(0);
    }

    private static List<Integer> extraerValores(List<Card> cartas) {
        List<Integer> valores = new ArrayList<>();
        for (Card carta : cartas) {
            valores.add(carta.valor);
        }
        return valores;
    }

    private static List<Card> ordenarParaMostrar(List<Card> cartas) {
        List<Card> copia = new ArrayList<>(cartas);
        copia.sort(Comparator.comparingInt((Card c) -> c.valor).reversed());
        return copia;
    }

    private static List<Card> ordenarEscalera(List<Card> cartas, int escaleraAlta) {
        List<Card> copia = new ArrayList<>(cartas);
        if (escaleraAlta == 5) {
            copia.sort((a, b) -> Integer.compare(valorBajoAs(a.valor), valorBajoAs(b.valor)));
            Collections.reverse(copia);
            return copia;
        }
        copia.sort(Comparator.comparingInt((Card c) -> c.valor).reversed());
        return copia;
    }

    private static int valorBajoAs(int valor) {
        return valor == 14 ? 1 : valor;
    }

    private static List<Card> ordenarPorValores(List<Card> cartas, List<Integer> prioridadValores) {
        List<Card> copia = new ArrayList<>(cartas);
        copia.sort((a, b) -> {
            int pa = indicePrioridad(a.valor, prioridadValores);
            int pb = indicePrioridad(b.valor, prioridadValores);
            if (pa != pb) {
                return Integer.compare(pa, pb);
            }
            return Integer.compare(b.valor, a.valor);
        });
        return copia;
    }

    private static int indicePrioridad(int valor, List<Integer> prioridadValores) {
        int indice = prioridadValores.indexOf(valor);
        return indice >= 0 ? indice : prioridadValores.size();
    }

    private static String nombreValor(int valor) {
        switch (valor) {
            case 14: return "As";
            case 13: return "Rey";
            case 12: return "Reina";
            case 11: return "Jota";
            default: return String.valueOf(valor);
        }
    }

    private static String nombreValorPlural(int valor) {
        switch (valor) {
            case 14: return "ases";
            case 13: return "reyes";
            case 12: return "reinas";
            case 11: return "jotas";
            default: return valor + "s";
        }
    }

    private static String formatearCartas(List<Card> cartas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cartas.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(cartas.get(i));
        }
        return sb.toString();
    }

    private static Card robar(List<Card> baraja) {
        return baraja.remove(baraja.size() - 1);
    }

    private static final List<Card> cartasMesaActuales = new ArrayList<>();

    private static void sincronizarMesaActual(List<Card> mesa) {
        cartasMesaActuales.clear();
        cartasMesaActuales.addAll(mesa);
    }

    private static List<Card> crearBaraja() {
        List<Card> baraja = new ArrayList<>();
        char[] palos = {'\u2660', '\u2665', '\u2666', '\u2663'};
        for (char palo : palos) {
            for (int valor = 2; valor <= 14; valor++) {
                baraja.add(new Card(valor, palo));
            }
        }
        return baraja;
    }

    private static void pausar(String mensaje) {
        System.out.println();
        System.out.println(mensaje);
        SC.nextLine();
    }

    private static void imprimirSeparador() {
        System.out.println("--------------------------------");
    }

    private static void esperarSiguienteMano() {
        System.out.println();
        System.out.println("Pulsa ENTER para repartir la siguiente mano...");
        SC.nextLine();
    }

    private static class Card {
        private final int valor;
        private final char palo;

        private Card(int valor, char palo) {
            this.valor = valor;
            this.palo = palo;
        }

        @Override
        public String toString() {
            return simboloValor(valor) + palo;
        }

        private static String simboloValor(int valor) {
            switch (valor) {
                case 14: return "A";
                case 13: return "K";
                case 12: return "Q";
                case 11: return "J";
                default: return String.valueOf(valor);
            }
        }
    }

    private static class Player {
        private final int id;
        private Card c1;
        private Card c2;
        private final double agresividadIA;
        private final double solidezIA;
        private final double curiosidadIA;
        private int stack;
        private int apostadoRonda;
        private boolean activo;
        private boolean allIn;

        private Player(int id, int stack) {
            this.id = id;
            if (id == JUGADOR_HUMANO_ID) {
                this.agresividadIA = 0.0;
                this.solidezIA = 0.0;
                this.curiosidadIA = 0.0;
            } else {
                this.agresividadIA = 0.18 + RANDOM.nextDouble() * 0.42;
                this.solidezIA = 0.45 + RANDOM.nextDouble() * 0.40;
                this.curiosidadIA = 0.15 + RANDOM.nextDouble() * 0.35;
            }
            this.stack = stack;
            this.apostadoRonda = 0;
            this.activo = true;
            this.allIn = false;
        }

        private boolean esHumano() {
            return id == JUGADOR_HUMANO_ID;
        }

        private void reiniciarParaNuevaMano() {
            this.c1 = null;
            this.c2 = null;
            this.apostadoRonda = 0;
            this.activo = this.stack > 0;
            this.allIn = false;
        }
    }

    private static class PlayerResult {
        private final Player jugador;
        private final HandValue mano;

        private PlayerResult(Player jugador, HandValue mano) {
            this.jugador = jugador;
            this.mano = mano;
        }
    }

    private static class AIStats {
        private final double strength;
        private final double madeStrength;
        private final double drawStrength;
        private final double positionFactor;
        private final boolean topPairOrBetter;
        private final boolean potOddsBuenas;
        private final double raiseScore;
        private final double callScore;
        private final double betThreshold;
        private final double raiseThreshold;
        private final double callThreshold;

        private AIStats(
            double strength,
            double madeStrength,
            double drawStrength,
            double positionFactor,
            boolean topPairOrBetter,
            boolean potOddsBuenas,
            double raiseScore,
            double callScore,
            double betThreshold,
            double raiseThreshold,
            double callThreshold
        ) {
            this.strength = strength;
            this.madeStrength = madeStrength;
            this.drawStrength = drawStrength;
            this.positionFactor = positionFactor;
            this.topPairOrBetter = topPairOrBetter;
            this.potOddsBuenas = potOddsBuenas;
            this.raiseScore = raiseScore;
            this.callScore = callScore;
            this.betThreshold = betThreshold;
            this.raiseThreshold = raiseThreshold;
            this.callThreshold = callThreshold;
        }
    }

    private static class HandValue implements Comparable<HandValue> {
        private final int categoria;
        private final List<Integer> desempate;
        private final String descripcion;
        private final List<Card> cartasOrdenadas;

        private HandValue(int categoria, List<Integer> desempate, String descripcion, List<Card> cartasOrdenadas) {
            this.categoria = categoria;
            this.desempate = new ArrayList<>(desempate);
            this.descripcion = descripcion;
            this.cartasOrdenadas = new ArrayList<>(cartasOrdenadas);
        }

        @Override
        public int compareTo(HandValue otra) {
            if (categoria != otra.categoria) {
                return Integer.compare(categoria, otra.categoria);
            }

            int limite = Math.min(desempate.size(), otra.desempate.size());
            for (int i = 0; i < limite; i++) {
                if (!desempate.get(i).equals(otra.desempate.get(i))) {
                    return Integer.compare(desempate.get(i), otra.desempate.get(i));
                }
            }

            return Integer.compare(desempate.size(), otra.desempate.size());
        }
    }
}
