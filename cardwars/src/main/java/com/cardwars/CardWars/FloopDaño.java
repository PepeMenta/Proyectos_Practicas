package com.cardwars.CardWars;

import java.util.List;

public class FloopDaño implements Efecto {

    public ProveedorValor daño;
    public ObjetivoFloop objetivo;

    public FloopDaño (ProveedorValor daño) {
        this.daño = daño;
    }

    @Override
    public void ejecutar(ContextoPartida contexto, Jugador jugador, Carta carta) {
        List<Criatura> objetivos = objetivo.getObjetivos(contexto, jugador, carta);
        for (int i = 0; i < objetivos.size(); i++) {
            objetivos.get(i).recibirDaño(daño.getValue(contexto, jugador, carta));
        }
    }

}
