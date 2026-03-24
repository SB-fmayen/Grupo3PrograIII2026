package ui;

import javax.swing.*;
import modelo.HojaCalculo;
import java.awt.BorderLayout;

public class PanelHoja extends JPanel {

    private HojaCalculo hoja;
    private JTable tabla;//la tabla visual
    private ModeloTabla modeloTabla;//el puente entre la tabla y los datos

    public PanelHoja(HojaCalculo hoja) {
        this.hoja = hoja;
//Usa un diseño donde puedes colocar cosas: arriba, abajo, centro, etc. Aquí usarás el centro
        setLayout(new BorderLayout());
//Crear modelo de tabla con 30 filas y 10 columnas
        modeloTabla = new ModeloTabla(hoja, 30, 10);
//La tabla usa tu modelo, JTable = visual y ModeloTabla = lógica
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25); //Altura de filas, Hace las filas más grandes
        tabla.setDefaultEditor(Object.class, new CellEditorPersonalizado(hoja)); //Cuando el usuario edita una celda = usa tu editor personalizado
//Scroll, esto hace Aparecen barras de scroll, Puedes moverte en la tabla, Se coloca en el centro
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public HojaCalculo getHoja() {
        return hoja;
    }

    public JTable getTabla() {
        return tabla;
    }

    public ModeloTabla getModeloTabla() {
        return modeloTabla;
    }

}
