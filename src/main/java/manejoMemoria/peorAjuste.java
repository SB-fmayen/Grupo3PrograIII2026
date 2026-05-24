package manejoMemoria;

import java.util.ArrayList;
import javax.swing.JOptionPane;

public class peorAjuste {
    ArrayList<bloqueDeMemoria> memoria = new ArrayList<>();

    public peorAjuste() {

    }

    public void mostrar() {
        String ajuste = "";
        for (int i = 0; i < memoria.size(); i++) {
            if (memoria.get(i).getTamaño() == 0) {
                ajuste += "bloque lleno \n";
            } else {
                ajuste += "El tamaño del bloque es de " + memoria.get(i).getTamaño() + " kbs, " + memoria.get(i).getProcesos().size() + " procesos asignados" + "\n";

            }
        }
        JOptionPane.showMessageDialog(null, ajuste, "la cola quedo asi: ", JOptionPane.INFORMATION_MESSAGE);

    }

    public void creacion() {
        for (int i = 0; i < 10; i++) {
            bloqueDeMemoria bloque = new bloqueDeMemoria();
            bloque.setTamaño((int) (Math.random() * 20 + 1));
            memoria.add(bloque);
        }
    }

    public void agregar(String n, int tamaño) {
        ArrayList<proceso> lista;
        boolean bandera = false;
        int peorAjusteIndex = -1;
        int mayorEspacioDisponible = Integer.MIN_VALUE;

        for (int i = 0; i < memoria.size(); i++) {
            if (tamaño <= memoria.get(i).getTamaño()) {
                if (memoria.get(i).getTamaño() - tamaño > mayorEspacioDisponible) {
                    mayorEspacioDisponible = memoria.get(i).getTamaño() - tamaño;
                    peorAjusteIndex = i;
                }
            }
        }

        if (peorAjusteIndex != -1) {
            lista = memoria.get(peorAjusteIndex).getProcesos();
            proceso p = new proceso();
            p.setNombre(n);
            p.setTamaño(tamaño);
            lista.add(p);
            memoria.get(peorAjusteIndex).setProcesos(lista);
            memoria.get(peorAjusteIndex).setTamaño(memoria.get(peorAjusteIndex).getTamaño() - tamaño);
            bandera = true;
        }

        if (!bandera) {
            JOptionPane.showMessageDialog(null, "proceso invalido");
        }
    }

    public void Vaciar() {
        for (int i = 0; i < memoria.size(); i++) {
            memoria.get(i).procesos.clear();
        }
        JOptionPane.showMessageDialog(null, "memoria vacia");
    }
}