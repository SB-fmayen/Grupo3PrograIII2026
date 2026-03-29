package evaluador;

import listas.ListaDouble;
import modelo.HojaCalculo;
import operaciones.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Soporta funciones como SUMA, RESTA, MULTIPLICACION y DIVISION,
 * y también expresiones directas como =A1+B2*3
 */
public class ParserFormulas {
    private HojaCalculo hoja;
    private EvaluadorFormulas evaluador;
    private ExpressionEvaluator expressionEvaluator;

    public ParserFormulas(HojaCalculo hoja) {
        this.hoja = hoja;
        this.evaluador = new EvaluadorFormulas();
        this.expressionEvaluator = new ExpressionEvaluator();
    }

    /**
     * Recibe una fórmula como "=SUMA(A1:A5)" o "=A1+B2" y devuelve su resultado.
     * Detecta qué tipo de fórmula es y la manda al método correcto.
     */
    public double evaluarFormula(String formula) throws Exception {
        if (formula == null || !formula.startsWith("="))
            throw new IllegalArgumentException("La fórmula debe comenzar con '='");

        String expresion = formula.substring(1).trim(); // Quitamos el = del inicio

        // Detectamos qué función es y la evaluamos con su operación correspondiente
        if (expresion.toUpperCase().startsWith("SUMA("))
            return evaluarFuncion(expresion, new Suma());
        else if (expresion.toUpperCase().startsWith("RESTA("))
            return evaluarFuncion(expresion, new Resta());
        else if (expresion.toUpperCase().startsWith("MULTIPLICACION("))
            return evaluarFuncion(expresion, new Multiplicacion());
        else if (expresion.toUpperCase().startsWith("DIVISION("))
            return evaluarFuncion(expresion, new Division());
        else
            return evaluarOperacionDirecta(expresion); // Ej: =A1+B2*3
    }

    /**
     * Evalúa una función tipo SUMA(A1:A5). Extrae los valores del rango
     * y aplica la operación correspondiente.
     */
    private double evaluarFuncion(String expresion, OperacionMatematica op) throws Exception {
        String contenido = extraerContenidoFuncion(expresion); // Lo que hay dentro del paréntesis
        ListaDouble valores = extraerValoresDelRango(contenido);
        evaluador.setOperacion(op);
        return evaluador.calcular(valores);
    }

    /**
     * Evalúa expresiones directas como =A1+B2*3.
     * Primero reemplaza las referencias (A1, B2) por sus valores numéricos,
     * luego calcula la expresión resultante.
     */
    private double evaluarOperacionDirecta(String expresion) throws Exception {
        String expresionEvaluada = reemplazarReferencias(expresion);
        return expressionEvaluator.evaluate(expresionEvaluada);
    }

    /**
     * Busca referencias tipo A1, B3 en la expresión y las reemplaza
     * por el valor numérico que tiene esa celda en la hoja.
     * Ej: "A1+B2" → "10.0+5.0"
     */
    private String reemplazarReferencias(String expresion) throws Exception {
        Pattern pattern = Pattern.compile("([A-Z]+[0-9]+)");
        Matcher matcher = pattern.matcher(expresion);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String referencia = matcher.group(1);
            Object valor = hoja.obtenerValor(referencia);

            if (valor == null || valor.toString().isEmpty())
                throw new IllegalArgumentException("Celda " + referencia + " está vacía");
            try {
                matcher.appendReplacement(sb, String.valueOf(Double.parseDouble(valor.toString())));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Celda " + referencia + " no contiene un número");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Saca el contenido que hay dentro de los paréntesis de una función.
     * Ej: "SUMA(A1:A5)" → "A1:A5"
     */
    private String extraerContenidoFuncion(String expresion) {
        int inicio = expresion.indexOf('(');
        int fin = expresion.lastIndexOf(')');
        if (inicio == -1 || fin == -1)
            throw new IllegalArgumentException("Formato de función inválido");
        return expresion.substring(inicio + 1, fin);
    }

    /**
     * Obtiene los valores numéricos de un rango de celdas. Maneja tres casos:
     * - Rango continuo:  A1:A5  (todas las celdas entre A1 y A5)
     * - Celdas sueltas:  A1,B2,C3
     * - Celda única:     A1
     */
    private ListaDouble extraerValoresDelRango(String rango) throws Exception {
        ListaDouble valores = new ListaDouble();

        if (rango.contains(":")) {
            // Rango continuo: pedimos todos los valores de una vez
            String[] partes = rango.split(":");
            valores.agregarTodos(hoja.obtenerValoresDelRango(partes[0], partes[1]));

        } else if (rango.contains(",")) {
            // Celdas individuales separadas por coma
            String[] celdas = rango.split(",");
            for (String celda : celdas) {
                Object valor = hoja.obtenerValor(celda.trim());
                if (valor != null && !valor.toString().isEmpty()) {
                    try {
                        valores.agregar(Double.parseDouble(valor.toString()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Celda " + celda + " no es numérica");
                    }
                }
            }
        } else {
            // Una sola celda
            Object valor = hoja.obtenerValor(rango);
            if (valor != null && !valor.toString().isEmpty()) {
                valores.agregar(Double.parseDouble(valor.toString()));
            }
        }
        return valores;
    }

    // Verifica si un texto es una fórmula (empieza con =)
    public boolean esFormula(String valor) {
        return valor != null && valor.startsWith("=");
    }
}