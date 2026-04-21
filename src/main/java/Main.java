
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarMenuPrincipal);
    }

    private static void mostrarMenuPrincipal() { //Aquí construyes toda la ventanas de la interfaz

        JFrame menu = new JFrame("Grupo 3 - Programacion III 2026"); //Crear la ventana

        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Cerrar programa al cerrar ventana
        menu.setSize(480, 400);
        menu.setLocationRelativeTo(null);
        menu.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));//Solo diseño (márgenes y color)
        panel.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("MENU PRINCIPAL", SwingConstants.CENTER);//Título y subtítulo
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));//Texto centrado
        titulo.setForeground(new Color(30, 60, 120));

        JLabel subtitulo = new JLabel("Grupo 3 - Programacion III 2026", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.ITALIC, 13));
        subtitulo.setForeground(Color.GRAY);

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1));//Panel de títulos
        panelTitulos.setOpaque(false);
        panelTitulos.add(titulo);
        panelTitulos.add(subtitulo);

        JPanel botones = new JPanel(new GridLayout(4, 1, 10, 12));//Panel de botones
        botones.setOpaque(false);
        botones.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btn1 = crearBoton("1. Hoja de Calculo", new Color(0, 120, 215));//Creación de botones
        JButton btn2 = crearBoton("2. Arboles - Biblioteca Digital", new Color(0, 160, 80));
        JButton btn3 = crearBoton("3. En Desarrollo", new Color(150, 150, 150));
        JButton btn4 = crearBoton("4. Salir", new Color(200, 50, 50));

        btn1.addActionListener(e -> { //Eventos, cuando haces clic
            menu.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                InterfazGrafica ventana = new ui.InterfazGrafica();

                ventana.addWindowListener(new WindowAdapter() {//Cuando cierras la otra ventana:
                    @Override
                    public void windowClosed(WindowEvent we) {
                        menu.setVisible(true);
                    }
                });//                                           el menú vuelve a aparecer

                ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventana.setVisible(true);
            });
        });

        btn2.addActionListener(e -> {
            menu.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                SistemaBiblioteca ventana = new SistemaBiblioteca();

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

        btn3.addActionListener(e
                -> JOptionPane.showMessageDialog(
                        menu,
                        "Esta opcion esta en desarrollo.",
                        "En Desarrollo",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        btn4.addActionListener(e -> System.exit(0));

        botones.add(btn1);
        botones.add(btn2);
        botones.add(btn3);
        botones.add(btn4);

        panel.add(panelTitulos, BorderLayout.NORTH); // Agregar todo a la ventana
        panel.add(botones, BorderLayout.CENTER);

        menu.setContentPane(panel); //Muestra la ventana
        menu.setVisible(true);
    }

    private static JButton crearBoton(String texto, Color color) { //Este método construye botones personalizados

        JButton b = new JButton(texto);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);

        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color hover = color.brighter();

        b.addMouseListener(new MouseAdapter() { //Efecto hover (cuando pasas el mouse)
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(color);
            }
        });

        return b;
    }
}
