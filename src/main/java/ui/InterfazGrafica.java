/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import listas.ListaHojas;
import modelo.Celda;
import modelo.HojaCalculo;
import archivo.ExportadorCSV;
import archivo.ImportadorCSV;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

/**
 * Clase principal de la interfaz gráfica.
 * Extiende JFrame para crear la ventana principal de la aplicación.
 * Gestiona múltiples hojas de cálculo mediante pestañas,
 * incluye barra de fórmulas estilo Excel, numeración de filas,
 * soporte para cortar, copiar y pegar celdas,
 * eliminación de celdas con re-enlace de nodos (Punto 4),
 * y navegación real por los enlaces derecha/abajo de Celda (Punto 5).
 */
public class InterfazGrafica extends JFrame {

    private JTabbedPane pestanas;
    private ListaHojas hojas;
    private JLabel lblEstado;
    private JLabel lblCeldaActual;
    private JTextField txtBarraFormula;

    private NodoModeloTabla cabezaModelos = null;
    private NodoTabla cabezaTablas = null;

    /**
     * Nodo de lista enlazada propia para almacenar ModeloTabla.
     */
    private static class NodoModeloTabla {
        String nombre; ModeloTabla modelo; NodoModeloTabla siguiente;
        NodoModeloTabla(String n, ModeloTabla m) { nombre = n; modelo = m; }
    }

    /**
     * Nodo de lista enlazada propia para almacenar JTable.
     */
    private static class NodoTabla {
        String nombre; JTable tabla; NodoTabla siguiente;
        NodoTabla(String n, JTable t) { nombre = n; tabla = t; }
    }

    // portapapeles interno para cortar/copiar/pegar
    private String[][] clipboard = null;
    private boolean esCorte = false;
    private JTable tablaOrigen = null;
    private int[] filasCortadas = null;
    private int[] colsCortadas  = null;

    public InterfazGrafica() {
        this.hojas = new ListaHojas();
        setTitle("Hoja de Cálculo - Listas Enlazadas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        inicializarUI();
    }

    private void inicializarUI() {
        crearMenu();

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // ── barra de fórmulas ──────────────────────────────────────
        JPanel barraFormula = new JPanel(new BorderLayout(4, 0));
        barraFormula.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        barraFormula.setBackground(new Color(240, 240, 240));

        lblCeldaActual = new JLabel("A1");
        lblCeldaActual.setPreferredSize(new Dimension(60, 24));
        lblCeldaActual.setHorizontalAlignment(SwingConstants.CENTER);
        lblCeldaActual.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        lblCeldaActual.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel lblFx = new JLabel("  fx ");
        lblFx.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 13));
        lblFx.setForeground(new Color(0, 100, 200));

        txtBarraFormula = new JTextField();
        txtBarraFormula.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBarraFormula.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        txtBarraFormula.setEditable(false);

        JPanel izqBarra = new JPanel(new BorderLayout(2, 0));
        izqBarra.setOpaque(false);
        izqBarra.add(lblCeldaActual, BorderLayout.WEST);
        izqBarra.add(lblFx, BorderLayout.EAST);

        barraFormula.add(izqBarra, BorderLayout.WEST);
        barraFormula.add(txtBarraFormula, BorderLayout.CENTER);

        // ── panel de navegación por nodos (Punto 5) ───────────────
        JPanel panelNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panelNav.setBackground(new Color(230, 230, 230));
        panelNav.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                "Navegación por nodos enlazados"));

        JButton btnDerecha = new JButton("▶ Derecha");
        JButton btnAbajo   = new JButton("▼ Abajo");
        JLabel  lblNodo    = new JLabel("Celda actual por nodo: —");
        lblNodo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblNodo.setForeground(new Color(0, 80, 160));

        btnDerecha.addActionListener(e -> navegarPorNodo(true,  lblNodo));
        btnAbajo  .addActionListener(e -> navegarPorNodo(false, lblNodo));

        panelNav.add(new JLabel("Usar enlaces Celda.derecha / Celda.abajo:"));
        panelNav.add(btnDerecha);
        panelNav.add(btnAbajo);
        panelNav.add(lblNodo);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(barraFormula, BorderLayout.NORTH);
        panelNorte.add(panelNav,     BorderLayout.SOUTH);

        // ── pestañas y estado ──────────────────────────────────────
        pestanas = new JTabbedPane();
        lblEstado = new JLabel("Listo");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);
        panelPrincipal.add(pestanas,   BorderLayout.CENTER);
        panelPrincipal.add(lblEstado,  BorderLayout.SOUTH);

        crearNuevaHoja("Hoja1");
        setContentPane(panelPrincipal);
    }

    // ══════════════════════════════════════════════════════════════════
    // PUNTO 5 — Navegación real por los enlaces derecha/abajo de Celda
    // ══════════════════════════════════════════════════════════════════

    /**
     * Navega por los enlaces estructurales de la clase Celda:
     *   - derecha: siguiente celda en la misma fila
     *   - abajo:   siguiente celda en la misma columna
     *
     * Demuestra que la hoja se puede recorrer siguiendo los punteros
     * de nodo en nodo, sin usar índices de arreglo.
     *
     * @param usarDerecha true → sigue Celda.derecha  |  false → sigue Celda.abajo
     * @param lblNodo     label donde se muestra la celda destino
     */
    private void navegarPorNodo(boolean usarDerecha, JLabel lblNodo) {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return;
        String nombreHoja = pestanas.getTitleAt(idx);
        HojaCalculo hoja  = hojas.buscar(nombreHoja);
        JTable tabla       = buscarTabla(nombreHoja);
        if (hoja == null || tabla == null) return;

        int fila = tabla.getSelectedRow();
        int col  = tabla.getSelectedColumn();
        if (fila < 0 || col < 0) {
            lblNodo.setText("Selecciona una celda primero");
            return;
        }

        String refActual = hoja.posicionAReferencia(fila, col);
        Celda celdaActual = hoja.obtenerCelda(refActual);

        if (celdaActual == null) {
            lblNodo.setText("Celda " + refActual + " no tiene datos — no hay nodo");
            return;
        }

        // Seguir el enlace estructural de la Celda
        Celda destino = usarDerecha ? celdaActual.getDerecha() : celdaActual.getAbajo();

        if (destino == null) {
            String dir = usarDerecha ? "derecha" : "abajo";
            lblNodo.setText("Celda." + dir + " == null (no hay nodo siguiente)");
            return;
        }

        // Mover la selección de la JTable al nodo destino
        int filaDestino = destino.getFila();
        int colDestino  = destino.getColumna();
        tabla.setRowSelectionInterval(filaDestino, filaDestino);
        tabla.setColumnSelectionInterval(colDestino, colDestino);
        tabla.scrollRectToVisible(tabla.getCellRect(filaDestino, colDestino, true));

        String refDestino = hoja.posicionAReferencia(filaDestino, colDestino);
        String enlace     = usarDerecha ? "derecha" : "abajo";
        lblNodo.setText(refActual + ".get" + (usarDerecha ? "Derecha" : "Abajo")
                + "() → " + refDestino
                + "  [valor: " + destino.getValor() + "]");

        actualizarBarraFormula(tabla, hoja);
        lblEstado.setText("Navegación por nodo (" + enlace + "): "
                + refActual + " → " + refDestino);
    }

    // ══════════════════════════════════════════════════════════════════
    // PUNTO 4 — Eliminar celda con re-enlace de nodos desde la UI
    // ══════════════════════════════════════════════════════════════════

    /**
     * Elimina la celda seleccionada llamando a HojaCalculo.eliminarCelda(),
     * que repara los enlaces horizontales (derecha) y verticales (abajo)
     * antes de quitar el nodo de la ListaEnlazadaCeldas.
     *
     * Luego refresca la vista de la tabla para reflejar la celda vacía.
     */
    private void eliminarCeldaSeleccionada() {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return;
        String nombreHoja = pestanas.getTitleAt(idx);
        HojaCalculo hoja  = hojas.buscar(nombreHoja);
        JTable tabla       = buscarTabla(nombreHoja);
        ModeloTabla modelo = buscarModelo(nombreHoja);
        if (hoja == null || tabla == null || modelo == null) return;

        int fila = tabla.getSelectedRow();
        int col  = tabla.getSelectedColumn();
        if (fila < 0 || col < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una celda para eliminar.", "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ref = hoja.posicionAReferencia(fila, col);
        Celda existente = hoja.obtenerCelda(ref);

        if (existente == null) {
            lblEstado.setText("Celda " + ref + " ya está vacía — no hay nodo que eliminar");
            return;
        }

        // Confirmar antes de eliminar
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el nodo de la celda " + ref + " y re-enlazar nodos vecinos?",
                "Eliminar celda", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;

        // Llamar al método que re-enlaza y elimina el nodo
        hoja.eliminarCelda(ref);

        // Refrescar solo esa celda en la vista
        modelo.actualizarDatos();

        lblEstado.setText("Nodo " + ref + " eliminado y nodos vecinos re-enlazados");
        txtBarraFormula.setText("");
        lblCeldaActual.setText(ref);
    }

    // ══════════════════════════════════════════════════════════════════
    // Menú
    // ══════════════════════════════════════════════════════════════════

    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemNuevaHoja = new JMenuItem("Nueva Hoja");
        itemNuevaHoja.addActionListener(e -> nuevaHoja());
        menuArchivo.add(itemNuevaHoja);
        menuArchivo.addSeparator();
        JMenuItem itemExportar = new JMenuItem("Exportar a CSV");
        itemExportar.addActionListener(e -> exportarCSV());
        menuArchivo.add(itemExportar);
        JMenuItem itemImportar = new JMenuItem("Importar desde CSV");
        itemImportar.addActionListener(e -> importarCSV());
        menuArchivo.add(itemImportar);
        menuArchivo.addSeparator();
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);

        // Edición
        JMenu menuEdicion = new JMenu("Edición");

        JMenuItem itemCopiar = new JMenuItem("Copiar");
        itemCopiar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        itemCopiar.addActionListener(e -> copiarSeleccion());
        menuEdicion.add(itemCopiar);

        JMenuItem itemCortar = new JMenuItem("Cortar");
        itemCortar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        itemCortar.addActionListener(e -> cortarSeleccion());
        menuEdicion.add(itemCortar);

        JMenuItem itemPegar = new JMenuItem("Pegar");
        itemPegar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        itemPegar.addActionListener(e -> pegarSeleccion());
        menuEdicion.add(itemPegar);

        menuEdicion.addSeparator();

        // ── PUNTO 4: Eliminar celda desde el menú ─────────────────
        JMenuItem itemEliminar = new JMenuItem("Eliminar Celda (re-enlaza nodos)");
        itemEliminar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemEliminar.addActionListener(e -> eliminarCeldaSeleccionada());
        menuEdicion.add(itemEliminar);

        menuEdicion.addSeparator();
        JMenuItem itemLimpiar = new JMenuItem("Limpiar Hoja");
        itemLimpiar.addActionListener(e -> limpiarHojaActual());
        menuEdicion.add(itemLimpiar);
        menuBar.add(menuEdicion);

        setJMenuBar(menuBar);
    }

    // ══════════════════════════════════════════════════════════════════
    // Creación de hojas
    // ══════════════════════════════════════════════════════════════════

    private void nuevaHoja() {
        DialogoNuevaHoja dialogo = new DialogoNuevaHoja(this);
        String nombre = dialogo.mostrar();
        if (nombre != null && !nombre.isEmpty()) crearNuevaHoja(nombre);
    }

    private void crearNuevaHoja(String nombre) {
        HojaCalculo hoja = new HojaCalculo();
        ModeloTabla modelo = new ModeloTabla(hoja, 30, 10);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tabla.setDefaultEditor(Object.class, new CellEditorPersonalizado(modelo));
        tabla.setGridColor(new Color(200, 200, 200));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setReorderingAllowed(false);

        // ── scroll con numerador de filas ──────────────────────────
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setRowHeaderView(new NumeradorFilas(tabla));
        scroll.getRowHeader().setPreferredSize(new Dimension(40, 0));

        // ── listener de selección → barra de fórmulas ─────────────
        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) actualizarBarraFormula(tabla, hoja);
        });
        tabla.getColumnModel().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) actualizarBarraFormula(tabla, hoja);
        });

        // ── atajos de teclado ──────────────────────────────────────
        bindKey(tabla, KeyEvent.VK_C,      "copiar",   () -> copiarSeleccion());
        bindKey(tabla, KeyEvent.VK_X,      "cortar",   () -> cortarSeleccion());
        bindKey(tabla, KeyEvent.VK_V,      "pegar",    () -> pegarSeleccion());
        // PUNTO 4: DELETE elimina el nodo y re-enlaza
        tabla.getInputMap(JComponent.WHEN_FOCUSED)
             .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        tabla.getActionMap().put("eliminar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { eliminarCeldaSeleccionada(); }
        });

        // ── menú contextual (clic derecho) ─────────────────────────
        JPopupMenu popup = new JPopupMenu();
        addPopupItem(popup, "Copiar  Ctrl+C",  () -> copiarSeleccion());
        addPopupItem(popup, "Cortar  Ctrl+X",  () -> cortarSeleccion());
        addPopupItem(popup, "Pegar   Ctrl+V",  () -> pegarSeleccion());
        popup.addSeparator();
        // PUNTO 4: opción en clic derecho para eliminar nodo
        addPopupItem(popup, "Eliminar celda (re-enlaza nodos)  Del",
                () -> eliminarCeldaSeleccionada());
        tabla.setComponentPopupMenu(popup);

        pestanas.addTab(nombre, scroll);
        hojas.agregar(nombre, hoja);
        agregarModelo(nombre, modelo);
        agregarTabla(nombre, tabla);
        lblEstado.setText("Hoja '" + nombre + "' creada");
    }

    // ══════════════════════════════════════════════════════════════════
    // Utilidades de UI
    // ══════════════════════════════════════════════════════════════════

    private void bindKey(JTable t, int vk, String nombre, Runnable accion) {
        t.getInputMap(JComponent.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(vk, InputEvent.CTRL_DOWN_MASK), nombre);
        t.getActionMap().put(nombre, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { accion.run(); }
        });
    }

    private void addPopupItem(JPopupMenu m, String txt, Runnable accion) {
        JMenuItem item = new JMenuItem(txt);
        item.addActionListener(e -> accion.run());
        m.add(item);
    }

    private void actualizarBarraFormula(JTable tabla, HojaCalculo hoja) {
        int fila = tabla.getSelectedRow();
        int col  = tabla.getSelectedColumn();
        if (fila < 0 || col < 0) return;
        String ref = hoja.posicionAReferencia(fila, col);
        lblCeldaActual.setText(ref);
        String nombreHoja = pestanas.getTitleAt(pestanas.getSelectedIndex());
        ModeloTabla mt = buscarModelo(nombreHoja);
        if (mt != null)
            txtBarraFormula.setText(mt.getRawValue(fila, col));
        else {
            Object val = tabla.getValueAt(fila, col);
            txtBarraFormula.setText(val != null ? val.toString() : "");
        }
    }

    private JTable getTablaActual() {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return null;
        return buscarTabla(pestanas.getTitleAt(idx));
    }

    // ══════════════════════════════════════════════════════════════════
    // Copiar / Cortar / Pegar
    // ══════════════════════════════════════════════════════════════════

    private void copiarSeleccion() {
        JTable tabla = getTablaActual();
        if (tabla == null) return;
        int[] filas = tabla.getSelectedRows();
        int[] cols  = tabla.getSelectedColumns();
        if (filas.length == 0 || cols.length == 0) return;
        clipboard = new String[filas.length][cols.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filas.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                Object v = tabla.getValueAt(filas[i], cols[j]);
                clipboard[i][j] = v != null ? v.toString() : "";
                sb.append(clipboard[i][j]);
                if (j < cols.length - 1) sb.append("\t");
            }
            sb.append("\n");
        }
        esCorte = false;
        Toolkit.getDefaultToolkit().getSystemClipboard()
               .setContents(new StringSelection(sb.toString()), null);
        lblEstado.setText("Copiado (" + filas.length + " fila(s) × " + cols.length + " col(s))");
    }

    private void cortarSeleccion() {
        JTable tabla = getTablaActual();
        if (tabla == null) return;
        copiarSeleccion();
        esCorte       = true;
        tablaOrigen   = tabla;
        filasCortadas = tabla.getSelectedRows();
        colsCortadas  = tabla.getSelectedColumns();
        lblEstado.setText("Cortado — selecciona destino y pega");
    }

    private void pegarSeleccion() {
        JTable tabla = getTablaActual();
        if (tabla == null || clipboard == null) return;
        int fInicio = tabla.getSelectedRow();
        int cInicio = tabla.getSelectedColumn();
        if (fInicio < 0 || cInicio < 0) return;
        for (int i = 0; i < clipboard.length; i++)
            for (int j = 0; j < clipboard[i].length; j++) {
                int fd = fInicio + i, cd = cInicio + j;
                if (fd < tabla.getRowCount() && cd < tabla.getColumnCount())
                    tabla.setValueAt(clipboard[i][j], fd, cd);
            }
        if (esCorte && tablaOrigen != null) {
            for (int i = 0; i < filasCortadas.length; i++)
                for (int j = 0; j < colsCortadas.length; j++)
                    tablaOrigen.setValueAt("", filasCortadas[i], colsCortadas[j]);
            esCorte = false; tablaOrigen = null;
        }
        int idx = pestanas.getSelectedIndex();
        if (idx >= 0) {
            HojaCalculo hoja = hojas.buscar(pestanas.getTitleAt(idx));
            if (hoja != null) actualizarBarraFormula(tabla, hoja);
        }
        lblEstado.setText("Pegado correctamente");
    }

    // ══════════════════════════════════════════════════════════════════
    // Listas enlazadas internas de modelos y tablas
    // ══════════════════════════════════════════════════════════════════

    private void agregarModelo(String nombre, ModeloTabla modelo) {
        NodoModeloTabla nuevo = new NodoModeloTabla(nombre, modelo);
        if (cabezaModelos == null) { cabezaModelos = nuevo; return; }
        NodoModeloTabla a = cabezaModelos;
        while (a.siguiente != null) a = a.siguiente;
        a.siguiente = nuevo;
    }

    private ModeloTabla buscarModelo(String nombre) {
        NodoModeloTabla a = cabezaModelos;
        while (a != null) { if (a.nombre.equals(nombre)) return a.modelo; a = a.siguiente; }
        return null;
    }

    private void agregarTabla(String nombre, JTable tabla) {
        NodoTabla nuevo = new NodoTabla(nombre, tabla);
        if (cabezaTablas == null) { cabezaTablas = nuevo; return; }
        NodoTabla a = cabezaTablas;
        while (a.siguiente != null) a = a.siguiente;
        a.siguiente = nuevo;
    }

    private JTable buscarTabla(String nombre) {
        NodoTabla a = cabezaTablas;
        while (a != null) { if (a.nombre.equals(nombre)) return a.tabla; a = a.siguiente; }
        return null;
    }

    // ══════════════════════════════════════════════════════════════════
    // CSV y limpieza
    // ══════════════════════════════════════════════════════════════════

    private void exportarCSV() {
        String nombre = pestanas.getTitleAt(pestanas.getSelectedIndex());
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String ruta = fc.getSelectedFile().getAbsolutePath();
                if (!ruta.endsWith(".csv")) ruta += ".csv";
                ExportadorCSV.exportar(hojas.buscar(nombre), ruta);
                lblEstado.setText("Exportado: " + ruta);
                JOptionPane.showMessageDialog(this, "Hoja exportada exitosamente");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String nombre = pestanas.getTitleAt(pestanas.getSelectedIndex());
                ImportadorCSV.importar(hojas.buscar(nombre), fc.getSelectedFile().getAbsolutePath());
                buscarModelo(nombre).actualizarDatos();
                lblEstado.setText("Importado: " + fc.getSelectedFile().getName());
                JOptionPane.showMessageDialog(this, "Datos importados exitosamente");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarHojaActual() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Limpiar la hoja actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            String nombre = pestanas.getTitleAt(pestanas.getSelectedIndex());
            hojas.buscar(nombre).obtenerTodasLasCeldas().limpiar();
            buscarModelo(nombre).actualizarDatos();
            lblEstado.setText("Hoja limpiada");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // Numerador de filas
    // ══════════════════════════════════════════════════════════════════

    private static class NumeradorFilas extends JList<String> {
        private final JTable tabla;

        NumeradorFilas(JTable tabla) {
            this.tabla = tabla;
            setFixedCellWidth(40);
            setFixedCellHeight(tabla.getRowHeight());
            setBackground(new Color(242, 242, 242));
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
            setFont(new Font("SansSerif", Font.PLAIN, 11));

            DefaultListModel<String> model = new DefaultListModel<>();
            for (int i = 1; i <= tabla.getRowCount(); i++) model.addElement(String.valueOf(i));
            setModel(model);

            setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(
                            list, value, index, false, false);
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setBackground(new Color(242, 242, 242));
                    lbl.setForeground(Color.DARK_GRAY);
                    lbl.setBorder(BorderFactory.createMatteBorder(
                            0, 0, 1, 1, new Color(200, 200, 200)));
                    if (tabla.getSelectedRow() == index) {
                        lbl.setBackground(new Color(180, 200, 230));
                        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                    }
                    return lbl;
                }
            });

            tabla.getSelectionModel().addListSelectionListener(e -> repaint());
        }
    }
}