/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletType;
import com.bdx.bwallet.tools.controllers.MainController;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvent;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.utils.FailureMessageUtils;
import com.bdx.bwallet.tools.model.Device;
import com.bdx.bwallet.tools.ui.utils.ButtonColumn;
import com.bdx.bwallet.tools.ui.utils.IconUtils;
import com.bdx.bwallet.tools.ui.utils.PINEntryUtils;
import com.bdx.bwallet.tools.ui.utils.TableUtils;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.hid4java.HidDevice;

/**
 *
 * @author Administrator
 */
public class AccountLabelDialog extends javax.swing.JDialog implements WindowListener {

    private ResourceBundle bundle;
    
    private MainController mainController;

    private final JDialog messageDialog;

    private Device device;

    private final ImageIcon editIcon = IconUtils.createImageIcon("/icons/edit.png", "Edit");
    private final ImageIcon removeIcon = IconUtils.createImageIcon("/icons/remove.png", "Remove");

    /**
     * Creates new form ApplySettingsDialog
     *
     * @param parent
     * @param modal
     * @param mainController
     * @param device
     */
    public AccountLabelDialog(java.awt.Frame parent, boolean modal, ResourceBundle bundle, MainController mainController, Device device) {
        super(parent, modal);
        initComponents();

        this.mainController = mainController;
        this.device = device;

        JOptionPane messagePanel = new JOptionPane(bundle.getString("MessageDialog.confirmAction"), JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null,
                new Object[]{}, null);
        messageDialog = messagePanel.createDialog(this, bundle.getString("AccountLabelDialog.title"));
        messageDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        messageDialog.setSize(400, 150);
        messageDialog.setLocationRelativeTo(null);
        messageDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AccountLabelDialog.this.mainController.cancel();
            }
        });

        labelTable.setRowHeight(30);
        labelTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        labelTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        labelTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        labelTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        labelTable.getColumnModel().getColumn(3).setPreferredWidth(30);
        Action remove = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Device device = AccountLabelDialog.this.device;
                final MainController mainController = AccountLabelDialog.this.mainController;
                if (device != null) {
                    JTable table = (JTable) e.getSource();
                    int modelRow = Integer.valueOf(e.getActionCommand());
                    Integer index = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 0);
                    mainController.removeAccountLabel(device, (String) coinComboBox.getSelectedItem(), index);
                } else {
                    JOptionPane.showMessageDialog(AccountLabelDialog.this, AccountLabelDialog.this.bundle.getString("MessageDialog.deviceDetached"));
                }
            }
        };
        Action edit = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Device device = AccountLabelDialog.this.device;
                final MainController mainController = AccountLabelDialog.this.mainController;
                if (device != null) {
                    JTable table = (JTable) e.getSource();
                    int modelRow = Integer.valueOf(e.getActionCommand());
                    Integer index = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 0);
                    String label = (String) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 1);
                    AccountLabelEntryDialog entryDialog = new AccountLabelEntryDialog(AccountLabelDialog.this, true, AccountLabelDialog.this.bundle, true,
                            Optional.fromNullable(index), Optional.fromNullable(label));
                    entryDialog.setLocationRelativeTo(null);
                    entryDialog.setVisible(true);
                    if (entryDialog.isCancel()) {
                        mainController.cancel();
                    } else {
                        String l = entryDialog.getLabel();
                        mainController.setAccountLabel(device, (String) coinComboBox.getSelectedItem(), index, l);
                    }
                } else {
                    JOptionPane.showMessageDialog(AccountLabelDialog.this, AccountLabelDialog.this.bundle.getString("MessageDialog.deviceDetached"));
                }
            }
        };
        ButtonColumn buttonColumn1 = new ButtonColumn(labelTable, remove, 2);
        ButtonColumn buttonColumn2 = new ButtonColumn(labelTable, edit, 3);

        this.addWindowListener(this);

        coinComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                Object item = e.getItem();
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (AccountLabelDialog.this.device != null) {
                        AccountLabelDialog.this.mainController.getAccountLabels(AccountLabelDialog.this.device, (String) e.getItem(), true, 1);
                    } else {
                        JOptionPane.showMessageDialog(AccountLabelDialog.this, AccountLabelDialog.this.bundle.getString("MessageDialog.deviceDetached"));
                    }
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                }
            }
        });
        
        this.bundle = bundle;
        applyResourceBundle();
    }

    public void applyResourceBundle() {
        setTitle(bundle.getString("AccountLabelDialog.title")); 
        settingButton.setText(bundle.getString("AccountLabelDialog.settingButton.text")); 
        
        TableUtils.setHeader(labelTable, 0, bundle.getString("AccountLabelDialog.labelTable.header.text.0"));
        TableUtils.setHeader(labelTable, 1, bundle.getString("AccountLabelDialog.labelTable.header.text.1"));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingButton = new javax.swing.JButton();
        coinComboBox = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        labelTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Account Label Setting");
        setResizable(false);

        settingButton.setText("Start Setting");
        settingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingButtonActionPerformed(evt);
            }
        });

        coinComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bitcoin", "Testnet", "Namecoin", "Litecoin", "Dogecoin", "Dash" }));

        labelTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Index", "Label", " ", " "
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(labelTable);
        if (labelTable.getColumnModel().getColumnCount() > 0) {
            labelTable.getColumnModel().getColumn(0).setResizable(false);
            labelTable.getColumnModel().getColumn(1).setResizable(false);
            labelTable.getColumnModel().getColumn(2).setResizable(false);
            labelTable.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coinComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(settingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(coinComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(settingButton)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void settingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingButtonActionPerformed
        if (device != null) {
            DefaultTableModel wordTableModel = (DefaultTableModel) labelTable.getModel();
            if (wordTableModel.getRowCount() >= 32) {
                JOptionPane.showMessageDialog(this, bundle.getString("AccountLabelDialog.MessageDialog.labelCannotMoreThan32"));
                return ;
            }
            AccountLabelEntryDialog entryDialog = new AccountLabelEntryDialog(this, true, bundle, false, Optional.<Integer>absent(), Optional.<String>absent());
            entryDialog.setLocationRelativeTo(null);
            entryDialog.setVisible(true);
            if (entryDialog.isCancel()) {
                mainController.cancel();
            } else {
                String coin = (String) coinComboBox.getSelectedItem();
                Integer index = entryDialog.getIndex();
                String label = entryDialog.getLabel();
                mainController.setAccountLabel(device, coin, index, label);
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceDetached"));
        }
    }//GEN-LAST:event_settingButtonActionPerformed

    @Subscribe
    public void onHardwareWalletEvent(HardwareWalletEvent event) {
        System.out.println(event.getEventType());
        switch (event.getEventType()) {
            case SHOW_PIN_ENTRY:
                PINEntryDialog pinEntryDialog = PINEntryUtils.createDialog(this, bundle, event.getMessage());
                pinEntryDialog.setVisible(true);
                if (pinEntryDialog.isCancel()) {
                    mainController.cancel();
                } else {
                    String pin = pinEntryDialog.getPin();
                    mainController.providePin(pin);
                }
                break;
            case SHOW_BUTTON_PRESS:
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        messageDialog.setVisible(true);
                    }
                });
                break;
            case ACCOUNT_LABELS:
                final BWalletMessage.AccountLabels accountLabels = (BWalletMessage.AccountLabels) event.getMessage().get();
                DefaultTableModel wordTableModel = (DefaultTableModel) labelTable.getModel();
                System.out.println(accountLabels.getCoinName());
                for (int i = wordTableModel.getRowCount() - 1; i >= 0; i--) {
                    wordTableModel.removeRow(i);
                }
                for (int i = 0; i < accountLabels.getLabelsCount(); i++) {
                    BWalletType.AccountLabelType accountLabel = accountLabels.getLabels(i);
                    Integer index = accountLabel.getIndex();
                    String label = accountLabel.getLabel();
                    wordTableModel.addRow(new Object[]{index, label, removeIcon, editIcon});
                }
                break;
            case SHOW_OPERATION_SUCCEEDED:
                messageDialog.setVisible(false);
                JOptionPane.showMessageDialog(this, bundle.getString("AccountLabelDialog.MessageDialog.success"));
                loadLables();
                break;
            case SHOW_OPERATION_FAILED:
                messageDialog.setVisible(false);
                JOptionPane.showMessageDialog(this, FailureMessageUtils.extract(event.getMessage()));
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.getEventType() == MessageEventType.DEVICE_DETACHED) {
            HidDevice hidDevice = event.getDevice().get();
            if (hidDevice.getPath() != null && hidDevice.getPath().equals(device.getPath())) {
                device = null;
                messageDialog.setVisible(false);
                JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceDetached"));
            }
        }
    }

    protected void loadLables() {
        if (this.device != null) {
            mainController.getAccountLabels(device, (String) coinComboBox.getSelectedItem(), true, 1);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        HardwareWalletEvents.subscribe(this);
        MessageEvents.subscribe(this);

        loadLables();
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        HardwareWalletEvents.unsubscribe(this);
        MessageEvents.unsubscribe(this);
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
            java.util.logging.Logger.getLogger(AccountLabelDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AccountLabelDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AccountLabelDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AccountLabelDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AccountLabelDialog dialog = new AccountLabelDialog(new javax.swing.JFrame(), true, ResourceBundle.getBundle("com/bdx/bwallet/tools/ui/Bundle"), null, null);
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
    private javax.swing.JComboBox coinComboBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable labelTable;
    private javax.swing.JButton settingButton;
    // End of variables declaration//GEN-END:variables
}
