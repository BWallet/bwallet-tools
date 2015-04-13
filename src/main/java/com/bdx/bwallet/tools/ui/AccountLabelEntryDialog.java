/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.google.common.base.Optional;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class AccountLabelEntryDialog extends javax.swing.JDialog implements WindowListener {

    private ResourceBundle bundle;

    private boolean edit;

    private Integer index;

    private String label;

    private boolean cancel = false;

    /**
     * Creates new form AccountLabelEntryDialog
     */
    public AccountLabelEntryDialog(java.awt.Dialog parent, boolean modal, ResourceBundle bundle, boolean edit, Optional<Integer> index, Optional<String> label) {
        super(parent, modal);
        initComponents();
        this.edit = edit;
        if (edit) {
            indexTextField.setEditable(false);
        }
        if (index.isPresent()) {
            indexTextField.setText(index.get().toString());
        }
        if (label.isPresent()) {
            labelTextField.setText(label.get());
        }
        this.addWindowListener(this);

        this.bundle = bundle;
        applyResourceBundle();
    }

    public void applyResourceBundle() {
        setTitle(bundle.getString("AccountLabelEntryDialog.title")); 
        indexLabel.setText(bundle.getString("AccountLabelEntryDialog.indexLabel.text")); 
        labelLabel.setText(bundle.getString("AccountLabelEntryDialog.labelLabel.text")); 
        enterButton.setText(bundle.getString("AccountLabelEntryDialog.enterButton.text")); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enterButton = new javax.swing.JButton();
        indexLabel = new javax.swing.JLabel();
        labelLabel = new javax.swing.JLabel();
        indexTextField = new javax.swing.JTextField();
        labelTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Account Label Setting");
        setResizable(false);

        enterButton.setFont(new java.awt.Font("宋体", 0, 24)); // NOI18N
        enterButton.setText("Enter");
        enterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterButtonActionPerformed(evt);
            }
        });

        indexLabel.setText("Account Index");

        labelLabel.setText("Account Label");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enterButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(indexLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelTextField)
                            .addComponent(indexTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(indexLabel)
                    .addComponent(indexTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLabel))
                .addGap(18, 18, 18)
                .addComponent(enterButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void enterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterButtonActionPerformed
        String indexText = indexTextField.getText().trim();
        if ("".equals(indexText)) {
            JOptionPane.showMessageDialog(this, "Empty index");
            return;
        }
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid index");
            return;
        }
        if (index < 1) {
            JOptionPane.showMessageDialog(this, "Index must be greater than or equal to 1");
            return;
        }

        String labelText = labelTextField.getText().trim();
        if ("".equals(labelText)) {
            JOptionPane.showMessageDialog(this, "Empty label");
            return;
        }
        BWalletMessage.SetAccountLabel message = BWalletMessage.SetAccountLabel
                .newBuilder()
                .setCoinName("Bitcoin")
                .setIndex(index)
                .setLabel(labelText)
                .build();
        if (message.getLabelBytes().size() > 18) {
            JOptionPane.showMessageDialog(this, "Label is to long");
            return;
        }

        this.index = index;
        this.label = labelText;
        this.cancel = false;
        this.dispose();
    }//GEN-LAST:event_enterButtonActionPerformed

    public boolean isEdit() {
        return edit;
    }

    public Integer getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    public boolean isCancel() {
        return cancel;
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.index = 0;
        this.label = "";
        this.cancel = true;
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
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
            java.util.logging.Logger.getLogger(AccountLabelEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AccountLabelEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AccountLabelEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AccountLabelEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AccountLabelEntryDialog dialog = new AccountLabelEntryDialog(new javax.swing.JDialog(), true, ResourceBundle.getBundle("com/bdx/bwallet/tools/ui/Bundle"), false, Optional.<Integer>absent(), Optional.<String>absent());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton enterButton;
    private javax.swing.JLabel indexLabel;
    private javax.swing.JTextField indexTextField;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JTextField labelTextField;
    // End of variables declaration//GEN-END:variables
}
