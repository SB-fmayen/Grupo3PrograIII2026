/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package manejoMemoria;

import java.util.ArrayList;

/**PPPPP
 *
 */
public class bloqueDeMemoria {
    int tamaño;
    int tamañoOriginal; 
    ArrayList <proceso> procesos = new ArrayList<>();

    public int getTamaño() {
        return tamaño;
    }
    
    public void setTamañoOriginal(int tamaño) {
        this.tamañoOriginal = tamaño;
    }

    public int getTamañoOriginal() {
        return tamañoOriginal;
    }

    public void setTamaño(int tamaño) {
        this.tamaño = tamaño;
    }

    public ArrayList<proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(ArrayList<proceso> procesos) {
        this.procesos = procesos;
    }
     
}
