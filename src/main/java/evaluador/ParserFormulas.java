                         /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluador;


import listas.ListaDouble;
import modelo.HojaCalculo;
import operaciones.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ALFREDO
 * 
 */
public class ParserFormulas {
    private HojaCalculo hoja;
    private EvaluadorFormulas evaluador;
    private ExpressionEvaluator expressionEvaluator;

    /**
     * Para usar el parser solo necesitás pasarle la hoja de cálculo.
     * Él solito arma las piezas internas que necesita para trabajar.
     */
    public ParserFormulas(HojaCalculo hoja) {
        this.hoja = hoja;
        this.evaluador = new EvaluadorFormulas();
        this.expressionEvaluator = new ExpressionEvaluator();
    }

    /**
     * Este es el método principal. Recibe una fórmula como "=SUMA(A1:A5)"
     * o "=A1+B2" y devuelve el resultado numérico. Si la fórmula no empieza
     * con "="  lanza un error  de inmediato porque algo está mal.
     */
    public double evaluarFormula(String formula) throws Exception {
        if (formula == null || !formula.startsWith("="))
            throw new IllegalArgumentException("La fórmula debe comenzar con '='");

        String expresion = formula.substring(1).trim();

        if (expresion.toUpperCase().startsWith("SUMA("))                 return evaluarFuncion(expresion, new Suma());
        else if (expresion.toUpperCase().startsWith("RESTA("))           return evaluarFuncion(expresion, new Resta());
        else if (expresion.toUpperCase().startsWith("MULTIPLICACION("))  return evaluarFuncion(expresion, new Multiplicacion());
        else if (expresion.toUpperCase().startsWith("DIVISION("))        return evaluarFuncion(expresion, new Division());
        else return evaluarOperacionDirecta(expresion);
    }

    /**
     * Cuando la fórmula usa una función conocida (SUMA, RESTA, etc.),
     * este método saca los valores del rango, le pasa la operación correcta
     * al evaluador y devuelve el resultado. Es como el intermediario entre
     * la fórmula en texto y el cálculo real.
     */
    private double evaluarFuncion(String expresion, OperacionMatematica op) throws Exception {
        String contenido = extraerContenidoFuncion(expresion);
        ListaDouble valores = extraerValoresDelRango(contenido);
        evaluador.setOperacion(op);
        return evaluador.calcular(valores);
    }

    /**
     * Para fórmulas tipo "=A1+B2*3" donde no hay una función sino una
     * expresión directa. Primero cambia las referencias de celdas por sus
     * valores reales y luego evalúa la expresión matemática resultante.
     */
    private double evaluarOperacionDirecta(String expresion) throws Exception {
        String expresionEvaluada = reemplazarReferencias(expresion);
        return expressionEvaluator.evaluate(expresionEvaluada);
    }

    /**
     * Busca en la expresión todo lo que parezca una referencia de celda
     * (como "A1", "BC23") y lo sustituye por el valor numérico que tiene
     * esa celda en la hoja. Si una celda está vacía o tiene texto, lanza
     * un error para no hacer un cálculo con datos basura.
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
     * De una expresión como "SUMA(A1:A5)" saca solo lo que está adentro
     * de los paréntesis, en este caso "A1:A5". Si no encuentra paréntesis,
     * avisa que algo está mal escrito.
     */
    private String extraerContenidoFuncion(String expresion) {
        int inicio = expresion.indexOf('(');
        int fin = expresion.lastIndexOf(')');
        if (inicio == -1 || fin == -1)
            throw new IllegalArgumentException("Formato de función inválido");
        return expresion.substring(inicio + 1, fin);
    }

    /**
     * Dado el contenido de una función, se encarga de armar la lista
     * de valores numéricos que se van a operar. Sabe manejar tres casos:
     * un rango con ":" como "A1:A5", celdas separadas por "," como "A1,B2,C3",
     * o una celda suelta como "A1".
     */
    private ListaDouble extraerValoresDelRango(String rango) throws Exception {
        ListaDouble valores = new ListaDouble();
        if (rango.contains(":")) {
            String[] partes = rango.split(":");
            valores.agregarTodos(hoja.obtenerValoresDelRango(partes[0], partes[1]));
        } else if (rango.contains(",")) {
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
            Object valor = hoja.obtenerValor(rango);
            if (valor != null && !valor.toString().isEmpty()) {
                valores.agregar(Double.parseDouble(valor.toString()));
            }
        }
        return valores;
    }

    /**
     * Forma rápida de saber si un texto es una fórmula o no.
     * Simplemente revisa si empieza con "=", que es la señal de que
     * hay algo que calcular.
     */
    public boolean esFormula(String valor) {
        return valor != null && valor.startsWith("=");
    }
}