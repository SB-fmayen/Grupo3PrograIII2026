package biblioteca;

import java.util.function.Consumer;

public class ArbolB {

    private static final int M         = 5;
    private static final int MAX_CLAVES = M - 1;      // 4
    private static final int MIN_CLAVES = (M / 2);    // 2

    private class Nodo {
        int    numClaves;
        int[]  claves  = new int[MAX_CLAVES];
        Libro[] libros = new Libro[MAX_CLAVES];
        Nodo[]  hijos  = new Nodo[M];
        boolean esHoja;
        Nodo(boolean esHoja) { this.esHoja = esHoja; }
    }

    private Nodo raiz;
    private int  divisiones;
    private int  fusiones;
    private int  redistribuciones;
    private int  comparaciones;
    private int  totalLibros;
    private int  altura;

    public ArbolB() {
        raiz = new Nodo(true);
        altura = 1;
    }

    // ── busqueda ──────────────────────────────────────────────────────────────

    public Libro buscar(int clave) { return buscarRec(raiz, clave); }

    private Libro buscarRec(Nodo n, int clave) {
        if (n == null) return null;
        int i = 0;
        while (i < n.numClaves) {
            comparaciones++;
            if (clave == n.claves[i]) return n.libros[i];
            if (clave <  n.claves[i]) break;
            i++;
        }
        return n.esHoja ? null : buscarRec(n.hijos[i], clave);
    }

    // ── insercion ─────────────────────────────────────────────────────────────

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
            while (i >= 0 && clave < nodo.claves[i]) { comparaciones++; i--; }
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
        if (!hijo.esHoja)
            for (int j = 0; j <= nuevo.numClaves; j++)
                nuevo.hijos[j] = hijo.hijos[mid + 1 + j];
        hijo.numClaves = mid;
        for (int j = padre.numClaves; j >= idx + 1; j--) padre.hijos[j + 1]  = padre.hijos[j];
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

    // ── eliminacion ───────────────────────────────────────────────────────────

    public boolean eliminar(int clave) {
        if (buscar(clave) == null) return false;
        eliminarRec(raiz, clave);
        totalLibros--;
        if (raiz.numClaves == 0 && !raiz.esHoja) { raiz = raiz.hijos[0]; altura--; }
        return true;
    }

    private void eliminarRec(Nodo nodo, int clave) {
        int idx = encontrarClave(nodo, clave);
        if (idx < nodo.numClaves && nodo.claves[idx] == clave) {
            if (nodo.esHoja) eliminarDeHoja(nodo, idx);
            else             eliminarDeNodoInterno(nodo, idx);
        } else {
            if (nodo.esHoja) return;
            boolean enUltimo = (idx == nodo.numClaves);
            if (nodo.hijos[idx].numClaves < MIN_CLAVES) llenarHijo(nodo, idx);
            if (enUltimo && idx > nodo.numClaves) eliminarRec(nodo.hijos[idx - 1], clave);
            else                                  eliminarRec(nodo.hijos[idx], clave);
        }
    }

    private int encontrarClave(Nodo n, int clave) {
        int i = 0;
        while (i < n.numClaves && n.claves[i] < clave) { comparaciones++; i++; }
        return i;
    }

    private void eliminarDeHoja(Nodo n, int idx) {
        for (int i = idx + 1; i < n.numClaves; i++) {
            n.claves[i-1] = n.claves[i]; n.libros[i-1] = n.libros[i];
        }
        n.numClaves--;
    }

    private void eliminarDeNodoInterno(Nodo n, int idx) {
        int clave = n.claves[idx];
        if (n.hijos[idx].numClaves >= MIN_CLAVES) {
            Libro pred = obtenerPredecesor(n, idx);
            n.claves[idx] = pred.getCodigoLibro(); n.libros[idx] = pred;
            eliminarRec(n.hijos[idx], pred.getCodigoLibro());
        } else if (n.hijos[idx+1].numClaves >= MIN_CLAVES) {
            Libro suc = obtenerSucesor(n, idx);
            n.claves[idx] = suc.getCodigoLibro(); n.libros[idx] = suc;
            eliminarRec(n.hijos[idx+1], suc.getCodigoLibro());
        } else {
            fusionar(n, idx);
            eliminarRec(n.hijos[idx], clave);
        }
    }

    private Libro obtenerPredecesor(Nodo n, int idx) {
        Nodo cur = n.hijos[idx];
        while (!cur.esHoja) cur = cur.hijos[cur.numClaves];
        return cur.libros[cur.numClaves - 1];
    }

    private Libro obtenerSucesor(Nodo n, int idx) {
        Nodo cur = n.hijos[idx + 1];
        while (!cur.esHoja) cur = cur.hijos[0];
        return cur.libros[0];
    }

    private void llenarHijo(Nodo n, int idx) {
        if (idx != 0 && n.hijos[idx-1].numClaves >= MIN_CLAVES)
            tomarDelAnterior(n, idx);
        else if (idx != n.numClaves && n.hijos[idx+1].numClaves >= MIN_CLAVES)
            tomarDelSiguiente(n, idx);
        else {
            if (idx != n.numClaves) fusionar(n, idx);
            else                    fusionar(n, idx - 1);
        }
    }

    private void tomarDelAnterior(Nodo n, int idx) {
        Nodo hijo = n.hijos[idx], hermano = n.hijos[idx-1];
        for (int i = hijo.numClaves - 1; i >= 0; i--) {
            hijo.claves[i+1] = hijo.claves[i]; hijo.libros[i+1] = hijo.libros[i];
        }
        if (!hijo.esHoja)
            for (int i = hijo.numClaves; i >= 0; i--) hijo.hijos[i+1] = hijo.hijos[i];
        hijo.claves[0] = n.claves[idx-1]; hijo.libros[0] = n.libros[idx-1];
        if (!hijo.esHoja) hijo.hijos[0] = hermano.hijos[hermano.numClaves];
        n.claves[idx-1] = hermano.claves[hermano.numClaves-1];
        n.libros[idx-1] = hermano.libros[hermano.numClaves-1];
        hijo.numClaves++; hermano.numClaves--;
        redistribuciones++;
    }

    private void tomarDelSiguiente(Nodo n, int idx) {
        Nodo hijo = n.hijos[idx], hermano = n.hijos[idx+1];
        hijo.claves[hijo.numClaves] = n.claves[idx];
        hijo.libros[hijo.numClaves] = n.libros[idx];
        if (!hijo.esHoja) hijo.hijos[hijo.numClaves+1] = hermano.hijos[0];
        n.claves[idx] = hermano.claves[0]; n.libros[idx] = hermano.libros[0];
        for (int i = 1; i < hermano.numClaves; i++) {
            hermano.claves[i-1] = hermano.claves[i]; hermano.libros[i-1] = hermano.libros[i];
        }
        if (!hermano.esHoja)
            for (int i = 1; i <= hermano.numClaves; i++) hermano.hijos[i-1] = hermano.hijos[i];
        hijo.numClaves++; hermano.numClaves--;
        redistribuciones++;
    }

    private void fusionar(Nodo n, int idx) {
        Nodo hijo = n.hijos[idx], hermano = n.hijos[idx+1];
        hijo.claves[MIN_CLAVES-1] = n.claves[idx];
        hijo.libros[MIN_CLAVES-1] = n.libros[idx];
        for (int i = 0; i < hermano.numClaves; i++) {
            hijo.claves[i + MIN_CLAVES] = hermano.claves[i];
            hijo.libros[i + MIN_CLAVES] = hermano.libros[i];
        }
        if (!hijo.esHoja)
            for (int i = 0; i <= hermano.numClaves; i++)
                hijo.hijos[i + MIN_CLAVES] = hermano.hijos[i];
        for (int i = idx+1; i < n.numClaves; i++) {
            n.claves[i-1] = n.claves[i]; n.libros[i-1] = n.libros[i];
        }
        for (int i = idx+2; i <= n.numClaves; i++) n.hijos[i-1] = n.hijos[i];
        hijo.numClaves += hermano.numClaves + 1;
        n.numClaves--;
        fusiones++;
    }

    // ── estadisticas ──────────────────────────────────────────────────────────

    public int getAltura()           { return altura; }
    public int getDivisiones()       { return divisiones; }
    public int getFusiones()         { return fusiones; }
    public int getRedistribuciones() { return redistribuciones; }
    public int getComparaciones()    { return comparaciones; }
    public int getTotalLibros()      { return totalLibros; }
    public void resetContadores()    { divisiones=0; fusiones=0; redistribuciones=0; comparaciones=0; }

    // ── recorrido inorden ─────────────────────────────────────────────────────

    public void inorden(Consumer<Libro> accion) { inordenRec(raiz, accion); }

    private void inordenRec(Nodo n, Consumer<Libro> accion) {
        if (n == null) return;
        for (int i = 0; i < n.numClaves; i++) {
            if (!n.esHoja) inordenRec(n.hijos[i], accion);
            accion.accept(n.libros[i]);
        }
        if (!n.esHoja) inordenRec(n.hijos[n.numClaves], accion);
    }
}