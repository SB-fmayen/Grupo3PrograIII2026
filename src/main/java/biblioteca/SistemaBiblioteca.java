/*
 * SistemaBiblioteca.java
 * Ventana principal del modulo de Arboles - Biblioteca Digital.
 * Gestiona las estructuras AVL y Arbol B en memoria, la conexion
 * a SQL Server y la comparacion de rendimiento entre ambos arboles.
 */
package biblioteca;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class SistemaBiblioteca extends JFrame {

    // --- Estructuras de datos en memoria ---
    private ArbolAVL   avl;
    private ArbolB     arbolB;

    // --- Capa de acceso a base de datos ---
    private ConexionBD bd;

    // --- Componentes de la interfaz ---
    private JTextArea  areaResultados;
    private JTextField txtCodigo, txtIsbn, txtTitulo;
    private JTextField txtAutor, txtAnio, txtCategoria;
    private JTextField txtBuscarCodigo;
    private JLabel     lblEstado;

    // Panel inferior de estadisticas: usa JTable para mostrar metricas en grilla
    // En lugar de JTextArea con fondo de color, una tabla es mas limpia y legible
    private DefaultTableModel modeloAVL;
    private DefaultTableModel modeloB;

    // ── Constructor ───────────────────────────────────────────────────────────

    public SistemaBiblioteca() {
        avl    = new ArbolAVL();
        arbolB = new ArbolB();
        bd     = new ConexionBD();

        setTitle("Biblioteca Digital - AVL y Arbol B (M=5)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        construirUI();
    }

    // ── Construccion general ──────────────────────────────────────────────────

    private void construirUI() {
        setLayout(new BorderLayout(4, 4));

        add(panelConexion(),    BorderLayout.NORTH);

        // Divisor horizontal: izquierda operaciones, derecha resultados
        JSplitPane centro = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            panelOperaciones(),
            panelResultados()
        );
        centro.setDividerLocation(300);
        centro.setDividerSize(4);
        add(centro, BorderLayout.CENTER);

        add(panelEstadisticas(), BorderLayout.SOUTH);

        // Barra de estado pegada al fondo de la ventana
        lblEstado = new JLabel("  Sistema listo.");
        lblEstado.setBorder(BorderFactory.createEtchedBorder());
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        add(lblEstado, BorderLayout.PAGE_END);
    }

    // ── Panel NORTE: botones de conexion ──────────────────────────────────────

    private JPanel panelConexion() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        p.setBorder(BorderFactory.createTitledBorder("Conexion SQL Server"));

        // Botones Swing estandar — sin setBackground, sin setForeground
        JButton btnConectar = new JButton("Conectar BD");
        JButton btnCargar   = new JButton("Cargar Datos");
        JButton btnAnalisis = new JButton("Analisis Comparativo");
        JButton btnLimpiar  = new JButton("Limpiar Todo");

        btnConectar.addActionListener(e -> conectarBD());
        btnCargar  .addActionListener(e -> cargarDatos());
        btnAnalisis.addActionListener(e -> mostrarAnalisisComparativo());
        btnLimpiar .addActionListener(e -> limpiarTodo());

        p.add(btnConectar);
        p.add(btnCargar);
        // Separador visual entre grupos de botones
        p.add(makeSeparator());
        p.add(btnAnalisis);
        p.add(makeSeparator());
        p.add(btnLimpiar);
        return p;
    }

    // Separador vertical delgado para la barra de botones
    private JSeparator makeSeparator() {
        JSeparator s = new JSeparator(JSeparator.VERTICAL);
        s.setPreferredSize(new Dimension(1, 20));
        return s;
    }

    // ── Panel CENTRO-IZQUIERDA: formulario + CRUD + busqueda ─────────────────

    private JPanel panelOperaciones() {
        JPanel p = new JPanel();
        // BoxLayout Y_AXIS: apila secciones sin estirarlas verticalmente
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        p.add(construirFormulario());
        p.add(Box.createVerticalStrut(4)); // espacio fijo de 4px entre secciones
        p.add(construirBotonesCRUD());
        p.add(Box.createVerticalStrut(4));
        p.add(construirBusqueda());

        // Glue ocupa el espacio sobrante abajo para que todo suba
        p.add(Box.createVerticalGlue());
        return p;
    }

    // Subpanel: 6 campos etiqueta+textfield en GridLayout
    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridLayout(6, 2, 4, 2));
        p.setBorder(BorderFactory.createTitledBorder("Datos del Libro"));

        // Etiquetas alineadas a la derecha para alinear con los campos
        txtCodigo    = campoForm(p, "Codigo:");
        txtIsbn      = campoForm(p, "ISBN:");
        txtTitulo    = campoForm(p, "Titulo:");
        txtAutor     = campoForm(p, "Autor:");
        txtAnio      = campoForm(p, "Anio:");
        txtCategoria = campoForm(p, "Categoria:");

        // Altura maxima fija: evita que BoxLayout estire este panel
        Dimension pref = p.getPreferredSize();
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height + 8));
        return p;
    }

    // Subpanel: fila de 3 botones CRUD
    private JPanel construirBotonesCRUD() {
        JPanel p = new JPanel(new GridLayout(1, 3, 4, 0));
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JButton btnIns = new JButton("Insertar");
        JButton btnEli = new JButton("Eliminar");
        JButton btnLis = new JButton("Listar");

        btnIns.addActionListener(e -> insertarLibro());
        btnEli.addActionListener(e -> eliminarLibro());
        btnLis.addActionListener(e -> mostrarTodo());

        p.add(btnIns);
        p.add(btnEli);
        p.add(btnLis);

        // Altura maxima fija: una sola fila de botones
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnIns.getPreferredSize().height + 4));
        return p;
    }

    // Subpanel: campo de busqueda en una sola linea horizontal
    private JPanel construirBusqueda() {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setBorder(BorderFactory.createTitledBorder("Buscar por Codigo"));

        txtBuscarCodigo = new JTextField();
        JButton btnBus  = new JButton("Buscar");

        // Enter en el campo tambien dispara la busqueda
        btnBus.addActionListener(e -> buscarLibro());
        txtBuscarCodigo.addActionListener(e -> buscarLibro());

        p.add(new JLabel("Codigo: "), BorderLayout.WEST);
        p.add(txtBuscarCodigo,         BorderLayout.CENTER);
        p.add(btnBus,                  BorderLayout.EAST);

        // Altura maxima fija: una sola fila con borde TitledBorder
        Dimension pref = p.getPreferredSize();
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height + 8));
        return p;
    }

    // ── Panel CENTRO-DERECHA: log de resultados ───────────────────────────────

    private JPanel panelResultados() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createTitledBorder("Resultados"));

        // Fuente monoespaciada para alinear columnas de metricas
        areaResultados = new JTextArea();
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> areaResultados.setText(""));

        p.add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        p.add(btnLimpiar,                       BorderLayout.SOUTH);
        return p;
    }

    // ── Panel SUR: estadisticas en tablas comparativas ────────────────────────

    /*
     * Usa dos JTable en lugar de JTextArea para mostrar las metricas.
     * Ventajas: alineacion automatica de columnas, sin necesidad de String.format,
     * y aspecto de tabla nativo de Swing que se ve limpio en cualquier Look & Feel.
     */
    // ── Panel SUR: estadisticas en tablas comparativas ────────────────────────

/*
 * Se reemplaza setPreferredSize() por un panel contenedor con altura minima
 * garantizada mediante GridLayout. El problema anterior era que BorderLayout.SOUTH
 * ignoraba el tamaño preferido cuando la ventana no tenia suficiente espacio vertical.
 */
private JPanel panelEstadisticas() {
    // Panel externo con borde y titulo general
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setBorder(BorderFactory.createTitledBorder("Estadisticas Comparativas"));

    // Panel interno con las dos tablas lado a lado
    JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
    p.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4));

    String[] cols = {"Metrica", "Valor"};

    // Modelo AVL: 4 metricas
    modeloAVL = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    modeloAVL.addRow(new Object[]{"Nodos",         "0"});
    modeloAVL.addRow(new Object[]{"Altura",        "0"});
    modeloAVL.addRow(new Object[]{"Rotaciones",    "0"});
    modeloAVL.addRow(new Object[]{"Comparaciones", "0"});

    // Modelo Arbol B: 6 metricas
    modeloB = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    modeloB.addRow(new Object[]{"Libros",           "0"});
    modeloB.addRow(new Object[]{"Altura",           "0"});
    modeloB.addRow(new Object[]{"Divisiones",       "0"});
    modeloB.addRow(new Object[]{"Fusiones",         "0"});
    modeloB.addRow(new Object[]{"Redistribuciones", "0"});
    modeloB.addRow(new Object[]{"Comparaciones",    "0"});

    p.add(crearPanelTabla("Arbol AVL",     modeloAVL));
    p.add(crearPanelTabla("Arbol B (M=5)", modeloB));

    wrapper.add(p, BorderLayout.CENTER);
    return wrapper;
}

// Crea un subpanel con titulo y tabla de metricas para una estructura
private JPanel crearPanelTabla(String titulo, DefaultTableModel modelo) {
    JTable tabla = new JTable(modelo);
    tabla.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
    tabla.setRowHeight(19);
    tabla.setShowGrid(false);
    tabla.setIntercellSpacing(new Dimension(0, 0));
    // Sin encabezado de columnas — la tabla es autoexplicativa
    tabla.setTableHeader(null);

    // Columna izquierda: nombre de la metrica, ancho fijo
    TableColumn colMetrica = tabla.getColumnModel().getColumn(0);
    colMetrica.setPreferredWidth(130);
    colMetrica.setMaxWidth(150);

    // Columna derecha: valor numerico alineado a la derecha
    DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
    derecha.setHorizontalAlignment(SwingConstants.RIGHT);
    tabla.getColumnModel().getColumn(1).setCellRenderer(derecha);

    // El JScrollPane contiene la tabla — sin barra de scroll visible normalmente
    JScrollPane scroll = new JScrollPane(tabla);
    // Deshabilitar el borde del scroll para que el TitledBorder del panel sea el unico
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JPanel p = new JPanel(new BorderLayout());
    // TitledBorder con el nombre de la estructura como titulo visible
    p.setBorder(BorderFactory.createTitledBorder(titulo));
    p.add(scroll, BorderLayout.CENTER);

    // Calcular la altura preferida exacta: filas * rowHeight + espacio del TitledBorder
    int filas       = modelo.getRowCount();
    int alturaTabla = filas * 19 + 6;         // 6px de padding interno
    int alturaBorde = 20;                      // altura aproximada del TitledBorder
    p.setPreferredSize(new Dimension(200, alturaTabla + alturaBorde));

    return p;
}

    // ── Acciones de negocio ───────────────────────────────────────────────────

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
            // Reiniciar estructuras antes de cargar para evitar duplicados
            avl    = new ArbolAVL();
            arbolB = new ArbolB();

            long t0 = System.nanoTime();
            int  n  = bd.cargarLibros(avl, arbolB);
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

            // Resetear contadores antes de la operacion para medir solo esta insercion
            avl.resetContadores();
            arbolB.resetContadores();

            long t1    = System.nanoTime();
            avl.insertar(libro);
            long avlNs = System.nanoTime() - t1;

            long t2   = System.nanoTime();
            arbolB.insertar(libro);
            long bNs  = System.nanoTime() - t2;

            imprimir(">> INSERCION: " + libro + "\n");
            imprimir(String.format(
                "   AVL:     %,d ns | Rotaciones: %d | Altura: %d\n",
                avlNs, avl.getRotaciones(), avl.getAltura()));
            imprimir(String.format(
                "   Arbol B: %,d ns | Divisiones: %d | Altura: %d\n\n",
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

            avl.resetContadores();
            arbolB.resetContadores();

            long t1    = System.nanoTime();
            Libro rAVL = avl.buscar(codigo);
            long avlNs = System.nanoTime() - t1;

            long t2   = System.nanoTime();
            Libro rB  = arbolB.buscar(codigo);
            long bNs  = System.nanoTime() - t2;

            if (rAVL != null) {
                imprimir(">> BUSQUEDA codigo=" + codigo + "\n");
                imprimir("   Resultado: " + rAVL + "\n");
                imprimir(String.format(
                    "   AVL:     %,d ns | Comparaciones: %d\n",
                    avlNs, avl.getComparaciones()));
                imprimir(String.format(
                    "   Arbol B: %,d ns | Comparaciones: %d\n\n",
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

            avl.resetContadores();
            arbolB.resetContadores();

            long t1       = System.nanoTime();
            boolean okAVL = avl.eliminar(codigo);
            long avlNs    = System.nanoTime() - t1;

            long t2      = System.nanoTime();
            boolean okB  = arbolB.eliminar(codigo);
            long bNs     = System.nanoTime() - t2;

            if (okAVL) {
                imprimir(">> ELIMINACION codigo=" + codigo + "\n");
                imprimir(String.format(
                    "   AVL:     %,d ns | Rotaciones: %d | Altura: %d\n",
                    avlNs, avl.getRotaciones(), avl.getAltura()));
                imprimir(String.format(
                    "   Arbol B: %,d ns | Fusiones: %d | Redistrib: %d | Altura: %d\n\n",
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
        avl.inorden(libro -> {
            imprimir("  " + libro + "\n");
            n[0]++;
        });
        imprimir("Total: " + n[0] + " libros.\n\n");
        actualizarEstadisticas();
    }

    private void limpiarTodo() {
        avl    = new ArbolAVL();
        arbolB = new ArbolB();
        areaResultados.setText("");
        actualizarEstadisticas();
        estado("Estructuras limpiadas.");
    }

    // ── Analisis comparativo ──────────────────────────────────────────────────

    /*
     * Cubre el requisito: "determinar que estructura es mas adecuada segun el
     * contexto, justificando la decision desde un enfoque tecnico."
     */
    private void mostrarAnalisisComparativo() {
        int alturaAVL  = avl.getAltura();
        int alturaB    = arbolB.getAltura();
        int nodosAVL   = avl.getTotalNodos();
        int librosB    = arbolB.getTotalLibros();
        int rotaciones = avl.getRotaciones();
        int divisiones = arbolB.getDivisiones();
        int fusiones   = arbolB.getFusiones();
        int redistrib  = arbolB.getRedistribuciones();

        int reestAVL = rotaciones;
        int reestB   = divisiones + fusiones + redistrib;

        String ganadorAltura = (alturaAVL <= alturaB) ? "Arbol AVL" : "Arbol B";
        String ganadorReest  = (reestAVL  <= reestB)  ? "Arbol AVL" : "Arbol B";

        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("     ANALISIS COMPARATIVO DE ESTRUCTURAS    \n");
        sb.append("============================================\n\n");
        sb.append(String.format("%-24s  %8s  %12s\n", "Metrica", "AVL", "Arbol B (M=5)"));
        sb.append("----------------------------------------------\n");
        sb.append(String.format("%-24s  %8d  %12d\n", "Registros cargados",   nodosAVL,   librosB));
        sb.append(String.format("%-24s  %8d  %12d\n", "Altura actual",        alturaAVL,  alturaB));
        sb.append(String.format("%-24s  %8d  %12s\n", "Rotaciones",           rotaciones, "-"));
        sb.append(String.format("%-24s  %8s  %12d\n", "Divisiones",           "-",        divisiones));
        sb.append(String.format("%-24s  %8s  %12d\n", "Fusiones",             "-",        fusiones));
        sb.append(String.format("%-24s  %8s  %12d\n", "Redistribuciones",     "-",        redistrib));
        sb.append(String.format("%-24s  %8d  %12d\n", "Total reestructurac.", reestAVL,   reestB));
        sb.append("\n");

        sb.append("---- DESCRIPCION TECNICA ----\n\n");
        sb.append("Arbol AVL:\n");
        sb.append("  - Balance estricto: factor de balance <= 1 en cada nodo.\n");
        sb.append("  - Optimo para busquedas exactas frecuentes por clave.\n");
        sb.append("  - Rotaciones O(1) por insercion/eliminacion.\n");
        sb.append("  - Altura actual: ").append(alturaAVL).append("\n\n");

        sb.append("Arbol B (M=5):\n");
        sb.append("  - Hasta 4 claves por nodo; menor altura para datasets grandes.\n");
        sb.append("  - Divisiones y fusiones afectan bloques completos de claves.\n");
        sb.append("  - Disenado para minimizar accesos a disco en BD reales.\n");
        sb.append("  - Altura actual: ").append(alturaB).append("\n\n");

        sb.append("---- RECOMENDACION ----\n\n");
        if (nodosAVL == 0) {
            sb.append("No hay datos cargados. Cargue los libros desde la BD\n");
            sb.append("y ejecute el analisis nuevamente.\n");
        } else if (nodosAVL < 100) {
            sb.append("Con ").append(nodosAVL).append(" registros, ambas estructuras\n");
            sb.append("tienen rendimiento similar. Para mas de 1000 registros\n");
            sb.append("el Arbol B ofrece ventaja por su menor altura.\n");
        } else if (alturaAVL <= alturaB) {
            sb.append("El ARBOL AVL presenta menor altura: busquedas\n");
            sb.append("exactas por codigoLibro son mas rapidas.\n");
        } else {
            sb.append("El ARBOL B presenta menor altura: mas eficiente\n");
            sb.append("en carga masiva e inserciones frecuentes.\n");
        }

        sb.append("\nMenor altura              : ").append(ganadorAltura).append("\n");
        sb.append("Menos reestructuraciones  : ").append(ganadorReest).append("\n");
        sb.append("============================================\n");

        imprimir(sb.toString());

        // Dialogo con scroll para leer el reporte completo
        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        area.setEditable(false);
        area.setRows(28);
        area.setColumns(50);

        JOptionPane.showMessageDialog(
            this,
            new JScrollPane(area),
            "Analisis Comparativo de Estructuras",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ── Actualizacion de estadisticas ─────────────────────────────────────────

    /*
     * Actualiza las dos JTable del panel sur con los valores actuales.
     * Se usa setValueAt() en lugar de recrear el modelo para evitar
     * parpadeo visual en la tabla.
     */
    private void actualizarEstadisticas() {
        // Tabla AVL: filas 0-3
        modeloAVL.setValueAt(String.valueOf(avl.getTotalNodos()),    0, 1);
        modeloAVL.setValueAt(String.valueOf(avl.getAltura()),        1, 1);
        modeloAVL.setValueAt(String.valueOf(avl.getRotaciones()),    2, 1);
        modeloAVL.setValueAt(String.valueOf(avl.getComparaciones()), 3, 1);

        // Tabla Arbol B: filas 0-5
        modeloB.setValueAt(String.valueOf(arbolB.getTotalLibros()),        0, 1);
        modeloB.setValueAt(String.valueOf(arbolB.getAltura()),             1, 1);
        modeloB.setValueAt(String.valueOf(arbolB.getDivisiones()),         2, 1);
        modeloB.setValueAt(String.valueOf(arbolB.getFusiones()),           3, 1);
        modeloB.setValueAt(String.valueOf(arbolB.getRedistribuciones()),   4, 1);
        modeloB.setValueAt(String.valueOf(arbolB.getComparaciones()),      5, 1);
    }

    // ── Utilidades de UI ──────────────────────────────────────────────────────

    // Agrega una fila etiqueta+textfield al panel de formulario
    private JTextField campoForm(JPanel panel, String etiqueta) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl);
        JTextField tf = new JTextField();
        panel.add(tf);
        return tf;
    }

    // Agrega texto al log y desplaza el scroll al ultimo renglon
    private void imprimir(String texto) {
        areaResultados.append(texto);
        areaResultados.setCaretPosition(areaResultados.getDocument().getLength());
    }

    // Actualiza la barra de estado inferior
    private void estado(String msg) {
        lblEstado.setText("  " + msg);
    }

    // Vacia los seis campos del formulario de libro
    private void limpiarForm() {
        txtCodigo.setText("");
        txtIsbn.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtAnio.setText("");
        txtCategoria.setText("");
    }
}