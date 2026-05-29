package Objects.CardWars;

import java.util.List;

public class FloopModificadorAtaque implements Efecto{

    public ProveedorValor modificacion;
    public ObjetivoFloop objetivo;

    @Override
    public void ejecutar(ContextoPartida contexto, Jugador jugador, Carta carta) {
        List<Criatura> objetivos = objetivo.getObjetivos(contexto, jugador, carta);
        for (int i = 0; i < objetivos.size(); i++) {
            objetivos.get(i).ataque += modificacion.getValue(contexto, jugador, carta);
        }
    }

}
