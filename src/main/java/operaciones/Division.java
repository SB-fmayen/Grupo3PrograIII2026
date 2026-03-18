
package operaciones;

//La clase Division toma una lista de números y los divide en orden.
import listas.ListaDouble;
import listas.NodoDouble;

// (implements) Esta clase PROMETE que va a cumplir ciertas reglas si o silas tiene que cumplir, si no nos tira error po no cumplir
public class Division  implements OperacionMatematica{
    
//Recibe una lista de números y Devuelve el resultado de la división    
    @Override
    public double ejecutar(ListaDouble valores) {
        if (valores.estaVacia()) return 0; // validasi esta vacio, si no hay nada evuelve un 0
        double resultado = valores.get(0); //get(0) Toma el PRIMER valor de la lista
        NodoDouble actual = valores.getCabeza().siguiente;// getCabeza() = primer nodo (20) y .siguiente = segundo nodo (2)
        while (actual != null) { //recorreomos la lista
            if (actual.valor == 0) throw new ArithmeticExce // Si intenta dividir entre 0 Se detiene el programa
            ption("División por cero");
            resultado = resultado / actual.valor;
            actual = actual.siguiente;
    }
    return resultado;
  }
}