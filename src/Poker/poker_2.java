import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class poker_2 {
    static private Random random = new Random();
    static private Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ArrayList<String> baraja = new ArrayList<>();

        String[] palos = {"\u2660", "\u2665", "\u2666", "\u2663"};
        String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] manosPoker = {"Carta", "Par", "Doble", "Trio", "Escalera", "Color", "Full", "Poker"};
        ArrayList<String> mesa = new ArrayList<>();
        ArrayList<String[]> manos = new ArrayList<>();
        int numJugadores;

        for (String palo : palos) {
            for (String valor : valores) {
                baraja.add(valor + palo);
            }
        }

        System.out.println("NUMERO DE JUGADORES?: ");
        numJugadores = 4;

        for (int i = 0; i < numJugadores; i++) {
            manos.add(new String[2]);

            int n = random.nextInt(baraja.size());
            manos.get(i)[0] = baraja.get(n);
            baraja.remove(n);

            n = random.nextInt(baraja.size());
            manos.get(i)[1] = baraja.get(n);
            baraja.remove(n);
        }

        System.out.println("\nPREFLOP");
        for (int i = 0; i < manos.size(); i++) {
            System.out.println((i + 1) + ": " + manos.get(i)[0] + " " + manos.get(i)[1]);
        }

        for (int i = 0; i < 3; i++) {
            int n = random.nextInt(baraja.size());
            mesa.add(baraja.get(n));
            baraja.remove(n);
        }
        System.out.println("FLOP");
        System.out.print("\tMESA: ");
        for (int i = 0; i < mesa.size(); i++) {
            System.out.print(mesa.get(i) + " ");
        }
        System.out.println("\n");

        int n = random.nextInt(baraja.size());
        mesa.add(baraja.get(n));
        baraja.remove(n);
        System.out.println("FOURTH STREET");
        System.out.print("\tMESA: ");
        for (int i = 0; i < mesa.size(); i++) {
            System.out.print(mesa.get(i) + " ");
        }
        System.out.println("\n");

        n = random.nextInt(baraja.size());
        mesa.add(baraja.get(n));
        baraja.remove(n);
        System.out.println("FIFTH STREET");
        System.out.print("\tMESA: ");
        for (int i = 0; i < mesa.size(); i++) {
            System.out.print(mesa.get(i) + " ");
        }
        System.out.println("\n");

        String ganador = "1";
        ArrayList<String> combinaciones = new ArrayList<>();
        ArrayList<Integer> combinaciones_jug = new ArrayList<>();
        String mejorCombinacion = "";

        for (int i = 0; i < manos.size(); i++) {
            String[] mano = manos.get(i);
            String resultado = "";

            ArrayList<String> valoresRepetidos = new ArrayList<>();
            ArrayList<Integer> repetidos = new ArrayList<>();
            ArrayList<String> todasCartas = new ArrayList<>();

            for (int j = 0; j < mano.length; j++) {
                todasCartas.add(mano[j]);
            }
            for (int j = 0; j < mesa.size(); j++) {
                todasCartas.add(mesa.get(j));
            }

            for (int j = 0; j < todasCartas.size() - 1; j++) {
                for (int k = j + 1; k < todasCartas.size(); k++) {
                    if (Arrays.asList(valores).indexOf(todasCartas.get(j)) > Arrays.asList(valores).indexOf(todasCartas.get(k))) {
                        String c = todasCartas.get(j);
                        todasCartas.set(j, todasCartas.get(k));
                        todasCartas.set(k, c);
                    }
                }
            }

            for (int j = 0; j < todasCartas.size(); j++) {
                String valorActual = todasCartas.get(j).substring(0, todasCartas.get(j).length() - 1);
                if (valoresRepetidos.indexOf(valorActual) == -1) {
                    valoresRepetidos.add(valorActual);
                    repetidos.add(1);
                } else {
                    int posValor = valoresRepetidos.indexOf(valorActual);
                    repetidos.set(posValor, repetidos.get(posValor) + 1);
                }
            }

            for (int j = 0; j < valoresRepetidos.size(); j++) {
                if (repetidos.get(j) > 1) {
                    String primerToken = resultado.split(" ")[0];
                    if (repetidos.get(j) == 2) {
                        if (!primerToken.equals("Poker") && !primerToken.equals("Full")) {
                            if (primerToken.equals("Par")) {
                                resultado = "Doble par de " + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j) + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1] + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1];
                            } else if (primerToken.equals("Trio")) {
                                resultado = "Full de " + resultado.split(" ")[resultado.split(" ").length - 1] + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1] + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1] + " "
                                        + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j);
                            } else if (primerToken.equals("Doble")) {
                                resultado = "Doble par de " + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j) + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1] + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1];
                            } else {
                                resultado = "Par de " + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j);
                            }
                        }
                    } else if (repetidos.get(j) == 3) {
                        if (!primerToken.equals("Poker") && !primerToken.equals("Full")) {
                            if (primerToken.equals("Par")) {
                                resultado = "Full de " + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j) + " "
                                        + valoresRepetidos.get(j) + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1] + " "
                                        + resultado.split(" ")[resultado.split(" ").length - 1];
                            } else if (primerToken.equals("Trio")) {
                                String ultimoResultado = resultado.split(" ")[resultado.split(" ").length - 1];
                                if (Arrays.asList(valores).indexOf(ultimoResultado) > Arrays.asList(valores).indexOf(valoresRepetidos.get(j))) {
                                    resultado = "Full de " + ultimoResultado + " " + ultimoResultado + " " + ultimoResultado + " "
                                            + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j);
                                } else {
                                    resultado = "Full de " + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j) + " "
                                            + valoresRepetidos.get(j) + " " + ultimoResultado + " " + ultimoResultado;
                                }
                            } else {
                                resultado = "Trio de " + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j) + " "
                                        + valoresRepetidos.get(j);
                            }
                        }
                    } else {
                        resultado = "Poker de " + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j) + " "
                                + valoresRepetidos.get(j) + " " + valoresRepetidos.get(j);
                    }
                }
            }

            if (!resultado.split(" ")[0].equals("Poker") && !resultado.split(" ")[0].equals("Full")) {
                ArrayList<String> todosValores = new ArrayList<>();

                for (int j = 0; j < mano.length; j++) {
                    String valorActual = mano[j].substring(0, mano[j].length() - 1);
                    if (todosValores.indexOf(valorActual) == -1) {
                        todosValores.add(valorActual);
                    }
                }
                for (int j = 0; j < mesa.size(); j++) {
                    String valorActual = mesa.get(j).substring(0, mesa.get(j).length() - 1);
                    if (todosValores.indexOf(valorActual) == -1) {
                        todosValores.add(valorActual);
                    }
                }

                for (int j = 0; j < todosValores.size() - 1; j++) {
                    for (int k = j + 1; k < todosValores.size(); k++) {
                        if (Arrays.asList(valores).indexOf(todosValores.get(j)) > Arrays.asList(valores).indexOf(todosValores.get(k))) {
                            String c = todosValores.get(j);
                            todosValores.set(j, todosValores.get(k));
                            todosValores.set(k, c);
                        }
                    }
                }

                if (todosValores.size() > 0 && todosValores.get(todosValores.size() - 1).equals("A")) {
                    todosValores.add(0, "A");
                }

                int escalera = 1;
                String valoresEscalera = "";
                String[] mayorEscalera = new String[5];

                for (int j = 0; j < todosValores.size() - 1; j++) {
                    int actual = Arrays.asList(valores).indexOf(todosValores.get(j));
                    int siguiente = Arrays.asList(valores).indexOf(todosValores.get(j + 1));

                    if (actual - siguiente == -1) {
                        valoresEscalera = valoresEscalera + " " + todosValores.get(j);
                        if (escalera < 5) {
                            escalera++;
                        }
                        if (escalera == 5) {
                            mayorEscalera = valoresEscalera.split(" ");
                            valoresEscalera = mayorEscalera[1] + " " + mayorEscalera[2] + " " + mayorEscalera[3] + " " + mayorEscalera[4];
                            valoresEscalera = valoresEscalera + " " + todosValores.get(j + 1);
                        }
                    } else if ((actual - 13) - siguiente == -1) {
                        escalera++;
                        valoresEscalera = valoresEscalera + " " + todosValores.get(j);
                    } else {
                        if (escalera == 5) {
                            break;
                        }
                        escalera = 1;
                        valoresEscalera = "";
                    }
                }

                if (escalera == 5) {
                    resultado = "Escalera de " + valoresEscalera;
                }
            }

            ArrayList<String> todosPalos = new ArrayList<>();
            for (int j = 0; j < mano.length; j++) {
                todosPalos.add(mano[j]);
            }
            for (int j = 0; j < mesa.size(); j++) {
                todosPalos.add(mesa.get(j));
            }

            for (int j = 0; j < todosPalos.size() - 1; j++) {
                for (int k = j + 1; k < todosPalos.size(); k++) {
                    String valorJ = todosPalos.get(j).substring(0, todosPalos.get(j).length() - 1);
                    String valorK = todosPalos.get(k).substring(0, todosPalos.get(k).length() - 1);
                    if (Arrays.asList(valores).indexOf(valorJ) > Arrays.asList(valores).indexOf(valorK)) {
                        String c = todosPalos.get(j);
                        todosPalos.set(j, todosPalos.get(k));
                        todosPalos.set(k, c);
                    }
                }
            }

            int[] color = {0, 0, 0, 0};
            String[] palosColor = {"\u2660", "\u2665", "\u2663", "\u2666"};
            for (int j = 0; j < todosPalos.size(); j++) {
                int x = 0;
                boolean ok = false;
                while (!ok && x < palosColor.length) {
                    if (todosPalos.get(j).split("")[todosPalos.get(j).length() - 1].equals(palosColor[x])) {
                        ok = true;
                        color[x] = color[x] + 1;
                    } else {
                        x++;
                    }
                }
            }

            boolean hayColor = false;
            String palo = "";
            int x = 0;
            while (!hayColor && x < color.length) {
                if (color[x] >= 5) {
                    hayColor = true;
                    palo = palosColor[x];
                } else {
                    x++;
                }
            }

            String cartasColor = "";
            String[] auxf;
            for (int j = 0; j < todosPalos.size(); j++) {
                if (todosPalos.get(j).split("")[todosPalos.get(j).length() - 1].equals(palo)) {
                    if (cartasColor.split(" ").length < 5) {
                        cartasColor = cartasColor + todosPalos.get(j) + " ";
                    } else {
                        auxf = cartasColor.split(" ");
                        cartasColor = auxf[1] + " " + auxf[2] + " " + auxf[3] + " " + auxf[4] + " " + todosPalos.get(j);
                    }
                }
            }

            if (hayColor) {
                resultado = "Color de " + palo + " con " + cartasColor;
            }

            if (resultado.equals("")) {
                String valor0 = mano[0].substring(0, mano[0].length() - 1);
                String valor1 = mano[1].substring(0, mano[1].length() - 1);
                if (Arrays.asList(valores).indexOf(valor0) > Arrays.asList(valores).indexOf(valor1)) {
                    resultado = "Carta alta de " + mano[0];
                } else {
                    resultado = "Carta alta de " + mano[1];
                }
            }

            if (resultado.split(" ")[0].equals("Color")) {
                ArrayList<String> todasCartasColor = new ArrayList<>();
                for (int j = 0; j < mano.length; j++) {
                    todasCartasColor.add(mano[j]);
                }
                for (int j = 0; j < mesa.size(); j++) {
                    todasCartasColor.add(mesa.get(j));
                }

                for (int j = 0; j < todasCartasColor.size() - 1; j++) {
                    for (int k = j + 1; k < todasCartasColor.size(); k++) {
                        String valorJ = todasCartasColor.get(j).substring(0, todasCartasColor.get(j).length() - 1);
                        String valorK = todasCartasColor.get(k).substring(0, todasCartasColor.get(k).length() - 1);
                        if (Arrays.asList(valores).indexOf(valorJ) > Arrays.asList(valores).indexOf(valorK)) {
                            String c = todasCartasColor.get(j);
                            todasCartasColor.set(j, todasCartasColor.get(k));
                            todasCartasColor.set(k, c);
                        }
                    }
                }

                int[] colorEsc = {0, 0, 0, 0};
                String[] palosEsc = {"\u2660", "\u2665", "\u2663", "\u2666"};
                for (int j = 0; j < todasCartasColor.size(); j++) {
                    int indice = 0;
                    boolean ok = false;
                    while (!ok && indice < palosEsc.length) {
                        if (todasCartasColor.get(j).split("")[todasCartasColor.get(j).length() - 1].equals(palosEsc[indice])) {
                            ok = true;
                            colorEsc[indice] = colorEsc[indice] + 1;
                        } else {
                            indice++;
                        }
                    }
                }

                boolean hayColorEsc = false;
                String paloEsc = "";
                int indice = 0;
                while (!hayColorEsc && indice < colorEsc.length) {
                    if (colorEsc[indice] >= 5) {
                        hayColorEsc = true;
                        paloEsc = palosEsc[indice];
                    } else {
                        indice++;
                    }
                }

                ArrayList<String> cartasMismoPalo = new ArrayList<>();
                for (int j = 0; j < todasCartasColor.size(); j++) {
                    if (todasCartasColor.get(j).split("")[todasCartasColor.get(j).length() - 1].equals(paloEsc)) {
                        cartasMismoPalo.add(todasCartasColor.get(j));
                    }
                }

                if (cartasMismoPalo.size() > 0
                        && cartasMismoPalo.get(cartasMismoPalo.size() - 1).substring(0, cartasMismoPalo.get(cartasMismoPalo.size() - 1).length() - 1).equals("A")) {
                    cartasMismoPalo.add(0, cartasMismoPalo.get(cartasMismoPalo.size() - 1));
                }

                int escalera = 1;
                String valoresEscalera = "";
                String[] mayorEscalera = new String[5];

                for (int j = 0; j < cartasMismoPalo.size() - 1; j++) {
                    int actual = Arrays.asList(valores).indexOf(cartasMismoPalo.get(j).substring(0, cartasMismoPalo.get(j).length() - 1));
                    int siguiente = Arrays.asList(valores).indexOf(cartasMismoPalo.get(j + 1).substring(0, cartasMismoPalo.get(j + 1).length() - 1));

                    if (actual - siguiente == -1) {
                        valoresEscalera = valoresEscalera + " " + cartasMismoPalo.get(j);
                        if (escalera < 5) {
                            escalera++;
                        }
                        if (escalera == 5) {
                            mayorEscalera = valoresEscalera.split(" ");
                            valoresEscalera = mayorEscalera[1] + " " + mayorEscalera[2] + " " + mayorEscalera[3] + " " + mayorEscalera[4];
                            valoresEscalera = valoresEscalera + " " + cartasMismoPalo.get(j + 1);
                        }
                    } else if ((actual - 13) - siguiente == -1) {
                        escalera++;
                        valoresEscalera = valoresEscalera + " " + cartasMismoPalo.get(j);
                    } else {
                        if (escalera == 5) {
                            break;
                        }
                        escalera = 1;
                        valoresEscalera = "";
                    }
                }

                if (escalera == 5) {
                    resultado = "Escalera_de_color de " + valoresEscalera;
                }
            }

            if (resultado.split(" ")[0].equals("Escalera_de_color")
                    && resultado.split(" ")[resultado.split(" ").length - 1]
                            .substring(0, resultado.split(" ")[resultado.split(" ").length - 1].length() - 1).equals("A")) {
                String[] auxResultado = resultado.split(" ");
                resultado = "Escalera_real de ";
                for (int j = 0; j < 5; j++) {
                    resultado = resultado + auxResultado[auxResultado.length - 5 + j] + " ";
                }
            }

            String combinacion = resultado;
            System.out.println((i + 1) + ". \t" + mano[0] + " " + mano[1] + "\t" + combinacion);

            if (i == 0) {
                mejorCombinacion = combinacion.split(" ")[0];
            } else if (Arrays.asList(manosPoker).indexOf(mejorCombinacion) < Arrays.asList(manosPoker).indexOf(combinacion.split(" ")[0])) {
                mejorCombinacion = combinacion.split(" ")[0];
                combinaciones.clear();
                combinaciones_jug.clear();
                ganador = String.valueOf(i + 1);
            }
            if (mejorCombinacion.equals(combinacion.split(" ")[0])) {
                combinaciones.add(combinacion);
                combinaciones_jug.add(i + 1);
            }
        }

        if (combinaciones.size() > 1) {
            ArrayList<String[]> manosGanadoras = new ArrayList<>();
            for (int i = 0; i < combinaciones_jug.size(); i++) {
                manosGanadoras.add(manos.get(combinaciones_jug.get(i) - 1));
            }

            if (mejorCombinacion.equals("Carta") || mejorCombinacion.equals("Par") || mejorCombinacion.equals("Doble")
                    || mejorCombinacion.equals("Trio") || mejorCombinacion.equals("Poker")) {
                ArrayList<String[]> kickers = new ArrayList<>();

                if (mejorCombinacion.equals("Carta")) {
                    for (int i = 0; i < manosGanadoras.size(); i++) {
                        ArrayList<String> cartasAgrupadas = new ArrayList<>();
                        for (int j = 0; j < manosGanadoras.get(i).length; j++) {
                            cartasAgrupadas.add(manosGanadoras.get(i)[j]);
                        }
                        for (int j = 0; j < mesa.size(); j++) {
                            cartasAgrupadas.add(mesa.get(j));
                        }
                        for (int j = 0; j < cartasAgrupadas.size() - 1; j++) {
                            for (int k = j + 1; k < cartasAgrupadas.size(); k++) {
                                String valorJ = cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1);
                                String valorK = cartasAgrupadas.get(k).substring(0, cartasAgrupadas.get(k).length() - 1);
                                if (Arrays.asList(valores).indexOf(valorK) > Arrays.asList(valores).indexOf(valorJ)) {
                                    String c = cartasAgrupadas.get(j);
                                    cartasAgrupadas.set(j, cartasAgrupadas.get(k));
                                    cartasAgrupadas.set(k, c);
                                }
                            }
                        }
                        kickers.add(new String[5]);
                        for (int j = 0; j < 5; j++) {
                            kickers.get(i)[j] = cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1);
                        }
                    }
                } else if (mejorCombinacion.equals("Par")) {
                    for (int i = 0; i < manosGanadoras.size(); i++) {
                        kickers.add(new String[5]);
                        kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 1];
                        kickers.get(i)[1] = kickers.get(i)[0];

                        ArrayList<String> cartasAgrupadas = new ArrayList<>();
                        for (int j = 0; j < manosGanadoras.get(i).length; j++) {
                            cartasAgrupadas.add(manosGanadoras.get(i)[j]);
                        }
                        for (int j = 0; j < mesa.size(); j++) {
                            cartasAgrupadas.add(mesa.get(j));
                        }
                        for (int j = 0; j < cartasAgrupadas.size() - 1; j++) {
                            for (int k = j + 1; k < cartasAgrupadas.size(); k++) {
                                String valorJ = cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1);
                                String valorK = cartasAgrupadas.get(k).substring(0, cartasAgrupadas.get(k).length() - 1);
                                if (Arrays.asList(valores).indexOf(valorK) > Arrays.asList(valores).indexOf(valorJ)) {
                                    String c = cartasAgrupadas.get(j);
                                    cartasAgrupadas.set(j, cartasAgrupadas.get(k));
                                    cartasAgrupadas.set(k, c);
                                }
                            }
                        }

                        ArrayList<String> valoresAgrupados = new ArrayList<>();
                        for (int j = 0; j < cartasAgrupadas.size(); j++) {
                            valoresAgrupados.add(cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1));
                        }
                        valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[0]));

                        for (int j = 0; j < 3; j++) {
                            kickers.get(i)[j + 2] = valoresAgrupados.get(j);
                        }
                    }
                } else if (mejorCombinacion.equals("Doble")) {
                    for (int i = 0; i < manosGanadoras.size(); i++) {
                        kickers.add(new String[5]);
                        kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 1];
                        kickers.get(i)[1] = kickers.get(i)[0];
                        kickers.get(i)[2] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 3];
                        kickers.get(i)[3] = kickers.get(i)[2];

                        for (int j = 0; j < kickers.get(i).length - 1; j++) {
                            for (int k = j + 1; k < kickers.get(i).length; k++) {
                                if (kickers.get(i)[k] != null && (kickers.get(i)[j] == null
                                        || Arrays.asList(valores).indexOf(kickers.get(i)[k]) > Arrays.asList(valores).indexOf(kickers.get(i)[j]))) {
                                    String c = kickers.get(i)[j];
                                    kickers.get(i)[j] = kickers.get(i)[k];
                                    kickers.get(i)[k] = c;
                                }
                            }
                        }

                        ArrayList<String> cartasAgrupadas = new ArrayList<>();
                        for (int j = 0; j < manosGanadoras.get(i).length; j++) {
                            cartasAgrupadas.add(manosGanadoras.get(i)[j]);
                        }
                        for (int j = 0; j < mesa.size(); j++) {
                            cartasAgrupadas.add(mesa.get(j));
                        }
                        for (int j = 0; j < cartasAgrupadas.size() - 1; j++) {
                            for (int k = j + 1; k < cartasAgrupadas.size(); k++) {
                                String valorJ = cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1);
                                String valorK = cartasAgrupadas.get(k).substring(0, cartasAgrupadas.get(k).length() - 1);
                                if (Arrays.asList(valores).indexOf(valorK) > Arrays.asList(valores).indexOf(valorJ)) {
                                    String c = cartasAgrupadas.get(j);
                                    cartasAgrupadas.set(j, cartasAgrupadas.get(k));
                                    cartasAgrupadas.set(k, c);
                                }
                            }
                        }

                        ArrayList<String> valoresAgrupados = new ArrayList<>();
                        for (int j = 0; j < cartasAgrupadas.size(); j++) {
                            valoresAgrupados.add(cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1));
                        }
                        final int k = i;
                        valoresAgrupados.removeIf(s -> s.equals(kickers.get(k)[0]));
                        valoresAgrupados.removeIf(s -> s.equals(kickers.get(k)[2]));
                        kickers.get(i)[4] = valoresAgrupados.get(0);
                    }
                } else if (mejorCombinacion.equals("Trio")) {
                    for (int i = 0; i < manosGanadoras.size(); i++) {
                        kickers.add(new String[5]);
                        kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 1];
                        kickers.get(i)[1] = kickers.get(i)[0];
                        kickers.get(i)[2] = kickers.get(i)[0];

                        ArrayList<String> cartasAgrupadas = new ArrayList<>();
                        for (int j = 0; j < manosGanadoras.get(i).length; j++) {
                            cartasAgrupadas.add(manosGanadoras.get(i)[j]);
                        }
                        for (int j = 0; j < mesa.size(); j++) {
                            cartasAgrupadas.add(mesa.get(j));
                        }
                        for (int j = 0; j < cartasAgrupadas.size() - 1; j++) {
                            for (int k = j + 1; k < cartasAgrupadas.size(); k++) {
                                String valorJ = cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1);
                                String valorK = cartasAgrupadas.get(k).substring(0, cartasAgrupadas.get(k).length() - 1);
                                if (Arrays.asList(valores).indexOf(valorK) > Arrays.asList(valores).indexOf(valorJ)) {
                                    String c = cartasAgrupadas.get(j);
                                    cartasAgrupadas.set(j, cartasAgrupadas.get(k));
                                    cartasAgrupadas.set(k, c);
                                }
                            }
                        }

                        ArrayList<String> valoresAgrupados = new ArrayList<>();
                        for (int j = 0; j < cartasAgrupadas.size(); j++) {
                            valoresAgrupados.add(cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1));
                        }
                        valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[0]));

                        for (int j = 0; j < 2; j++) {
                            kickers.get(i)[j + 3] = valoresAgrupados.get(j);
                        }
                    }
                } else if (mejorCombinacion.equals("Poker")) {
                    for (int i = 0; i < manosGanadoras.size(); i++) {
                        kickers.add(new String[5]);
                        kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 1];
                        kickers.get(i)[1] = kickers.get(i)[0];
                        kickers.get(i)[2] = kickers.get(i)[0];
                        kickers.get(i)[3] = kickers.get(i)[0];

                        ArrayList<String> cartasAgrupadas = new ArrayList<>();
                        for (int j = 0; j < manosGanadoras.get(i).length; j++) {
                            cartasAgrupadas.add(manosGanadoras.get(i)[j]);
                        }
                        for (int j = 0; j < mesa.size(); j++) {
                            cartasAgrupadas.add(mesa.get(j));
                        }
                        for (int j = 0; j < cartasAgrupadas.size() - 1; j++) {
                            for (int k = j + 1; k < cartasAgrupadas.size(); k++) {
                                String valorJ = cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1);
                                String valorK = cartasAgrupadas.get(k).substring(0, cartasAgrupadas.get(k).length() - 1);
                                if (Arrays.asList(valores).indexOf(valorK) > Arrays.asList(valores).indexOf(valorJ)) {
                                    String c = cartasAgrupadas.get(j);
                                    cartasAgrupadas.set(j, cartasAgrupadas.get(k));
                                    cartasAgrupadas.set(k, c);
                                }
                            }
                        }

                        ArrayList<String> valoresAgrupados = new ArrayList<>();
                        for (int j = 0; j < cartasAgrupadas.size(); j++) {
                            valoresAgrupados.add(cartasAgrupadas.get(j).substring(0, cartasAgrupadas.get(j).length() - 1));
                        }
                        valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[0]));
                        valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[2]));
                        kickers.get(i)[4] = valoresAgrupados.get(0);
                    }
                }

                String[] mejor = kickers.get(0);
                for (int i = 1; i < kickers.size(); i++) {
                    int comparacion = 0;
                    for (int j = 0; j < kickers.get(i).length; j++) {
                        int posA = -1;
                        int posB = -1;
                        for (int k = 0; k < valores.length; k++) {
                            if (valores[k].equals(kickers.get(i)[j])) {
                                posA = k;
                            }
                            if (valores[k].equals(mejor[j])) {
                                posB = k;
                            }
                        }
                        if (posA > posB) {
                            comparacion = 1;
                            break;
                        }
                        if (posA < posB) {
                            comparacion = -1;
                            break;
                        }
                    }
                    if (comparacion == 1) {
                        mejor = kickers.get(i);
                    }
                }

                Iterator<String[]> it = kickers.iterator();
                Iterator<Integer> jugs = combinaciones_jug.iterator();
                while (it.hasNext()) {
                    jugs.next();
                    String[] actual = it.next();
                    int comparacion = 0;
                    for (int j = 0; j < actual.length; j++) {
                        int posA = -1;
                        int posB = -1;
                        for (int k = 0; k < valores.length; k++) {
                            if (valores[k].equals(actual[j])) {
                                posA = k;
                            }
                            if (valores[k].equals(mejor[j])) {
                                posB = k;
                            }
                        }
                        if (posA > posB) {
                            comparacion = 1;
                            break;
                        }
                        if (posA < posB) {
                            comparacion = -1;
                            break;
                        }
                    }
                    if (comparacion != 0) {
                        it.remove();
                        jugs.remove();
                    }
                }
            } else {
                ArrayList<String[]> desempateManos = new ArrayList<>();
                for (int i = 0; i < combinaciones.size(); i++) {
                    if (mejorCombinacion.equals("Full")) {
                        desempateManos.add(new String[5]);
                        desempateManos.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 3];
                        desempateManos.get(i)[1] = desempateManos.get(i)[0];
                        desempateManos.get(i)[2] = desempateManos.get(i)[0];
                        desempateManos.get(i)[3] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 1];
                        desempateManos.get(i)[4] = desempateManos.get(i)[3];
                    } else if (mejorCombinacion.equals("Escalera") || mejorCombinacion.equals("Escalera_de_color")) {
                        desempateManos.add(new String[1]);
                        if (mejorCombinacion.equals("Escalera")) {
                            desempateManos.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 1];
                        } else {
                            String carta = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 1];
                            desempateManos.get(i)[0] = carta.substring(0, carta.length() - 1);
                        }
                    } else if (mejorCombinacion.equals("Color")) {
                        desempateManos.add(new String[5]);
                        for (int j = 0; j < desempateManos.get(i).length; j++) {
                            String carta = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length - 5 + j];
                            desempateManos.get(i)[j] = carta.substring(0, carta.length() - 1);
                        }
                        for (int j = 0; j < desempateManos.get(i).length - 1; j++) {
                            for (int k = j + 1; k < desempateManos.get(i).length; k++) {
                                if (Arrays.asList(valores).indexOf(desempateManos.get(i)[k]) > Arrays.asList(valores).indexOf(desempateManos.get(i)[j])) {
                                    String c = desempateManos.get(i)[j];
                                    desempateManos.get(i)[j] = desempateManos.get(i)[k];
                                    desempateManos.get(i)[k] = c;
                                }
                            }
                        }
                    }
                }

                String[] mejor = desempateManos.get(0);
                for (int i = 1; i < desempateManos.size(); i++) {
                    int comparacion = 0;
                    for (int j = 0; j < desempateManos.get(i).length; j++) {
                        int posA = -1;
                        int posB = -1;
                        for (int k = 0; k < valores.length; k++) {
                            if (valores[k].equals(desempateManos.get(i)[j])) {
                                posA = k;
                            }
                            if (valores[k].equals(mejor[j])) {
                                posB = k;
                            }
                        }
                        if (posA > posB) {
                            comparacion = 1;
                            break;
                        }
                        if (posA < posB) {
                            comparacion = -1;
                            break;
                        }
                    }
                    if (comparacion == 1) {
                        mejor = desempateManos.get(i);
                    }
                }

                Iterator<String[]> it = desempateManos.iterator();
                Iterator<Integer> jugs = combinaciones_jug.iterator();
                while (it.hasNext()) {
                    jugs.next();
                    String[] actual = it.next();
                    int comparacion = 0;
                    for (int j = 0; j < actual.length; j++) {
                        int posA = -1;
                        int posB = -1;
                        for (int k = 0; k < valores.length; k++) {
                            if (valores[k].equals(actual[j])) {
                                posA = k;
                            }
                            if (valores[k].equals(mejor[j])) {
                                posB = k;
                            }
                        }
                        if (posA > posB) {
                            comparacion = 1;
                            break;
                        }
                        if (posA < posB) {
                            comparacion = -1;
                            break;
                        }
                    }
                    if (comparacion != 0) {
                        it.remove();
                        jugs.remove();
                    }
                }
            }

            if (combinaciones_jug.size() > 1) {
                for (int i = 0; i < combinaciones_jug.size(); i++) {
                    if (i == 0) {
                        ganador = "" + combinaciones_jug.get(i);
                    } else {
                        ganador = ganador + ", " + combinaciones_jug.get(i);
                    }
                }
            } else {
                ganador = "" + combinaciones_jug.get(0);
            }
        }

        System.out.println(ganador);
    }
}
