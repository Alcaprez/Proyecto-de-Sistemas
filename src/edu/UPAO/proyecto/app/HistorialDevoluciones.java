

package edu.UPAO.proyecto.app;

public class HistorialDevoluciones extends javax.swing.JPanel {

    public HistorialDevoluciones() {
        initComponents();
        configurarTabla();
        cargarDatosEjemplo();
    }
    
     private void configurarTabla() {
        
        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
    };
    
    
    tablaDevoluciones.setModel(modelo);
        String[] columnas = {"ID Producto", "Cliente", "Cantidad", "Motivo", "Fecha", "Estado"};
        modelo.setColumnIdentifiers(columnas);
        
        // Configurar apariencia
        tablaDevoluciones.setRowHeight(25);
        tablaDevoluciones.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        tablaDevoluciones.getTableHeader().setForeground(java.awt.Color.WHITE);
        
        // Configurar botones según selección
        tablaDevoluciones.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaDevoluciones.getSelectedRow();
            boolean haySeleccion = fila != -1;
            
            btnVer.setEnabled(haySeleccion);
            
            
            if (haySeleccion && fila < modelo.getRowCount()) {
                String estado = modelo.getValueAt(fila, 5).toString();
                btnAprobar.setEnabled("Pendiente".equals(estado));
                btnRechazar.setEnabled("Pendiente".equals(estado));
            } else {
                btnAprobar.setEnabled(false);
                btnRechazar.setEnabled(false);
            }
        });
        
       
    }
    

    
    
    private void cargarDatosEjemplo() {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tablaDevoluciones.getModel();
        
        
        Object[][] datos = {
            {"PROD001", "Juan Pérez", "1", "Defectuoso", "25/09/2024", "Pendiente"},
            {"PROD002", "Ana García", "2", "Caducado", "24/09/2024", "Pendiente"},
            {"PROD003", "Carlos López", "1", "Caducado", "23/09/2024", "Aprobada"},
            {"PROD004", "María Rodríguez", "1", "Se confundio mi cholo", "22/09/2024", "Rechazada"}
        };
        
        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }
    }
    
    private void revisarDevolucion(int fila) {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tablaDevoluciones.getModel();
        
        String idProducto = modelo.getValueAt(fila, 0).toString();
        String cliente = modelo.getValueAt(fila, 1).toString();
        String cantidad = modelo.getValueAt(fila, 2).toString();
        String motivo = modelo.getValueAt(fila, 3).toString();
        String fecha = modelo.getValueAt(fila, 4).toString();
        String estado = modelo.getValueAt(fila, 5).toString();
        
        String detalles = String.format(
            "DETALLES DE LA DEVOLUCIÓN\n\n" +
            "ID Producto: %s\n" +
            "Cliente: %s\n" +
            "Cantidad: %s\n" +
            "Motivo: %s\n" +
            "Fecha: %s\n" +
            "Estado: %s",
            idProducto, cliente, cantidad, motivo, fecha, estado
        );
        
        javax.swing.JOptionPane.showMessageDialog(this, detalles, "Detalles - " + idProducto, 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    private void aprobarDevolucion(int fila) {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tablaDevoluciones.getModel();
        
        String idProducto = modelo.getValueAt(fila, 0).toString();
        String producto = modelo.getValueAt(fila, 1).toString();
        
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Aprobar la devolución?\n\n" +
            "ID Producto: " + idProducto,
            "Confirmar Aprobación",
            javax.swing.JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            modelo.setValueAt("Aprobada", fila, 5);
            
            String numeroNota = "NC" + System.currentTimeMillis() % 10000;
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "DEVOLUCIÓN APROBADA\n\n" +
                "ID Producto: " + idProducto + "\n" +
                "Nota de Crédito: " + numeroNota,
                "Aprobación Exitosa",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            
        }
    }
    
    
    private void rechazarDevolucion(int fila) {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tablaDevoluciones.getModel();
        
        String idProducto = modelo.getValueAt(fila, 0).toString();
        
        String motivoRechazo = javax.swing.JOptionPane.showInputDialog(this,
            "Motivo del rechazo para: " + idProducto,
            "Rechazar Devolución",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        
        if (motivoRechazo != null && !motivoRechazo.trim().isEmpty()) {
            modelo.setValueAt("Rechazada", fila, 5);
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "DEVOLUCIÓN RECHAZADA\n\n" +
                "ID Producto: " + idProducto + "\n" +
                "Motivo: " + motivoRechazo,
                "Rechazo Registrado",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            
            
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnVer = new javax.swing.JButton();
        btnAprobar = new javax.swing.JButton();
        btnRechazar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaDevoluciones = new javax.swing.JTable();

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Historial de Devoluciones");

        btnVer.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        btnVer.setText("Ver Detalles");
        btnVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerActionPerformed(evt);
            }
        });

        btnAprobar.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        btnAprobar.setText("Aprobar");
        btnAprobar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAprobarActionPerformed(evt);
            }
        });

        btnRechazar.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        btnRechazar.setText("Rechazar");
        btnRechazar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRechazarActionPerformed(evt);
            }
        });

        tablaDevoluciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Producto", "Cantidad", "Estado", "Fecha"
            }
        ));
        jScrollPane3.setViewportView(tablaDevoluciones);

        jScrollPane1.setViewportView(jScrollPane3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 23, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 820, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnVer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAprobar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRechazar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(btnVer)
                        .addGap(18, 18, 18)
                        .addComponent(btnAprobar)
                        .addGap(18, 18, 18)
                        .addComponent(btnRechazar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerActionPerformed
        int fila = tablaDevoluciones.getSelectedRow();
        if (fila != -1) {
            revisarDevolucion(fila);
        }
    }//GEN-LAST:event_btnVerActionPerformed

    private void btnAprobarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAprobarActionPerformed
        int fila = tablaDevoluciones.getSelectedRow();
        if (fila != -1) {
            aprobarDevolucion(fila);
        }
    }//GEN-LAST:event_btnAprobarActionPerformed

    private void btnRechazarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRechazarActionPerformed
        int fila = tablaDevoluciones.getSelectedRow();
        if (fila != -1) {
            rechazarDevolucion(fila);
        }
    }//GEN-LAST:event_btnRechazarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAprobar;
    private javax.swing.JButton btnRechazar;
    private javax.swing.JButton btnVer;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tablaDevoluciones;
    // End of variables declaration//GEN-END:variables
}

