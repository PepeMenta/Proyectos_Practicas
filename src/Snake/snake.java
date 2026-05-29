import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.*;

public class snake extends JPanel implements KeyListener {
    static final int TAMAÑO_TABLA = 9;
    public static void main (String [] args){
        Tabla tabla = new Tabla();
        tabla.generarTabla();
        tabla.generarManzana();
        tabla.mostrarTabla();

        juego(tabla);
    }
    private static void juego (Tabla tabla) {
        Scanner sc = new Scanner (System.in);
        String d;
        while (true) {
            try {
                Thread.sleep(1000);
                tabla.moverSnake();
                tabla.mostrarTabla();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    private static class Snake {
        private ArrayList <int[]> pos = new ArrayList<int[]>();
        private int tamaño = 1;
        private String direccion = "s";

        public Snake () {
            pos.add(new int[]{1,5});
        }

        void mover () {
            if (direccion.equals("s")){
                pos.set(0, new int []{pos.get(0)[0]+1,pos.get(0)[1]});
            }
            else if (direccion.equals("w")){
                pos.set(0, new int []{pos.get(0)[0]-1,pos.get(0)[1]});
            }
            else if (direccion.equals("a")){
                pos.set(0, new int []{pos.get(0)[0],pos.get(0)[1]-1});
            }
            else if (direccion.equals("d")){
                pos.set(0, new int []{pos.get(0)[0]+1,pos.get(0)[1]+1});
            }
        }
    }
    private static class Tabla {
        String [][] tabla = new String[TAMAÑO_TABLA][TAMAÑO_TABLA];
        ArrayList <int []> coordenadas = new ArrayList<>();
        int [] coordenada = new int[2];
        Snake snake;

        void mostrarTabla () {
            System.out.println();
            for (int i = 0; i < tabla.length; i++) {
                System.out.print("---");
            }
            for (int i = 0; i < tabla.length; i++) {
                System.out.print("\n|");
                for (int j = 0; j < tabla.length; j++) {
                    System.out.print(tabla[i][j]+"|");
                }
            }
            System.out.println();
            for (int i = 0; i < tabla.length; i++) {
                System.out.print("---");
            }
        }

        void generarTabla() {
            for (int i = 0; i < tabla.length; i++) {
                for (int j = 0; j < tabla.length; j++) {
                    tabla[i][j] = "  ";
                    coordenadas.add(new int[]{i, j});
                }
            }
            this.snake = new Snake();
            tabla[snake.pos.get(0)[0]][snake.pos.get(0)[1]]= "\uD83D\uDC0D";
        }
        
        void clear () {
            tabla = new String[TAMAÑO_TABLA][TAMAÑO_TABLA];
            coordenadas.clear();
        }

        void generarManzana() {
            Random random = new Random();
            coordenada = coordenadas.remove(random.nextInt(coordenadas.size()));
            tabla[coordenada[0]][coordenada[1]] = "\uD83C\uDF4E";
        }

        void moverSnake () {
            tabla[snake.pos.get(0)[0]][snake.pos.get(0)[1]]="  "; 
            snake.mover();
            tabla[snake.pos.get(0)[0]][snake.pos.get(0)[1]]="\uD83D\uDC0D"; 
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
    }
    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }
}
