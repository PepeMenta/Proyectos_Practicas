package Objects.CardWars;

public abstract class Carta {
    public String nombre;
    public int coste;
    public String unicode;

    public abstract void play(ContextoPartida contexto);
}
