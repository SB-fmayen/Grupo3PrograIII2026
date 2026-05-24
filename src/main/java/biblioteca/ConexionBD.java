/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca;

import java.sql.*;

public class ConexionBD {

    // ── CONFIGURAR ANTES DE LA PRESENTACION ──────────────────────────────────
    private static final String SERVIDOR   = "localhost";
    private static final String PUERTO     = "1433";
    private static final String BASE_DATOS = "biblioteca_db";
    private static final String USUARIO    = "sa";
    private static final String PASSWORD   = "Admin123";
    // ─────────────────────────────────────────────────────────────────────────

    private static final String URL =
        "jdbc:sqlserver://" + SERVIDOR + ":" + PUERTO +
        ";databaseName=" + BASE_DATOS +
        ";encrypt=false;trustServerCertificate=true";

    private Connection conexion;

    public void conectar() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("[BD] Conexion exitosa.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver no encontrado. Verifica el JAR.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar: " + e.getMessage(), e);
        }
    }

    public void desconectar() {
        if (conexion != null) {
            try { conexion.close(); }
            catch (SQLException e) { System.err.println("[BD] Error al cerrar: " + e.getMessage()); }
        }
    }

    public boolean estaConectado() {
        try { return conexion != null && !conexion.isClosed(); }
        catch (SQLException e) { return false; }
    }

    public int cargarLibros(ArbolAVL avl, ArbolB arbolB) {
        if (!estaConectado())
            throw new RuntimeException("Sin conexion activa.");
        int count = 0;
        String sql = "SELECT codigoLibro, isbn, titulo, autor, anio, categoria " +
                     "FROM libro ORDER BY codigoLibro";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Libro libro = new Libro(
                    rs.getInt("codigoLibro"),
                    rs.getString("isbn"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getInt("anio"),
                    rs.getString("categoria")
                );
                avl.insertar(libro);
                arbolB.insertar(libro);
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al leer: " + e.getMessage(), e);
        }
        return count;
    }

    public Connection getConexion() { return conexion; }
}