package Objects.Horizon;    

    public class DecisionRival {
        public int pieza;
        public String accion;
        public String destino;
        public int refuerzo;

        public DecisionRival (int p, String a, String d, int r) {
            this.pieza = p;
            this.accion = a;
            this.destino = d;
            this.refuerzo = r;
        }
        public DecisionRival () {}
        public String toString () {
            return pieza+" "+accion+" "+(8-Integer.parseInt(destino.split(" ")[0]))+" "+(Integer.parseInt(destino.split(" ")[1])+1)+" "+refuerzo;
        }
    }