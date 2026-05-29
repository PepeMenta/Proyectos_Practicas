package com.cardwars.CardWars;

import java.util.ArrayList;
import java.util.List;

public class Tablero {
    List<Carril[]> tablero;

    public Tablero () {
        for (int i = 0; i < 4; i++) {
            tablero.add(new Carril[2]);
        }
    }

    public Carril[] get (int i) {
        return tablero.get(i);
    }
    public int size () {
        return tablero.size();
    }
    public List<Carril> carrilesBando (boolean bando) {
        List<Carril> carrilesBando = new ArrayList<>();

        for (int i = 0; i < tablero.size(); i++) {
            carrilesBando.add(tablero.get(0)[bando ? 0 : 1]);
        }
        
        return carrilesBando;
    }
    public List<Criatura> criaturasBando (boolean bando) {
        List<Criatura> criaturasBando = new ArrayList<>();

        for (int i = 0; i < tablero.size(); i++) {
            if (tablero.get(0)[bando ? 0 : 1].criatura != null) criaturasBando.add(tablero.get(0)[bando ? 0 : 1].criatura);
        }
        
        return criaturasBando;

    }
}
