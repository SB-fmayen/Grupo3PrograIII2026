package archivo;

import fabrica.CeldaFactory;
import modelo.HojaCalculo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Lee un archivo CSV y carga su contenido en la hoja de cálculo.
 */
public class ImportadorCSV {

    /**
     * Abre el archivo CSV y llena la hoja con los datos que encuentra.
     *
     * @param hoja          La hoja donde se van a cargar los datos.
     * @param nombreArchivo El archivo a leer, ej: "datos.csv"
     * @throws IOException Si hay un problema al leer el archivo.
     */
    public static void importar(HojaCalculo hoja, String nombreArchivo) throws IOException {

        // Abre el archivo línea por línea. Se cierra solo al terminar.
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            int fila = 0;

            // Leemos una línea del CSV (= una fila de la hoja)
            while ((linea = reader.readLine()) != null) {
                String[] valores = parseCSVLinea(linea); // Separamos los valores de esa fila

                for (int columna = 0; columna < valores.length; columna++) {
                    String valor = valores[columna].trim();

                    // Solo insertamos la celda si tiene algo, las vacías las ignoramos
                    if (!valor.isEmpty()) {
                        hoja.insertarCelda(CeldaFactory.crearCelda(fila, columna, valor));
                    }
                }
                fila++; // Pasamos a la siguiente fila
            }
        }
    }

    /**
     * Divide una línea de CSV en sus valores individuales.
     * Maneja correctamente los casos donde un valor tiene comas o comillas adentro,
     * por ejemplo: "Gómez, Juan" no debe partirse en dos valores.
     */
    private static String[] parseCSVLinea(String linea) {

        // Primero contamos cuántos campos tiene la línea para crear el arreglo del tamaño exacto
        int campos = 1;
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') entreComillas = !entreComillas;
            else if (c == ',' && !entreComillas) campos++; // Solo cuenta la coma si no está entre comillas
        }

        String[] resultado = new String[campos];
        int indice = 0;
        StringBuilder actual = new StringBuilder();
        entreComillas = false;

        // Recorremos la línea carácter por carácter para extraer cada valor
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"'); i++; // "" dentro de comillas significa una comilla literal
                } else {
                    entreComillas = !entreComillas; // Abre o cierra el bloque entre comillas
                }
            } else if (c == ',' && !entreComillas) {
                resultado[indice++] = actual.toString(); // Guardamos el valor actual y pasamos al siguiente
                actual = new StringBuilder();
            } else {
                actual.append(c); // Seguimos armando el valor actual
            }
        }

        resultado[indice] = actual.toString(); // Guardamos el último valor de la línea
        return resultado;
    }
}