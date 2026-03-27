package listas;

import modelo.HojaCalculo;

public class NodoHoja {
    public String nombre;
    public HojaCalculo hoja;
    public NodoHoja siguiente;

    public NodoHoja(String nombre, HojaCalculo hoja) {
        this.nombre = nombre;
        this.hoja = hoja;
        this.siguiente = null;
    }
}
