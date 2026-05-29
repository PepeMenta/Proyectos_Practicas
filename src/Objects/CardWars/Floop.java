package Objects.CardWars;

import java.util.List;

public class Floop implements Efecto {

    public int coste;
    public List<Efecto> efectos;

    public Floop() {}

    public Floop(int coste, List<Efecto> efectos) {
        this.coste = coste;
        this.efectos = efectos;
    }

    @Override
    public void ejecutar(ContextoPartida contexto, Jugador jugador, Carta carta) {
        for (Efecto floopHabilidad : efectos) {
            floopHabilidad.ejecutar(contexto, jugador, carta);
        }
    }
    
}
