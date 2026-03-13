/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package listas;
import modelo.Celda;

/**
 * @author ALFREDO
 * Lista enlazada simple propia para almacenar Celdas
 */

public class ListaEnlazadaCeldas {
    private NodoCelda cabeza;
    private int tamaño;

    public ListaEnlazadaCeldas() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    /** Agrega una celda al final de la lista */
    public void agregar(Celda celda) {
        NodoCelda nuevo = new NodoCelda(celda);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoCelda actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamaño++;
    }

    /** Busca celda por referencia */
    public Celda buscarPorReferencia(String referencia, java.util.function.Function<Celda, String> refFn) {
        NodoCelda actual = cabeza;
        while (actual != null) {
            if (refFn.apply(actual.celda).equals(referencia)) {
                return actual.celda;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    /** Elimina una celda por referencia */
    public boolean eliminar(String referencia, java.util.function.Function<Celda, String> refFn) {
        if (cabeza == null) return false;
        if (refFn.apply(cabeza.celda).equals(referencia)) {
            cabeza = cabeza.siguiente;
            tamaño--;
            return true;
        }
        NodoCelda actual = cabeza;
        while (actual.siguiente != null) {
            if (refFn.apply(actual.siguiente.celda).equals(referencia)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamaño--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /** Retorna la cabeza para iterar manualmente */
    public NodoCelda getCabeza() {
        return cabeza;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    /** Vacía completamente la lista */
    public void limpiar() {
        cabeza = null;
        tamaño = 0;
    }
}