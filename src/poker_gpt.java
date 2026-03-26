import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class poker {
    static private Scanner sc = new Scanner(System.in);
    static private int fichasJugador1 = 100;
    static private int fichasJugador2 = 100;

    public static void main(String[] args) {
        ArrayList<String> deck = createDeck();
        Collections.shuffle(deck);

        String[] player1 = {deck.remove(0), deck.remove(0)};
        String[] player2 = {deck.remove(0), deck.remove(0)};
        ArrayList<String> community = new ArrayList<>();

        int bote = 0; // bote de fichas

        System.out.println("=== Póker Texas Hold'em - 2 jugadores ===");

        // Pre-flop
        System.out.println("\n--- Pre-flop ---");
        mostrarMano(player1, "Jugador 1");
        mostrarMano(player2, "Jugador 2");
        bote += rondaApuestas();

        // Flop
        System.out.println("\n--- Flop ---");
        for (int i = 0; i < 3; i++) community.add(deck.remove(0));
        System.out.println("Cartas comunitarias: " + community);
        bote += rondaApuestas();

        // Turn
        System.out.println("\n--- Turn ---");
        community.add(deck.remove(0));
        System.out.println("Cartas comunitarias: " + community);
        bote += rondaApuestas();

        // River
        System.out.println("\n--- River ---");
        community.add(deck.remove(0));
        System.out.println("Cartas comunitarias: " + community);
        bote += rondaApuestas();

        // Showdown
        System.out.println("\n--- Showdown ---");
        System.out.println("Cartas comunitarias: " + community);
        mostrarMano(player1, "Jugador 1");
        mostrarMano(player2, "Jugador 2");

        int resultado = compararManos(player1, player2, community);
        if (resultado > 0) {
            System.out.println("¡Jugador 1 gana el bote de " + bote + " fichas!");
            fichasJugador1 += bote;
        } else if (resultado < 0) {
            System.out.println("¡Jugador 2 gana el bote de " + bote + " fichas!");
            fichasJugador2 += bote;
        } else {
            System.out.println("Empate! El bote se divide.");
            fichasJugador1 += bote / 2;
            fichasJugador2 += bote / 2;
        }

        System.out.println("\nFichas finales:");
        System.out.println("Jugador 1: " + fichasJugador1);
        System.out.println("Jugador 2: " + fichasJugador2);
    }

    private static ArrayList<String> createDeck() {
        ArrayList<String> deck = new ArrayList<>();
        String[] suits = {"\u2660", "\u2665", "\u2666", "\u2663"}; // ♠ ♥ ♦ ♣
        String[] values = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        for (String suit : suits) {
            for (String value : values) {
                deck.add(value + suit);
            }
        }
        return deck;
    }

    private static void mostrarMano(String[] mano, String jugador) {
        System.out.println(jugador + ": " + mano[0] + " " + mano[1]);
    }

    private static int rondaApuestas() {
        int apuesta1 = pedirApuesta("Jugador 1");
        int apuesta2 = pedirApuesta("Jugador 2");
        return apuesta1 + apuesta2;
    }

    private static int pedirApuesta(String jugador) {
        System.out.println(jugador + ", ingresa tu apuesta (fichas disponibles):");
        int apuesta = sc.nextInt();
        sc.nextLine(); // limpiar buffer
        return apuesta;
    }

    // Método simple para comparar manos: solo suma de valores, sin combinaciones avanz
    private static int compararManos(String[] player1, String[] player2, ArrayList<String> community) {
        int valor1 = calcularValorMano(player1, community);
        int valor2 = calcularValorMano(player2, community);

        System.out.println("Valor Jugador 1: " + valor1);
        System.out.println("Valor Jugador 2: " + valor2);

        return Integer.compare(valor1, valor2);
    }

    private static int calcularValorMano(String[] mano, ArrayList<String> community) {
        String[] todas = new String[mano.length + community.size()];
        System.arraycopy(mano, 0, todas, 0, mano.length);
        for (int i = 0; i < community.size(); i++) todas[i + mano.length] = community.get(i);

        int suma = 0;
        for (String carta : todas) {
            String valor = carta.substring(0, carta.length() - 1);
            switch (valor) {
                case "J" -> suma += 11;
                case "Q" -> suma += 12;
                case "K" -> suma += 13;
                case "A" -> suma += 14;
                default -> suma += Integer.parseInt(valor);
            }
        }
        return suma;
    }
}