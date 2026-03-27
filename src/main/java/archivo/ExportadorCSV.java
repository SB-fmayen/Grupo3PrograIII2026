package archivo;

import listas.NodoCelda;
import modelo.HojaCalculo;
import java.io.FileWriter;
import java.io.IOException;

public class ExportadorCSV {

//Recibe una hoja, recibe el nombre del archivo creamos el CSV (guardamos una hoja de calculo)
    public static void exportar(HojaCalculo hoja, String nombreArchivo) throws IOException {
//habre el archivo, si no existe lo crea y se cierra automatico
        try (FileWriter writer = new FileWriter(nombreArchivo)) {

            int maxFila = 0;//busca el tamaño de la hoja
            int maxColumna = 0;

            NodoCelda actual = hoja.obtenerTodasLasCeldas().getCabeza();//recorre todas las celdas
            while (actual != null) {

//detecta la fila y columna mas grande esto define el tamaño de la tabla
                if (actual.celda.getFila() > maxFila) {
                    maxFila = actual.celda.getFila();
                }
                if (actual.celda.getColumna() > maxColumna) {
                    maxColumna = actual.celda.getColumna();
                }
                actual = actual.siguiente;
            }

            for (int i = 0; i <= maxFila; i++) { // recorre como matriz, recorre filas 


                StringBuilder linea = new StringBuilder();//Va armando cada fila del CSV

                for (int j = 0; j <= maxColumna; j++) {

//Obtener referencia tipo Excel ((0,0)=A1 o B1)
                    String ref = hoja.posicionAReferencia(i, j);

// obtener el valor, Busca el valor de esa celda                    
                    Object valor = hoja.obtenerValor(ref);

//combertir a texto, Si hay valor lo convierte o Si no → deja vacío                
                    String valorStr = (valor != null) ? valor.toString() : "";
// esto hace que las comas separen las columnas o comillas
                    if (valorStr.contains(",") || valorStr.contains("\"")) {
                        valorStr = "\"" + valorStr.replace("\"", "\"\"") + "\"";
                    }

                    linea.append(valorStr);

                    if (j < maxColumna) { // separador, agrega coma entre columnas
                        linea.append(",");
                    }
                }
//Escribir en archivo guarda la fila en el archivo               
                writer.write(linea.toString());
                writer.write("\n");

            }
        }

    }
