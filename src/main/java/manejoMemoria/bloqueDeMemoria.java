/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package manejoMemoria;

import java.util.ArrayList;

/**
 *
 */
public class bloqueDeMemoria {
    int tamaño;
    ArrayList <proceso> procesos = new ArrayList<>();

    public int getTamaño() {
        return tamaño;
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
