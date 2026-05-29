package Objects.Horizon;

import java.util.ArrayList;
import java.util.List;

public class Pieza {
        public int id;
        public String nombre;
        public String unicode;
        public Habitat habitat;
        public int buffHabitat;
        public int movimiento;
        public int coste;
        public int vida;
        public int vidaMAX;
        public int ataque;
        public int alcanceAtaque;
        public int saltoAtaque;
        public int adyacenteAtaque;
        public int acciones;   
        public int accionesMAX;
        public boolean movido;
        public boolean ataco;
        public boolean aliado;
        public int f = -1;
        public int c = -1;
        public List<Integer[]> movimientosPosibles = new ArrayList<>();
        public List<Integer[]> ataquesPosibles = new ArrayList<>();


        public Pieza (String unicode) {
            this.unicode = unicode;
        }
        public Pieza (int id, String nombre, String unicode, Habitat habitat, int buffHabitat, int movimiento, int coste, int vida, int ataque, int alcanceAtaque, int saltoAtaque, int adyacenteAtaque, int acciones) {
            this.id = id;
            this.nombre = nombre;
            this.unicode = unicode;
            this.habitat = habitat;
            this.buffHabitat = buffHabitat;
            this.movimiento = movimiento;
            this.coste = coste;
            this.vida = vida;
            this.vidaMAX = vida;
            this.ataque = ataque;
            this.alcanceAtaque = alcanceAtaque;
            this.saltoAtaque = saltoAtaque;
            this.adyacenteAtaque = adyacenteAtaque;
            this.acciones = acciones;
            this.accionesMAX = acciones;
            this.movido = false;
        }
        public Pieza (Pieza p) {
            this.id = p.id;
            this.f = p.f;
            this.c = p.c;
            this.nombre = p.nombre;
            this.unicode = p.unicode;
            this.habitat = p.habitat;
            this.buffHabitat = p.buffHabitat;
            this.movimiento = p.movimiento;
            this.coste = p.coste;
            this.vida = p.vida;
            this.vidaMAX = p.vida;
            this.ataque = p.ataque;
            this.alcanceAtaque = p.alcanceAtaque;
            this.saltoAtaque = p.saltoAtaque;
            this.adyacenteAtaque = p.adyacenteAtaque;
            this.acciones = p.acciones;
            this.accionesMAX = p.accionesMAX;
            this.movido = p.movido;
            this.ataco = p.ataco;
            this.aliado = p.aliado;
            this.movimientosPosibles = new ArrayList<>();
            this.ataquesPosibles = new ArrayList<>();
        }
        public int afinidadHabitat (Habitat habitat) {
            if (habitat.equals(this.habitat)) {
                return this.buffHabitat;
            }
            else return 0;
        }
        public boolean estaVivo () {
            return vida != 0;
        }
        public void mostrarPiezaDetallada () {
            System.out.printf("%-4s %-25s \t%s\n", coste+"🔶    ", unicode+"   "+nombre, habitat.textoTabla(5)+"   "+buffHabitat+"⬆️"+"    "+movimiento+"🐾"+"   "+vida+"❤️"+" ".repeat(vida>=10 ? 3 : 4)+ataque+"💥"+"   "+alcanceAtaque+"➜"+"   "+saltoAtaque+"🏹"+"   "+adyacenteAtaque+"↔️"+"    "+acciones+"⚡"+" = "+calculoValor());
        }
        public int calculoValor () {
            return movimiento+vida+ataque+buffHabitat*2+habitat.altura+acciones*5+alcanceAtaque-saltoAtaque+adyacenteAtaque;
        }
        public void mostrarPieza () {
            System.out.println("\n\n\n\t"+unicode+"\t"+vida+"❤️"+"\t"+ataque+"💥");
            System.out.println("\t\t"+acciones+"⚡");
        }
        public boolean noMasAcciones () {
            if (acciones == 0 ){
                System.out.println("No le quedan acciones...");
                return true;
            }
            else return false;
        }
        public void setCoordenadas (int f, int c) {
            this.f = f;
            this.c = c;
        }
    }
