package evaluador;

import modelo.HojaCalculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserFormulasTest {

    private HojaCalculo hoja;
    private ParserFormulas parser;

    @BeforeEach
    void setUp() {
        hoja = new HojaCalculo();
        parser = new ParserFormulas(hoja);
    }

    @Test
    void esFormulaDetectsFormulaPrefix() {
        assertTrue(parser.esFormula("=A1+B1"));
        assertFalse(parser.esFormula("A1+B1"));
        assertFalse(parser.esFormula(null));
    }

    @Test
    void evaluarFormulaRejectsInvalidPrefix() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parser.evaluarFormula("SUMA(A1:A2)"));
        assertTrue(ex.getMessage().contains("debe comenzar"));
    }

    @Test
    void evaluarFormulaResolvesDirectExpressionWithReferences() throws Exception {
        hoja.modificarValor("A1", 10);
        hoja.modificarValor("B2", 5);

        double result = parser.evaluarFormula("=A1+B2*2");
        assertEquals(20.0, result, 1e-9);
    }

    @Test
    void evaluarFormulaComputesSumaForRange() throws Exception {
        hoja.modificarValor("A1", 1);
        hoja.modificarValor("A2", 2);
        hoja.modificarValor("A3", 3);

        double result = parser.evaluarFormula("=SUMA(A1:A3)");
        assertEquals(6.0, result, 1e-9);
    }

    @Test
    void evaluarFormulaComputesRestaForCommaSeparatedReferences() throws Exception {
        hoja.modificarValor("A1", 10);
        hoja.modificarValor("B1", 3);
        hoja.modificarValor("C1", 2);

        double result = parser.evaluarFormula("=RESTA(A1,B1,C1)");
        assertEquals(5.0, result, 1e-9);
    }

    @Test
    void evaluarFormulaComputesMultiplicacionForSingleCell() throws Exception {
        hoja.modificarValor("A1", 7);

        double result = parser.evaluarFormula("=MULTIPLICACION(A1)");
        assertEquals(7.0, result, 1e-9);
    }

    @Test
    void evaluarFormulaThrowsWhenDirectReferenceIsEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parser.evaluarFormula("=A1+2"));
        assertTrue(ex.getMessage().contains("está vacía"));
    }

    @Test
    void evaluarFormulaThrowsWhenDirectReferenceIsNotNumeric() {
        hoja.modificarValor("A1", "texto");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parser.evaluarFormula("=A1+2"));
        assertTrue(ex.getMessage().contains("no contiene un número"));
    }

    @Test
    void evaluarFormulaThrowsWhenFunctionHasInvalidFormat() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> parser.evaluarFormula("=SUMA(A1:A3"));
        assertTrue(ex.getMessage().contains("Formato de función inválido"));
    }

    @Test
    void evaluarFormulaIgnoresNonNumericValuesInsideRange() throws Exception {
        hoja.modificarValor("A1", 1);
        hoja.modificarValor("A2", "x");
        hoja.modificarValor("A3", 2);

        double result = parser.evaluarFormula("=SUMA(A1:A3)");
        assertEquals(3.0, result, 1e-9);
    }

    @Test
    void evaluarFormulaThrowsOnDivisionByZeroInFunction() {
        hoja.modificarValor("A1", 10);
        hoja.modificarValor("B1", 0);

        assertThrows(ArithmeticException.class,
                () -> parser.evaluarFormula("=DIVISION(A1,B1)"));
    }
}
