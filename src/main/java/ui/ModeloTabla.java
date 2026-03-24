package ui;

import javax.swing.table.DefaultTableModel;
import modelo.HojaCalculo;

public class ModeloTabla extends DefaultTableModel{

    private HojaCalculo hoja;
    private int filas;
    private int columnas;

    public ModeloTabla(HojaCalculo hoja, int filas, int columnas) {
        this.hoja = hoja;
        this.filas = filas;
        this.columnas = columnas;
        inicializarTabla();
    }
//Este método arma la tabla visual desde cero
    private void inicializarTabla() {
        String[] columnNames = new String[columnas];//Crear nombres de columnas

        for (int i = 0; i < columnas; i++) {
            columnNames[i] = String.valueOf((char) ('A' + i));//Generar letras (tipo Excel)
        }
//Asignar nombres, Le pone los títulos a la tabla
        setColumnIdentifiers(columnNames);
//Llenar filas, Recorre filas
        for (int i = 0; i < filas; i++) {
//Crear fila
            String[] row = new String[columnas];

            for (int j = 0; j < columnas; j++) {
//Obtener valores (0,0) = A1
                Object val = hoja.obtenerValor(hoja.posicionAReferencia(i, j));
//Guardar en la tabla, Si hay dato = lo muestra, Si no = vacío
                row[j] = val != null ? val.toString() : "";
            }
//Agregar fila
            addRow(row);
        }
    }

//Esto se ejecuta cuando el usuario edita una celda en la tabla
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);//Actualiza la tabla visual
        hoja.modificarValor(hoja.posicionAReferencia(row, column), aValue);//Actualiza tu modelo 
    }

//Sirve para refrescar la tabla
    public void actualizarDatos() {
        for (int i = 0; i < filas; i++) { //Recorre todo

            for (int j = 0; j < columnas; j++) {
                Object val = hoja.obtenerValor(hoja.posicionAReferencia(i, j)); //Obtiene valores actualizados

                super.setValueAt(val != null ? val.toString() : "", i, j); //Actualiza visual
            }
        }

        fireTableDataChanged();//Notifica cambios para que se actualice
    }

}
