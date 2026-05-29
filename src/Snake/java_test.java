import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class java_test {
    static Random random = new Random();
    public static void main(String[] args) {
        String [][] tablero = new String[9][9];
        int alcanceAtaque = 2;
        int saltoAtaque = 0;
        int adyacenteAtaque = 0;

        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero.length; j++) {
                tablero[i][j] = " O ";
            }
        }
        int f = 5;
        int c = 3;
        Pieza pieza = new Pieza(f, c);
        pieza.movimiento = 2;

        tablero[f][c] = ("\u001B[34m A \u001B[0m");
        tablero[5][4] = ("\u001B[34m B \u001B[0m"); 
            int maxF = 0;
            int sumMax = pieza.acciones < 2 ? 1 : 2;
            for (int i = -pieza.movimiento-(sumMax-1); i < pieza.movimiento+sumMax; i++) {
                maxF = pieza.movimiento - (i < 0 ? i*-1 : i)+sumMax;
                for (int k = 0; k < maxF; k++) {
                    if (!(i == 0 && k == 0)) {
                        if ((0 <= pieza.f+k && pieza.f+k < tablero.length) && (0 <= pieza.c+i && pieza.c+i < tablero.length)) {
                            if (!tablero[pieza.f+k][pieza.c+i].equals("\u001B[34m B \u001B[0m")) {
                                tablero[pieza.f+k][pieza.c+i] = "\u001B[31m M \u001B[0m";
                                pieza.movimientosPosibles.add(new Integer[]{pieza.f+k,pieza.c+i});
                            }
                            else {
                                int disF = f - pieza.f+k;
                                int disC = c - pieza.c+i;

                                disF = maxF - disF;
                                disC = i - disC;
                                maxF -= disF;
                                i -= disC;

                                System.out.println(disF+" "+disC+"---"+maxF+" "+i);
                            }
                        }
                        if ((0 <= pieza.f-k && pieza.f-k < tablero.length) && (0 <= pieza.c+i && pieza.c+i < tablero.length) && k != 0) {
                            if (!tablero[pieza.f-k][pieza.c+i].equals("\u001B[34m B \u001B[0m")) {
                                tablero[pieza.f-k][pieza.c+i] = "\u001B[31m M \u001B[0m";
                                pieza.movimientosPosibles.add(new Integer[]{pieza.f-k, pieza.c+i});
                            }
                            else {
                                int disF = f - pieza.f-k;
                                int disC = c - pieza.c+i;
                                
                                disF = maxF - disF;
                                disC = i - disC;
                                maxF -= disF;
                                i -= disC;

                                System.out.println(disF+" "+disC+"---"+maxF+" "+i);
                            }
                        }
                    }
                }
            }            

        imp(tablero);
        System.out.println();

        // pieza.movimientosPosibles.sort((a, b) -> a[0] - b[0]);        
        // for (int i = 0; i < pieza.movimientosPosibles.size(); i++) {
        //     System.out.println(pieza.movimientosPosibles.get(i)[0]+" "+pieza.movimientosPosibles.get(i)[1]);
        // }

    }
    static void imp (String [][] tablero) {
        for (int i = 0; i < tablero.length; i++) {
            System.out.println();
            for (int j = 0; j < tablero.length; j++) {
                System.out.print(tablero[i][j]);
            }
        }
    }
    static class Pieza {
        int f;
        int c;
        List<Integer[]> movimientosPosibles = new ArrayList<>();
        int acciones = 2;
        int movimiento;
        public Pieza (int f, int c) {
            this.f = f;
            this.c = c;
        }
    }
}






        // for (int i = -alcanceAtaque; i < alcanceAtaque+1; i++) {
        //     System.out.println(c+"+"+i+" != "+saltoAtaque+"+"+c+" && "+c+"+"+i+" != "+saltoAtaque+"-"+c+")"); //c = izquierda/derecha, f = arriba/abajo
        //     if (i != 0 && (!(c <= c+i && c+i <= c+saltoAtaque) && !(c-saltoAtaque <= c+i && c+i <= c))) tablero[f][c+i] = "\u001B[31m Ñ \u001B[0m";
        //     if (i != 0 && (!(f <= f+i && f+i <= f+saltoAtaque) && !(f-saltoAtaque <= f+i && f+i <= f))) tablero[f+i][c] = "\u001B[31m Ñ \u001B[0m";
        // }
