package evaluador;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    @Test
    void evaluateRespectsOperatorPrecedenceAndParentheses() throws Exception {
        double result = evaluator.evaluate("3 + 5 * (2 - 1)");
        assertEquals(8.0, result);
    }

    @Test
    void evaluateHandlesNegativeAndDecimalNumbers() throws Exception {
        double result = evaluator.evaluate("-2.5 * 4 + 10");
        assertEquals(0.0, result, 1e-9);
    }

    @Test
    void evaluateSupportsNestedParentheses() throws Exception {
        double result = evaluator.evaluate("(2 + (3 * 4)) - 5");
        assertEquals(9.0, result);
    }

    @Test
    void evaluateThrowsOnDivisionByZero() {
        Exception ex = assertThrows(Exception.class, () -> evaluator.evaluate("10/0"));
        assertTrue(ex.getMessage().contains("División por cero"));
    }

    @Test
    void evaluateThrowsOnEmptyExpression() {
        Exception ex = assertThrows(Exception.class, () -> evaluator.evaluate("   "));
        assertTrue(ex.getMessage().contains("Expresión vacía"));
    }

    @Test
    void evaluateThrowsOnMalformedExpression() {
        Exception ex = assertThrows(Exception.class, () -> evaluator.evaluate("2+*3"));
        assertTrue(ex.getMessage().contains("Número esperado"));
    }
}
