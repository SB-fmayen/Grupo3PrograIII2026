/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package operaciones;

import listas.ListaDouble;
import listas.NodoDouble;

/**
 *
 * @author caste
 */
public class Resta implements OperacionMatematica{

    @Override
    public double ejecutar(ListaDouble valores) {
     if (valores.estaVacia())
         return 0;
     double resultado = valores.get(0);
     NodoDouble actual = valores.getCabeza().siguiente;
     while (actual != null) {
         resultado -= actual.valor;
         actual = actual.siguiente;
          
     }
     return resultado;
    }
}
    
