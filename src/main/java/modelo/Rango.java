package modelo;

import listas.ListaEnlazadaCeldas;
import listas.NodoCelda;

/**
 * Rango de celdas. Usa ListaEnlazadaCeldas propia.
 */
public class Rango {
    private String inicio;
    private String fin;
    private HojaCalculo hoja;
    private ListaEnlazadaCeldas celdas;

    public Rango(String inicio, String fin, HojaCalculo hoja) {
        this.inicio = inicio;
        this.fin = fin;
        this.hoja = hoja;
        this.celdas = new ListaEnlazadaCeldas();
        obtenerCeldasDelRango();
    }

    private void obtenerCeldasDelRango() {
        int[] posInicio = hoja.referenciaAPosicion(inicio);
        int[] posFin = hoja.referenciaAPosicion(fin);
        NodoCelda actual = hoja.obtenerTodasLasCeldas().getCabeza();
        while (actual != null) {
            Celda c = actual.celda;
            if (c.getFila() >= posInicio[0] && c.getFila() <= posFin[0] &&
                    c.getColumna() >= posInicio[1] && c.getColumna() <= posFin[1]) {
                celdas.agregar(c);
            }
            actual = actual.siguiente;
        }
    }

    public ListaEnlazadaCeldas obtenerCeldas() {
        return celdas;
    }

    public String getInicio() { return inicio; }
    public String getFin() { return fin; }
}
