package com.cardwars.CardWars;

public class ValorFijo implements ProveedorValor{

    private int dañoFijo;

    public ValorFijo (int daño) {
        this.dañoFijo = daño;
    }

    @Override
    public int getValue(ContextoPartida contexto, Jugador jugador, Carta carta) {
        return this.dañoFijo;
    }


}
