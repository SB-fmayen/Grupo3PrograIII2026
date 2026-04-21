/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca;


import java.util.function.Consumer;

public class ArbolAVL {

    private class Nodo {
        Libro libro;
        Nodo izq, der;
        int altura;

        Nodo(Libro l) {
            this.libro = l;
            this.altura = 1;
        }
    }

    private Nodo raiz;
    private int rotaciones;
    private int comparaciones;
    private int totalNodos;

    public ArbolAVL() {
        raiz = null;
        rotaciones = 0;
        comparaciones = 0;
        totalNodos = 0;
    }

    // ── utilidades ─────────────────────────────

    private int altura(Nodo n) {
        return (n == null) ? 0 : n.altura;
    }

    private int balance(Nodo n) {
        return (n == null) ? 0 : altura(n.izq) - altura(n.der);
    }

    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der));
    }

    // ── rotaciones ─────────────────────────────

    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izq;
        Nodo T2 = x.der;

        x.der = y;
        y.izq = T2;

        actualizarAltura(y);
        actualizarAltura(x);

        rotaciones++;
        return x;
    }

    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.der;
        Nodo T2 = y.izq;

        y.izq = x;
        x.der = T2;

        actualizarAltura(x);
        actualizarAltura(y);

        rotaciones++;
        return y;
    }

    private Nodo balancear(Nodo nodo, int clave) {
        actualizarAltura(nodo);
        int fb = balance(nodo);

        if (fb > 1 && clave < nodo.izq.libro.getCodigoLibro())
            return rotarDerecha(nodo);

        if (fb < -1 && clave > nodo.der.libro.getCodigoLibro())
            return rotarIzquierda(nodo);

        if (fb > 1 && clave > nodo.izq.libro.getCodigoLibro()) {
            nodo.izq = rotarIzquierda(nodo.izq);
            return rotarDerecha(nodo);
        }

        if (fb < -1 && clave < nodo.der.libro.getCodigoLibro()) {
            nodo.der = rotarDerecha(nodo.der);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    // ── inserción ─────────────────────────────

    public void insertar(Libro libro) {
        raiz = insertarRec(raiz, libro);
        totalNodos++;
    }

    private Nodo insertarRec(Nodo nodo, Libro libro) {
        if (nodo == null)
            return new Nodo(libro);

        comparaciones++;
        int clave = libro.getCodigoLibro();

        if (clave < nodo.libro.getCodigoLibro())
            nodo.izq = insertarRec(nodo.izq, libro);
        else if (clave > nodo.libro.getCodigoLibro())
            nodo.der = insertarRec(nodo.der, libro);
        else {
            totalNodos--; // duplicado
            return nodo;
        }

        return balancear(nodo, clave);
    }

    // ── búsqueda ─────────────────────────────

    public Libro buscar(int codigo) {
        return buscarRec(raiz, codigo);
    }

    private Libro buscarRec(Nodo nodo, int clave) {
        if (nodo == null)
            return null;

        comparaciones++;

        if (clave == nodo.libro.getCodigoLibro())
            return nodo.libro;

        if (clave < nodo.libro.getCodigoLibro())
            return buscarRec(nodo.izq, clave);

        return buscarRec(nodo.der, clave);
    }

    // ── eliminación ───────────────────────────

    public boolean eliminar(int codigo) {
        if (buscar(codigo) == null)
            return false;

        raiz = eliminarRec(raiz, codigo);
        totalNodos--;
        return true;
    }

    private Nodo eliminarRec(Nodo nodo, int clave) {
        if (nodo == null)
            return null;

        comparaciones++;

        if (clave < nodo.libro.getCodigoLibro()) {
            nodo.izq = eliminarRec(nodo.izq, clave);
        } else if (clave > nodo.libro.getCodigoLibro()) {
            nodo.der = eliminarRec(nodo.der, clave);
        } else {
            if (nodo.izq == null)
                return nodo.der;

            if (nodo.der == null)
                return nodo.izq;

            Nodo sucesor = minimoNodo(nodo.der);
            nodo.libro = sucesor.libro;
            nodo.der = eliminarRec(nodo.der, sucesor.libro.getCodigoLibro());
        }

        return balancear(nodo, nodo.libro.getCodigoLibro());
    }

    private Nodo minimoNodo(Nodo n) {
        while (n.izq != null)
            n = n.izq;
        return n;
    }

    // ── estadísticas ──────────────────────────

    public int getAltura() {
        return altura(raiz);
    }

    public int getRotaciones() {
        return rotaciones;
    }

    public int getComparaciones() {
        return comparaciones;
    }

    public int getTotalNodos() {
        return totalNodos;
    }

    public void resetContadores() {
        rotaciones = 0;
        comparaciones = 0;
    }

    // ── recorrido inorden ─────────────────────

    public void inorden(Consumer<Libro> accion) {
        inordenRec(raiz, accion);
    }

    private void inordenRec(Nodo n, Consumer<Libro> accion) {
        if (n == null)
            return;

        inordenRec(n.izq, accion);
        accion.accept(n.libro);
        inordenRec(n.der, accion);
    }
}
