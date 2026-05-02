/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SistemaBiblioteca extends JFrame {

    private ArbolAVL  avl;
    private ArbolB    arbolB;
    private ConexionBD bd;

    private JTextArea  areaResultados;
    private JTextArea  areaEstadAVL;
    private JTextArea  areaEstadB;
    private JTextField txtCodigo, txtIsbn, txtTitulo;
    private JTextField txtAutor, txtAnio, txtCategoria;
    private JTextField txtBuscarCodigo;
    private JLabel     lblEstado;

    public SistemaBiblioteca() {
        avl    = new ArbolAVL();
        arbolB = new ArbolB();
        bd     = new ConexionBD();
        setTitle("Biblioteca Digital - AVL y Arbol B (M=5)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(5, 5));
        add(panelConexion(),   BorderLayout.NORTH);

        JSplitPane centro = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                panelOperaciones(), panelResultados());
        centro.setDividerLocation(420);
        add(centro, BorderLayout.CENTER);
        add(panelEstadisticas(), BorderLayout.SOUTH);

        lblEstado = new JLabel("  Sistema listo.");
        lblEstado.setBorder(BorderFactory.createEtchedBorder());
        add(lblEstado, BorderLayout.PAGE_END);
    }

    // ── panel conexion ────────────────────────────────────────────────────────

    private JPanel panelConexion() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        p.setBorder(BorderFactory.createTitledBorder("Conexion SQL Server"));
        p.setBackground(new Color(230, 240, 255));

        JButton btnConectar = boton("Conectar BD",    new Color(0, 120, 215));
        JButton btnCargar   = boton("Cargar Datos",   new Color(0, 160, 80));
        JButton btnLimpiar  = boton("Limpiar Todo",   new Color(200, 50, 50));

        btnConectar.addActionListener(e -> conectarBD());
        btnCargar  .addActionListener(e -> cargarDatos());
        btnLimpiar .addActionListener(e -> limpiarTodo());

        p.add(btnConectar); p.add(btnCargar);
        p.add(new JSeparator(JSeparator.VERTICAL)); p.add(btnLimpiar);
        return p;
    }

    // ── panel operaciones ─────────────────────────────────────────────────────

    private JPanel panelOperaciones() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder("Operaciones"));

        JPanel form = new JPanel(new GridLayout(6, 2, 4, 4));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Libro"));
        txtCodigo    = campo(form, "Codigo:");
        txtIsbn      = campo(form, "ISBN:");
        txtTitulo    = campo(form, "Titulo:");
        txtAutor     = campo(form, "Autor:");
        txtAnio      = campo(form, "Anio:");
        txtCategoria = campo(form, "Categoria:");

        JPanel btnCRUD = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnIns = boton("Insertar", new Color(0, 150, 80));
        JButton btnEli = boton("Eliminar", new Color(200, 50, 50));
        JButton btnLis = boton("Listar",   new Color(80, 80, 200));
        btnIns.addActionListener(e -> insertarLibro());
        btnEli.addActionListener(e -> eliminarLibro());
        btnLis.addActionListener(e -> mostrarTodo());
        btnCRUD.add(btnIns); btnCRUD.add(btnEli); btnCRUD.add(btnLis);

        JPanel panelBuscar = new JPanel(new BorderLayout(5, 0));
        panelBuscar.setBorder(BorderFactory.createTitledBorder("Buscar por Codigo"));
        txtBuscarCodigo = new JTextField();
        JButton btnBus  = boton("Buscar", new Color(0, 120, 215));
        btnBus.addActionListener(e -> buscarLibro());
        txtBuscarCodigo.addActionListener(e -> buscarLibro());
        panelBuscar.add(new JLabel("Codigo: "), BorderLayout.WEST);
        panelBuscar.add(txtBuscarCodigo,        BorderLayout.CENTER);
        panelBuscar.add(btnBus,                 BorderLayout.EAST);

        p.add(form,        BorderLayout.NORTH);
        p.add(panelBuscar, BorderLayout.CENTER);
        p.add(btnCRUD,     BorderLayout.SOUTH);
        return p;
    }

    // ── panel resultados ──────────────────────────────────────────────────────

    private JPanel panelResultados() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Resultados"));
        areaResultados = new JTextArea();
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        JButton btnLimpiarArea = new JButton("Limpiar");
        btnLimpiarArea.addActionListener(e -> areaResultados.setText(""));
        p.add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        p.add(btnLimpiarArea,                  BorderLayout.SOUTH);
        return p;
    }

    // ── panel estadisticas ────────────────────────────────────────────────────

    private JPanel panelEstadisticas() {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setBorder(BorderFactory.createTitledBorder("Estadisticas Comparativas"));
        p.setPreferredSize(new Dimension(0, 155));

        areaEstadAVL = nuevaAreaEstado(new Color(220, 240, 255));
        areaEstadB   = nuevaAreaEstado(new Color(220, 255, 220));

        JScrollPane scrollAVL = new JScrollPane(areaEstadAVL);
        scrollAVL.setBorder(BorderFactory.createTitledBorder("Arbol AVL"));
        JScrollPane scrollB = new JScrollPane(areaEstadB);
        scrollB.setBorder(BorderFactory.createTitledBorder("Arbol B (M=5)"));

        p.add(scrollAVL); p.add(scrollB);
        actualizarEstadisticas();
        return p;
    }

    private JTextArea nuevaAreaEstado(Color fondo) {
        JTextArea a = new JTextArea();
        a.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        a.setEditable(false);
        a.setBackground(fondo);
        return a;
    }

    // ── acciones ──────────────────────────────────────────────────────────────

    private void conectarBD() {
        try {
            estado("Conectando...");
            bd.conectar();
            imprimir("[OK] Conexion a SQL Server establecida.\n");
            estado("Conectado a SQL Server.");
        } catch (RuntimeException ex) {
            imprimir("[ERROR] " + ex.getMessage() + "\n");
            estado("Error de conexion.");
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatos() {
        if (!bd.estaConectado()) {
            JOptionPane.showMessageDialog(this, "Conectese primero a la BD.",
                    "Sin conexion", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            avl = new ArbolAVL(); arbolB = new ArbolB();
            long t0 = System.nanoTime();
            int n = bd.cargarLibros(avl, arbolB);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            imprimir("[OK] " + n + " libros cargados en " + ms + " ms.\n");
            estado("Datos cargados: " + n + " libros.");
            actualizarEstadisticas();
        } catch (RuntimeException ex) {
            imprimir("[ERROR] " + ex.getMessage() + "\n");
        }
    }

    private void insertarLibro() {
        try {
            int    cod  = Integer.parseInt(txtCodigo.getText().trim());
            String isbn = txtIsbn.getText().trim();
            String tit  = txtTitulo.getText().trim();
            String aut  = txtAutor.getText().trim();
            int    anio = Integer.parseInt(txtAnio.getText().trim());
            String cat  = txtCategoria.getText().trim();
            if (isbn.isEmpty() || tit.isEmpty() || aut.isEmpty() || cat.isEmpty())
                throw new IllegalArgumentException("Complete todos los campos.");

            Libro libro = new Libro(cod, isbn, tit, aut, anio, cat);
            avl.resetContadores(); arbolB.resetContadores();

            long t1 = System.nanoTime(); avl.insertar(libro);    long avlNs = System.nanoTime()-t1;
            long t2 = System.nanoTime(); arbolB.insertar(libro); long bNs  = System.nanoTime()-t2;

            imprimir(">> INSERCION: " + libro + "\n");
            imprimir(String.format("   AVL:     %,d ns | Rotaciones: %d | Altura: %d\n",
                    avlNs, avl.getRotaciones(), avl.getAltura()));
            imprimir(String.format("   Arbol B: %,d ns | Divisiones: %d | Altura: %d\n\n",
                    bNs, arbolB.getDivisiones(), arbolB.getAltura()));
            actualizarEstadisticas();
            limpiarForm();
            estado("Libro " + cod + " insertado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Codigo y Anio deben ser numericos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void buscarLibro() {
        String txt = txtBuscarCodigo.getText().trim();
        if (txt.isEmpty()) return;
        try {
            int codigo = Integer.parseInt(txt);
            avl.resetContadores(); arbolB.resetContadores();

            long t1 = System.nanoTime(); Libro rAVL = avl.buscar(codigo);    long avlNs = System.nanoTime()-t1;
            long t2 = System.nanoTime(); Libro rB   = arbolB.buscar(codigo); long bNs  = System.nanoTime()-t2;

            if (rAVL != null) {
                imprimir(">> BUSQUEDA codigo=" + codigo + "\n");
                imprimir("   Resultado: " + rAVL + "\n");
                imprimir(String.format("   AVL:     %,d ns | Comparaciones: %d\n",
                        avlNs, avl.getComparaciones()));
                imprimir(String.format("   Arbol B: %,d ns | Comparaciones: %d\n\n",
                        bNs, arbolB.getComparaciones()));
                estado("Libro " + codigo + " encontrado.");
            } else {
                imprimir(">> BUSQUEDA codigo=" + codigo + " -> NO ENCONTRADO\n\n");
                estado("Libro " + codigo + " no existe.");
            }
            actualizarEstadisticas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un codigo numerico.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarLibro() {
        String txt = txtCodigo.getText().trim();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el codigo a eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int codigo = Integer.parseInt(txt);
            avl.resetContadores(); arbolB.resetContadores();

            long t1 = System.nanoTime(); boolean okAVL = avl.eliminar(codigo);    long avlNs = System.nanoTime()-t1;
            long t2 = System.nanoTime(); boolean okB   = arbolB.eliminar(codigo); long bNs  = System.nanoTime()-t2;

            if (okAVL) {
                imprimir(">> ELIMINACION codigo=" + codigo + "\n");
                imprimir(String.format("   AVL:     %,d ns | Rotaciones: %d | Altura: %d\n",
                        avlNs, avl.getRotaciones(), avl.getAltura()));
                imprimir(String.format("   Arbol B: %,d ns | Fusiones: %d | Redistrib: %d | Altura: %d\n\n",
                        bNs, arbolB.getFusiones(), arbolB.getRedistribuciones(), arbolB.getAltura()));
                estado("Libro " + codigo + " eliminado.");
            } else {
                imprimir(">> ELIMINACION codigo=" + codigo + " -> NO ENCONTRADO\n\n");
                estado("Libro " + codigo + " no existe.");
            }
            actualizarEstadisticas();
            limpiarForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Codigo invalido.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarTodo() {
        imprimir("=== LISTADO INORDEN (AVL) ===\n");
        final int[] n = {0};
        avl.inorden(libro -> { imprimir("  " + libro + "\n"); n[0]++; });
        imprimir("Total: " + n[0] + " libros.\n\n");
        actualizarEstadisticas();
    }

    private void limpiarTodo() {
        avl = new ArbolAVL(); arbolB = new ArbolB();
        areaResultados.setText("");
        actualizarEstadisticas();
        estado("Estructuras limpiadas.");
    }

    // ── estadisticas ──────────────────────────────────────────────────────────

    private void actualizarEstadisticas() {
        areaEstadAVL.setText(String.format(
            "ARBOL AVL\n" +
            "----------------------------\n" +
            "Nodos        : %d\n" +
            "Altura       : %d\n" +
            "Rotaciones   : %d\n" +
            "Comparaciones: %d",
            avl.getTotalNodos(), avl.getAltura(),
            avl.getRotaciones(), avl.getComparaciones()));

        areaEstadB.setText(String.format(
            "ARBOL B  (M=5)\n" +
            "----------------------------\n" +
            "Libros          : %d\n" +
            "Altura          : %d\n" +
            "Divisiones      : %d\n" +
            "Fusiones        : %d\n" +
            "Redistribuciones: %d\n" +
            "Comparaciones   : %d",
            arbolB.getTotalLibros(), arbolB.getAltura(),
            arbolB.getDivisiones(), arbolB.getFusiones(),
            arbolB.getRedistribuciones(), arbolB.getComparaciones()));
    }

    // ── utilidades UI ─────────────────────────────────────────────────────────

    private JButton crearBoton(String texto, Color color) {
        return boton(texto, color);
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        return b;
    }

    private JTextField campo(JPanel panel, String etiqueta) {
        panel.add(new JLabel(etiqueta));
        JTextField tf = new JTextField();
        panel.add(tf);
        return tf;
    }

    private void imprimir(String texto) {
        areaResultados.append(texto);
        areaResultados.setCaretPosition(areaResultados.getDocument().getLength());
    }

    private void estado(String msg) { lblEstado.setText("  " + msg); }

    private void limpiarForm() {
        txtCodigo.setText(""); txtIsbn.setText(""); txtTitulo.setText("");
        txtAutor.setText(""); txtAnio.setText(""); txtCategoria.setText("");
    }
}