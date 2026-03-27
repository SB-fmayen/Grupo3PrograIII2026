package evaluador;

/**
 * Calcula el resultado de una expresión matemática escrita como texto.
 * Por ejemplo: "3 + 5 * (2 - 1)" → 8.0
 * Respeta el orden correcto de operaciones: primero * y /, luego + y -,
 * y los paréntesis tienen prioridad sobre todo.
 */
public class ExpressionEvaluator {

    /**
     * Punto de entrada. Recibe la expresión como texto y devuelve el resultado.
     * Primero limpia los espacios y luego comienza a evaluarla.
     */
    public double evaluate(String expression) throws Exception {
        expression = expression.replaceAll("\\s+", ""); // Quitamos espacios en blanco
        if (expression.isEmpty()) throw new Exception("Expresión vacía");
        return evaluateExpression(expression);
    }

    // Inicia el proceso de evaluación desde el nivel más bajo de prioridad (+ y -)
    private double evaluateExpression(String expr) throws Exception {
        return parseAddSub(expr, new int[]{0});
    }

    /**
     * Maneja sumas y restas. Son las operaciones de menor prioridad,
     * así que se evalúan de último.
     */
    private double parseAddSub(String expr, int[] pos) throws Exception {
        double result = parseMulDiv(expr, pos); // Primero resolvemos * y /

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
     * Maneja multiplicaciones y divisiones. Tienen más prioridad que + y -,
     * así que se evalúan antes.
     */
    private double parseMulDiv(String expr, int[] pos) throws Exception {
        double result = parseNumber(expr, pos); // Primero obtenemos el número

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
     * Extrae un número de la expresión. También maneja dos casos especiales:
     * - Si encuentra un paréntesis, evalúa lo que hay adentro primero.
     * - Si encuentra un signo negativo, lo aplica al número que sigue.
     */
    private double parseNumber(String expr, int[] pos) throws Exception {

        // Si hay un paréntesis, evaluamos su contenido como una sub-expresión
        if (pos[0] < expr.length() && expr.charAt(pos[0]) == '(') {
            pos[0]++;
            double result = parseAddSub(expr, pos); // Evaluamos lo de adentro
            if (pos[0] < expr.length()) pos[0]++;   // Saltamos el paréntesis de cierre
            return result;
        }

        // Verificamos si el número es negativo
        boolean negative = false;
        if (pos[0] < expr.length() && expr.charAt(pos[0]) == '-') {
            negative = true;
            pos[0]++;
        }

        // Leemos los dígitos del número (incluyendo el punto decimal si lo tiene)
        StringBuilder num = new StringBuilder();
        while (pos[0] < expr.length() &&
                (Character.isDigit(expr.charAt(pos[0])) || expr.charAt(pos[0]) == '.')) {
            num.append(expr.charAt(pos[0]));
            pos[0]++;
        }

        if (num.length() == 0)
            throw new Exception("Número esperado en posición " + pos[0]);

        double val = Double.parseDouble(num.toString());
        return negative ? -val : val; // Aplicamos el signo negativo si corresponde
    }
}