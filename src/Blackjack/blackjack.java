import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.List;

public class blackjack {
    static Scanner sc = new Scanner(System.in);
    public static void main (String [] args) {
        Baraja baraja = new Baraja();
        Crupier crupier = new Crupier();
        Jugador jugador = new Jugador();

        juego(baraja, crupier, jugador);
    }
    private static void juego (Baraja baraja, Crupier crupier, Jugador jugador) {
        while (0 < jugador.bote && jugador.bote < 10000) {            
            baraja.generarBaraja();
            crupier.darPrimerasCartas(baraja);
            jugador.darPrimerasCartas(baraja);

            System.out.println("TURNO DE JUGADOR: "+jugador.bote);
            jugador.apostar();
            jugador.mostrarValor();
            while (jugador.pedirCarta(baraja)) {}

            System.out.println("TURNO DE CRUPIER:");
            crupier.mostrarValor();
            try {
                Thread.sleep(3000);
            } catch (Exception e) {}
            while (crupier.pedirCarta(baraja)){
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {}
                crupier.mostrarValor();
            }

            if (crupier.haPerdido()){
                System.out.println("El Crupier se ha pasado de 21");
            }
            else System.out.println("El Crupier ha sacado un "+crupier.getValorSumado());

            if (crupier.getValorSumado() > jugador.getValorSumado() && !crupier.haPerdido() || jugador.haPerdido()) {
                System.out.println("HAS PERDIDO");
            }
            else if (crupier.getValorSumado() < jugador.getValorSumado() && !jugador.haPerdido() || (crupier.haPerdido() && !jugador.haPerdido())) {
                System.out.println("HAS GANADO");
                jugador.bote += jugador.apostado*2;
            }
            else System.out.println("Empate"); 

            crupier.clear();
            jugador.clear();
        }

        if (jugador.bote >= 10000) System.out.println("ENHORABUENA HAS GANADO EL JUEGO");
        else System.out.println("HAS PERDIDO TODO EL BOTE");
    }
    public static class Carta {
        private String valor;
        private String palo;

        public Carta (String valor, String palo) {
            this.valor = valor;
            this.palo = palo;
        }

        public int getValor () {            
            if (Character.isDigit(valor.charAt(0))) return Integer.parseInt(valor);
            else if (valor.equals("A")) return 11;
            else return 10;
        }
    }
    public static class Persona {
        private int valorSumado = 0;
        private boolean continua = true;
        List <Carta> cartasUsadas = new ArrayList<>();

        public void darPrimerasCartas (Baraja baraja) {
            sumarValor(baraja.getCartaAzar());
            sumarValor(baraja.getCartaAzar());
        }

        public void clear() {
            valorSumado = 0;
            continua = true;
            cartasUsadas.clear();
        }

        public void sumarValor (Carta carta) {
            valorSumado += carta.getValor();
            cartasUsadas.add(carta);
        }

        public boolean haPerdido () {
            return valorSumado > 21;
        }
        
        public void mostrarValor () {
            for (int i = 0; i < cartasUsadas.size(); i++) {
                System.out.print(cartasUsadas.get(i).valor+cartasUsadas.get(i).palo+" ");
            }
            System.out.println("\n"+valorSumado+"\n");
        }

        public int getValorSumado() {
            return valorSumado;
        }
    }
    public static class Crupier extends Persona {
        public boolean pedirCarta (Baraja baraja) {
            if (super.valorSumado < 17) {
                Carta carta = baraja.getCartaAzar();
                System.out.println("Ha sacado "+carta.valor+carta.palo);
                if (carta.getValor() == 11 && carta.getValor() + super.valorSumado > 21) super.valorSumado++;
                else sumarValor(carta);
            } 
            else super.continua = false;

            mostrarValor();
            return super.continua;
        }
    }
    public static class Jugador extends Persona {
        private int bote = 1000;
        private int apostado = 0;

        public boolean sinBote () {
            return bote == 0;
        }

        public void clear () {
            super.clear();
            apostado = 0;
        }

        public void apostar () {
            String opcion;
            boolean incorrecte = true;
                do{
                    try {
                    System.out.print("Apuesta:");
                    opcion = sc.nextLine();
                    this.apostado = Integer.parseInt(opcion);
                    if (bote - apostado >= 0) incorrecte = false;
                    } catch (Exception e) {
                        incorrecte = true;            
                    }
                }while(incorrecte);
            

            bote -= apostado;
        }

        public boolean pedirCarta (Baraja baraja) {
            String opcion;
            Carta carta;

            do{
                System.out.println("+ : -");
                opcion = sc.nextLine();
            }while(!(opcion.equals("+") || opcion.equals("-")));
            
            if (opcion.equals("+")) {
                carta = baraja.getCartaAzar();
                System.out.println("Has sumado "+carta.valor+carta.palo);
                if (carta.getValor() == 11 && carta.getValor() + super.valorSumado > 21) super.valorSumado++;
                else sumarValor(carta);
                mostrarValor();
                if (super.valorSumado >21) {
                    super.continua = false;
                    System.out.println("Te has pasado de 21");
                }
                else if (super.valorSumado == 21) {
                    System.out.println("Has sacado 21");
                    super.continua = false;
                }
            }
            else super.continua = false;

            return super.continua;
        }
    }
    public static class Baraja {
        ArrayList<Carta> baraja = new ArrayList<>();

        public Baraja () {}

        public void generarBaraja () {
            baraja.clear();
            String[] palos = {"\u2660", "\u2665", "\u2666", "\u2663"}; // ♠ ♥ ♦ ♣
            String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

            for (String palo : palos) {
                for (String valor : valores) {
                    baraja.add(new Carta (valor, palo));
                }
            }
        }

        public Carta getCartaAzar () {
            Random random = new Random();
            return baraja.remove(random.nextInt(baraja.size()));
        }
    }
}
