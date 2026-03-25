/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluador;

/**
 * @author ALFREDO
 * Evaluador de expresiones matemáticas en formato de cadena de texto.
 * Soporta las cuatro operaciones básicas (+, -, *, /) y paréntesis anidados.
 * Implementa un parser de descenso recursivo respetando la precedencia de operadores.
 *
 * Ejemplos de uso:
 *   evaluator.evaluate("3+5*2")     → 13.0
 *   evaluator.evaluate("(3+5)*2")   → 16.0
 *   evaluator.evaluate("10/2-1")    →  4.0
 */
public class ExpressionEvaluator {

    /**
     * Punto de entrada principal. Recibe una expresión como texto, elimina los
     * espacios en blanco y lanza el proceso de evaluación.
     * @param expression Expresión matemática a evaluar (ej: "3 + 5 * (2 - 1)")
     * @return El resultado numérico de la expresión
     * @throws Exception Si la expresión está vacía, contiene caracteres inválidos o intenta dividir entre cero
     */
    public double evaluate(String expression) throws Exception {
        expression = expression.replaceAll("\\s+", "");
        if (expression.isEmpty()) throw new Exception("Expresión vacía");
        return evaluateExpression(expression);
    }

    /**
     * Inicia el parsing desde el nivel de menor precedencia (suma/resta).
     * El cursor de posición se pasa como array de un elemento para poder
     * modificarlo por referencia a lo largo de todos los métodos del parser.
     */
    private double evaluateExpression(String expr) throws Exception {
        return parseAddSub(expr, new int[]{0});
    }

    /**
     * Maneja sumas y restas. Tiene la menor precedencia, por lo que se evalúa
     * "por fuera": primero delega a parseMulDiv para resolver multiplicaciones
     * y divisiones, y luego acumula los términos con + o -.
     * Ejemplo: "3+5*2" → parseMulDiv resuelve 5*2=10, luego aquí se hace 3+10=13
     */
    private double parseAddSub(String expr, int[] pos) throws Exception {
        double result = parseMulDiv(expr, pos);
        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op == '+' || op == '-') {
                pos[0]++;
                double right = parseMulDiv(expr, pos);
                result = (op == '+') ? result + right : result - right;
            } else break;
        }
        return result;
    }

    /**
     * Maneja multiplicaciones y divisiones. Tiene mayor precedencia que + y -,
     * por lo que se evalúa "por dentro": delega a parseNumber para obtener
     * operandos individuales y luego los combina con * o /.
     * Lanza excepción explícita si se detecta una división por cero.
     */
    private double parseMulDiv(String expr, int[] pos) throws Exception {
        double result = parseNumber(expr, pos);
        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op == '*' || op == '/') {
                pos[0]++;
                double right = parseNumber(expr, pos);
                if (op == '*') result *= right;
                else {
                    if (right == 0) throw new Exception("División por cero");
                    result /= right;
                }
            } else break;
        }
        return result;
    }

    /**
     * Lee el elemento más básico de la expresión: un número literal o una
     * sub-expresión entre paréntesis.
     *
     * - Si encuentra '(' → avanza el cursor, evalúa recursivamente el contenido
     *   llamando a parseAddSub, y consume el ')' de cierre.
     * - Si encuentra dígitos o '.' → los acumula y los convierte a double.
     * - Si no encuentra ninguno de los anteriores → lanza excepción indicando
     *   la posición exacta del error para facilitar la depuración.
     */
    private double parseNumber(String expr, int[] pos) throws Exception {
        if (pos[0] < expr.length() && expr.charAt(pos[0]) == '(') {
            pos[0]++; // consume '('
            double result = parseAddSub(expr, pos);
            if (pos[0] < expr.length()) pos[0]++; // consume ')'
            return result;
        }
        StringBuilder num = new StringBuilder();
        while (pos[0] < expr.length() &&
               (Character.isDigit(expr.charAt(pos[0])) || expr.charAt(pos[0]) == '.')) {
            num.append(expr.charAt(pos[0]));
            pos[0]++;
        }
        if (num.length() == 0)
            throw new Exception("Número esperado en posición " + pos[0]);
        return Double.parseDouble(num.toString());
    }
}