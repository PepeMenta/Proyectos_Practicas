package Objects.Horizon;

import java.util.ArrayList;
import java.util.List;

public class Equipo {
        public List <Pieza> equipo = new ArrayList<>();
        public Inventario inventario = new Inventario();

        public Equipo () {}
        public Equipo (List<Pieza> piezas) {
            inventario.piezasBase(piezas);
        }
        public String unicaOpcionViva () {
            String opcion = null;
            for (int i = 0; i < equipo.size(); i++) {
                if (equipo.get(i).estaVivo()) opcion = (i+1)+"";
            }
            return opcion;
        }
        public void addInventario (Pieza pieza) {
            this.inventario.add(pieza);
        }
        public Pieza getInventario (int num) {
            return this.inventario.get(num);
        }
        public int numPiezasVivas () {
            int num = 0;

            for (int i = 0; i < equipo.size(); i++) {
                if (equipo.get(i).estaVivo()) num++;
            }
            
            return num;
        }
        public boolean entradaCorrectaInventario (String opcion) {
            boolean correcto = false;

            try {
                int op = Integer.parseInt(opcion);

                if (1 <= op && op <= inventario.piezasDesbloqueadas.size()) {
                    correcto = true;
                }
            } catch (Exception e) {}
            
            return correcto;
        }
        public boolean entradaCorrectaEquipo (String opcion) {
            boolean correcto = false;

            try {
                int op = Integer.parseInt(opcion);

                if (1 <= op && op <= equipo.size()) {
                    correcto = true;
                }
            } catch (Exception e) {}
            
            return correcto;
        }
        public int size () {
            return equipo.size();
        }
        public Pieza get(int i) {
            return this.equipo.get(i);
        }
        public void add(Pieza pieza) {
            this.equipo.add(pieza);
        }
        public int add(String opcion, List<Pieza> piezas, int num_costes) {
            try {
                if (num_costes - piezas.get(Integer.parseInt(opcion)-1).coste >= 0) {
                    System.out.println("\tasdadd"+inventario.get(Integer.parseInt(opcion)-1));
                    this.equipo.add(new Pieza(inventario.get(Integer.parseInt(opcion)-1)));
                    num_costes -= piezas.get(Integer.parseInt(opcion)-1).coste;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return num_costes;
        }
        public void mostrarEquipoPorColocar () {
            for (int i = 0; i < equipo.size(); i++) {
                if (equipo.get(i).c == -1) System.out.printf("   "+(i+1)+".   %-4s %-22s %s %-7s %-6s %-7s\n", equipo.get(i).coste+"🔶", equipo.get(i).unicode+" "+equipo.get(i).nombre, equipo.get(i).habitat.textoTabla(15), equipo.get(i).vida+"❤️", equipo.get(i).ataque+"💥", equipo.get(i).acciones+"⚡");
            }
        }
        public void mostrarEquipo () {
            for (int i = 0; i < equipo.size(); i++) {
                if (equipo.get(i).estaVivo()) System.out.printf("   "+(i+1)+".   %-4s %-22s \t%s %-7s %-6s %-7s\n", equipo.get(i).coste+"🔶", equipo.get(i).unicode+" "+equipo.get(i).nombre, equipo.get(i).habitat.textoTabla(15), equipo.get(i).vida+"❤️", equipo.get(i).ataque+"💥", equipo.get(i).acciones+"⚡");
            }
        }

    }
