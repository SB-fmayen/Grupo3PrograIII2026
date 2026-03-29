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
 
    private static class NodoModeloTabla {
        String nombre; ModeloTabla modelo; NodoModeloTabla siguiente;
        NodoModeloTabla(String n, ModeloTabla m) { nombre = n; modelo = m; }
    }
 
    private static class NodoTabla {
        String nombre; JTable tabla; NodoTabla siguiente;
        NodoTabla(String n, JTable t) { nombre = n; tabla = t; }
    }
 
    private String[][] clipboard = null;
    private boolean esCorte = false;
    private JTable tablaOrigen = null;
    private int[] filasCortadas = null;
    private int[] colsCortadas  = null;
 
    public InterfazGrafica() {
        this.hojas = new ListaHojas();
        setTitle("Hoja de Calculo - Listas Enlazadas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        inicializarUI();
    }
 
    private void inicializarUI() {
        crearMenu();
 
        JPanel panelPrincipal = new JPanel(new BorderLayout());
 
        // barra de formulas
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
 
        pestanas = new JTabbedPane();
        lblEstado = new JLabel("Listo");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
 
        panelPrincipal.add(barraFormula, BorderLayout.NORTH);
        panelPrincipal.add(pestanas,     BorderLayout.CENTER);
        panelPrincipal.add(lblEstado,    BorderLayout.SOUTH);
 
        crearNuevaHoja("Hoja1");
        setContentPane(panelPrincipal);
    }
 
    // ══════════════════════════════════════════════════════════════════
    // MENU
    // ══════════════════════════════════════════════════════════════════
 
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
 
        // Archivo
        JMenu menuArchivo = new JMenu("Archivo");
 
        JMenuItem itemNuevaHoja = new JMenuItem("Nueva Hoja");
        itemNuevaHoja.addActionListener(e -> nuevaHoja());
        menuArchivo.add(itemNuevaHoja);
 
        JMenuItem itemEliminarHoja = new JMenuItem("Eliminar Hoja Actual");
        itemEliminarHoja.addActionListener(e -> eliminarHojaActual());
        menuArchivo.add(itemEliminarHoja);
 
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
 
        // Edicion
        JMenu menuEdicion = new JMenu("Edicion");
 
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
 
        JMenuItem itemEliminarCelda = new JMenuItem("Eliminar Celda (re-enlaza nodos)");
        itemEliminarCelda.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        itemEliminarCelda.addActionListener(e -> eliminarCeldaSeleccionada());
        menuEdicion.add(itemEliminarCelda);
 
        menuEdicion.addSeparator();
 
        JMenuItem itemAgregarFila = new JMenuItem("Agregar Fila");
        itemAgregarFila.addActionListener(e -> agregarFila());
        menuEdicion.add(itemAgregarFila);
 
        JMenuItem itemAgregarColumna = new JMenuItem("Agregar Columna");
        itemAgregarColumna.addActionListener(e -> agregarColumna());
        menuEdicion.add(itemAgregarColumna);
 
        menuEdicion.addSeparator();
 
        JMenuItem itemLimpiar = new JMenuItem("Limpiar Hoja");
        itemLimpiar.addActionListener(e -> limpiarHojaActual());
        menuEdicion.add(itemLimpiar);
 
        menuBar.add(menuEdicion);
        setJMenuBar(menuBar);
    }
 
    // ══════════════════════════════════════════════════════════════════
    // PUNTO 4 - Eliminar celda con re-enlace de nodos
    // ══════════════════════════════════════════════════════════════════
 
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
                    "Selecciona una celda para eliminar.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        String ref = hoja.posicionAReferencia(fila, col);
        Celda existente = hoja.obtenerCelda(ref);
 
        if (existente == null) {
            lblEstado.setText("Celda " + ref + " ya esta vacia - no hay nodo que eliminar");
            return;
        }
 
        int op = JOptionPane.showConfirmDialog(this,
                "Eliminar el nodo de la celda " + ref + " y re-enlazar nodos vecinos?",
                "Eliminar celda", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
 
        // Eliminar nodo en la estructura (re-enlaza derecha y abajo)
        hoja.eliminarCelda(ref);
 
        // Limpiar visualmente solo esa celda sin volver a guardar en la hoja
        modelo.limpiarCeldaVista(fila, col);
 
        lblEstado.setText("Nodo " + ref + " eliminado y nodos vecinos re-enlazados");
        txtBarraFormula.setText("");
        lblCeldaActual.setText(ref);
    }
 
    // ══════════════════════════════════════════════════════════════════
    // PUNTO 5 - Navegacion por nodos (funcion interna sin panel visual)
    // ══════════════════════════════════════════════════════════════════
 
    private void navegarPorNodo(boolean usarDerecha) {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return;
        String nombreHoja = pestanas.getTitleAt(idx);
        HojaCalculo hoja  = hojas.buscar(nombreHoja);
        JTable tabla       = buscarTabla(nombreHoja);
        if (hoja == null || tabla == null) return;
 
        int fila = tabla.getSelectedRow();
        int col  = tabla.getSelectedColumn();
        if (fila < 0 || col < 0) return;
 
        Celda celdaActual = hoja.obtenerCelda(hoja.posicionAReferencia(fila, col));
        if (celdaActual == null) return;
 
        Celda destino = usarDerecha ? celdaActual.getDerecha() : celdaActual.getAbajo();
        if (destino == null) return;
 
        int fd = destino.getFila();
        int cd = destino.getColumna();
        tabla.setRowSelectionInterval(fd, fd);
        tabla.setColumnSelectionInterval(cd, cd);
        tabla.scrollRectToVisible(tabla.getCellRect(fd, cd, true));
        actualizarBarraFormula(tabla, hoja);
    }
 
    // ══════════════════════════════════════════════════════════════════
    // AGREGAR FILA / COLUMNA
    // ══════════════════════════════════════════════════════════════════
 
    private void agregarFila() {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return;
        ModeloTabla modelo = buscarModelo(pestanas.getTitleAt(idx));
        if (modelo == null) return;
 
        int cols = modelo.getColumnCount();
        Object[] filaVacia = new Object[cols];
        for (int i = 0; i < cols; i++) filaVacia[i] = "";
        modelo.addRow(filaVacia);
 
        lblEstado.setText("Fila agregada - total: " + modelo.getRowCount());
    }
 
    private void agregarColumna() {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return;
        ModeloTabla modelo = buscarModelo(pestanas.getTitleAt(idx));
        if (modelo == null) return;
 
        int colActual = modelo.getColumnCount();
        String nombreCol = "";
        int c = colActual;
        while (c >= 0) {
            nombreCol = (char) ('A' + (c % 26)) + nombreCol;
            c = (c / 26) - 1;
        }
 
        modelo.addColumn(nombreCol);
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.setValueAt("", i, colActual);
        }
 
        lblEstado.setText("Columna '" + nombreCol + "' agregada - total: " + modelo.getColumnCount());
    }
 
    // ══════════════════════════════════════════════════════════════════
    // ELIMINAR HOJA
    // ══════════════════════════════════════════════════════════════════
 
    private void eliminarHojaActual() {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return;
 
        if (pestanas.getTabCount() == 1) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar la unica hoja.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        String nombre = pestanas.getTitleAt(idx);
        int op = JOptionPane.showConfirmDialog(this,
                "Eliminar la hoja '" + nombre + "'?",
                "Eliminar hoja", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
 
        pestanas.removeTabAt(idx);
        eliminarModeloPorNombre(nombre);
        eliminarTablaPorNombre(nombre);
        lblEstado.setText("Hoja '" + nombre + "' eliminada");
    }
 
    // ══════════════════════════════════════════════════════════════════
    // CREAR HOJAS
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
 
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setRowHeaderView(new NumeradorFilas(tabla));
        scroll.getRowHeader().setPreferredSize(new Dimension(40, 0));
 
        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) actualizarBarraFormula(tabla, hoja);
        });
        tabla.getColumnModel().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) actualizarBarraFormula(tabla, hoja);
        });
 
        bindKey(tabla, KeyEvent.VK_C, "copiar", () -> copiarSeleccion());
        bindKey(tabla, KeyEvent.VK_X, "cortar", () -> cortarSeleccion());
        bindKey(tabla, KeyEvent.VK_V, "pegar",  () -> pegarSeleccion());
 
        tabla.getInputMap(JComponent.WHEN_FOCUSED)
             .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        tabla.getActionMap().put("eliminar", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { eliminarCeldaSeleccionada(); }
        });
 
        // Menu contextual clic derecho
        JPopupMenu popup = new JPopupMenu();
        addPopupItem(popup, "Copiar  Ctrl+C",                        () -> copiarSeleccion());
        addPopupItem(popup, "Cortar  Ctrl+X",                        () -> cortarSeleccion());
        addPopupItem(popup, "Pegar   Ctrl+V",                        () -> pegarSeleccion());
        popup.addSeparator();
        addPopupItem(popup, "Eliminar celda (re-enlaza nodos)  Del", () -> eliminarCeldaSeleccionada());
        popup.addSeparator();
        addPopupItem(popup, "Agregar Fila",                          () -> agregarFila());
        addPopupItem(popup, "Agregar Columna",                       () -> agregarColumna());
        tabla.setComponentPopupMenu(popup);
 
        pestanas.addTab(nombre, scroll);
        hojas.agregar(nombre, hoja);
        agregarModelo(nombre, modelo);
        agregarTabla(nombre, tabla);
        lblEstado.setText("Hoja '" + nombre + "' creada");
    }
 
    // ══════════════════════════════════════════════════════════════════
    // UTILIDADES UI
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
    // COPIAR / CORTAR / PEGAR
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
        lblEstado.setText("Copiado (" + filas.length + " fila(s) x " + cols.length + " col(s))");
    }
 
    private void cortarSeleccion() {
        JTable tabla = getTablaActual();
        if (tabla == null) return;
        copiarSeleccion();
        esCorte       = true;
        tablaOrigen   = tabla;
        filasCortadas = tabla.getSelectedRows();
        colsCortadas  = tabla.getSelectedColumns();
        lblEstado.setText("Cortado - selecciona destino y pega");
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
    // LISTAS ENLAZADAS INTERNAS
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
 
    private void eliminarModeloPorNombre(String nombre) {
        if (cabezaModelos == null) return;
        if (cabezaModelos.nombre.equals(nombre)) { cabezaModelos = cabezaModelos.siguiente; return; }
        NodoModeloTabla a = cabezaModelos;
        while (a.siguiente != null) {
            if (a.siguiente.nombre.equals(nombre)) { a.siguiente = a.siguiente.siguiente; return; }
            a = a.siguiente;
        }
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
 
    private void eliminarTablaPorNombre(String nombre) {
        if (cabezaTablas == null) return;
        if (cabezaTablas.nombre.equals(nombre)) { cabezaTablas = cabezaTablas.siguiente; return; }
        NodoTabla a = cabezaTablas;
        while (a.siguiente != null) {
            if (a.siguiente.nombre.equals(nombre)) { a.siguiente = a.siguiente.siguiente; return; }
            a = a.siguiente;
        }
    }
 
    // ══════════════════════════════════════════════════════════════════
    // CSV Y LIMPIEZA
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
                "Limpiar la hoja actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            String nombre = pestanas.getTitleAt(pestanas.getSelectedIndex());
            hojas.buscar(nombre).obtenerTodasLasCeldas().limpiar();
            buscarModelo(nombre).actualizarDatos();
            lblEstado.setText("Hoja limpiada");
        }
    }
 
    // ══════════════════════════════════════════════════════════════════
    // NUMERADOR DE FILAS
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