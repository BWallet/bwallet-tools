/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui;

import com.bdx.bwallet.tools.controllers.MainController;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import org.bitcoinj.crypto.MnemonicCode;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Administrator
 */
public class WordEntryDialog extends javax.swing.JDialog implements WindowListener {

    private MainController mainController;
    
    private List<String> wordList = new ArrayList<>();

    private DefaultComboBoxModel wordModel = new DefaultComboBoxModel();
    
    /**
     * Creates new form WordEntryDialog
     */
    public WordEntryDialog(java.awt.Dialog parent, boolean modal, MainController mainController) {
        super(parent, modal);
        initComponents();

        this.addWindowListener(this);
        
        this.mainController = mainController;
        
        wordComboBox.setModel(wordModel);     
        AutoCompleteDecorator.decorate(wordComboBox);
        
        try {
            MnemonicCode mnemonicCode = new MnemonicCode();
            wordList = mnemonicCode.getWordList();
        } catch (IOException ex) {
            Logger.getLogger(WordEntryDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        wordModel.addElement("");
        for (String word : wordList) {
            wordModel.addElement(word);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        message1Label = new javax.swing.JLabel();
        wordComboBox = new javax.swing.JComboBox();
        message2Label = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        wordTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Seed recovery");

        message1Label.setText("<html>Please follow the instructions on your device. Words are going to be entered in shuffled order.<br>Also you'll be asked to retype some words that are not part of your recovery seed.</html>");

        wordComboBox.setEditable(true);
        wordComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordComboBoxActionPerformed(evt);
            }
        });
        wordComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                wordComboBoxKeyTyped(evt);
            }
        });

        message2Label.setText("Confirm choice by pressing enter.");

        wordTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " "
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(wordTable);
        if (wordTable.getColumnModel().getColumnCount() > 0) {
            wordTable.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(message1Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(wordComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(message2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(message1Label, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wordComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(message2Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void wordComboBoxKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_wordComboBoxKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_wordComboBoxKeyTyped

    private void wordComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordComboBoxActionPerformed
        // TODO add your handling code here:
        JComboBox cb = (JComboBox) evt.getSource();
        if ("comboBoxEdited".equals(evt.getActionCommand())) {
            // User has typed in a string; only possible with an editable combobox
            String word = (String)cb.getSelectedItem();
            if (!"".equals(word)) {
                System.out.println(word);
                this.disableWordComboBox();
                mainController.provideWord(word);
                cb.setSelectedIndex(0);             
                DefaultTableModel wordTableModel = (DefaultTableModel)wordTable.getModel();
                wordTableModel.addRow(new String[]{word});
            }
        }
    }//GEN-LAST:event_wordComboBoxActionPerformed

    public void disableWordComboBox() {
        wordComboBox.setEnabled(false);
    }
    
    public void enableWordComboBox() {
        wordComboBox.setEnabled(true);
        wordComboBox.grabFocus();
    }
    
    public void clearWordTable() {
        DefaultTableModel wordTableModel = (DefaultTableModel)wordTable.getModel();
        for (int i = wordTableModel.getRowCount() - 1; i >= 0 ; i--) {
            wordTableModel.removeRow(i);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {  
    }

    @Override
    public void windowClosing(WindowEvent e) {
        mainController.cancel();
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
            java.util.logging.Logger.getLogger(WordEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WordEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WordEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WordEntryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                WordEntryDialog dialog = new WordEntryDialog(new javax.swing.JDialog(), true, null);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel message1Label;
    private javax.swing.JLabel message2Label;
    private javax.swing.JComboBox wordComboBox;
    private javax.swing.JTable wordTable;
    // End of variables declaration//GEN-END:variables

}