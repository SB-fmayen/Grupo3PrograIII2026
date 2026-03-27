package fabrica;

import modelo.Celda;

/**
 * Facilita la creación de celdas sin tener que armar el Builder cada vez.
 * Es el único lugar donde se construyen celdas en toda la aplicación.
 */
public class CeldaFactory {

    // Crea una celda normal con un valor (texto, número, lo que sea)
    public static Celda crearCelda(int fila, int columna, Object valor) {
        return new Celda.Builder().fila(fila).columna(columna).valor(valor).build();
    }

    // Crea una celda que contiene una fórmula, ej: "=SUMA(A1:A5)"
    public static Celda crearCeldaConFormula(int fila, int columna, String formula) {
        return crearCelda(fila, columna, formula);
    }

    // Crea una celda sin contenido (celda en blanco)
    public static Celda crearCeldaVacia(int fila, int columna) {
        return crearCelda(fila, columna, "");
    }
}