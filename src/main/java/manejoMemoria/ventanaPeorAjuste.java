/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manejoMemoria;

/**
 *
 */
public class ventanaPeorAjuste extends javax.swing.JFrame {
    peorAjuste Ventana = new peorAjuste();

    /**
     * Creates new form ventanaMejorAjuste
     */
    public ventanaPeorAjuste() {
        initComponents();
        Ventana.creacion();
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        bEstadoMemoria = new javax.swing.JButton();
        bAgregar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        bVaciar = new javax.swing.JButton();
        bSalida = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        tNombre = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tTamaño = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 153));
        jLabel1.setText("Peor ajuste");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, -1, -1));

        bEstadoMemoria.setText("Mostrar datos");
        bEstadoMemoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEstadoMemoriaActionPerformed(evt);
            }
        });
        jPanel1.add(bEstadoMemoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 110, 160, -1));

        bAgregar.setText("agregar");
        bAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAgregarActionPerformed(evt);
            }
        });
        jPanel1.add(bAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 300, 180, -1));

        jLabel2.setText("estado de la memoria");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 153));
        jLabel3.setText("agregar proceso");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, -1, -1));

        jLabel4.setText("vaciar memoria");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 380, -1, -1));

        bVaciar.setText("Vaciar");
        bVaciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bVaciarActionPerformed(evt);
            }
        });
        jPanel1.add(bVaciar, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 380, 180, -1));

        bSalida.setBackground(new java.awt.Color(255, 102, 102));
        bSalida.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        bSalida.setForeground(new java.awt.Color(255, 255, 255));
        bSalida.setText("regresar");
        bSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSalidaActionPerformed(evt);
            }
        });
        jPanel1.add(bSalida, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 0, 100, 30));

        jLabel5.setText("nombre del proceso");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, -1, -1));
        jPanel1.add(tNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 230, 180, -1));

        jLabel6.setText("tamaño del proceso");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 230, -1, -1));
        jPanel1.add(tTamaño, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 230, 180, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 792, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 510, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bEstadoMemoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEstadoMemoriaActionPerformed
        Ventana.mostrar();
    }//GEN-LAST:event_bEstadoMemoriaActionPerformed

    private void bAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAgregarActionPerformed
        Ventana.agregar(tNombre.getText(), Integer.parseInt(tTamaño.getText()));
        VaciarCampos();
    }//GEN-LAST:event_bAgregarActionPerformed

    private void bVaciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bVaciarActionPerformed
        Ventana.Vaciar();
    }//GEN-LAST:event_bVaciarActionPerformed

    private void bSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSalidaActionPerformed
        dispose();
    }//GEN-LAST:event_bSalidaActionPerformed

    public void VaciarCampos(){
        tNombre.setText("");
        tTamaño.setText("");
        tNombre.requestFocus();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ventanaPeorAjuste.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ventanaPeorAjuste.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ventanaPeorAjuste.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ventanaPeorAjuste.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ventanaPeorAjuste().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAgregar;
    private javax.swing.JButton bEstadoMemoria;
    private javax.swing.JButton bSalida;
    private javax.swing.JButton bVaciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tNombre;
    private javax.swing.JTextField tTamaño;
    // End of variables declaration//GEN-END:variables
}
