/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.tools.controllers.MainController;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvent;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.utils.DeterministicKeyUtils;
import com.bdx.bwallet.tools.model.Device;
import com.bdx.bwallet.tools.ui.utils.ButtonColumn;
import com.bdx.bwallet.tools.ui.utils.IconUtils;
import com.bdx.bwallet.tools.ui.utils.LabelUtils;
import com.bdx.bwallet.tools.ui.utils.PINEntryUtils;
import com.bdx.bwallet.tools.ui.utils.QRCodes;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.hid4java.HidDevice;

/**
 *
 * @author Administrator
 */
public final class AccountDetailsDialog extends javax.swing.JDialog implements WindowListener {

    final static int PAGE_SIZE = 15;

    private ResourceBundle bundle;

    private MainController mainController;

    private final JDialog messageDialog;

    private Device device;

    private DeterministicKey xpub = null;

    private int rCurrentPage = 0;

    private int cCurrentPage = 0;

    private final ImageIcon viewIcon = IconUtils.createImageIcon("/icons/view.png", "View");

    /**
     * Creates new form AccountDetailsDialog
     */
    public AccountDetailsDialog(java.awt.Frame parent, boolean modal, ResourceBundle bundle, MainController mainController, Device device) {
        super(parent, modal);
        initComponents();

        this.mainController = mainController;
        this.device = device;

        JOptionPane messagePanel = new JOptionPane(bundle.getString("MessageDialog.confirmAction"), JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null,
                new Object[]{}, null);
        messageDialog = messagePanel.createDialog(this, bundle.getString("AccountDetailsDialog.title"));
        messageDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        messageDialog.setSize(400, 150);
        messageDialog.setLocationRelativeTo(null);
        messageDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AccountDetailsDialog.this.mainController.cancel();
            }
        });

        rAddressTable.setRowHeight(30);
        rAddressTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rAddressTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        rAddressTable.getColumnModel().getColumn(1).setPreferredWidth(255);
        rAddressTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        Action view1 = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.valueOf(e.getActionCommand());
                Integer index = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 0);
                if (xpub != null) {
                    DeterministicKey parentXpub = HDKeyDerivation.deriveChildKey(xpub, 0);
                    DeterministicKey childXpub = HDKeyDerivation.deriveChildKey(parentXpub, index);
                    AddressDetailsDialog addressDialog = new AddressDetailsDialog(AccountDetailsDialog.this, true, AccountDetailsDialog.this.bundle);
                    addressDialog.init(childXpub);
                    addressDialog.setLocationRelativeTo(null);
                    addressDialog.setVisible(true);
                }
            }
        };
        ButtonColumn buttonColumn1 = new ButtonColumn(rAddressTable, view1, 2);

        cAddressTable.setRowHeight(30);
        cAddressTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        cAddressTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        cAddressTable.getColumnModel().getColumn(1).setPreferredWidth(255);
        cAddressTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        Action view2 = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.valueOf(e.getActionCommand());
                Integer index = (Integer) ((DefaultTableModel) table.getModel()).getValueAt(modelRow, 0);
                if (xpub != null) {
                    DeterministicKey parentXpub = HDKeyDerivation.deriveChildKey(xpub, 1);
                    DeterministicKey childXpub = HDKeyDerivation.deriveChildKey(parentXpub, index);
                    AddressDetailsDialog addressDialog = new AddressDetailsDialog(AccountDetailsDialog.this, true, AccountDetailsDialog.this.bundle);
                    addressDialog.init(childXpub);
                    addressDialog.setLocationRelativeTo(null);
                    addressDialog.setVisible(true);
                }
            }
        };
        ButtonColumn buttonColumn2 = new ButtonColumn(cAddressTable, view2, 2);

        this.addWindowListener(this);

        this.bundle = bundle;
        applyResourceBundle();
    }

    public void applyResourceBundle() {
        setTitle(bundle.getString("AccountDetailsDialog.title")); 
        accountIndexLabel.setText(bundle.getString("AccountDetailsDialog.accountIndexLabel.text")); 
        getButton.setText(bundle.getString("AccountDetailsDialog.getButton.text")); 
        receivingAddressesLabel.setText(bundle.getString("AccountDetailsDialog.receivingAddressesLabel.text")); 
        changeAddressesLabel.setText(bundle.getString("AccountDetailsDialog.changeAddressesLabel.text")); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        xpubTextArea = new javax.swing.JTextArea();
        accountIndexLabel = new javax.swing.JLabel();
        accountIndexTextField = new javax.swing.JTextField();
        getButton = new javax.swing.JButton();
        separator = new javax.swing.JSeparator();
        xpubPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rAddressTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        cAddressTable = new javax.swing.JTable();
        nRButton = new javax.swing.JButton();
        pRButton = new javax.swing.JButton();
        receivingAddressesLabel = new javax.swing.JLabel();
        changeAddressesLabel = new javax.swing.JLabel();
        pCButton = new javax.swing.JButton();
        nCButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Get Account Details");
        setResizable(false);

        xpubTextArea.setEditable(false);
        xpubTextArea.setColumns(20);
        xpubTextArea.setLineWrap(true);
        xpubTextArea.setRows(5);
        jScrollPane1.setViewportView(xpubTextArea);

        accountIndexLabel.setText("Account Index");

        getButton.setText("Get");
        getButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout xpubPanelLayout = new javax.swing.GroupLayout(xpubPanel);
        xpubPanel.setLayout(xpubPanelLayout);
        xpubPanelLayout.setHorizontalGroup(
            xpubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        xpubPanelLayout.setVerticalGroup(
            xpubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        rAddressTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Index", "Address", " "
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(rAddressTable);
        if (rAddressTable.getColumnModel().getColumnCount() > 0) {
            rAddressTable.getColumnModel().getColumn(2).setResizable(false);
        }

        cAddressTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Index", "Address", " "
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(cAddressTable);
        if (cAddressTable.getColumnModel().getColumnCount() > 0) {
            cAddressTable.getColumnModel().getColumn(2).setResizable(false);
        }

        nRButton.setText(">>");
        nRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nRButtonActionPerformed(evt);
            }
        });

        pRButton.setText("<<");
        pRButton.setActionCommand("");
        pRButton.setEnabled(false);
        pRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pRButtonActionPerformed(evt);
            }
        });

        receivingAddressesLabel.setText("Receiving addresses");

        changeAddressesLabel.setText("Change addresses");

        pCButton.setText("<<");
        pCButton.setEnabled(false);
        pCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pCButtonActionPerformed(evt);
            }
        });

        nCButton.setText(">>");
        nCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nCButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                                .addComponent(xpubPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(accountIndexLabel)
                                .addGap(18, 18, 18)
                                .addComponent(accountIndexTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(getButton))
                            .addComponent(separator, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 2, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(pRButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nRButton))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(receivingAddressesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(changeAddressesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(pCButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nCButton)))
                                .addGap(20, 20, 20))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accountIndexLabel)
                    .addComponent(accountIndexTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(getButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(xpubPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(receivingAddressesLabel)
                    .addComponent(changeAddressesLabel))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nRButton)
                            .addComponent(pRButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pCButton)
                            .addComponent(nCButton))))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void getButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getButtonActionPerformed
        if (device != null) {
            String indexText = accountIndexTextField.getText();
            if ("".equals(indexText)) {
                JOptionPane.showMessageDialog(this, bundle.getString("AccountDetailsDialog.MessageDialog.emptyAccount"));
                return;
            }
            int index;
            try {
                index = Integer.parseInt(indexText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("AccountDetailsDialog.MessageDialog.invalidAccount"));
                return;
            }
            if (index < 1) {
                JOptionPane.showMessageDialog(this, bundle.getString("AccountDetailsDialog.MessageDialog.accountMustGeOne"));
                return;
            }
            List<ChildNumber> childNumbers = new ArrayList();
            childNumbers.add(new ChildNumber(44, true));
            childNumbers.add(new ChildNumber(0, true));
            childNumbers.add(new ChildNumber(index - 1, true));
            mainController.getDeterministicHierarchy(device, childNumbers);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceDetached"));
        }
    }//GEN-LAST:event_getButtonActionPerformed

    private void pRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pRButtonActionPerformed
        if (xpub != null) {
            rCurrentPage = rCurrentPage - 1;
            if (rCurrentPage < 1) {
                pRButton.setEnabled(false);
            }
            updateAddressTable(HDKeyDerivation.deriveChildKey(xpub, 0), (DefaultTableModel) rAddressTable.getModel(), rCurrentPage);
        }
    }//GEN-LAST:event_pRButtonActionPerformed

    private void nRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nRButtonActionPerformed
        if (xpub != null) {
            pRButton.setEnabled(true);
            rCurrentPage = rCurrentPage + 1;
            updateAddressTable(HDKeyDerivation.deriveChildKey(xpub, 0), (DefaultTableModel) rAddressTable.getModel(), rCurrentPage);
        }
    }//GEN-LAST:event_nRButtonActionPerformed

    private void pCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pCButtonActionPerformed
        if (xpub != null) {
            cCurrentPage = cCurrentPage - 1;
            if (cCurrentPage < 1) {
                pCButton.setEnabled(false);
            }
            updateAddressTable(HDKeyDerivation.deriveChildKey(xpub, 1), (DefaultTableModel) cAddressTable.getModel(), cCurrentPage);
        }
    }//GEN-LAST:event_pCButtonActionPerformed

    private void nCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nCButtonActionPerformed
        if (xpub != null) {
            pCButton.setEnabled(true);
            cCurrentPage = cCurrentPage + 1;
            updateAddressTable(HDKeyDerivation.deriveChildKey(xpub, 1), (DefaultTableModel) cAddressTable.getModel(), cCurrentPage);
        }
    }//GEN-LAST:event_nCButtonActionPerformed

    @Subscribe
    public void onHardwareWalletEvent(HardwareWalletEvent event) {
        System.out.println(event.getEventType());
        String msg = "";
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
            case SHOW_PASSPHRASE_ENTRY:
                PassphraseEntryDialog passphraseEntryDialog = new PassphraseEntryDialog(this, true, bundle);
                passphraseEntryDialog.setLocationRelativeTo(null);
                passphraseEntryDialog.setVisible(true);
                if (passphraseEntryDialog.isCancel()) {
                    mainController.cancel();
                } else {
                    String passphrase = passphraseEntryDialog.getPassphrase();
                    mainController.providePassphrase(passphrase);
                }
                break;
            case SHOW_BUTTON_PRESS:
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        messageDialog.setVisible(true);
                    }
                });
                break;
            case DETERMINISTIC_HIERARCHY:
                BWalletMessage.PublicKey publicKey = (BWalletMessage.PublicKey) event.getMessage().get();
                xpubTextArea.setText(publicKey.getXpub());

                Optional<BufferedImage> qrCodeImage = QRCodes.generateQRCode(publicKey.getXpub(), 2);
                JLabel imageLabel = LabelUtils.newImageLabel(qrCodeImage);
                imageLabel.setSize(180, 180);
                xpubPanel.removeAll();
                xpubPanel.add(imageLabel);
                xpubPanel.repaint();

                pRButton.setEnabled(false);
                pCButton.setEnabled(false);

                this.xpub = DeterministicKeyUtils.deserializeB58(publicKey.getXpub());
                rCurrentPage = 0;
                cCurrentPage = 0;
                updateAddressTable(HDKeyDerivation.deriveChildKey(xpub, 0), (DefaultTableModel) rAddressTable.getModel(), rCurrentPage);
                updateAddressTable(HDKeyDerivation.deriveChildKey(xpub, 1), (DefaultTableModel) cAddressTable.getModel(), cCurrentPage);

                break;
            case SHOW_OPERATION_SUCCEEDED:
                messageDialog.setVisible(false);
                msg = "Account Label Setting was successful";
                if (event.getMessage().isPresent()) {
                    BWalletMessage.Success success = (BWalletMessage.Success) event.getMessage().get();
                    msg = success.getMessage();
                }
                JOptionPane.showMessageDialog(this, msg);
                break;
            case SHOW_OPERATION_FAILED:
                messageDialog.setVisible(false);
                msg = "Set Account Label failed";
                if (event.getMessage().isPresent()) {
                    BWalletMessage.Failure failure = (BWalletMessage.Failure) event.getMessage().get();
                    msg = failure.getMessage();
                }
                JOptionPane.showMessageDialog(this, msg);
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
        } else if (event.getEventType() == MessageEventType.DEVICE_FAILED) {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
        }
    }

    protected void updateAddressTable(DeterministicKey parentXpub, DefaultTableModel tableModel, int page) {
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
        for (int i = 0; i < PAGE_SIZE; i++) {
            Integer index = i + PAGE_SIZE * page;
            String address = HDKeyDerivation.deriveChildKey(parentXpub, index).toAddress(MainNetParams.get()).toString();
            tableModel.addRow(new Object[]{index, address, viewIcon});
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        HardwareWalletEvents.subscribe(this);
        MessageEvents.subscribe(this);
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
            java.util.logging.Logger.getLogger(AccountDetailsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AccountDetailsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AccountDetailsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AccountDetailsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AccountDetailsDialog dialog = new AccountDetailsDialog(new javax.swing.JFrame(), true, ResourceBundle.getBundle("com/bdx/bwallet/tools/ui/Bundle"), null, null);
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
    private javax.swing.JLabel accountIndexLabel;
    private javax.swing.JTextField accountIndexTextField;
    private javax.swing.JTable cAddressTable;
    private javax.swing.JLabel changeAddressesLabel;
    private javax.swing.JButton getButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton nCButton;
    private javax.swing.JButton nRButton;
    private javax.swing.JButton pCButton;
    private javax.swing.JButton pRButton;
    private javax.swing.JTable rAddressTable;
    private javax.swing.JLabel receivingAddressesLabel;
    private javax.swing.JSeparator separator;
    private javax.swing.JPanel xpubPanel;
    private javax.swing.JTextArea xpubTextArea;
    // End of variables declaration//GEN-END:variables
}
