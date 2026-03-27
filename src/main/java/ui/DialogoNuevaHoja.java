package ui;

import javax.swing.*;
import java.awt.*;
//Es una ventana emergente que pide el nombre de una nueva hoja
//JDialog = ventana pequeña encima de la principal
public class DialogoNuevaHoja extends JDialog {

    private JTextField txtNombreHoja;//donde escribes el nombre
    private String resultado = null;//lo que el usuario decide

    public DialogoNuevaHoja(JFrame parent) {
//parent = ventana principal
        super(parent, "Nueva Hoja", true);
//Tamaño y posición, Ventana pequeña centrada
        setSize(300, 150);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1));
//Etiqueta, Texto que ve el usuario
        panel.add(new JLabel("Nombre de la hoja:"));
//Campo de texto generar número Hoja3, Hoja7, etc
        txtNombreHoja = new JTextField("Hoja" + (System.nanoTime() % 10));
        panel.add(txtNombreHoja);

        JPanel botones = new JPanel(new FlowLayout());
        JButton btnOk = new JButton("Crear");
        JButton btnCancelar = new JButton("Cancelar");
        //Botón Crear
        btnOk.addActionListener(e -> {resultado = txtNombreHoja.getText().trim(); dispose(); }); //Guarda el texto en resultado y Cierra la ventana
        //Botón Cancelar, Resultado = null, Cierra ventana
        btnCancelar.addActionListener(e -> {resultado = null; dispose();});
        botones.add(btnOk);
        botones.add(btnCancelar);
        panel.add(botones);
        setContentPane(panel); // Mostrar contenido, Mete todo en la ventana
    }

    public String mostrar() {
        setVisible(true);
        return resultado;

    }
}
