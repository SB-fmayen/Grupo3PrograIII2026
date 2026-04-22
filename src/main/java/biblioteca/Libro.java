package biblioteca;

public class Libro implements Comparable<Libro> {

        private int codigoLibro;
        private String isbn;
        private String titulo;
        private String autor;
        private int anio;
        private String categoria;

        public Libro(int codigoLibro, String isbn, String titulo, String autor, int anio, String categoria) {
            this.codigoLibro = codigoLibro;
            this.isbn = isbn;
            this.titulo = titulo;
            this.autor = autor;
            this.anio = anio;
            this.categoria = categoria;
        }
        
        public int getCodigoLibro() { return codigoLibro; } //Sirven para obtener los datos
        public String getIsbn() {return isbn;}
        public String getTitulo() {return titulo;}
        public String getAutor() {return autor;}
        public int getAnio() {return anio;}
        public String getCategoria() {return categoria;}
        
        //Comparable, permite ordenar libros
        @Override 
        public int compareTo(Libro otro) {
            return Integer.compare(this.codigoLibro, otro.codigoLibro); //Compara por codigoLibro
        }
        
        //Define cómo se imprime el objeto
        @Override
        public String toString(){
            return String.format("[%d] \"%s\" - %s (%d) | ISBN: %s | Cat: %s", codigoLibro, titulo, autor, anio, isbn, categoria);
        } 
        
    }
