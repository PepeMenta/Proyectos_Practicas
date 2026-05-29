package Objects.Horizon;

import java.util.List;

public class Partida {
    public Tablero tablero = new Tablero();
    public Equipo equipo = new Equipo();
    public Pieza piezaQueDesbloquea;
    public int maxRubies;

    public Partida () {}

    public void reiniciar() {
        tablero.generarTablero();
        for (int i = 0; i < equipo.size(); i++) {
            equipo.get(i).vida = equipo.get(i).vidaMAX;
            equipo.get(i).acciones = equipo.get(i).accionesMAX;
        }
    }
    public Partida (Equipo equipo, Pieza p, int maxRubies) {
        this.equipo = equipo;
        this.piezaQueDesbloquea = p;
        this.maxRubies = maxRubies;
    }
    public void mostrarPartida () {
        equipo.mostrarEquipo();
        System.out.println(piezaQueDesbloquea != null ? piezaQueDesbloquea.nombre+" "+piezaQueDesbloquea.unicode : "");
        System.out.println(maxRubies+"🔶\n");
    }
}
