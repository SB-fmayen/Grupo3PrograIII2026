package ui;

import modelo.HojaCalculo;
import javax.swing.*; 
import java.awt.Component;

/**
 * Editor de celda:
 * - Cuando el usuario empieza a editar, muestra el valor RAW (la fórmula original)
 * - Al confirmar, devuelve el raw tal cual — ModeloTabla se encarga de evaluarlo
 */
public class CellEditorPersonalizado extends DefaultCellEditor {
    private final ModeloTabla modelo;

    public CellEditorPersonalizado(ModeloTabla modelo) {
        super(new JTextField());
        this.modelo = modelo;
        setClickCountToStart(2); // doble clic para editar, igual que Excel
    }

    /** Al iniciar la edición muestra el valor RAW (fórmula o número) */
    @Override
    public Component getTableCellEditorComponent(
            javax.swing.JTable table, Object value,
            boolean isSelected, int row, int column) {

        // Obtener el RAW desde la HojaCalculo, no el valor mostrado
        String raw = modelo.getRawValue(row, column);
        return super.getTableCellEditorComponent(table, raw, isSelected, row, column);
    }

    /** Devuelve el texto tal cual — ModeloTabla.setValueAt lo evaluará */
    @Override
    public Object getCellEditorValue() {
        return super.getCellEditorValue().toString().trim();
    }
}
