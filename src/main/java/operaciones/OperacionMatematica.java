package operaciones;

import listas.ListaDouble;

/**
 * Contrato que deben cumplir todas las operaciones matemáticas (Suma, Resta, etc.).
 * Cada operación recibe una lista de valores y devuelve un resultado.
 */
public interface OperacionMatematica {
    double ejecutar(ListaDouble valores);
}