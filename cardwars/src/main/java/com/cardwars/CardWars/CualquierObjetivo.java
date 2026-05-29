package com.cardwars.CardWars;

import java.util.ArrayList;
import java.util.List;

public class CualquierObjetivo implements ObjetivoFloop {

    private int [] posiciones;
    private boolean  [] aliados;

    public CualquierObjetivo (int [] posiciones, boolean [] aliados) {
        this.posiciones = posiciones;
        this.aliados = aliados;
    }

    @Override
    public List<Criatura> getObjetivos(ContextoPartida contexto, Jugador jugador, Carta carta) {
        List<Criatura> objetivos = new ArrayList<>();
        Criatura criatura = (Criatura)carta;

        for (int j = 0; j < posiciones.length; j++) {
            int posicionObjetivo = criatura.posicion + posiciones[j];

            if (0 <= posicionObjetivo && posicionObjetivo < contexto.tablero.size()) {
                boolean bandoObjetivo = aliados[j] ? criatura.bando : !criatura.bando;
                Carril carril = contexto.tablero.get(posicionObjetivo)[bandoObjetivo ? 0 : 1];
                if (carril.criatura != null) objetivos.add(carril.criatura);
            }
        }

        return objetivos;
    }

}
