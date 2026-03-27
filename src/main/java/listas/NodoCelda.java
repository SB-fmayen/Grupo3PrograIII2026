package listas;

import modelo.Celda;

public class NodoCelda {
    public Celda celda;
    public NodoCelda siguiente;

    public NodoCelda(Celda celda) {
        this.celda = celda;
        this.siguiente = null;
    }
}
