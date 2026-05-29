package com.cardwars.CardWars;

import java.util.List;

public class FloopCura implements Efecto {

    public ProveedorValor cura;
    public ObjetivoFloop objetivo;

    public FloopCura (int cura) {
        this.cura = new ValorFijo(cura);
    }

    public FloopCura (ProveedorValor cura) {
        this.cura = cura;
    }

    @Override
    public void ejecutar(ContextoPartida contexto, Jugador jugador, Carta carta) {
        List<Criatura> objetivos = objetivo.getObjetivos(contexto, jugador, carta);
        for (int i = 0; i < objetivos.size(); i++) {
            objetivos.get(i).recibirCura(cura.getValue(contexto, jugador, carta));
        }
    }

}
