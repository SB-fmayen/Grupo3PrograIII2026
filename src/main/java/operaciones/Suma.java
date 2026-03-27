package operaciones;

import listas.ListaDouble;
import listas.NodoDouble;

public class Suma implements OperacionMatematica {
    @Override
    public double ejecutar(ListaDouble valores) {
        if (valores.estaVacia()) return 0;
        double resultado = 0;
        NodoDouble actual = valores.getCabeza();
        while (actual != null) {
            resultado += actual.valor;
            actual = actual.siguiente;
        }
        return resultado;
    }
}
