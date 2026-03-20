/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package listas;

import modelo.Celda;
/**
 *
 * @author caste
 */
public class NodoCelda {

    public Celda celda;
    public NodoCelda siguiente;
    
    public NodoCelda(Celda celda) {
    this.celda = celda;
    this.siguiente = null;
    }
    
    
}
