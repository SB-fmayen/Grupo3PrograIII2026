package archivo;

import fabrica.CeldaFactory;
import modelo.HojaCalculo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//Lee un archivo CSV y lo convierte en tu HojaCalculo
public class ImportadorCSV {

    public static void importar(HojaCalculo hoja, String nombreArchivo) throws IOException {
//Abre el archivo para leer línea por línea        
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            int fila = 0;

            while ((linea = reader.readLine()) != null) { //recrreo lineas, Lee cada fila del CSV
                String[] valores = parseCSVLinea(linea); //Convertir línea a columnas

                for (int columna = 0; columna < valores.length; columna++) {  //Recorrer columnas
                    String valor = valores[columna].trim(); //Limpiar texto, Quita espacios innecesarios
                    //Validar vacío, Solo guarda si hay contenido
                    if (!valor.isEmpty()) {                   
                        hoja.insertarCelda(CeldaFactory.crearCelda(fila, columna, valor)); //crea celdas, CeldaFactory crea una celta, la inserta en la hoja 
                    }

                }
//Siguiente fila
                fila++;
            }
        }

    }
//Convierte una línea de texto en valores separados correctamente, sin romper los datos.
    private static String[] parseCSVLinea(String linea) {

// Contar columnas, si estás dentro de " "
        int campos = 1;
// ¿Estoy dentro de " " o no?
        boolean entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
//Si encuentra ", entra o sale de comillas
            if (c == '"') {
                entreComillas = !entreComillas;
//Solo separa si la coma está fuera de comillas a si se sabe cuantas colunas hay
            } else if (c == ',' && !entreComillas) {
                campos++;
            }
        }

        String[] resultado = new String[campos];
        int indice = 0;
        StringBuilder actual = new StringBuilder();
        entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
//A qui lo que pasa es que si hay comillas dobles: "Hola ""Juan""" lo conviete en: Hola "Juan"
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else {
//cambio de comillas
                    entreComillas =! entreComillas;
                }
//coma fuera de comillas
            } else if (c == ',' && !entreComillas) {
                resultado[indice++] = actual.toString();
                actual = new StringBuilder();
                
            } else {
                actual.append(c);
            }
        }
        //Guarda el último valor
        resultado[indice] = actual.toString();
        return resultado;
    }
}
