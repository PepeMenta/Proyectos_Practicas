package com.cardwars.CardWars;

import java.util.List;

public class ValorPorCriaturaFloopeada implements ProveedorValor{

    private int suma;

    public ValorPorCriaturaFloopeada (int suma) {
        this.suma = suma;
    }

    @Override
    public int getValue(ContextoPartida contexto, Jugador jugador, Carta carta) {
        int sumTotal = 0;
        List<Criatura> criaturas = contexto.tablero.criaturasBando(jugador.bando);

        for (int i = 0; i < criaturas.size(); i++) {
            if (criaturas.get(i).floopeado) sumTotal += suma;
        }

        return sumTotal;
    }

}
