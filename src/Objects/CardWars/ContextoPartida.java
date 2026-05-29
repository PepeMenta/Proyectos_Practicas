package Objects.CardWars;

import java.util.List;
import java.util.Random;

public class ContextoPartida {
    public List<Jugador> jugadores;
    public Jugador jugadorActual;
    public int turnoActual;
    public Tablero tablero;

    public ContextoPartida () {}

    public void colocarCriatura (Criatura criatura) {
        tablero.get(criatura.posicion)[criatura.bando ? 0 : 1].criatura = criatura;
    }
    public void colocarTerreno (Terreno terreno, int posicion) {
        tablero.get(posicion)[jugadorActual.bando ? 0 : 1].terreno = terreno.tipo;
    }
    public void primeraMano () {
        Random random = new Random();

        for (int i = 0; i < jugadores.size(); i++) {
            for (int j = 0; j < 5; j++) {
                jugadores.get(i).mano.add(jugadores.get(i).baraja.remove(random.nextInt(jugadores.get(i).baraja.size())));
            }
        }
    }
    public void robarCarta () {
        Random random = new Random();

        jugadorActual.mano.add(jugadorActual.baraja.remove(random.nextInt(jugadorActual.baraja.size())));
    }
    public void finalizarTurno () {
        
        turnoActual++;
    }

}
