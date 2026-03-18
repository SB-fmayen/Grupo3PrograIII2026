/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package listas;

/**
 * @author ALFREDO
 * Lista enlazada simple propia para alamacenar valores double.
 */
public class ListaDouble {
    private NodoDouble cabeza;
    private int tamanio;

    public ListaDouble() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    public void agregar(double valor) {
        NodoDouble nuevo = new NodoDouble(valor);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoDouble actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamanio++;
    }

    public double get(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        NodoDouble actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public NodoDouble getCabeza() {
        return cabeza;
    }

    /** Agrega todos los elementos de otra ListaDouble a esta */
    public void agregarTodos(ListaDouble otra) {
        NodoDouble actual = otra.getCabeza();
        while (actual != null) {
            this.agregar(actual.valor);
            actual = actual.siguiente;
        }
    }
}