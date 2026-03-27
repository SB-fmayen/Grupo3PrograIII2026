package archivo;


import listas.NodoCelda;
import modelo.HojaCalculo;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exporta la hoja de cálculo a un archivo CSV (datos separados por comas).
 * No usa colecciones de Java, recorre la lista enlazada manualmente.
 */
public class ExportadorCSV {

    /**
     * Guarda todos los datos de la hoja en un archivo .csv.
     * Si el archivo no existe, lo crea. Si ya existe, lo sobreescribe.
     *
     * @param hoja          La hoja con los datos a exportar.
     * @param nombreArchivo El nombre del archivo, ej: "datos.csv"
     * @throws IOException Si hay un problema al escribir el archivo.
     */
    public static void exportar(HojaCalculo hoja, String nombreArchivo) throws IOException {

        // Abre el archivo. Al terminar el try, se cierra solo.
        try (FileWriter writer = new FileWriter(nombreArchivo)) {

            // Recorremos todas las celdas para saber qué tan grande es la hoja
            int maxFila = 0;
            int maxColumna = 0;

            NodoCelda actual = hoja.obtenerTodasLasCeldas().getCabeza();
            while (actual != null) {
                if (actual.celda.getFila() > maxFila)
                    maxFila = actual.celda.getFila();
                if (actual.celda.getColumna() > maxColumna)
                    maxColumna = actual.celda.getColumna();
                actual = actual.siguiente;
            }

            // Escribimos fila por fila, columna por columna
            for (int i = 0; i <= maxFila; i++) {
                StringBuilder linea = new StringBuilder();

                for (int j = 0; j <= maxColumna; j++) {
                    String ref = hoja.posicionAReferencia(i, j);
                    Object valor = hoja.obtenerValor(ref);
                    String valorStr = (valor != null) ? valor.toString() : "";

                    // Si el valor tiene comas o comillas, lo envolvemos entre comillas
                    // para que el CSV no se confunda al leerlo
                    if (valorStr.contains(",") || valorStr.contains("\""))
                        valorStr = "\"" + valorStr.replace("\"", "\"\"") + "\"";

                    linea.append(valorStr);
                    if (j < maxColumna) linea.append(","); // Coma entre columnas, no al final
                }

                writer.write(linea.toString());
                writer.write("\n"); // Salto de línea al terminar cada fila
            }
        }
    }
}