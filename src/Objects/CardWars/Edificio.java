package Objects.CardWars;

public class Edificio extends Carta implements Efecto {
    public String nombre;
    public TipoTerreno terreno;
    public String descripcion;
    public int posicion;
    public boolean bando;

    public Edificio () {}

    @Override
    public void play(ContextoPartida contexto) {

    }

    @Override
    public void ejecutar(ContextoPartida contexto, Jugador jugador, Carta carta) {

    }

}
