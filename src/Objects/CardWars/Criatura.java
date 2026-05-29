package Objects.CardWars;

public class Criatura extends Carta {
    public TipoTerreno terreno;
    public int vida;
    public int vidaMax;
    public int ataque; 
    public Floop floop;
    public boolean floopeado = false;
    public int posicion;
    public boolean bando;

    public Criatura () {}

    public Criatura (String nombre, String unicode, TipoTerreno terreno, int vida, int ataque, Floop floop, int posicion, boolean bando, int coste) {
        super.nombre = nombre;
        super.unicode = unicode;
        this.terreno = terreno;
        this.vida = vida;
        this.ataque = ataque;
        this.floop = floop;
        this.posicion = posicion;
        this.bando = bando;
        super.coste = coste;
    }
    public String toString () {
        return String.format("%-20s %-5s \t%-15s %-5s %-5s %-5s %-5s", nombre, unicode, terreno.name(), coste, vida, ataque, floop.coste);
    }
    public boolean setPosicion (String posicion) {
        boolean correcto = true;
        try {
            int pos = Integer.parseInt(posicion)-1;
            if (0 <= pos && pos < 4) {
                this.posicion = pos;
            }
            else correcto = false;
        } catch (Exception e) {
            correcto = false;
        }
        
        return correcto;
    }
    public boolean tieneFloop () {
        return floop != null;
    }
    public void recibirCura (int cura) {
        vida += cura;
        if (vidaMax < vida) vida = vidaMax;
    }
    public void recibirDaño (int daño) {
        vida -= daño;
        if (vida < 0) vida = 0;
    }
    @Override
    public void play(ContextoPartida contexto) {}

}
