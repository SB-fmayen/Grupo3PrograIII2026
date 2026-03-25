/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fabrica;


import modelo.Celda;

/**
 *
 * @author caste
 */
public class CeldaFactory {
   public static Celda crearCelda(int fila, int columna, Object valor){
       return new Celda.Builder().fila(fila).columna(columna).valor(valor).build();}
   
   public static Celda crearCeldaConFormula(int fila, int columna, String formula){
       return crearCelda(fila, columna, formula);
   }
   public static Celda crearCeldavacia (int fila, int columna){
       return crearCelda (fila, columna,"");
   }
   
}
