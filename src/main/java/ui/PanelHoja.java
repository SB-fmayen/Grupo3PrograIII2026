package ui;

import javax.swing.*;
import modelo.HojaCalculo;
import java.awt.BorderLayout;

/**
 * Panel visual que muestra una hoja de cálculo como una tabla.
 * Es lo que el usuario ve y edita directamente en pantalla.
 */
public class PanelHoja extends JPanel {
    private HojaCalculo hoja;
    private JTable tabla;
    private ModeloTabla modeloTabla;

    public PanelHoja(HojaCalculo hoja) {
        this.hoja = hoja;
        setLayout(new BorderLayout());

        // Conectamos la hoja con el modelo visual (30 filas x 10 columnas)
        modeloTabla = new ModeloTabla(hoja, 30, 10);
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);

        // Editor personalizado para manejar fórmulas y valores al editar una celda
        tabla.setDefaultEditor(Object.class, new CellEditorPersonalizado(modeloTabla));

        // Agregamos la tabla dentro de un scroll por si el contenido es grande
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public HojaCalculo getHoja() { return hoja; }
    public JTable getTabla() { return tabla; }
    public ModeloTabla getModeloTabla() { return modeloTabla; }
}