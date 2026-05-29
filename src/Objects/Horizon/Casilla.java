package Objects.Horizon;

    public class Casilla {
        public Pieza pieza;
        public Habitat habitat;

        public Casilla () {}
        public Casilla (Pieza pieza, Habitat habitat) {
            this.pieza = pieza;
            this.habitat = habitat;
        }
        public Casilla (Casilla casilla) {
            this.pieza = casilla.pieza;
            this.habitat = casilla.habitat;
        }
        public boolean tienePieza () {
            return pieza != null;
        }
        public void entrarPieza (Pieza pieza) {
            this.pieza = pieza;
        }
        public String toString () {
            String unicodePieza = pieza == null ? "▫" : pieza.unicode;
            return unicodePieza+(" ".repeat(2-unicodePieza.length() < 0 ? 0 : 2-unicodePieza.length()))+habitat.getUnicode();
        }
    }
