/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import listas.ListaHojas;
import modelo.HojaCalculo;
import archivo.ExportadorCSV;
import archivo.ImportadorCSV;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;


/**
 *
 * @author ALFREDO
 */

/**
 * Ventana principal de la aplicación de hoja de cálculo.
 * Maneja pestañas, barra de fórmulas, numeración de filas
 * y portapapeles interno para cortar/copiar/pegar.
 */
public class InterfazGrafica extends JFrame {

    private JTabbedPane pestanas;       // panel de pestañas (una por hoja)
    private ListaHojas hojas;           // lista enlazada con las hojas lógicas
    private JLabel lblEstado;           // barra de estado inferior
    private JLabel lblCeldaActual;      // muestra referencia activa, ej. "B3"
    private JTextField txtBarraFormula; // muestra la fórmula/valor crudo de la celda

    // listas enlazadas propias para relacionar nombre ↔ modelo/tabla
    private NodoModeloTabla cabezaModelos = null;
    private NodoTabla cabezaTablas = null;

    /** Nodo que vincula el nombre de una hoja con su ModeloTabla. */
    private static class NodoModeloTabla {
        String nombre; ModeloTabla modelo; NodoModeloTabla siguiente;
        NodoModeloTabla(String n, ModeloTabla m) { nombre = n; modelo = m; }
    }

    /** Nodo que vincula el nombre de una hoja con su JTable visual. */
    private static class NodoTabla {
        String nombre; JTable tabla; NodoTabla siguiente;
        NodoTabla(String n, JTable t) { nombre = n; tabla = t; }
    }

    // portapapeles interno: guarda lo copiado/cortado hasta que se pegue
    private String[][] clipboard = null;  // valores copiados
    private boolean esCorte = false;       // true si fue Ctrl+X (no Ctrl+C)
    private JTable tablaOrigen = null;     // tabla de donde se cortó
    private int[] filasCortadas = null;    // filas que se deben borrar al pegar
    private int[] colsCortadas  = null;    // columnas que se deben borrar al pegar

    /**
     * Inicializa la ventana, la lista de hojas y construye la interfaz.
     */
    public InterfazGrafica() {
        this.hojas = new ListaHojas();
        setTitle("Hoja de Cálculo - Listas Enlazadas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        inicializarUI();
    }

    /**
     * Construye el layout principal:
     * NORTH → barra de fórmulas | CENTER → pestañas | SOUTH → estado.
     * Crea la hoja inicial "Hoja1" al terminar.
     */
    private void inicializarUI() {
        crearMenu();

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // ── barra de fórmulas (similar a Excel) ───────────────────
        JPanel barraFormula = new JPanel(new BorderLayout(4, 0));
        barraFormula.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        barraFormula.setBackground(new Color(240, 240, 240));

        // celda activa: muestra "A1", "B3", etc.
        lblCeldaActual = new JLabel("A1");
        lblCeldaActual.setPreferredSize(new Dimension(60, 24));
        lblCeldaActual.setHorizontalAlignment(SwingConstants.CENTER);
        lblCeldaActual.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        lblCeldaActual.setFont(new Font("SansSerif", Font.BOLD, 12));

        // icono decorativo "fx" a la izquierda del campo de fórmula
        JLabel lblFx = new JLabel("  fx ");
        lblFx.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 13));
        lblFx.setForeground(new Color(0, 100, 200));

        // campo que muestra el valor raw (ej: "=SUMA(A1:A3)")
        txtBarraFormula = new JTextField();
        txtBarraFormula.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtBarraFormula.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        txtBarraFormula.setEditable(false); // solo lectura; edición ocurre en la celda

        JPanel izqBarra = new JPanel(new BorderLayout(2, 0));
        izqBarra.setOpaque(false);
        izqBarra.add(lblCeldaActual, BorderLayout.WEST);
        izqBarra.add(lblFx, BorderLayout.EAST);

        barraFormula.add(izqBarra, BorderLayout.WEST);
        barraFormula.add(txtBarraFormula, BorderLayout.CENTER);

        // ── resto del layout ───────────────────────────────────────
        pestanas = new JTabbedPane();
        lblEstado = new JLabel("Listo");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        panelPrincipal.add(barraFormula, BorderLayout.NORTH);
        panelPrincipal.add(pestanas,     BorderLayout.CENTER);
        panelPrincipal.add(lblEstado,    BorderLayout.SOUTH);

        crearNuevaHoja("Hoja1");
        setContentPane(panelPrincipal);
    }

    /**
     * Construye la barra de menús con:
     * - Archivo: Nueva Hoja / Exportar CSV / Importar CSV / Salir
     * - Edición: Copiar, Cortar, Pegar (Ctrl+C/X/V) / Limpiar Hoja
     */
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();

        // menú Archivo
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

        // menú Edición con atajos de teclado
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
        JMenuItem itemLimpiar = new JMenuItem("Limpiar Hoja");
        itemLimpiar.addActionListener(e -> limpiarHojaActual());
        menuEdicion.add(itemLimpiar);
        menuBar.add(menuEdicion);

        setJMenuBar(menuBar);
    }

    /**
     * Muestra el diálogo para ingresar el nombre de la nueva hoja.
     * Solo crea la hoja si el usuario ingresa un nombre válido.
     */
    private void nuevaHoja() {
        DialogoNuevaHoja dialogo = new DialogoNuevaHoja(this);
        String nombre = dialogo.mostrar();
        if (nombre != null && !nombre.isEmpty()) crearNuevaHoja(nombre);
    }

    /**
     * Crea una hoja completa (modelo lógico + tabla visual) y la agrega como pestaña.
     * Configura: editor de celdas con soporte de fórmulas, numerador de filas,
     * listeners para la barra de fórmulas, atajos Ctrl+C/X/V y menú contextual.
     *
     * @param nombre Nombre visible de la pestaña
     */
    private void crearNuevaHoja(String nombre) {
        HojaCalculo hoja = new HojaCalculo();
        ModeloTabla modelo = new ModeloTabla(hoja, 30, 10);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tabla.setDefaultEditor(Object.class, new CellEditorPersonalizado(modelo));
        tabla.setGridColor(new Color(200, 200, 200));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setReorderingAllowed(false); // evita reordenar columnas con drag

        // scroll con numerador de filas en el margen izquierdo
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setRowHeaderView(new NumeradorFilas(tabla));
        scroll.getRowHeader().setPreferredSize(new Dimension(40, 0));

        // actualiza la barra de fórmulas cada vez que cambia la celda seleccionada
        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) actualizarBarraFormula(tabla, hoja);
        });
        tabla.getColumnModel().getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) actualizarBarraFormula(tabla, hoja);
        });

        // atajos de teclado dentro de la tabla
        bindKey(tabla, KeyEvent.VK_C, "copiar",  () -> copiarSeleccion());
        bindKey(tabla, KeyEvent.VK_X, "cortar",  () -> cortarSeleccion());
        bindKey(tabla, KeyEvent.VK_V, "pegar",   () -> pegarSeleccion());

        // menú contextual al hacer clic derecho sobre la tabla
        JPopupMenu popup = new JPopupMenu();
        addPopupItem(popup, "Copiar  Ctrl+C",  () -> copiarSeleccion());
        addPopupItem(popup, "Cortar  Ctrl+X",  () -> cortarSeleccion());
        addPopupItem(popup, "Pegar   Ctrl+V",  () -> pegarSeleccion());
        tabla.setComponentPopupMenu(popup);

        // registra en las listas enlazadas y agrega la pestaña al panel
        pestanas.addTab(nombre, scroll);
        hojas.agregar(nombre, hoja);
        agregarModelo(nombre, modelo);
        agregarTabla(nombre, tabla);
        lblEstado.setText("Hoja '" + nombre + "' creada");
    }

    /**
     * Registra un atajo Ctrl+[tecla] en la tabla para ejecutar una acción.
     *
     * @param t      Tabla destino del atajo
     * @param vk     Código de tecla (ej. KeyEvent.VK_C)
     * @param nombre Nombre interno de la acción en el ActionMap
     * @param accion Lógica a ejecutar al presionar el atajo
     */
    private void bindKey(JTable t, int vk, String nombre, Runnable accion) {
        t.getInputMap(JComponent.WHEN_FOCUSED)
         .put(KeyStroke.getKeyStroke(vk, InputEvent.CTRL_DOWN_MASK), nombre);
        t.getActionMap().put(nombre, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { accion.run(); }
        });
    }

    /**
     * Agrega una opción al menú contextual (clic derecho) de la tabla.
     *
     * @param m      Menú donde se agrega el ítem
     * @param txt    Texto visible del ítem
     * @param accion Lógica a ejecutar al seleccionarlo
     */
    private void addPopupItem(JPopupMenu m, String txt, Runnable accion) {
        JMenuItem item = new JMenuItem(txt);
        item.addActionListener(e -> accion.run());
        m.add(item);
    }

    /**
     * Refresca la barra de fórmulas con la celda actualmente seleccionada.
     * Muestra la referencia (ej. "B3") y el valor raw (ej. "=SUMA(A1:A3)").
     *
     * @param tabla Tabla donde ocurrió el cambio de selección
     * @param hoja  Hoja lógica asociada a esa tabla
     */
    private void actualizarBarraFormula(JTable tabla, HojaCalculo hoja) {
        int fila = tabla.getSelectedRow();
        int col  = tabla.getSelectedColumn();
        if (fila < 0 || col < 0) return;

        // referencia textual de la celda, ej: "C5"
        String ref = hoja.posicionAReferencia(fila, col);
        lblCeldaActual.setText(ref);

        // muestra la fórmula original, no el resultado calculado
        String nombreHoja = pestanas.getTitleAt(pestanas.getSelectedIndex());
        ModeloTabla mt = buscarModelo(nombreHoja);
        if (mt != null)
            txtBarraFormula.setText(mt.getRawValue(fila, col));
        else {
            Object val = tabla.getValueAt(fila, col);
            txtBarraFormula.setText(val != null ? val.toString() : "");
        }
    }

    /**
     * Retorna la JTable de la pestaña activa, o null si no hay ninguna.
     */
    private JTable getTablaActual() {
        int idx = pestanas.getSelectedIndex();
        if (idx < 0) return null;
        return buscarTabla(pestanas.getTitleAt(idx));
    }

    /**
     * Copia las celdas seleccionadas al portapapeles interno y al del sistema.
     * El portapapeles del sistema permite pegar en otras apps (ej. Excel, Notepad).
     */
    private void copiarSeleccion() {
        JTable tabla = getTablaActual();
        if (tabla == null) return;
        int[] filas = tabla.getSelectedRows();
        int[] cols  = tabla.getSelectedColumns();
        if (filas.length == 0 || cols.length == 0) return;

        // llena clipboard[][] y construye texto tabulado para el SO
        clipboard = new String[filas.length][cols.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filas.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                Object v = tabla.getValueAt(filas[i], cols[j]);
                clipboard[i][j] = v != null ? v.toString() : "";
                sb.append(clipboard[i][j]);
                if (j < cols.length - 1) sb.append("\t"); // separador de columna
            }
            sb.append("\n"); // separador de fila
        }
        esCorte = false; // es copia, no corte
        Toolkit.getDefaultToolkit().getSystemClipboard()
               .setContents(new StringSelection(sb.toString()), null);
        lblEstado.setText("Copiado (" + filas.length + " fila(s) × " + cols.length + " col(s))");
    }

    /**
     * Corta las celdas seleccionadas: copia los valores y marca el origen
     * para borrarlo cuando el usuario pegue (borrado diferido).
     */
    private void cortarSeleccion() {
        JTable tabla = getTablaActual();
        if (tabla == null) return;
        copiarSeleccion();          // reutiliza la lógica de copia
        esCorte       = true;       // señal para borrar el origen al pegar
        tablaOrigen   = tabla;
        filasCortadas = tabla.getSelectedRows();
        colsCortadas  = tabla.getSelectedColumns();
        lblEstado.setText("Cortado — selecciona destino y pega");
    }

    /**
     * Pega el portapapeles interno a partir de la celda seleccionada.
     * Si fue un corte, borra las celdas de origen después de pegar.
     * Ignora posiciones que estén fuera del rango de la tabla.
     */
    private void pegarSeleccion() {
        JTable tabla = getTablaActual();
        if (tabla == null || clipboard == null) return;
        int fInicio = tabla.getSelectedRow();
        int cInicio = tabla.getSelectedColumn();
        if (fInicio < 0 || cInicio < 0) return;

        // vuelca clipboard en la tabla desde la celda de destino
        for (int i = 0; i < clipboard.length; i++)
            for (int j = 0; j < clipboard[i].length; j++) {
                int fd = fInicio + i, cd = cInicio + j;
                if (fd < tabla.getRowCount() && cd < tabla.getColumnCount())
                    tabla.setValueAt(clipboard[i][j], fd, cd);
            }

        // si fue corte, borra el origen ahora que ya se pegó
        if (esCorte && tablaOrigen != null) {
            for (int i = 0; i < filasCortadas.length; i++)
                for (int j = 0; j < colsCortadas.length; j++)
                    tablaOrigen.setValueAt("", filasCortadas[i], colsCortadas[j]);
            esCorte = false; tablaOrigen = null;
        }

        // refresca la barra de fórmulas con la nueva celda activa
        int idx = pestanas.getSelectedIndex();
        if (idx >= 0) {
            HojaCalculo hoja = hojas.buscar(pestanas.getTitleAt(idx));
            if (hoja != null) actualizarBarraFormula(tabla, hoja);
        }
        lblEstado.setText("Pegado correctamente");
    }

    /**
     * Inserta un ModeloTabla al final de la lista enlazada de modelos.
     *
     * @param nombre Clave de búsqueda (nombre de la hoja)
     * @param modelo Modelo a almacenar
     */
    private void agregarModelo(String nombre, ModeloTabla modelo) {
        NodoModeloTabla nuevo = new NodoModeloTabla(nombre, modelo);
        if (cabezaModelos == null) { cabezaModelos = nuevo; return; }
        NodoModeloTabla a = cabezaModelos;
        while (a.siguiente != null) a = a.siguiente;
        a.siguiente = nuevo;
    }

    /**
     * Busca un ModeloTabla por el nombre de su hoja.
     *
     * @param nombre Nombre de la hoja
     * @return ModeloTabla encontrado, o null si no existe
     */
    private ModeloTabla buscarModelo(String nombre) {
        NodoModeloTabla a = cabezaModelos;
        while (a != null) { if (a.nombre.equals(nombre)) return a.modelo; a = a.siguiente; }
        return null;
    }

    /**
     * Inserta una JTable al final de la lista enlazada de tablas.
     *
     * @param nombre Clave de búsqueda (nombre de la hoja)
     * @param tabla  Tabla a almacenar
     */
    private void agregarTabla(String nombre, JTable tabla) {
        NodoTabla nuevo = new NodoTabla(nombre, tabla);
        if (cabezaTablas == null) { cabezaTablas = nuevo; return; }
        NodoTabla a = cabezaTablas;
        while (a.siguiente != null) a = a.siguiente;
        a.siguiente = nuevo;
    }

    /**
     * Busca una JTable por el nombre de su hoja.
     *
     * @param nombre Nombre de la hoja
     * @return JTable encontrada, o null si no existe
     */
    private JTable buscarTabla(String nombre) {
        NodoTabla a = cabezaTablas;
        while (a != null) { if (a.nombre.equals(nombre)) return a.tabla; a = a.siguiente; }
        return null;
    }

    /**
     * Exporta la hoja activa a un archivo CSV elegido por el usuario.
     * Agrega ".csv" al nombre si el usuario no lo escribió.
     */
    private void exportarCSV() {
        String nombre = pestanas.getTitleAt(pestanas.getSelectedIndex());
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String ruta = fc.getSelectedFile().getAbsolutePath();
                if (!ruta.endsWith(".csv")) ruta += ".csv"; // extensión automática
                ExportadorCSV.exportar(hojas.buscar(nombre), ruta);
                lblEstado.setText("Exportado: " + ruta);
                JOptionPane.showMessageDialog(this, "Hoja exportada exitosamente");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Importa datos de un CSV al modelo lógico y refresca la tabla visual.
     */
    private void importarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String nombre = pestanas.getTitleAt(pestanas.getSelectedIndex());
                ImportadorCSV.importar(hojas.buscar(nombre), fc.getSelectedFile().getAbsolutePath());
                buscarModelo(nombre).actualizarDatos(); // sincroniza vista con modelo
                lblEstado.setText("Importado: " + fc.getSelectedFile().getName());
                JOptionPane.showMessageDialog(this, "Datos importados exitosamente");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Borra todos los datos de la hoja activa previa confirmación del usuario.
     * Vacía la lista enlazada de celdas y refresca la vista.
     */
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

    /**
     * Panel lateral izquierdo que muestra los números de fila (1, 2, 3...).
     * Resalta en azul la fila seleccionada para imitar el estilo de Excel.
     */
    private static class NumeradorFilas extends JList<String> {
        private final JTable tabla;

        /**
         * Crea el numerador y lo vincula a la tabla para sincronizar la selección.
         *
         * @param tabla Tabla a la que pertenece este numerador
         */
        NumeradorFilas(JTable tabla) {
            this.tabla = tabla;
            setFixedCellWidth(40);
            setFixedCellHeight(tabla.getRowHeight());
            setBackground(new Color(242, 242, 242));
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
            setFont(new Font("SansSerif", Font.PLAIN, 11));

            // llena el modelo con "1", "2", ... hasta el total de filas
            DefaultListModel<String> model = new DefaultListModel<>();
            for (int i = 1; i <= tabla.getRowCount(); i++) model.addElement(String.valueOf(i));
            setModel(model);

            // renderer personalizado: resalta la fila activa en azul claro
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
                    // fila seleccionada: fondo azul + negrita
                    if (tabla.getSelectedRow() == index) {
                        lbl.setBackground(new Color(180, 200, 230));
                        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                    }
                    return lbl;
                }
            });

            // repinta el numerador cada vez que cambia la fila seleccionada
            tabla.getSelectionModel().addListSelectionListener(e -> repaint());
        }
    }
}