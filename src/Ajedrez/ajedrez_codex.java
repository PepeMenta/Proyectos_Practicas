import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ajedrez_codex {
    private static final Scanner SC = new Scanner(System.in);
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_WHITE = "\u001B[97m";
    private static final String ANSI_BG_LIGHT = "\u001B[48;5;230m";
    private static final String ANSI_BG_DARK = "\u001B[48;5;101m";
    private static final int PROFUNDIDAD_IA = 3;

    public static void main(String[] args) {
        Partida partida = new Partida();
        partida.jugar();
    }

    private enum Color {
        BLANCO,
        NEGRO;

        private Color opuesto() {
            return this == BLANCO ? NEGRO : BLANCO;
        }

        private String nombre() {
            return this == BLANCO ? "Blancas" : "Negras";
        }
    }

    private enum TipoPieza {
        PEON("P"),
        TORRE("T"),
        CABALLO("C"),
        ALFIL("A"),
        DAMA("D"),
        REY("R");

        private final String simbolo;

        TipoPieza(String simbolo) {
            this.simbolo = simbolo;
        }
    }

    private static class Pieza {
        private final TipoPieza tipo;
        private final Color color;
        private boolean movida;

        private Pieza(TipoPieza tipo, Color color) {
            this.tipo = tipo;
            this.color = color;
            this.movida = false;
        }

        private Pieza copia() {
            Pieza copia = new Pieza(tipo, color);
            copia.movida = movida;
            return copia;
        }

        private String texto() {
            return simboloUnicode();
        }

        private String simboloUnicode() {
            if (color == Color.BLANCO) {
                switch (tipo) {
                    case PEON: return "\u2659";
                    case TORRE: return "\u2656";
                    case CABALLO: return "\u2658";
                    case ALFIL: return "\u2657";
                    case DAMA: return "\u2655";
                    case REY: return "\u2654";
                    default: return tipo.simbolo;
                }
            }

            switch (tipo) {
                case PEON: return "\u265F";
                case TORRE: return "\u265C";
                case CABALLO: return "\u265E";
                case ALFIL: return "\u265D";
                case DAMA: return "\u265B";
                case REY: return "\u265A";
                default: return tipo.simbolo;
            }
        }
    }

    private static class Movimiento {
        private final int filaOrigen;
        private final int colOrigen;
        private final int filaDestino;
        private final int colDestino;
        private final boolean enroqueCorto;
        private final boolean enroqueLargo;
        private final boolean promocion;

        private Movimiento(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
            this(filaOrigen, colOrigen, filaDestino, colDestino, false, false, false);
        }

        private Movimiento(
            int filaOrigen,
            int colOrigen,
            int filaDestino,
            int colDestino,
            boolean enroqueCorto,
            boolean enroqueLargo,
            boolean promocion
        ) {
            this.filaOrigen = filaOrigen;
            this.colOrigen = colOrigen;
            this.filaDestino = filaDestino;
            this.colDestino = colDestino;
            this.enroqueCorto = enroqueCorto;
            this.enroqueLargo = enroqueLargo;
            this.promocion = promocion;
        }

        private boolean coincideCon(int fila, int col) {
            return filaDestino == fila && colDestino == col;
        }

        private String texto() {
            return nombreCasilla(filaOrigen, colOrigen) + " " + nombreCasilla(filaDestino, colDestino);
        }
    }

    private static class ResultadoBusqueda {
        private final Movimiento movimiento;
        private final int puntuacion;

        private ResultadoBusqueda(Movimiento movimiento, int puntuacion) {
            this.movimiento = movimiento;
            this.puntuacion = puntuacion;
        }
    }

    private static class Tablero {
        private final Pieza[][] casillas;

        private Tablero() {
            this.casillas = new Pieza[8][8];
            colocarPiezasIniciales();
        }

        private Tablero(boolean vacio) {
            this.casillas = new Pieza[8][8];
            if (!vacio) {
                colocarPiezasIniciales();
            }
        }

        private Tablero copia() {
            Tablero copia = new Tablero(true);
            for (int fila = 0; fila < 8; fila++) {
                for (int col = 0; col < 8; col++) {
                    if (casillas[fila][col] != null) {
                        copia.casillas[fila][col] = casillas[fila][col].copia();
                    }
                }
            }
            return copia;
        }

        private void colocarPiezasIniciales() {
            TipoPieza[] orden = {
                TipoPieza.TORRE, TipoPieza.CABALLO, TipoPieza.ALFIL, TipoPieza.DAMA,
                TipoPieza.REY, TipoPieza.ALFIL, TipoPieza.CABALLO, TipoPieza.TORRE
            };

            for (int col = 0; col < 8; col++) {
                casillas[0][col] = new Pieza(orden[col], Color.NEGRO);
                casillas[1][col] = new Pieza(TipoPieza.PEON, Color.NEGRO);
                casillas[6][col] = new Pieza(TipoPieza.PEON, Color.BLANCO);
                casillas[7][col] = new Pieza(orden[col], Color.BLANCO);
            }
        }

        private Pieza get(int fila, int col) {
            return casillas[fila][col];
        }

        private void set(int fila, int col, Pieza pieza) {
            casillas[fila][col] = pieza;
        }

        private boolean dentro(int fila, int col) {
            return fila >= 0 && fila < 8 && col >= 0 && col < 8;
        }

        private void mostrar() {
            System.out.println();
            for (int fila = 0; fila < 8; fila++) {
                System.out.print((8 - fila) + "  ");
                for (int col = 0; col < 8; col++) {
                    Pieza pieza = casillas[fila][col];
                    boolean clara = (fila + col) % 2 == 0;
                    String fondo = clara ? ANSI_BG_LIGHT : ANSI_BG_DARK;
                    String contenido;
                    if (pieza == null) {
                        contenido = clara ? " " : "·";
                    } else {
                        contenido = pieza.texto();
                    }
                    String colorTexto = pieza == null ? ANSI_BLACK : (pieza.color == Color.BLANCO ? ANSI_WHITE : ANSI_BLACK);
                    System.out.print(fondo + colorTexto + " " + contenido + " " + ANSI_RESET);
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("    a  b  c  d  e  f  g  h");
            System.out.println();
        }

        private List<Movimiento> movimientosLegales(Color turno) {
            List<Movimiento> legales = new ArrayList<>();
            for (int fila = 0; fila < 8; fila++) {
                for (int col = 0; col < 8; col++) {
                    Pieza pieza = get(fila, col);
                    if (pieza == null || pieza.color != turno) {
                        continue;
                    }
                    List<Movimiento> pseudo = movimientosPseudoLegalesDesde(fila, col);
                    for (Movimiento mov : pseudo) {
                        Tablero copia = copia();
                        copia.aplicarMovimiento(mov);
                        if (!copia.estaEnJaque(turno)) {
                            legales.add(mov);
                        }
                    }
                }
            }
            return legales;
        }

        private List<Movimiento> movimientosPseudoLegalesDesde(int fila, int col) {
            List<Movimiento> movimientos = new ArrayList<>();
            Pieza pieza = get(fila, col);
            if (pieza == null) {
                return movimientos;
            }

            switch (pieza.tipo) {
                case PEON:
                    agregarMovimientosPeon(fila, col, pieza, movimientos);
                    break;
                case TORRE:
                    agregarDeslizantes(fila, col, pieza, movimientos, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
                    break;
                case ALFIL:
                    agregarDeslizantes(fila, col, pieza, movimientos, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
                    break;
                case DAMA:
                    agregarDeslizantes(
                        fila,
                        col,
                        pieza,
                        movimientos,
                        new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}
                    );
                    break;
                case CABALLO:
                    agregarMovimientosCaballo(fila, col, pieza, movimientos);
                    break;
                case REY:
                    agregarMovimientosRey(fila, col, pieza, movimientos);
                    break;
                default:
                    break;
            }
            return movimientos;
        }

        private void agregarMovimientosPeon(int fila, int col, Pieza pieza, List<Movimiento> movimientos) {
            int direccion = pieza.color == Color.BLANCO ? -1 : 1;
            int filaSiguiente = fila + direccion;

            if (dentro(filaSiguiente, col) && get(filaSiguiente, col) == null) {
                movimientos.add(crearMovimientoPeon(fila, col, filaSiguiente, col, pieza));
                int filaDoble = fila + 2 * direccion;
                if (!pieza.movida && dentro(filaDoble, col) && get(filaDoble, col) == null) {
                    movimientos.add(new Movimiento(fila, col, filaDoble, col));
                }
            }

            for (int deltaCol : new int[]{-1, 1}) {
                int nuevaCol = col + deltaCol;
                if (!dentro(filaSiguiente, nuevaCol)) {
                    continue;
                }
                Pieza objetivo = get(filaSiguiente, nuevaCol);
                if (objetivo != null && objetivo.color != pieza.color) {
                    movimientos.add(crearMovimientoPeon(fila, col, filaSiguiente, nuevaCol, pieza));
                }
            }
        }

        private Movimiento crearMovimientoPeon(int fila, int col, int filaDestino, int colDestino, Pieza pieza) {
            boolean promocion = (pieza.color == Color.BLANCO && filaDestino == 0) || (pieza.color == Color.NEGRO && filaDestino == 7);
            return new Movimiento(fila, col, filaDestino, colDestino, false, false, promocion);
        }

        private void agregarDeslizantes(int fila, int col, Pieza pieza, List<Movimiento> movimientos, int[][] direcciones) {
            for (int[] dir : direcciones) {
                int f = fila + dir[0];
                int c = col + dir[1];
                while (dentro(f, c)) {
                    Pieza objetivo = get(f, c);
                    if (objetivo == null) {
                        movimientos.add(new Movimiento(fila, col, f, c));
                    } else {
                        if (objetivo.color != pieza.color) {
                            movimientos.add(new Movimiento(fila, col, f, c));
                        }
                        break;
                    }
                    f += dir[0];
                    c += dir[1];
                }
            }
        }

        private void agregarMovimientosCaballo(int fila, int col, Pieza pieza, List<Movimiento> movimientos) {
            int[][] saltos = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
            };
            for (int[] salto : saltos) {
                int f = fila + salto[0];
                int c = col + salto[1];
                if (!dentro(f, c)) {
                    continue;
                }
                Pieza objetivo = get(f, c);
                if (objetivo == null || objetivo.color != pieza.color) {
                    movimientos.add(new Movimiento(fila, col, f, c));
                }
            }
        }

        private void agregarMovimientosRey(int fila, int col, Pieza pieza, List<Movimiento> movimientos) {
            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (df == 0 && dc == 0) {
                        continue;
                    }
                    int f = fila + df;
                    int c = col + dc;
                    if (!dentro(f, c)) {
                        continue;
                    }
                    Pieza objetivo = get(f, c);
                    if (objetivo == null || objetivo.color != pieza.color) {
                        movimientos.add(new Movimiento(fila, col, f, c));
                    }
                }
            }

            if (!pieza.movida && !estaEnJaque(pieza.color)) {
                agregarEnroques(fila, col, pieza, movimientos);
            }
        }

        private void agregarEnroques(int fila, int col, Pieza rey, List<Movimiento> movimientos) {
            if (puedeEnrocarCorto(fila, col, rey.color)) {
                movimientos.add(new Movimiento(fila, col, fila, col + 2, true, false, false));
            }
            if (puedeEnrocarLargo(fila, col, rey.color)) {
                movimientos.add(new Movimiento(fila, col, fila, col - 2, false, true, false));
            }
        }

        private boolean puedeEnrocarCorto(int fila, int col, Color color) {
            if (!dentro(fila, col + 3)) {
                return false;
            }
            Pieza torre = get(fila, col + 3);
            if (torre == null || torre.tipo != TipoPieza.TORRE || torre.color != color || torre.movida) {
                return false;
            }
            if (get(fila, col + 1) != null || get(fila, col + 2) != null) {
                return false;
            }
            return !casillaAtacada(fila, col + 1, color.opuesto()) && !casillaAtacada(fila, col + 2, color.opuesto());
        }

        private boolean puedeEnrocarLargo(int fila, int col, Color color) {
            if (!dentro(fila, col - 4)) {
                return false;
            }
            Pieza torre = get(fila, col - 4);
            if (torre == null || torre.tipo != TipoPieza.TORRE || torre.color != color || torre.movida) {
                return false;
            }
            if (get(fila, col - 1) != null || get(fila, col - 2) != null || get(fila, col - 3) != null) {
                return false;
            }
            return !casillaAtacada(fila, col - 1, color.opuesto()) && !casillaAtacada(fila, col - 2, color.opuesto());
        }

        private boolean estaEnJaque(Color color) {
            int[] rey = buscarRey(color);
            return rey != null && casillaAtacada(rey[0], rey[1], color.opuesto());
        }

        private int[] buscarRey(Color color) {
            for (int fila = 0; fila < 8; fila++) {
                for (int col = 0; col < 8; col++) {
                    Pieza pieza = get(fila, col);
                    if (pieza != null && pieza.color == color && pieza.tipo == TipoPieza.REY) {
                        return new int[]{fila, col};
                    }
                }
            }
            return null;
        }

        private boolean casillaAtacada(int fila, int col, Color atacante) {
            for (int f = 0; f < 8; f++) {
                for (int c = 0; c < 8; c++) {
                    Pieza pieza = get(f, c);
                    if (pieza == null || pieza.color != atacante) {
                        continue;
                    }
                    if (atacaCasilla(f, c, pieza, fila, col)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean atacaCasilla(int fila, int col, Pieza pieza, int filaObjetivo, int colObjetivo) {
            int df = filaObjetivo - fila;
            int dc = colObjetivo - col;

            switch (pieza.tipo) {
                case PEON:
                    int direccion = pieza.color == Color.BLANCO ? -1 : 1;
                    return df == direccion && Math.abs(dc) == 1;
                case CABALLO:
                    return (Math.abs(df) == 2 && Math.abs(dc) == 1) || (Math.abs(df) == 1 && Math.abs(dc) == 2);
                case ALFIL:
                    return Math.abs(df) == Math.abs(dc) && caminoLibre(fila, col, filaObjetivo, colObjetivo);
                case TORRE:
                    return (df == 0 || dc == 0) && caminoLibre(fila, col, filaObjetivo, colObjetivo);
                case DAMA:
                    return ((df == 0 || dc == 0) || Math.abs(df) == Math.abs(dc))
                        && caminoLibre(fila, col, filaObjetivo, colObjetivo);
                case REY:
                    return Math.max(Math.abs(df), Math.abs(dc)) == 1;
                default:
                    return false;
            }
        }

        private boolean caminoLibre(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
            int pasoFila = Integer.compare(filaDestino, filaOrigen);
            int pasoCol = Integer.compare(colDestino, colOrigen);
            int fila = filaOrigen + pasoFila;
            int col = colOrigen + pasoCol;
            while (fila != filaDestino || col != colDestino) {
                if (get(fila, col) != null) {
                    return false;
                }
                fila += pasoFila;
                col += pasoCol;
            }
            return true;
        }

        private void aplicarMovimiento(Movimiento mov) {
            Pieza pieza = get(mov.filaOrigen, mov.colOrigen);
            set(mov.filaOrigen, mov.colOrigen, null);

            if (mov.enroqueCorto) {
                Pieza torre = get(mov.filaOrigen, 7);
                set(mov.filaOrigen, 7, null);
                set(mov.filaOrigen, 5, torre);
                if (torre != null) {
                    torre.movida = true;
                }
            } else if (mov.enroqueLargo) {
                Pieza torre = get(mov.filaOrigen, 0);
                set(mov.filaOrigen, 0, null);
                set(mov.filaOrigen, 3, torre);
                if (torre != null) {
                    torre.movida = true;
                }
            }

            if (mov.promocion) {
                pieza = new Pieza(TipoPieza.DAMA, pieza.color);
                pieza.movida = true;
            } else if (pieza != null) {
                pieza.movida = true;
            }

            set(mov.filaDestino, mov.colDestino, pieza);
        }

        private int evaluar(Color perspectiva) {
            int total = 0;

            for (int fila = 0; fila < 8; fila++) {
                for (int col = 0; col < 8; col++) {
                    Pieza pieza = get(fila, col);
                    if (pieza == null) {
                        continue;
                    }

                    int valor = valorBase(pieza.tipo) + bonusPosicional(pieza, fila, col);
                    total += pieza.color == perspectiva ? valor : -valor;
                }
            }

            int movilidad = movimientosLegales(perspectiva).size() - movimientosLegales(perspectiva.opuesto()).size();
            total += movilidad * 4;

            if (estaEnJaque(perspectiva.opuesto())) {
                total += 25;
            }
            if (estaEnJaque(perspectiva)) {
                total -= 25;
            }

            return total;
        }

        private int valorBase(TipoPieza tipo) {
            switch (tipo) {
                case PEON: return 100;
                case CABALLO: return 320;
                case ALFIL: return 330;
                case TORRE: return 500;
                case DAMA: return 900;
                case REY: return 20000;
                default: return 0;
            }
        }

        private int bonusPosicional(Pieza pieza, int fila, int col) {
            int filaNormalizada = pieza.color == Color.BLANCO ? 7 - fila : fila;
            switch (pieza.tipo) {
                case PEON:
                    return filaNormalizada * 12 - Math.abs(3 - col) * 2;
                case CABALLO:
                    return 30 - (Math.abs(3 - fila) + Math.abs(3 - col)) * 8;
                case ALFIL:
                    return 20 - (Math.abs(3 - fila) + Math.abs(3 - col)) * 4;
                case TORRE:
                    return filaNormalizada * 3;
                case DAMA:
                    return 10 - (Math.abs(3 - fila) + Math.abs(3 - col)) * 2;
                case REY:
                    return pieza.movida ? 25 : 0;
                default:
                    return 0;
            }
        }
    }

    private static class Partida {
        private final Tablero tablero;
        private Color turno;
        private final Color colorHumano;
        private final Color colorIA;

        private Partida() {
            this.tablero = new Tablero();
            this.turno = Color.BLANCO;
            this.colorHumano = Color.BLANCO;
            this.colorIA = Color.NEGRO;
        }

        private void jugar() {
            System.out.println("AJEDREZ");
            System.out.println("Escribe movimientos con formato: e2 e4");
            System.out.println("Escribe 'salir' para terminar.");
            System.out.println("Juegas con blancas. La IA juega con negras.");

            while (true) {
                tablero.mostrar();
                List<Movimiento> legales = tablero.movimientosLegales(turno);

                if (legales.isEmpty()) {
                    if (tablero.estaEnJaque(turno)) {
                        System.out.println("Jaque mate. Ganan " + turno.opuesto().nombre() + ".");
                    } else {
                        System.out.println("Tablas por ahogado.");
                    }
                    break;
                }

                if (tablero.estaEnJaque(turno)) {
                    System.out.println("Jaque a " + turno.nombre() + ".");
                }

                if (turno == colorHumano) {
                    System.out.print("Turno de " + turno.nombre() + ": ");
                    String linea = SC.nextLine().trim();
                    if (linea.equalsIgnoreCase("salir")) {
                        System.out.println("Partida terminada.");
                        break;
                    }

                    String[] partes = linea.split("\\s+");
                    if (partes.length != 2) {
                        System.out.println("Formato invalido. Usa por ejemplo: e2 e4");
                        continue;
                    }

                    int[] origen = parsearCasilla(partes[0]);
                    int[] destino = parsearCasilla(partes[1]);
                    if (origen == null || destino == null) {
                        System.out.println("Casillas invalidas. Usa columnas a-h y filas 1-8.");
                        continue;
                    }

                    Movimiento elegido = null;
                    for (Movimiento mov : legales) {
                        if (mov.filaOrigen == origen[0] && mov.colOrigen == origen[1] && mov.coincideCon(destino[0], destino[1])) {
                            elegido = mov;
                            break;
                        }
                    }

                    if (elegido == null) {
                        System.out.println("Movimiento no legal.");
                        continue;
                    }

                    tablero.aplicarMovimiento(elegido);
                    if (elegido.promocion) {
                        System.out.println("Peon promocionado a dama.");
                    }
                    turno = turno.opuesto();
                } else {
                    System.out.println("La IA está pensando...");
                    Movimiento mejorMovimiento = elegirMovimientoIA(legales);
                    tablero.aplicarMovimiento(mejorMovimiento);
                    System.out.println("La IA juega: " + mejorMovimiento.texto());
                    if (mejorMovimiento.promocion) {
                        System.out.println("La IA promociona un peón a dama.");
                    }
                    turno = turno.opuesto();
                }
            }
        }

        private Movimiento elegirMovimientoIA(List<Movimiento> legales) {
            ResultadoBusqueda mejor = new ResultadoBusqueda(legales.get(0), Integer.MIN_VALUE);

            for (Movimiento mov : ordenarMovimientos(tablero, legales)) {
                Tablero copia = tablero.copia();
                copia.aplicarMovimiento(mov);
                int puntuacion = minimax(copia, colorIA.opuesto(), PROFUNDIDAD_IA - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (puntuacion > mejor.puntuacion) {
                    mejor = new ResultadoBusqueda(mov, puntuacion);
                }
            }

            return mejor.movimiento;
        }

        private int minimax(Tablero estado, Color turnoActual, int profundidad, int alpha, int beta) {
            List<Movimiento> legales = estado.movimientosLegales(turnoActual);

            if (legales.isEmpty()) {
                if (estado.estaEnJaque(turnoActual)) {
                    return turnoActual == colorIA ? -100000 - profundidad : 100000 + profundidad;
                }
                return 0;
            }

            if (profundidad == 0) {
                return estado.evaluar(colorIA);
            }

            if (turnoActual == colorIA) {
                int mejor = Integer.MIN_VALUE;
                for (Movimiento mov : ordenarMovimientos(estado, legales)) {
                    Tablero copia = estado.copia();
                    copia.aplicarMovimiento(mov);
                    int valor = minimax(copia, turnoActual.opuesto(), profundidad - 1, alpha, beta);
                    mejor = Math.max(mejor, valor);
                    alpha = Math.max(alpha, mejor);
                    if (beta <= alpha) {
                        break;
                    }
                }
                return mejor;
            }

            int mejor = Integer.MAX_VALUE;
            for (Movimiento mov : ordenarMovimientos(estado, legales)) {
                Tablero copia = estado.copia();
                copia.aplicarMovimiento(mov);
                int valor = minimax(copia, turnoActual.opuesto(), profundidad - 1, alpha, beta);
                mejor = Math.min(mejor, valor);
                beta = Math.min(beta, mejor);
                if (beta <= alpha) {
                    break;
                }
            }
            return mejor;
        }

        private List<Movimiento> ordenarMovimientos(Tablero estado, List<Movimiento> movimientos) {
            List<Movimiento> ordenados = new ArrayList<>(movimientos);
            ordenados.sort((a, b) -> Integer.compare(pesoMovimiento(estado, b), pesoMovimiento(estado, a)));
            return ordenados;
        }

        private int pesoMovimiento(Tablero estado, Movimiento mov) {
            int peso = 0;
            Pieza origen = estado.get(mov.filaOrigen, mov.colOrigen);
            Pieza destino = estado.get(mov.filaDestino, mov.colDestino);

            if (destino != null && origen != null) {
                peso += 10 * valorPieza(destino.tipo) - valorPieza(origen.tipo);
            }
            if (mov.promocion) {
                peso += 800;
            }
            if (mov.enroqueCorto || mov.enroqueLargo) {
                peso += 50;
            }

            Tablero copia = estado.copia();
            copia.aplicarMovimiento(mov);
            if (copia.estaEnJaque(origen.color.opuesto())) {
                peso += 40;
            }
            return peso;
        }

        private int valorPieza(TipoPieza tipo) {
            switch (tipo) {
                case PEON: return 100;
                case CABALLO: return 320;
                case ALFIL: return 330;
                case TORRE: return 500;
                case DAMA: return 900;
                case REY: return 20000;
                default: return 0;
            }
        }

        private int[] parsearCasilla(String texto) {
            if (texto == null || texto.length() != 2) {
                return null;
            }
            char columna = Character.toLowerCase(texto.charAt(0));
            char fila = texto.charAt(1);
            if (columna < 'a' || columna > 'h' || fila < '1' || fila > '8') {
                return null;
            }
            int col = columna - 'a';
            int filaTablero = 8 - (fila - '0');
            return new int[]{filaTablero, col};
        }
    }

    private static String nombreCasilla(int fila, int col) {
        char columna = (char) ('a' + col);
        int filaHumana = 8 - fila;
        return String.valueOf(columna) + filaHumana;
    }
}
