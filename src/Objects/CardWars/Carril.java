package Objects.CardWars;

public class Carril {
    public Criatura criatura;
    public TipoTerreno terreno;
    public Edificio edificio;
    public int buffAtaque;
    public int buffDefensa;

    public Carril () {
        this.buffAtaque = 0;
        this.buffDefensa = 0;
    }
}
