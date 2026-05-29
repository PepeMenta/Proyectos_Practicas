package Objects.Horizon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import Objects.Horizon.*;

    public class Tablero  {
        static Scanner sc=new Scanner (System.in);
        static Random random = new Random();
        public Casilla [][] tablero = new Casilla[8][8];

        public Tablero () {
            generarTablero();
        }
        public Tablero (Tablero tablero) {
            for (int i = 0; i < this.tablero.length; i++) {
                for (int j = 0; j < this.tablero.length; j++) {
                    this.tablero[i][j] = new Casilla(tablero.tablero[i][j]);
                }
            }
        }

        public boolean colocarCorrecto (String coordenadas) {
            int[] coor = procesarCoordenadas(coordenadas);
            boolean correcto = false;

            if (coor != null) {
                if ((coor[0] == 7 || coor[0] == 6) && (0 <= coor[1] && coor[1] <= 7)) {
                    correcto = true;
                }
            }
            return correcto;
        }
        private void clear() {
            try {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
        public boolean mostrarAtaquesPosibles (Pieza pieza) {
            boolean puedeAtacar = false;
            ataquesPosibles(pieza);
            Casilla [][] tableroTemporal = new Casilla[tablero.length][tablero.length];

            for (int i = 0; i < tableroTemporal.length; i++) {
                for (int j = 0; j < tableroTemporal.length; j++) {
                    tableroTemporal[i][j] = new Casilla (tablero[i][j]);
                }
            }

            for (int i = 0; i < pieza.ataquesPosibles.size(); i++) {
                int fPos = pieza.ataquesPosibles.get(i)[0];
                int cPos = pieza.ataquesPosibles.get(i)[1];
                if (tablero[fPos][cPos].pieza != null) {
                    tableroTemporal[fPos][cPos].pieza = new Pieza("💥");
                    puedeAtacar = true;
                }
                else tableroTemporal[fPos][cPos].pieza = new Pieza("⚔️ ");
            }
            sc.nextLine();
            clear();
            mostrarTablero(tableroTemporal);
            pieza.mostrarPieza();
            return puedeAtacar;
        }
        public void mostrarMovimientosPosibles (Pieza pieza, int [] coor) {
            movimientosPosibles(pieza);
            Casilla [][] tableroTemporal = new Casilla[tablero.length][tablero.length];
            int fPos;
            int cPos;

            for (int i = 0; i < tableroTemporal.length; i++) {
                for (int j = 0; j < tableroTemporal.length; j++) {
                    tableroTemporal[i][j] = new Casilla (tablero[i][j]);
                }
            }

            for (int i = 0; i < pieza.movimientosPosibles.size(); i++) {
                fPos = pieza.movimientosPosibles.get(i)[0];
                cPos = pieza.movimientosPosibles.get(i)[1];
                tableroTemporal[fPos][cPos].pieza = distanciaDesplazada(pieza.f, pieza.c, fPos, cPos) == pieza.movimiento + 1 ? new Pieza("🚀") : new Pieza("🐾");
            }
            clear();
            tableroTemporal[coor[0]][coor[1]].pieza = new Pieza("❌");
            mostrarTablero(tableroTemporal);
            pieza.mostrarPieza();
            
        }
        public void mostrarMovimientosPosibles (Pieza pieza) {
            movimientosPosibles(pieza);
            Casilla [][] tableroTemporal = new Casilla[tablero.length][tablero.length];
            int fPos;
            int cPos;

            for (int i = 0; i < tableroTemporal.length; i++) {
                for (int j = 0; j < tableroTemporal.length; j++) {
                    tableroTemporal[i][j] = new Casilla (tablero[i][j]);
                }
            }

            for (int i = 0; i < pieza.movimientosPosibles.size(); i++) {
                fPos = pieza.movimientosPosibles.get(i)[0];
                cPos = pieza.movimientosPosibles.get(i)[1];
                tableroTemporal[fPos][cPos].pieza = distanciaDesplazada(pieza.f, pieza.c, fPos, cPos) == pieza.movimiento + 1 ? new Pieza("🚀") : new Pieza("🐾");
            }
            clear();
            mostrarTablero(tableroTemporal);
            pieza.mostrarPieza();
            
        }
        public Pieza piezaPorCoordenadas (String coordenadas) {
            return tablero[Integer.parseInt(coordenadas.split(" ")[0])][Integer.parseInt(coordenadas.split(" ")[1])].pieza;
        }
        public Pieza piezaPorCoordenadas (int f, int c) {
            return tablero[f][c].pieza;
        }
        public void recuperarAccionesPiezas () {
            for (int i = 0; i < tablero.length; i++) {
                for (int j = 0; j < tablero.length; j++) {
                    if (tablero[i][j].pieza != null) {
                        tablero[i][j].pieza.acciones = tablero[i][j].pieza.accionesMAX;
                        tablero[i][j].pieza.movido = false;
                        tablero[i][j].pieza.ataco = false;
                    }
                }
            }
        }
        public int distanciaDesplazada (int f, int c, int fDes, int cDes) {
            if (f == fDes && c == cDes) return 0;
            if (f < 0 || f >= tablero.length || c < 0 || c >= tablero[0].length || fDes < 0 || fDes >= tablero.length || cDes < 0 || cDes >= tablero[0].length) return -1;

            boolean [][] visitado = new boolean[tablero.length][tablero[0].length];
            List<Integer[]> pendientes = new ArrayList<>();

            pendientes.add(new Integer[]{f, c, 0});
            visitado[f][c] = true;

            for (int i = 0; i < pendientes.size(); i++) {
                Integer[] actual = pendientes.get(i);
                int fActual = actual[0];
                int cActual = actual[1];
                int distancia = actual[2];

                int distanciaEncontrada = distanciaDesplazadaSiguiente(pendientes, visitado, fActual + 1, cActual, fDes, cDes, distancia + 1);
                if (distanciaEncontrada != -1) return distanciaEncontrada;
                distanciaEncontrada = distanciaDesplazadaSiguiente(pendientes, visitado, fActual - 1, cActual, fDes, cDes, distancia + 1);
                if (distanciaEncontrada != -1) return distanciaEncontrada;
                distanciaEncontrada = distanciaDesplazadaSiguiente(pendientes, visitado, fActual, cActual + 1, fDes, cDes, distancia + 1);
                if (distanciaEncontrada != -1) return distanciaEncontrada;
                distanciaEncontrada = distanciaDesplazadaSiguiente(pendientes, visitado, fActual, cActual - 1, fDes, cDes, distancia + 1);
                if (distanciaEncontrada != -1) return distanciaEncontrada;
            }

            return -1;
        }
        private int distanciaDesplazadaSiguiente(List<Integer[]> pendientes, boolean [][] visitado, int f, int c, int fDes, int cDes, int distancia) {
            if (f < 0 || f >= tablero.length || c < 0 || c >= tablero[0].length || visitado[f][c]) return -1;

            visitado[f][c] = true;
            if (f == fDes && c == cDes) return distancia;
            if (tablero[f][c].pieza != null) return -1;

            pendientes.add(new Integer[]{f, c, distancia});
            return -1;
        }
        public String procesarCoordenadasLectura (String coordenadas) {
            int [] coor = new int [2];
            try {
                coor[0] = (8-Integer.parseInt(coordenadas.split(" ")[0]));
                coor[1] = Integer.parseInt(coordenadas.split(" ")[1])+1;
            } catch (Exception e) {
                coor = null;
            }

            return coor[0]+" "+coor[1];
        }
        public int[] procesarCoordenadas (String coordenadas) {
            int [] coor = new int [2];

            try {
                coor[0] = 8-Integer.parseInt(coordenadas.split(" ")[0]);
                coor[1] = Integer.parseInt(coordenadas.split(" ")[1])-1;
            } catch (Exception e) {
                coor = null;
            }

            return coor;
        }
        public boolean esPieza (String coordenadas) {
            boolean esPieza = true;
            int f;
            int c;

            try {
                f = 8-Integer.parseInt(coordenadas.split(" ")[0]);
                c = Integer.parseInt(coordenadas.split(" ")[1])-1;

                if (!tablero[f][c].tienePieza()) esPieza = false;
            } catch (Exception e) {
                esPieza = false;
            }

            return esPieza;
        }
        public void usarPieza (Pieza pieza) {
            String coordenada = "";
            
            do {
                clear();
                mostrarTablero();
                pieza.mostrarPieza();
                System.out.println("\n\n1. Moverte\n2. Atacar\n0. Salir");
                do{
                    coordenada = sc.nextLine();
                }while(!coordenada.equals("1") && !coordenada.equals("2") && !coordenada.equals("0"));
                
                switch (coordenada) {
                    case "1":
                        mover(coordenada, pieza);
                        break;
                
                    case "2":
                        atacar(pieza);
                        break;
                }

                sc.nextLine();
            }while(!coordenada.equals("0") && !pieza.noMasAcciones()) ;
        }
        private void atacar (Pieza pieza) {
            if (mostrarAtaquesPosibles(pieza) && !pieza.ataco) {
                String opcion = "";

                System.out.print("\n: ");
                do{
                    opcion = sc.nextLine();
                }while(!ataqueCorrecto(opcion, pieza) && !opcion.equals("0"));

                if (!opcion.equals("0")) gestionDatosTrasAtaque(opcion, pieza);
            }
            sc.nextLine();
        }
        public void gestionDatosTrasAtaque (String opcion, Pieza pieza) {
            int [] coor = procesarCoordenadas(opcion);
            Pieza enemigo = tablero[coor[0]][coor[1]].pieza;
            pieza.acciones--;
            pieza.ataco = true;
            pieza.movido = false;
            realizarDaño(pieza, enemigo);
        }
        private void realizarDaño (Pieza atacante, Pieza recibidor) {
            int daño = atacante.ataque;
            daño += tablero[atacante.f][atacante.c].habitat.altura;
            daño += atacante.afinidadHabitat(tablero[atacante.f][atacante.c].habitat);
            
            daño -= tablero[recibidor.f][recibidor.c].habitat.altura;
            daño -= recibidor.afinidadHabitat(tablero[recibidor.f][recibidor.c].habitat);
            if (daño < 0) daño = 0;
            recibidor.vida = recibidor.vida - daño < 0 ? 0 : recibidor.vida-daño;
    
            System.out.println(recibidor.nombre+" -"+daño+"❤️");
            if (recibidor.vida == 0) {
                tablero[recibidor.f][recibidor.c].pieza = null;
                System.out.println(recibidor.nombre+" ha sido debilitado!!");
            }
        }
        private void mover (String coordenada, Pieza pieza) {
            mostrarMovimientosPosibles(pieza);

            if (!pieza.movido) {
                System.out.print("\n: ");
                do{
                    coordenada = sc.nextLine();
                }while(!movimientoCorrecto(coordenada, pieza) && !coordenada.equals("0"));

                if (!coordenada.equals("0")) gestionDatosTrasMover(procesarCoordenadas(coordenada), pieza);
            }
            else sc.nextLine();
        }
        public void gestionDatosTrasMover (int [] coor, Pieza pieza) {
            tablero[pieza.f][pieza.c].pieza = null;
            tablero[coor[0]][coor[1]].pieza = pieza;
            pieza.acciones -= distanciaDesplazada(pieza.f, pieza.c, coor[0], coor[1]) == pieza.movimiento + 1 ? 2 : 1;
            pieza.setCoordenadas(coor[0], coor[1]);
            pieza.movido = true;
            pieza.ataco = false;
            movimientosPosibles(pieza);
            ataquesPosibles(pieza);
        }
        private boolean ataqueCorrecto (String coordenada, Pieza pieza) {
            boolean correcto = false;
            int x = 0;
            int [] coor = procesarCoordenadas(coordenada);

            while (!correcto && x < pieza.ataquesPosibles.size() && coor != null) {
                if (coor[0] == pieza.ataquesPosibles.get(x)[0] && coor[1] == pieza.ataquesPosibles.get(x)[1] && tablero[coor[0]][coor[1]].pieza != null) {
                    correcto = true;
                }
                else x++;
            }

            return correcto;
        } 
        private boolean movimientoCorrecto (String coordenada,Pieza pieza) {
            boolean correcto = false;
            int x = 0;
            int [] coor = procesarCoordenadas(coordenada);

            while (!correcto && x < pieza.movimientosPosibles.size() && coor != null) {
                if (coor[0] == pieza.movimientosPosibles.get(x)[0] && coor[1] == pieza.movimientosPosibles.get(x)[1]) {
                    correcto = true;
                }
                else x++;
            }

            return correcto;
        }
        public void ataquesPosibles (Pieza p) {
            p.ataquesPosibles.clear();
            if (p.acciones <= 0 || !p.estaVivo() || p.f < 0 || p.c < 0) return;

            agregarAtaquesEnDireccion(p, 1, 0);
            agregarAtaquesEnDireccion(p, -1, 0);
            agregarAtaquesEnDireccion(p, 0, 1);
            agregarAtaquesEnDireccion(p, 0, -1);
        }
        private void agregarAtaquesEnDireccion(Pieza p, int direccionF, int direccionC) {
            int perpendicularF = direccionC;
            int perpendicularC = direccionF;

            for (int separacion = -p.adyacenteAtaque; separacion <= p.adyacenteAtaque; separacion++) {
                for (int distancia = 1; distancia <= p.alcanceAtaque; distancia++) {
                    int f = p.f + direccionF * distancia + perpendicularF * separacion;
                    int c = p.c + direccionC * distancia + perpendicularC * separacion;

                    if (f < 0 || f >= tablero.length || c < 0 || c >= tablero[0].length) break;

                    Pieza piezaEnCasilla = tablero[f][c].pieza;
                    if (distancia <= p.saltoAtaque) continue;

                    if (piezaEnCasilla == null) {
                        agregarAtaqueSiNoExiste(p, f, c);
                    }
                    else {
                        if (piezaEnCasilla.aliado != p.aliado) agregarAtaqueSiNoExiste(p, f, c);
                        break;
                    }
                }
            }
        }
        private void agregarAtaqueSiNoExiste(Pieza p, int f, int c) {
            for (int i = 0; i < p.ataquesPosibles.size(); i++) {
                if (p.ataquesPosibles.get(i)[0] == f && p.ataquesPosibles.get(i)[1] == c) return;
            }

            p.ataquesPosibles.add(new Integer[]{f, c});
        }
        public void movimientosPosibles (Pieza pieza) {
            pieza.movimientosPosibles.clear();
            if (pieza.acciones <= 0 || pieza.movido || !pieza.estaVivo() || pieza.f < 0 || pieza.c < 0) return;

            int movimientoMaximo = pieza.movimiento + (pieza.acciones < 2 ? 0 : 1);
            boolean [][] visitado = new boolean[tablero.length][tablero[0].length];
            List<Integer[]> pendientes = new ArrayList<>();

            pendientes.add(new Integer[]{pieza.f, pieza.c, 0});
            visitado[pieza.f][pieza.c] = true;

            for (int i = 0; i < pendientes.size(); i++) {
                Integer[] actual = pendientes.get(i);
                int fActual = actual[0];
                int cActual = actual[1];
                int distancia = actual[2];

                if (distancia == movimientoMaximo) continue;

                agregarMovimiento(pieza, pendientes, visitado, fActual + 1, cActual, distancia + 1);
                agregarMovimiento(pieza, pendientes, visitado, fActual - 1, cActual, distancia + 1);
                agregarMovimiento(pieza, pendientes, visitado, fActual, cActual + 1, distancia + 1);
                agregarMovimiento(pieza, pendientes, visitado, fActual, cActual - 1, distancia + 1);
            }
        }
        private void agregarMovimiento(Pieza pieza, List<Integer[]> pendientes, boolean [][] visitado, int f, int c, int distancia) {
            if (f < 0 || f >= tablero.length || c < 0 || c >= tablero[0].length || visitado[f][c]) return;

            visitado[f][c] = true;
            if (tablero[f][c].pieza != null) return;

            pieza.movimientosPosibles.add(new Integer[]{f, c});
            pendientes.add(new Integer[]{f, c, distancia});
        }
        public void entrarPieza(Pieza pieza, int x, int y) {
            tablero[x][y].entrarPieza(pieza);
            pieza.setCoordenadas(x, y);
            movimientosPosibles(pieza);
            ataquesPosibles(pieza);
        }
        public void generarTablero () {
            Habitat habitat;
            for (int i = 0; i < tablero.length; i++) {
                for (int j = 0; j < tablero.length; j++) {
                    habitat = Habitat.values()[random.nextInt(Habitat.values().length)];
                    tablero[i][j] = new Casilla(null, habitat);
                }
            }
        }
        public void mostrarTablero () {
            for (int i = 0; i < tablero.length; i++) {
                System.out.println("\n  |"+"-".repeat(tablero.length*6-1)+"|");
                for (int j = 0; j < tablero.length; j++) {
                    if (j == 0) System.out.print((8-i)+" ");
                    System.out.print("| "+tablero[i][j]+" ");
                    if (j == tablero.length-1) System.out.print("|");
                }
            }
            System.out.println("\n  |"+"-".repeat(tablero.length*6-1)+"|");
            System.out.print("     ");
            for (int i = 0; i < tablero.length; i++) {
                    System.out.printf("%-6s", (i+1));
            }
        }
        public void mostrarTablero (Casilla [][] tablero) {
            for (int i = 0; i < tablero.length; i++) {
                System.out.println("\n  |"+"-".repeat(tablero.length*6-1)+"|");
                for (int j = 0; j < tablero.length; j++) {
                    if (j == 0) System.out.print((8-i)+" ");
                    System.out.print("| "+tablero[i][j]+" ");
                    if (j == tablero.length-1) System.out.print("|");
                }
            }
            System.out.println("\n  |"+"-".repeat(tablero.length*6-1)+"|");
            System.out.print("     ");
            for (int i = 0; i < tablero.length; i++) {
                    System.out.printf("%-6s", (i+1));
            }
        }
    }
