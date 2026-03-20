/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author caste
 */
public class Celda {
    private int fila;
    private int columna;
    private Object valor;
    private Celda derecha;
    private Celda abajo;

    public Celda(Builder builder) {
        this.fila = fila;
        this.columna = columna;
        this.valor = valor;
        this.derecha = null;
        this.abajo = null;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Celda getDerecha() {
        return derecha;
    }

    public void setDerecha(Celda derecha) {
        this.derecha = derecha;
    }

    public Celda getAbajo() {
        return abajo;
    }

    public void setAbajo(Celda abajo) {
        this.abajo = abajo;
    }
    

    
}
