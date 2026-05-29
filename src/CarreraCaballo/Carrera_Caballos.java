import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Carrera_Caballos {
    static final int NUM_CABALLOS = 8;
    static final int META = 50;
    static Random random = new Random();
    static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws InterruptedException {
        String opcion = "";
        Apostador apostador = new Apostador();

        while (true) {
            
            do {
                clear();
                System.out.println("MENU\n1. Correr Caballos\n2. Ver dinero");
                opcion = sc.nextLine().toUpperCase();
            }while(!opcion.equals("1") && !opcion.equals("2"));

            switch (opcion) {
                case "1":
                    Caballo [] caballos = generar_caballos();
                    fase_apuesta(caballos, apostador);
                    correr_caballos(caballos, apostador);
                    sc.nextLine();
                    break;
            
                case "2":
                    System.out.println(apostador.dinero);
                    sc.nextLine();
                    break;
            }
        }
    }
    private static void fase_apuesta (Caballo [] caballos, Apostador apostador) {
        String caballo;
        String apostado;

        for (int i = 0; i < caballos.length; i++) {
            caballos[i].mostrar_cuotas();
        }
        System.out.println("Que caballo quieres apostar: ");
        do{
            caballo = sc.nextLine();
        }while(!caballo.matches("[1-"+caballos.length+"]"));

        System.out.println("Cuanto quieres apostar: "+apostador.dinero);
        do{
            apostado = sc.nextLine();
        }while(!apostador.apostadaCorrecta(apostado));
        
        apostador.caballo = caballos[Integer.parseInt(caballo)-1];
        apostador.dinero -= Integer.parseInt(apostado);
    }
    private static Caballo [] generar_caballos () {
        Caballo [] caballos = new Caballo[NUM_CABALLOS];
        double cuota = random.nextInt(2,5);
        // double cuota = random.nextInt(2);
        for (int i = 0; i < caballos.length; i++) {
            caballos[i] = new Caballo(i+1);
            caballos[i].cuota = cuota;
            cuota = Math.round((cuota * (random.nextBoolean() ? 1.25 : 1.75) * 100.0) / 100.0);
            // cuota = Math.round((cuota * (2) * 100.0) / 100.0);
        }
        return caballos;
    }
    private static void correr_caballos (Caballo [] caballos, Apostador apostador) throws InterruptedException {
        boolean acabado = false;
        ArrayList <String> ganadores = new ArrayList<>();
        Caballo caballoGanador;

        while (!acabado) {
            clear();
            for (int j = 0; j < caballos.length; j++) {
                caballos[j].avanzar();
                System.out.printf("%-10s",caballos[j].dorsal+". "+caballos[j].cuota);
                for (int k = 1; k < caballos[j].avanzado; k++) {
                    System.out.print("-");
                }
                System.out.println("🐎" + imprimir_meta(caballos[j].avanzado));
                if (caballos[j].avanzado >= META) {
                    acabado = true;
                    ganadores.add(""+caballos[j].dorsal);
                }
            }
            
            Thread.sleep(500);
        }
        caballoGanador = caballos[Integer.parseInt(escogerGanador(ganadores))-1];
        System.out.println("Ganador "+caballoGanador.dorsal);
        if (caballoGanador.equals(apostador.caballo))apostador.pagarApuesta();
    }
    private static String escogerGanador (ArrayList <String> ganadores) {
        return ganadores.get(random.nextInt(ganadores.size()));
    }
    private static String imprimir_meta (int avanzado){
        String txt = "";
        for (int i = 0; i < META - avanzado-1; i++) {
            txt += " ";
        }
        txt += "🏁";
        return txt;
    }
    private static void clear() {
        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static class Apostador {
        private int dinero = 100;
        private int apostado = 0;
        private Caballo caballo;

        public Apostador () {

        }
        public void pagarApuesta () {
            double ganado = apostado * caballo.cuota;
            System.out.println("Has ganado "+ganado+"!!!");
            dinero += ganado;
            sc.nextLine();
            System.out.println("Dinero actual: "+dinero);
        }
        public boolean apostadaCorrecta (String apuestaStr) {
            boolean correcto = true;
            int apuesta = 0;
            try {
                apuesta = Integer.parseInt(apuestaStr);
            }catch (Exception e) {
                return false;
            }
            if (dinero - apuesta < 0) correcto = false;
            if (correcto) this.apostado = apuesta;

            return correcto;
        }
    }
    public static class Caballo {
        private int avanzado = 0;
        private int dorsal; 
        private double cuota;
        public Caballo (int dorsal) {
            this.dorsal = dorsal;
        }
        public void mostrar_cuotas () {
            System.out.printf("%-15s", "Caballo "+dorsal+" -> "+cuota+"\n");
        }
        public void avanzar () {
            avanzado += (random.nextInt(0,100) < 100/cuota) ? random.nextInt(2,5) : random.nextInt(0,2); 
        }
    }
}
