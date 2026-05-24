
package manejoMemoria;

public class menuManejoMemoria extends javax.swing.JFrame {

    public menuManejoMemoria() {
        initComponents();
        setVisible(true);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        bPrimerA = new javax.swing.JButton();
        bMejorA = new javax.swing.JButton();
        bPeorA = new javax.swing.JButton();
        bSalida = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Manejo de memoria");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, -1, -1));

        bPrimerA.setText("Primer ajuste");
        bPrimerA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPrimerAActionPerformed(evt);
            }
        });
        jPanel1.add(bPrimerA, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 150, -1));

        bMejorA.setText("Mejor ajuste");
        bMejorA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMejorAActionPerformed(evt);
            }
        });
        jPanel1.add(bMejorA, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 150, -1));

        bPeorA.setText("Peor ajuste");
        bPeorA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPeorAActionPerformed(evt);
            }
        });
        jPanel1.add(bPeorA, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 150, -1));

        bSalida.setText("salida");
        bSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSalidaActionPerformed(evt);
            }
        });
        jPanel1.add(bSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 280, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 430, 310));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSalidaActionPerformed
        dispose();
    }//GEN-LAST:event_bSalidaActionPerformed

    private void bPrimerAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPrimerAActionPerformed
        ventanaPrimerAjuste ventana = new ventanaPrimerAjuste();
    }//GEN-LAST:event_bPrimerAActionPerformed

    private void bMejorAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMejorAActionPerformed
         ventanaMejorAjuste Ventana = new ventanaMejorAjuste();
    }//GEN-LAST:event_bMejorAActionPerformed

    private void bPeorAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPeorAActionPerformed
        // TODO add your handling code here:
 
        ventanaPeorAjuste Ventana = new ventanaPeorAjuste();
    }//GEN-LAST:event_bPeorAActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bMejorA;
    private javax.swing.JButton bPeorA;
    private javax.swing.JButton bPrimerA;
    private javax.swing.JButton bSalida;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
