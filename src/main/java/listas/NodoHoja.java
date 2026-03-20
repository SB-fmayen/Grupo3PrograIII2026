/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package listas;

import modelo.HojaCalculo;
/**
 *
 * @author caste
 */
public class NodoHoja {
    public string nombre;
    public HojaCalculo hoja;
    public NodoHoja siguiente;

    public NodoHoja(string nombre, HojaCalculo hoja) {
        this.nombre = nombre;
        this.hoja = hoja;
        this.siguiente = null;
    }
    
    
}
