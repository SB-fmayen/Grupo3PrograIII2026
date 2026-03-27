package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana emergente que le pide al usuario el nombre para una nueva hoja.
 * Muestra un campo de texto con un nombre sugerido, y botones para confirmar o cancelar.
 */
public class DialogoNuevaHoja extends JDialog {
    private JTextField txtNombreHoja;
    private String resultado = null; // Guarda el nombre ingresado, o null si canceló

    public DialogoNuevaHoja(JFrame parent) {
        super(parent, "Nueva Hoja", true); // true = bloquea la ventana principal mientras está abierto
        setSize(300, 150);
        setLocationRelativeTo(null); // Aparece centrado en la pantalla

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(new JLabel("Nombre de la hoja:"));

        // Campo de texto con un nombre sugerido automáticamente
        txtNombreHoja = new JTextField("Hoja" + (System.nanoTime() % 10));
        panel.add(txtNombreHoja);

        // Botones de acción
        JPanel botones = new JPanel(new FlowLayout());
        JButton btnOk = new JButton("Crear");
        JButton btnCancelar = new JButton("Cancelar");

        btnOk.addActionListener(e -> { resultado = txtNombreHoja.getText().trim(); dispose(); });
        btnCancelar.addActionListener(e -> { resultado = null; dispose(); }); // Sin nombre = canceló

        botones.add(btnOk);
        botones.add(btnCancelar);
        panel.add(botones);
        setContentPane(panel);
    }

    /**
     * Muestra el diálogo y espera a que el usuario responda.
     * Devuelve el nombre ingresado, o null si presionó Cancelar.
     */
    public String mostrar() { setVisible(true); return resultado; }
}