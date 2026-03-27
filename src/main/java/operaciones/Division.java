package operaciones;

import listas.ListaDouble;
import listas.NodoDouble;

public class Division implements OperacionMatematica {
    @Override
    public double ejecutar(ListaDouble valores) {
        if (valores.estaVacia()) return 0;
        double resultado = valores.get(0);
        NodoDouble actual = valores.getCabeza().siguiente;
        while (actual != null) {
            if (actual.valor == 0) throw new ArithmeticException("División por cero");
            resultado /= actual.valor;
            actual = actual.siguiente;
        }
        return resultado;
    }
}
