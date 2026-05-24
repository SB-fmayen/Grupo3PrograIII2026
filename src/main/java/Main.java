import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {

    public static void main(String[] args) {
        // Lanza la UI en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() {
        // Ventana principal del sistema
        JFrame menu = new JFrame("Grupo 3 - Programacion III 2026");
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setSize(420, 320);
        menu.setLocationRelativeTo(null); // Centrar en pantalla
        menu.setResizable(false);

        // Panel raíz con margen interno
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Bloque de títulos
        JLabel titulo = new JLabel("MENU PRINCIPAL", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitulo = new JLabel("Grupo 3 - Programacion III 2026", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(Color.DARK_GRAY);

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 4));
        panelTitulos.add(titulo);
        panelTitulos.add(subtitulo);

        // Panel de botones con espaciado vertical
        JPanel botones = new JPanel(new GridLayout(4, 1, 0, 8));
        botones.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        // Botones estándar de Swing — sin color personalizado
        JButton btn1 = new JButton("1.  Hoja de Calculo");
        JButton btn2 = new JButton("2.  Arboles - Biblioteca Digital");
        JButton btn3 = new JButton("3.  Manejo de Memoria");
        JButton btn4 = new JButton("4.  Salir");

        // Fuente uniforme para todos los botones
        Font fuenteBtn = new Font("SansSerif", Font.PLAIN, 13);
        for (JButton b : new JButton[]{btn1, btn2, btn3, btn4}) {
            b.setFont(fuenteBtn);
        }

        // Btn1: oculta menú, abre Hoja de Cálculo y lo restaura al cerrar
        btn1.addActionListener(e -> {
            menu.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                ui.InterfazGrafica ventana = new ui.InterfazGrafica();
                ventana.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent we) {
                        menu.setVisible(true);
                    }
                });
                ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventana.setVisible(true);
            });
        });

        // Btn2: oculta menú, abre sistema de Biblioteca y lo restaura al cerrar
        btn2.addActionListener(e -> {
            menu.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                biblioteca.SistemaBiblioteca ventana = new biblioteca.SistemaBiblioteca();
                ventana.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent we) {
                        menu.setVisible(true);
                    }
                });
                ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventana.setVisible(true);
            });
        });

        // Btn3: abre módulo de Manejo de Memoria
        btn3.addActionListener(e -> {
            menu.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                manejoMemoria.menuManejoMemoria ventana = new manejoMemoria.menuManejoMemoria();
                ventana.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent we) {
                        menu.setVisible(true);
                    }
                });
                ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventana.setVisible(true);
            });
        });

        // Btn4: cierra la aplicación completa
        btn4.addActionListener(e -> System.exit(0));

        botones.add(btn1);
        botones.add(btn2);
        botones.add(btn3);
        botones.add(btn4);

        panel.add(panelTitulos, BorderLayout.NORTH);
        panel.add(botones,      BorderLayout.CENTER);

        menu.setContentPane(panel);
        menu.setVisible(true);
    }
}