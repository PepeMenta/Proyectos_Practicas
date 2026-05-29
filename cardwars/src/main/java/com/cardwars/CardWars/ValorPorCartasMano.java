package com.cardwars.CardWars;

public class ValorPorCartasMano implements ProveedorValor{

    private int suma;

    public ValorPorCartasMano (int suma) {
        this.suma = suma;
    }

    @Override
    public int getValue(ContextoPartida contexto, Jugador jugador, Carta carta) {
        int sumTotal = 0;

        for (int i = 0; i < jugador.mano.size(); i++) {
            sumTotal += suma;
        }

        return sumTotal;
    }

}
