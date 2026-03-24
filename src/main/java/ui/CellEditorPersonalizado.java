package ui;

import evaluador.ParserFormulas;
import modelo.HojaCalculo;
import javax.swing.*;

//Es un editor de celdas personalizado para tu JTable, Es un editor de celdas personalizado para tu JTable
//DefaultCellEditor: estás modificando el comportamiento normal de edición
public class CellEditorPersonalizado extends DefaultCellEditor {

    private HojaCalculo hoja;
    private ParserFormulas parser; //quien entiende fórmulas

    public CellEditorPersonalizado(HojaCalculo hoja) {       
        super(new JTextField());//Define que el editor será un campo de texto
        this.hoja = hoja;
        this.parser = new ParserFormulas(hoja);//El parser: detecta fórmulas y las evalúa
    }

//Este método se ejecuta cuando: el usuario TERMINA de editar una celda    
    @Override
    public Object getCellEditorValue() {
//Obtener lo que escribió el usuario = SUMA(A1,B1)        
        String valor = super.getCellEditorValue().toString();
        if (parser.esFormula(valor)) { //Verificar si es fórmula =A1+B1 si 10 no
            try {
//Si ES fórmula. Evalúa la fórmula, Convierte resultado a texto (=A1+A2) y Lo devuelve = (15)
                return String.valueOf(parser.evaluarFormula(valor));

            } catch (Exception e) { //Si la fórmula falla muestra el mensaje
                JOptionPane.showMessageDialog(null, "Error en fórmula: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return valor;//No rompe el programa
            }
        }

        return valor; //Si NO es fórmula Solo devuelve lo que escribió
    }

}
