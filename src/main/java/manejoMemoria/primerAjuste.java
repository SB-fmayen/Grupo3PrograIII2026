
package manejoMemoria;

import Colas.Nodo;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;

public class primerAjuste {
    ArrayList <bloqueDeMemoria> memoria = new ArrayList<>();
    
    
    public primerAjuste(){
        
        
    }
    public void mostrar(){
        String ajuste = "";
        for (int i =  0; i <memoria.size(); i++) {
            if(memoria.get(i).getTamaño()== 0){
                ajuste += "bloque lleno \n";
            }
            else{
                ajuste += "El tamaño del bloque es de "+memoria.get(i).getTamaño() +" kbs, "+memoria.get(i).getProcesos().size()+" procesos asignados"+"\n";
        
            }
        }
        JOptionPane.showMessageDialog(null,  ajuste, "la cola quedo asi: ", JOptionPane.INFORMATION_MESSAGE);
        
    }
    public void creacion() {
    for (int i = 0; i < 10; i++) {
        bloqueDeMemoria bloque = new bloqueDeMemoria();
        int tam = (int) (Math.random() * 20 + 1);
        bloque.setTamaño(tam);
        bloque.setTamañoOriginal(tam); // ← AGREGAR ESTO
        memoria.add(bloque);
    }

        
    }
    public void agregar(String n, int tamaño){
        ArrayList<proceso> lista;
        boolean bandera = false;
        
        
        for(int i = 0; i<memoria.size(); i++){
            if(bandera == false){
               if(tamaño <= memoria.get(i).getTamaño())
                {
                    lista = memoria.get(i).getProcesos();
                    proceso p = new proceso();
                    p.setNombre(n);
                    p.setTamaño(tamaño);
                    lista.add(p);
                    memoria.get(i).setProcesos(lista);
                    memoria.get(i).setTamaño(memoria.get(i).getTamaño()-tamaño);
                    bandera = true;
                }
               
               
            }
        }
        if(bandera == false){
            JOptionPane.showMessageDialog(null, "proceso invalido");
        }
    }
    
    public void vaciar(){
        for(int i = 0; i<memoria.size(); i++){
            memoria.get(i).procesos.clear();
            memoria.get(i).setTamaño(memoria.get(i).getTamañoOriginal()); 
              
            }
        JOptionPane.showMessageDialog(null, "memoria vacia");
    }
    
}
