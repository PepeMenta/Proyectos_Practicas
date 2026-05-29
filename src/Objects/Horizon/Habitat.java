package Objects.Horizon;

    public enum Habitat {
        MONTAÑA("\u001B[33m▲\u001B[0m", 2),
        LLANURA("▬", 1),
        VALLE("\u001B[31m▼\u001B[0m", 0);

        private final String unicode;
        public final int altura;

        Habitat (String unicode, int altura) {
            this.unicode = unicode;
            this.altura = altura;
        }

        public String getUnicode() {
            return unicode;
        }

        public String textoTabla(int ancho) {
            return unicode + " ".repeat(Math.max(0, ancho - 1));
        }
    }
