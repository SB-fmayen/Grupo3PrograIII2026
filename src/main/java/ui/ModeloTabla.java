package ui;

import evaluador.ParserFormulas;
import javax.swing.table.DefaultTableModel;
import modelo.HojaCalculo;

/**
 * Modelo de tabla:
 *  - La HojaCalculo guarda siempre el valor RAW (puede ser "=SUMA(A1:A3)" o "20")
 *  - La tabla muestra el resultado evaluado cuando hay fórmula
 *  - La barra de fórmulas lee el valor RAW de la HojaCalculo
 */
public class ModeloTabla extends DefaultTableModel {
    private HojaCalculo hoja;
    private ParserFormulas parser;
    private int filas;
    private int columnas;
 
    public ModeloTabla(HojaCalculo hoja, int filas, int columnas) {
        this.hoja    = hoja;
        this.parser  = new ParserFormulas(hoja);
        this.filas   = filas;
        this.columnas = columnas;
        inicializarTabla();
    }
 
    private void inicializarTabla() {
        String[] columnNames = new String[columnas];
        for (int i = 0; i < columnas; i++)
            columnNames[i] = String.valueOf((char) ('A' + i));
        setColumnIdentifiers(columnNames);
        for (int i = 0; i < filas; i++) {
            Object[] row = new Object[columnas];
            for (int j = 0; j < columnas; j++)
                row[j] = "";
            addRow(row);
        }
    }
 
    /**
     * Cuando el usuario confirma una celda:
     * 1. Guarda el valor RAW en la HojaCalculo
     * 2. Muestra en la tabla el resultado evaluado
     */
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        String raw = aValue != null ? aValue.toString().trim() : "";
 
        // Guardar RAW en el modelo de datos
        hoja.modificarValor(hoja.posicionAReferencia(row, column), raw);
 
        // Calcular que mostrar en la celda
        String mostrar = evaluar(raw);
 
        // Actualizar la vista sin volver a llamar a hoja
        super.setValueAt(mostrar, row, column);
    }
 
    /**
     * Limpia SOLO la vista de una celda sin tocar la HojaCalculo.
     * Se usa despues de eliminarCelda() para no re-insertar el nodo.
     */
    public void limpiarCeldaVista(int row, int column) {
        super.setValueAt("", row, column);
    }
 
    private String evaluar(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        if (parser.esFormula(raw)) {
            try {
                double resultado = parser.evaluarFormula(raw);
                if (resultado == Math.floor(resultado) && !Double.isInfinite(resultado))
                    return String.valueOf((long) resultado);
                return String.valueOf(resultado);
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }
        return raw;
    }
 
    /**
     * Devuelve el valor RAW guardado en HojaCalculo
     * (lo usa la barra de formulas para mostrar la formula original)
     */
    public String getRawValue(int row, int column) {
        Object v = hoja.obtenerValor(hoja.posicionAReferencia(row, column));
        return v != null ? v.toString() : "";
    }
 
    /** Recalcula toda la hoja (util tras importar CSV) */
    public void actualizarDatos() {
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                String raw = getRawValue(i, j);
                super.setValueAt(evaluar(raw), i, j);
            }
        }
        fireTableDataChanged();
    }
 
    public HojaCalculo getHoja() { return hoja; }
}
 