package com.cardwars.CardWars;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Jugador {
    public String nombre;
    public int vida;
    public int magia = 2;
    public boolean bando;

    public List<Carta> mano = new ArrayList<>();
    public List<Carta> baraja = new ArrayList<>();
    public List<Carta> descarte = new ArrayList<>();

    public Jugador () {
    }
    public Jugador (String nombre, int vida, boolean bando, List<Carta> cartas) {
        this.nombre = nombre;
        this.vida = vida;
        this.bando = bando;
        generarBarajaAleatorio(cartas);
    }
    public void generarBarajaAleatorio (List<Carta> cartas) {
        Random random = new Random();
        List<Carta> generarBaraja = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            generarBaraja.add(cartas.get(random.nextInt(cartas.size())));
        }

        this.baraja = generarBaraja;
    }
    public boolean borrarCartaMano(String nombre) {
        Carta carta = null;
        boolean encontrado = false;
        int x = 0;

        while (!encontrado && x < mano.size()) {
            if (mano.get(x).nombre.equals(nombre)){
                mano.remove(x);
                encontrado = true;
            }
            else x++;
        }

        return encontrado;
    }
}
