import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ajedrez {
    private static Scanner sc = new Scanner (System.in);
    public static void main (String [] args) {
        Tablero tablero = new Tablero();
        String sPosPieza = "";
        String sNuevaPos = "";
        int [] posPieza = new int [2];
        int [] nuevaPos = new int [2];
        boolean valido;
        tablero.generarTablero();
        
        while (true) {
            tablero.mostrarTablero();
            System.out.println(tablero.turno ? "Turno: Blanco " : "Turno: Negro ");
                valido = false;
                while(!valido){
                    try {
                        System.out.print("Pos Pieza:");
                        sPosPieza = sc.nextLine();
                        posPieza[0] = Integer.parseInt(sPosPieza.split("")[1])-1;
                        posPieza[1] = valorLetra(sPosPieza.split("")[0].toUpperCase()   );

                        if (tablero.suPieza(new int [] {posPieza[0], posPieza[1]}) && tablero.pieza(posPieza).posiblesMovimientos(tablero.tablero).size()>0){
                            valido = true;
                        }
                    }catch (Exception e){
                        valido = false;
                    }
                }
                valido = false;
                while(!valido){
                    tablero.mostrarPosiblesMovimientos(posPieza);

                    try {
                        System.out.print("Pos Nueva:");
                        sNuevaPos = sc.nextLine();
                        nuevaPos[0] = Integer.parseInt(sNuevaPos.split("")[1])-1;
                        nuevaPos[1] = valorLetra(sNuevaPos.split("")[0]);

                        if (tablero.movimientoValido(posPieza, nuevaPos)){
                            valido = true;
                        }
                    }catch (Exception e){
                        valido = false;
                    }
                }

            tablero.moverPieza(posPieza, nuevaPos);
        }
        
    }
    private static int valorLetra (String letra) {
        String [] letras = {"A", "B", "C", "D", "E", "F", "G", "H"};
        return Arrays.asList(letras).indexOf(letra);
    }
    private static String transformarCoordenadas (int pos[]) {
        String [] letras = {"A", "B", "C", "D", "E", "F", "G", "H"};
        return letras[pos[1]]+""+(pos[0]+1);
    }
    private static class Tablero {
        private Pieza [][] tablero;
        private List<Pieza> negras;
        private List<Pieza> blancas;
        private boolean turno;

        public boolean movimientoValido(int [] pos, int [] nuevaPos) {
            List <int[]> movimientosPosibles = pieza(pos).posiblesMovimientos(tablero);
            boolean ok = false;
            int i = 0;

            while(!ok && i < movimientosPosibles.size()) {
                if (movimientosPosibles.get(i)[0]==nuevaPos[0] && movimientosPosibles.get(i)[1]==nuevaPos[1]) ok = true;
                else i++;
            }

            return ok;
        }
        public void moverPieza (int [] posPieza, int [] nuevaPos) {
            Pieza pieza = pieza(posPieza);
            Pieza piezaComida = tablero[nuevaPos[0]][nuevaPos[1]];

            tablero[posPieza[0]][posPieza[1]] = null;
            if (piezaComida != null) {
                negras.remove(piezaComida);
                blancas.remove(piezaComida);
            }
            tablero[nuevaPos[0]][nuevaPos[1]] = pieza;
            pieza.posicion = nuevaPos.clone();
            pieza.movida = true;
            turno = !turno;
            
        }
        private Pieza pieza (int [] pos) {
            return tablero[pos[0]][pos[1]];
        }
        public boolean suPieza (int [] pos) {
            Pieza pieza = tablero[pos[0]][pos[1]];
            if (pieza == null) return false;
            if (this.turno) return pieza.color.equals("B");
            else return pieza.color.equals("N");
        }
        public void mostrarPosiblesMovimientos(int [] pos) {
            mostrarPosiblesMovimientosTablero(pos);

            List<int[]> posiblesMovimientos = new ArrayList<>();
            Pieza pieza = pieza(pos);
            posiblesMovimientos = pieza.posiblesMovimientos(tablero);

            for (int i = 0; i < posiblesMovimientos.size(); i++) {
                System.out.print(transformarCoordenadas(posiblesMovimientos.get(i))+" ");
            }
            System.out.println();
        }
        private void mostrarPosiblesMovimientosTablero (int [] posPieza) {
            String [][] tableroTemporal = new String[tablero.length][tablero.length];
            for (int i = 0; i < tablero.length; i++) {
                for (int j = 0; j < tablero.length; j++) {
                    tableroTemporal[i][j] = textoCasilla(tablero[i][j]);
                }
            }            
            List<int[]> posMov = pieza(posPieza).posiblesMovimientos(this.tablero);

            for (int i = 0; i < posMov.size(); i++) {
                if (!tableroTemporal[posMov.get(i)[0]][posMov.get(i)[1]].equals("--")) tableroTemporal[posMov.get(i)[0]][posMov.get(i)[1]] = "XX";
                else tableroTemporal[posMov.get(i)[0]][posMov.get(i)[1]] = "xx";
            }

            mostrar(tableroTemporal);
        }
        private void mostrar(String [][] tablero){
            String [] letras = {"A", "B", "C", "D", "E", "F", "G", "H"};
            for (int i = 0; i < tablero.length; i++) {
                System.out.println();
                System.out.print(9-(i+1)+"  ");
                for (int j = 0; j < tablero.length; j++) {
                    System.out.print(colorTexto(tablero[i][j])+" ");
                }
            }
            System.out.print("\n\n ");
            for (int i = 0; i < letras.length; i++) {
                System.out.print("  "+letras[i]);
            }
            System.out.println();

        }
        public void mostrarTablero() {
            String [][] tableroTexto = new String[tablero.length][tablero.length];
            for (int i = 0; i < tablero.length; i++) {
                for (int j = 0; j < tablero.length; j++) {
                    tableroTexto[i][j] = textoCasilla(tablero[i][j]);
                }
            }
            mostrar(tableroTexto);
        }
        public void generarTablero() {
            this.tablero = new Pieza[8][8];
            this.turno = true;
            generarPiezas();

            for (int i = 0; i < tablero.length; i++) {
                for (int j = 0; j < tablero.length; j++) {
                    tablero[i][j] = null; 
                }
            }            
            Pieza negra;
            Pieza blanca;
            for (int i = 0; i < negras.size(); i++) {
                negra = negras.get(i);
                blanca = blancas.get(i);
                tablero[negra.posicion[0]][negra.posicion[1]] = negra;
                tablero[blanca.posicion[0]][blanca.posicion[1]] = blanca;
            }    
        }
        private String textoCasilla(Pieza pieza) {
            if (pieza == null) return "--";
            return pieza.nombre + pieza.color;
        }
        private String colorTexto(String casilla) {
            if (casilla.equals("--") || casilla.equals("xx") || casilla.equals("XX")) return casilla;
            return casilla.substring(1, 2).equals("N") ? "\u001B[30m" + casilla + "\u001B[0m" : casilla;
        }

        private void generarPiezas () {
            String[] piezas = {"T", "C", "A", "D", "R", "A", "C", "T", "P", "P", "P", "P", "P", "P", "P", "P"};
            List<Pieza> negras = new ArrayList <Pieza>();
            List<Pieza> blancas = new ArrayList <Pieza>();

            for (int i = 0; i < piezas.length; i++) {
                negras.add(new Pieza(piezas[i], "N", new int []{i/8,i%8}));
                blancas.add(new Pieza(piezas[i], "B", new int []{7-(i/8),i%8}));
            }

            this.negras = negras;
            this.blancas = blancas;
        }

    }
    private static class Pieza {
        private String nombre;
        private String color;
        private int [] posicion;
        private boolean movida;

        private Pieza (String nombre, String color, int [] posicion){
            this.nombre = nombre;
            this.color = color;
            this.posicion = posicion;
            this.movida = false;
        }
        public List<int[]> posiblesMovimientos (Pieza [][] tablero) {
            List<int[]> posiblesMovimientos = new ArrayList<>();
            String colorEnemigo = color.equals("B") ? "N" : "B";
            
            if (nombre.equals("P")) movimientosPeon(tablero, posiblesMovimientos, colorEnemigo);
            else if (nombre.equals("C")) movimientosCaballo(tablero, posiblesMovimientos, colorEnemigo);
            else if (nombre.equals("T")) movimientosTorre(tablero, posiblesMovimientos, colorEnemigo);
            else if (nombre.equals("A")) movimientosAlfil(tablero, posiblesMovimientos, colorEnemigo);
            else if (nombre.equals("D")) movimientosDama(tablero, posiblesMovimientos, colorEnemigo);
            else if (nombre.equals("R")) movimientosRey(tablero, posiblesMovimientos, colorEnemigo);

            return posiblesMovimientos;
        }
        private void movimientosRey(Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
            
                for (int i = -1; i < 2; i++) {
                    try {
                        if (tablero[posicion[0]+i][posicion[1]-1] == null || !tablero[posicion[0]+i][posicion[1]-1].color.equals(color)) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]-1});
                    } catch (Exception e){}
                    try {
                        if (tablero[posicion[0]+i][posicion[1]+1] == null || !tablero[posicion[0]+i][posicion[1]+1].color.equals(color)) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+1});
                    } catch (Exception e){}
                }
                try {
                    if (tablero[posicion[0]+1][posicion[1]] == null || !tablero[posicion[0]+1][posicion[1]].color.equals(color)) posiblesMovimientos.add(new int[]{posicion[0]+1, posicion[1]});
                } catch (Exception e){}
                try {
                    if (tablero[posicion[0]-1][posicion[1]] == null || !tablero[posicion[0]-1][posicion[1]].color.equals(color)) posiblesMovimientos.add(new int[]{posicion[0]-1, posicion[1]});
                } catch (Exception e){}

        }
        private void movimientosDama(Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
            movimientosDiagonalAbajoDerecha(tablero, posiblesMovimientos, colorEnemigo);
            movimientosDiagonalAbajoIzquierda(tablero, posiblesMovimientos, colorEnemigo);
            movimientosAbajoDerecha(tablero, posiblesMovimientos, colorEnemigo);
            movimientosArribaIzquierda(tablero, posiblesMovimientos, colorEnemigo);
        }
        private void movimientosAlfil(Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
            movimientosDiagonalAbajoDerecha(tablero, posiblesMovimientos, colorEnemigo);
            movimientosDiagonalAbajoIzquierda(tablero, posiblesMovimientos, colorEnemigo);
        }
        private void movimientosTorre (Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
            movimientosAbajoDerecha(tablero, posiblesMovimientos, colorEnemigo);
            movimientosArribaIzquierda(tablero, posiblesMovimientos, colorEnemigo);
        }
        private void movimientosCaballo (Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
            int [][] caballoMovimientos = {{+2,-1},{+2,+1},{-2,-1},{-2,+1},{-1,-2},{-1,+2},{+1,-2},{+1,+2}};
            for (int i = 0; i < caballoMovimientos.length; i++) {
                try{
                    Pieza destino = tablero[posicion[0] + caballoMovimientos[i][0]][posicion[1] + caballoMovimientos[i][1]];
                    if (destino == null || destino.color.equals(colorEnemigo)) {
                        posiblesMovimientos.add(new int[]{posicion[0] + caballoMovimientos[i][0], posicion[1] + caballoMovimientos[i][1]});
                    }
                }catch (Exception e){
                    
                }
            }
        }
        private void movimientosPeon (Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
                int mov = 1;
                if (color.equals("B")) mov = mov * -1;
                if (tablero[this.posicion[0]+mov][this.posicion[1]] == null) posiblesMovimientos.add(new int[]{this.posicion[0]+mov,this.posicion[1]});

                //SI HAY PIEZA PARA COMER
                if (posicion[1]>0) {
                    if (tablero[this.posicion[0]+mov][this.posicion[1]-1] != null && tablero[this.posicion[0]+mov][this.posicion[1]-1].color.equals(colorEnemigo)) posiblesMovimientos.add(new int[]{this.posicion[0]+mov, this.posicion[1]-1});
                }
                if (posicion[1]<7){
                    if (tablero[this.posicion[0]+mov][this.posicion[1]+1] != null && tablero[this.posicion[0]+mov][this.posicion[1]+1].color.equals(colorEnemigo)) posiblesMovimientos.add(new int[]{this.posicion[0]+mov, this.posicion[1]+1});
                }
                
                if (!movida && tablero[this.posicion[0]+mov][this.posicion[1]] == null) { //ESTA EN EL INICIO
                    
                    mov = 2;
                    if (color.equals("B")) mov = mov * -1;
                    if (tablero[this.posicion[0]+mov][this.posicion[1]] == null) posiblesMovimientos.add(new int[]{this.posicion[0]+mov,this.posicion[1]});
                }
        }
        private void movimientosDiagonalAbajoDerecha(Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo){
            boolean topeV = false;
            boolean topeH = false;

            int i = 1;
            int j = 1;
            while (!topeV || !topeH)  {

                    //ABAJO-DERECHA
                try {
                    if (tablero[posicion[0]+i][posicion[1]+j] == null && !topeV) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(colorEnemigo) && !topeV) {
                        posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                        topeV=true;
                    }
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(color) && !topeV) topeV = true;
                } catch (Exception e) {
                    topeV = true;
                }

                i = i * -1;
                j = j * -1;
              try { //ARRIBA-IZQUIERDA
                    if (tablero[posicion[0]+i][posicion[1]+j] == null && !topeH) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(colorEnemigo) && !topeH) {
                        posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                        topeH=true;
                    }
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(color) && !topeH) topeH = true;
                } catch (Exception e) {
                    topeH = true;
                }         
                i = i * -1;
                j = j * -1;
                i++;
                j++;
            }
        }
        private void movimientosDiagonalAbajoIzquierda(Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo){
            boolean topeV = false;
            boolean topeH = false;

            int i = 1;
            int j = -1;
            while (!topeV || !topeH)  {

                    //ABAJO-DERECHA
                try {
                    if (tablero[posicion[0]+i][posicion[1]+j] == null && !topeV) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(colorEnemigo) && !topeV) {
                        posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                        topeV=true;
                    }
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(color) && !topeV) topeV = true;
                } catch (Exception e) {
                    topeV = true;
                }

                i = i * -1;
                j = j * -1;
              try { //ARRIBA-IZQUIERDA
                    if (tablero[posicion[0]+i][posicion[1]+j] == null && !topeH) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(colorEnemigo) && !topeH) {
                        posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]+j});
                        topeH=true;
                    }
                    else if (tablero[posicion[0]+i][posicion[1]+j].color.equals(color) && !topeH) topeH = true;
                } catch (Exception e) {
                    topeH = true;
                }         
                i = i * -1;
                j = j * -1;
                i++;
                j--;
            }
        }

        private void movimientosAbajoDerecha (Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
            int i = 0;
            boolean topeV = false;
            boolean topeH = false;

            i = +1;
            while ((!topeV || !topeH) && (i+posicion[0] < tablero.length || i+posicion[1] < tablero.length) ) {

                    //ABAJO
                try {
                    if (tablero[posicion[0]+i][posicion[1]] == null && !topeV) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]});
                    else if (tablero[posicion[0]+i][posicion[1]].color.equals(colorEnemigo) && !topeV) {
                        posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]});
                        topeV=true;
                    }
                    else if (tablero[posicion[0]+i][posicion[1]].color.equals(color) && !topeV) topeV = true;
                } catch (Exception e) {
                    topeV = true;
                }

                try {   //DERECHA
                    if (tablero[posicion[0]][posicion[1]+i] == null && !topeH) posiblesMovimientos.add(new int[]{posicion[0], posicion[1]+i});
                    else if (tablero[posicion[0]][posicion[1]+i].color.equals(colorEnemigo) && !topeH) {
                        posiblesMovimientos.add(new int[]{posicion[0], posicion[1]+i});
                        topeH=true;
                    }
                    else if (tablero[posicion[0]][posicion[1]+i].color.equals(color) && !topeH) topeH = true;
                } catch (Exception e){
                    topeH=true;
                }        
                

                i++;
            }
        }
        private void movimientosArribaIzquierda (Pieza [][] tablero, List<int[]> posiblesMovimientos, String colorEnemigo) {
            boolean topeV = false;
            boolean topeH = false;
            int i = -1;

            while ((!topeV || !topeH) && (i+posicion[0] >= 0 || i+posicion[1] >= 0)) {
                    //ARRIBA
                try {
                    if (tablero[posicion[0]+i][posicion[1]] == null && !topeV) posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]});
                    else if (tablero[posicion[0]+i][posicion[1]].color.equals(colorEnemigo) && !topeV) {
                        posiblesMovimientos.add(new int[]{posicion[0]+i, posicion[1]});
                        topeV=true;
                    }
                    else if (tablero[posicion[0]+i][posicion[1]].color.equals(color) && !topeV) topeV = true;
                }catch(Exception e) {
                    topeV = true;
                }
                    
                    //IZQUIERDA
                try {
                    if (tablero[posicion[0]][posicion[1]+i] == null && !topeH) posiblesMovimientos.add(new int[]{posicion[0], posicion[1]+i});
                    else if (tablero[posicion[0]][posicion[1]+i].color.equals(colorEnemigo) && !topeH) {
                        posiblesMovimientos.add(new int[]{posicion[0], posicion[1]+i});
                        topeH=true;
                    }
                    else if (tablero[posicion[0]][posicion[1]+i].color.equals(color) && !topeH) topeH = true;
                }catch (Exception e){
                    topeH = true;
                }
                i--;
            }

        }

    }
}
