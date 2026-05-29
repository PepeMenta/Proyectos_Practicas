package Objects.CardWars;

public class Hechizo extends Carta implements Efecto{

    public Hechizo () {}

    @Override
    public void play(ContextoPartida contexto) {
        ejecutar(contexto, contexto.jugadorActual, this);
    }

    @Override
    public void ejecutar(ContextoPartida contexto, Jugador jugador, Carta carta) {

    }
}
