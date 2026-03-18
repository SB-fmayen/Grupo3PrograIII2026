/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package listas;

import modelo.HojaCalculo;

/**
 * @author ALFREDO
 * Lista enlazada simple propia para gestionar hojas de cálculo.
 */
public class ListaHojas {
    private NodoHoja cabeza;
    private int tamanio;
    
        public ListaHoja(){
            this.cabeza = null;
            this.tamanio = 0;
        }
        
  public void agregar(String nombre, HojaCalculo hoja) {
        NodoHoja nuevo = new NodoHoja(nombre, hoja);
        if (cabeza == null) {
            cabeza = nuevo;
        } else{
            NodoHoja actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamanio++;
    }  
    
    public HojaCalculo buscar(String nombre){
        NodoHoja actual = cabeza;
        while(actual != null){
            if(actual.nombre.equals(nombre)) return actual.hoja;
        }
        return null;
    }
    
    public boolean contiene(String nombre){
        return buscar(nombre) != null;
    }
    
    public NodoHoja getCabeza(){
        return cabeza;
    }
    
    public int getTamanio(){
        return tamanio;
    }
    
}
public class ListaHojas {
    private NodoHoja cabeza;
    private int tamanio;

    public ListaHojas() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    public void agregar(String nombre, HojaCalculo hoja) {
        NodoHoja nuevo = new NodoHoja(nombre, hoja);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoHoja actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamanio++;
    }

    public HojaCalculo buscar(String nombre) {
        NodoHoja actual = cabeza;
        while (actual != null) {
            if (actual.nombre.equals(nombre)) return actual.hoja;
            actual = actual.siguiente;
        }
        return null;
    }

    public boolean contiene(String nombre) {
        return buscar(nombre) != null;
    }

    public NodoHoja getCabeza() {
        return cabeza;
    }

    public int getTamanio() {
        return tamanio;
    }
}