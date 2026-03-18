
package operaciones;

// listas donde viene los numeros (ListaDouble) / (NodoDouble) trabajamos por listas enlazadas cada noo tiene dos datos y apunta al siguiente
import listas.ListaDouble;
import listas.NodoDouble;

// (implements) Esta clase PROMETE que va a cumplir ciertas reglas si o silas tiene que cumplir, si no nos tira error po no cumplir
public class Multiplicacion implements OperacionMatematica{

// este metodo recibe una lista(valores), devuelve un numero y va hacer la multiplicacion
        @Override
    public double ejecutar(ListaDouble valores) {
        
//si la lista valores esta vacia o no hay nada por multiplicar, devolvera 0 
    if (valores.estaVacia()) return 0;
    double resultado = 1.0; //inicia en 1 por que 1 es el elemento neutro de la multiplicación 1*5= 5
    NodoDouble actual = valores.getCabeza(); 
    while (actual != null) { 
    resultado = resultado * actual.valor;
    actual = actual.siguiente; // se mueve al siguiente nodo de la lista
    }
    return resultado;
    }
    
}

