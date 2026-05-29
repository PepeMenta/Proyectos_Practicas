package Objects.Horizon;

import java.util.ArrayList;
import java.util.List;

import Objects.Horizon.*;

public class Inventario {
    public List<Pieza> piezasDesbloqueadas = new ArrayList<>();
    public int maxRubies;

    public Inventario () {}

    public Inventario (List <Pieza> piezasDesbloqueadas, int maxRubies) {
        this.piezasDesbloqueadas = piezasDesbloqueadas; 
        this.maxRubies = maxRubies;
    }
    public void piezasBase (List <Pieza> piezas) {
        piezasDesbloqueadas.clear();
        for (int i = 0; i < 4; i++) {
            piezasDesbloqueadas.add(new Pieza(piezas.get(i)));
        }
    }
    public void todasPiezas (List <Pieza> piezas) {
        piezasDesbloqueadas.clear();
        for (int i = 0; i < piezas.size(); i++) {
            piezasDesbloqueadas.add(new Pieza(piezas.get(i)));
        }
    }
    public void add (Pieza pieza) {
        this.piezasDesbloqueadas.add(pieza);
    }
    public Pieza get (int num) {
        return this.piezasDesbloqueadas.get(num);
    }
}
