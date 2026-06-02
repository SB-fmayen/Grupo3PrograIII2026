package modelo;

import listas.ListaDouble;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HojaCalculoTest {

    @Test
    void posicionAReferenciaAndReferenciaAPosicionAreConsistent() {
        HojaCalculo hoja = new HojaCalculo();

        assertEquals("A1", hoja.posicionAReferencia(0, 0));
        assertEquals("AA1", hoja.posicionAReferencia(0, 26));

        assertArrayEquals(new int[]{0, 0}, hoja.referenciaAPosicion("A1"));
        assertArrayEquals(new int[]{4, 26}, hoja.referenciaAPosicion("AA5"));
    }

    @Test
    void modificarValorUpdatesExistingCellWithoutDuplicating() {
        HojaCalculo hoja = new HojaCalculo();

        hoja.modificarValor("B2", 5);
        hoja.modificarValor("B2", 8);

        assertEquals(1, hoja.obtenerTodasLasCeldas().getTamanio());
        assertEquals(8, hoja.obtenerValor("B2"));
    }

    @Test
    void obtenerValoresDelRangoReturnsOnlyNumericValues() {
        HojaCalculo hoja = new HojaCalculo();
        hoja.modificarValor("A1", 1);
        hoja.modificarValor("A2", "texto");
        hoja.modificarValor("A3", 3.5);

        ListaDouble valores = hoja.obtenerValoresDelRango("A1", "A3");
        assertEquals(2, valores.getTamanio());
        assertEquals(1.0, valores.get(0));
        assertEquals(3.5, valores.get(1));
    }

    @Test
    void eliminarCeldaRelinksHorizontalAndVerticalNeighbors() {
        HojaCalculo hoja = new HojaCalculo();

        hoja.modificarValor("A1", 1);
        hoja.modificarValor("B1", 2);
        hoja.modificarValor("C1", 3);
        hoja.modificarValor("A2", 4);
        hoja.modificarValor("A3", 5);

        hoja.eliminarCelda("B1");
        hoja.eliminarCelda("A2");

        Celda a1 = hoja.obtenerCelda("A1");
        assertNotNull(a1);
        assertEquals("C1", hoja.posicionAReferencia(a1.getDerecha().getFila(), a1.getDerecha().getColumna()));
        assertEquals("A3", hoja.posicionAReferencia(a1.getAbajo().getFila(), a1.getAbajo().getColumna()));
    }
}
