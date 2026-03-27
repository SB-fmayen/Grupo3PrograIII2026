package modelo;

public class Celda {
    private int fila;
    private int columna;
    private Object valor;
    private Celda derecha;  // Siguiente en la fila (horizontal)
    private Celda abajo;    // Siguiente en la columna (vertical)

    private Celda(Builder builder) {
        this.fila = builder.fila;
        this.columna = builder.columna;
        this.valor = builder.valor;
        this.derecha = null;
        this.abajo = null;
    }

    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public Object getValor() { return valor; }
    public void setValor(Object valor) { this.valor = valor; }
    public Celda getDerecha() { return derecha; }
    public void setDerecha(Celda derecha) { this.derecha = derecha; }
    public Celda getAbajo() { return abajo; }
    public void setAbajo(Celda abajo) { this.abajo = abajo; }

    public static class Builder {
        private int fila;
        private int columna;
        private Object valor;

        public Builder fila(int fila) { this.fila = fila; return this; }
        public Builder columna(int columna) { this.columna = columna; return this; }
        public Builder valor(Object valor) { this.valor = valor; return this; }

        public Celda build() {
            if (fila < 0 || columna < 0)
                throw new IllegalArgumentException("Fila y columna deben ser >= 0");
            return new Celda(this);
        }
    }

    @Override
    public String toString() {
        return String.format("Celda[%d,%d]=%s", fila, columna, valor);
    }
}
