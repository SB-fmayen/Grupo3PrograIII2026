/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import listas.ListaEnlazadaCeldas;
import listas.NodoCelda;

/**
 * @author ALFREDO
 * Rango de celdas. Usa ListaEnlazadaCeldas propia.
 */
public class Rango {
    private String inicio;
    private String fin;
    private HojaCalculo hoja;
    private ListaEnlazadaCeldas celdas;

    /**
     * Aquí nace el rango. Le pasás la celda donde empieza, la celda donde
     * termina y la hoja a la que pertenece. En cuanto se crea, ya va y
     * busca todas las celdas que le corresponden.
     */
    public Rango(String inicio, String fin, HojaCalculo hoja) {
        this.inicio = inicio;
        this.fin = fin;
        this.hoja = hoja;
        this.celdas = new ListaEnlazadaCeldas();
        obtenerCeldasDelRango();
    }

    /**
     * Se encarga de ir celda por celda en toda la hoja y quedarse solo
     * con las que caen dentro del rectángulo que forman inicio y fin.
     * No hay que llamarla manualmente, el constructor ya lo hace solo.
     */
    private void obtenerCeldasDelRango() {
        int[] posInicio = hoja.referenciaAPosicion(inicio);
        int[] posFin    = hoja.referenciaAPosicion(fin);

        NodoCelda actual = hoja.obtenerTodasLasCeldas().getCabeza();
        while (actual != null) {
            Celda c = actual.celda;
            if (c.getFila()    >= posInicio[0] && c.getFila()    <= posFin[0] &&
                c.getColumna() >= posInicio[1] && c.getColumna() <= posFin[1]) {
                celdas.agregar(c);
            }
            actual = actual.siguiente;
        }
    }

    /**
     * Te da la lista con todas las celdas que quedaron dentro del rango.
     */
    public ListaEnlazadaCeldas obtenerCeldas() {
        return celdas;
    }

    /**
     * Te dice cuál es la referencia de la celda donde arranca el rango.
     */
    public String getInicio() {
        return inicio;
    }

    /**
     * Te dice cuál es la referencia de la celda donde termina el rango.
     */
    public String getFin() {
        return fin;
    }
}