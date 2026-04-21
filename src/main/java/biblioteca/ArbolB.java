/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca;

import java.util.function.Consumer;

public class ArbolB {

    private static final int M = 5;
    private static final int MAX_CLAVES = M - 1;
    private static final int MIN_CLAVES = M / 2;

    private class Nodo {
        int numClaves;
        int[] claves = new int[MAX_CLAVES];
        Libro[] libros = new Libro[MAX_CLAVES];
        Nodo[] hijos = new Nodo[M];
        boolean esHoja;

        Nodo(boolean esHoja) {
            this.esHoja = esHoja;
            this.numClaves = 0;
        }
    }

    private Nodo raiz;
    private int divisiones;
    private int fusiones;
    private int redistribuciones;
    private int comparaciones;
    private int totalLibros;
    private int altura;

    public ArbolB() {
        raiz = new Nodo(true);
        altura = 1;
    }

    // ── BÚSQUEDA ───────────────────────────────────────

    public Libro buscar(int clave) {
        return buscarRec(raiz, clave);
    }

    private Libro buscarRec(Nodo n, int clave) {
        if (n == null) return null;

        int i = 0;
        while (i < n.numClaves) {
            comparaciones++;
            if (clave == n.claves[i]) return n.libros[i];
            if (clave < n.claves[i]) break;
            i++;
        }

        return n.esHoja ? null : buscarRec(n.hijos[i], clave);
    }

    // ── INSERCIÓN ─────────────────────────────────────

    public void insertar(Libro libro) {
        if (raiz.numClaves == MAX_CLAVES) {
            Nodo nueva = new Nodo(false);
            nueva.hijos[0] = raiz;
            dividirHijo(nueva, 0, raiz);
            raiz = nueva;
            altura++;
            insertarNoLleno(nueva, libro);
        } else {
            insertarNoLleno(raiz, libro);
        }
        totalLibros++;
    }

    private void insertarNoLleno(Nodo nodo, Libro libro) {
        int i = nodo.numClaves - 1;
        int clave = libro.getCodigoLibro();

        if (nodo.esHoja) {
            while (i >= 0 && clave < nodo.claves[i]) {
                comparaciones++;
                nodo.claves[i + 1] = nodo.claves[i];
                nodo.libros[i + 1] = nodo.libros[i];
                i--;
            }

            nodo.claves[i + 1] = clave;
            nodo.libros[i + 1] = libro;
            nodo.numClaves++;

        } else {
            while (i >= 0 && clave < nodo.claves[i]) {
                comparaciones++;
                i--;
            }

            i++;

            if (nodo.hijos[i].numClaves == MAX_CLAVES) {
                dividirHijo(nodo, i, nodo.hijos[i]);
                comparaciones++;
                if (clave > nodo.claves[i]) i++;
            }

            insertarNoLleno(nodo.hijos[i], libro);
        }
    }

    private void dividirHijo(Nodo padre, int idx, Nodo hijo) {
        int mid = MIN_CLAVES;

        Nodo nuevo = new Nodo(hijo.esHoja);
        nuevo.numClaves = MAX_CLAVES - mid - 1;

        for (int j = 0; j < nuevo.numClaves; j++) {
            nuevo.claves[j] = hijo.claves[mid + 1 + j];
            nuevo.libros[j] = hijo.libros[mid + 1 + j];
        }

        if (!hijo.esHoja) {
            for (int j = 0; j <= nuevo.numClaves; j++) {
                nuevo.hijos[j] = hijo.hijos[mid + 1 + j];
            }
        }

        hijo.numClaves = mid;

        for (int j = padre.numClaves; j >= idx + 1; j--) {
            padre.hijos[j + 1] = padre.hijos[j];
        }

        padre.hijos[idx + 1] = nuevo;

        for (int j = padre.numClaves - 1; j >= idx; j--) {
            padre.claves[j + 1] = padre.claves[j];
            padre.libros[j + 1] = padre.libros[j];
        }

        padre.claves[idx] = hijo.claves[mid];
        padre.libros[idx] = hijo.libros[mid];
        padre.numClaves++;

        divisiones++;
    }

    // ── ELIMINACIÓN ─────────────────────────────────────


    public boolean eliminar(int clave) {
        if (buscar(clave) == null) return false;

        eliminarRec(raiz, clave);
        totalLibros--;

        if (raiz.numClaves == 0 && !raiz.esHoja) {
            raiz = raiz.hijos[0];
            altura--;
        }
        return true;
    }

    private void eliminarRec(Nodo nodo, int clave) {
        int idx = encontrarClave(nodo, clave);

        if (idx < nodo.numClaves && nodo.claves[idx] == clave) {
            if (nodo.esHoja) eliminarDeHoja(nodo, idx);
            else eliminarDeNodoInterno(nodo, idx);
        } else {
            if (nodo.esHoja) return;

            boolean enUltimo = (idx == nodo.numClaves);

            if (nodo.hijos[idx].numClaves < MIN_CLAVES) {
                llenarHijo(nodo, idx);
            }

            if (enUltimo && idx > nodo.numClaves) {
                eliminarRec(nodo.hijos[idx - 1], clave);
            } else {
                eliminarRec(nodo.hijos[idx], clave);
            }
        }
    }

    private int encontrarClave(Nodo n, int clave) {
        int i = 0;
        while (i < n.numClaves && n.claves[i] < clave) {
            comparaciones++;
            i++;
        }
        return i;
    }

    private void eliminarDeHoja(Nodo n, int idx) {
        for (int i = idx + 1; i < n.numClaves; i++) {
            n.claves[i - 1] = n.claves[i];
            n.libros[i - 1] = n.libros[i];
        }
        n.numClaves--;
    }


    // ── ESTADÍSTICAS ─────────────────────────────────────

    public int getAltura() { return altura; }
    public int getDivisiones() { return divisiones; }
    public int getFusiones() { return fusiones; }
    public int getRedistribuciones() { return redistribuciones; }
    public int getComparaciones() { return comparaciones; }
    public int getTotalLibros() { return totalLibros; }

    public void resetContadores() {
        divisiones = 0;
        fusiones = 0;
        redistribuciones = 0;
        comparaciones = 0;
    }

    // ── INORDEN ─────────────────────────────────────────

    public void inorden(Consumer<Libro> accion) {
        inordenRec(raiz, accion);
    }

    private void inordenRec(Nodo n, Consumer<Libro> accion) {
        if (n == null) return;

        for (int i = 0; i < n.numClaves; i++) {
            if (!n.esHoja) inordenRec(n.hijos[i], accion);
            accion.accept(n.libros[i]);
        }

        if (!n.esHoja) inordenRec(n.hijos[n.numClaves], accion);
    }
}