/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import listas.ListaEnlazadaCeldas;
import listas.ListaDouble;
import listas.NodoCelda;

/**
 * @author ALFREDO
 * Hoja de cálculo implementada con listas enlazadas propias.
 */
public class HojaCalculo {
    private ListaEnlazadaCeldas celdas;  // Lista enlazada propia
    private Celda primeraCelda;

    public HojaCalculo() {
        this.celdas = new ListaEnlazadaCeldas();
        this.primeraCelda = null;
    }

    /**
     * Convierte una posición (fila, columna) numérica a referencia tipo Excel.
     * Ejemplo: fila=0, columna=0 → "A1"
     */
    public String posicionAReferencia(int fila, int columna) {
        String columnaLetra = "";
        int col = columna;
        while (col >= 0) {
            columnaLetra = (char) ('A' + (col % 26)) + columnaLetra;
            col = (col / 26) - 1;
        }
        return columnaLetra + (fila + 1);
    }

    /**
     * Convierte una referencia tipo Excel a posición numérica [fila, columna].
     * Ejemplo: "B3" → [2, 1]
     */
    public int[] referenciaAPosicion(String referencia) {
        int i = 0;
        while (i < referencia.length() && Character.isLetter(referencia.charAt(i))) i++;
        String columnaLetra = referencia.substring(0, i);
        int fila = Integer.parseInt(referencia.substring(i)) - 1;
        int columna = 0;
        for (char c : columnaLetra.toCharArray()) {
            columna = columna * 26 + (c - 'A' + 1);
        }
        return new int[]{fila, columna - 1};
    }

    /**
     * Inserta una celda en la hoja. Si ya existe una celda en esa posición,
     * actualiza su valor. También establece los enlaces horizontales y verticales.
     */
    public void insertarCelda(Celda celda) {
        String referencia = posicionAReferencia(celda.getFila(), celda.getColumna());
        Celda existente = celdas.buscarPorReferencia(referencia,
            c -> posicionAReferencia(c.getFila(), c.getColumna()));

        if (existente != null) {
            existente.setValor(celda.getValor());
            return;
        }

        celdas.agregar(celda);
        enlazarHorizontal(celda);
        enlazarVertical(celda);

        if (primeraCelda == null) primeraCelda = celda;
    }

    /**
     * Enlaza horizontalmente la nueva celda con las celdas de la misma fila,
     * manteniéndolas ordenadas de izquierda a derecha por número de columna.
     */
    private void enlazarHorizontal(Celda nuevaCelda) {
        NodoCelda actual = celdas.getCabeza();
        while (actual != null) {
            Celda c = actual.celda;
            if (c != nuevaCelda && c.getFila() == nuevaCelda.getFila()
                    && c.getColumna() < nuevaCelda.getColumna()) {
                if (c.getDerecha() == null
                        || c.getDerecha().getColumna() > nuevaCelda.getColumna()) {
                    Celda siguiente = c.getDerecha();
                    c.setDerecha(nuevaCelda);
                    nuevaCelda.setDerecha(siguiente);
                }
            }
            actual = actual.siguiente;
        }
    }

    /**
     * Enlaza verticalmente la nueva celda con las celdas de la misma columna,
     * manteniéndolas ordenadas de arriba hacia abajo por número de fila.
     */
    private void enlazarVertical(Celda nuevaCelda) {
        NodoCelda actual = celdas.getCabeza();
        while (actual != null) {
            Celda c = actual.celda;
            if (c != nuevaCelda && c.getColumna() == nuevaCelda.getColumna()
                    && c.getFila() < nuevaCelda.getFila()) {
                if (c.getAbajo() == null
                        || c.getAbajo().getFila() > nuevaCelda.getFila()) {
                    Celda siguiente = c.getAbajo();
                    c.setAbajo(nuevaCelda);
                    nuevaCelda.setAbajo(siguiente);
                }
            }
            actual = actual.siguiente;
        }
    }

    /**
     * Busca y retorna la celda correspondiente a la referencia dada.
     * Retorna null si no existe ninguna celda en esa referencia.
     */
    public Celda obtenerCelda(String referencia) {
        return celdas.buscarPorReferencia(referencia,
            c -> posicionAReferencia(c.getFila(), c.getColumna()));
    }

    /**
     * Retorna el valor almacenado en la celda indicada por la referencia.
     * Si la celda no existe, retorna una cadena vacía.
     */
    public Object obtenerValor(String referencia) {
        Celda celda = obtenerCelda(referencia);
        return celda != null ? celda.getValor() : "";
    }

    /**
     * Modifica el valor de una celda existente. Si la celda no existe,
     * la crea e inserta en la hoja con el valor proporcionado.
     */
    public void modificarValor(String referencia, Object nuevoValor) {
        Celda celda = obtenerCelda(referencia);
        if (celda != null) {
            celda.setValor(nuevoValor);
        } else {
            int[] pos = referenciaAPosicion(referencia);
            Celda nueva = new Celda.Builder()
                .fila(pos[0]).columna(pos[1]).valor(nuevoValor).build();
            insertarCelda(nueva);
        }
    }

    /**
     * Elimina la celda indicada por la referencia. Antes de eliminarla,
     * repara los enlaces horizontales y verticales de las celdas vecinas
     * para mantener la integridad de la lista enlazada.
     */
    public void eliminarCelda(String referencia) {
        Celda celda = obtenerCelda(referencia);
        if (celda != null) {
            NodoCelda actual = celdas.getCabeza();
            while (actual != null) {
                if (actual.celda.getDerecha() == celda)
                    actual.celda.setDerecha(celda.getDerecha());
                if (actual.celda.getAbajo() == celda)
                    actual.celda.setAbajo(celda.getAbajo());
                actual = actual.siguiente;
            }
            celdas.eliminar(referencia,
                c -> posicionAReferencia(c.getFila(), c.getColumna()));
        }
    }

    /**
     * Obtiene todos los valores numéricos de las celdas dentro de un rango
     * rectangular definido por dos referencias (inicio y fin).
     * Las celdas con valores no numéricos o vacíos son ignoradas.
     */
    public ListaDouble obtenerValoresDelRango(String inicio, String fin) {
        ListaDouble valores = new ListaDouble();
        int[] posInicio = referenciaAPosicion(inicio);
        int[] posFin = referenciaAPosicion(fin);

        NodoCelda actual = celdas.getCabeza();
        while (actual != null) {
            Celda c = actual.celda;
            if (c.getFila() >= posInicio[0] && c.getFila() <= posFin[0] &&
                c.getColumna() >= posInicio[1] && c.getColumna() <= posFin[1]) {
                try {
                    valores.agregar(Double.parseDouble(c.getValor().toString()));
                } catch (NumberFormatException | NullPointerException e) {
                    // Ignorar celdas no numéricas
                }
            }
            actual = actual.siguiente;
        }
        return valores;
    }

    /**
     * Retorna la lista enlazada completa con todas las celdas de la hoja.
     * Útil para recorrer o mostrar el contenido completo de la hoja.
     */
    public ListaEnlazadaCeldas obtenerTodasLasCeldas() {
        return celdas;
    }
}