package Objects.CardWars;

public interface Efecto {
    void ejecutar(ContextoPartida contexto, Jugador jugador, Carta carta);
}
