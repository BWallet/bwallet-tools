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
import com.bdx.bwallet.tools.core.utils.FailureMessageUtils;
import com.bdx.bwallet.tools.model.Device;
import com.bdx.bwallet.tools.ui.utils.PINEntryUtils;
import com.google.common.eventbus.Subscribe;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.KeyChain;
import org.hid4java.HidDevice;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.DecoderException;

/**
 *
 * @author Administrator
 */
public class SignMessageDialog extends javax.swing.JDialog implements WindowListener {

    private ResourceBundle bundle;

    private MainController mainController;

    private JDialog messageDialog;

    private Device device;

    /**
     * Creates new form SignMessageDialog
     */
    public SignMessageDialog(java.awt.Frame parent, boolean modal, ResourceBundle bundle, MainController mainController, Device device) {
        super(parent, modal);
        initComponents();

        this.mainController = mainController;
        this.device = device;
        this.bundle = bundle;
        
        init();
    }

    public SignMessageDialog(java.awt.Dialog parent, boolean modal, ResourceBundle bundle, MainController mainController, Device device) {
        super(parent, modal);
        initComponents();

        this.mainController = mainController;
        this.device = device;
        this.bundle = bundle;
        
        init();
    }
    
    private void init() {
        JOptionPane messagePanel = new JOptionPane(bundle.getString("MessageDialog.confirmAction"), JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null,
                new Object[]{}, null);
        messageDialog = messagePanel.createDialog(this, bundle.getString("SignMessageDialog.title"));
        messageDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        messageDialog.setSize(400, 150);
        messageDialog.setLocationRelativeTo(null);
        messageDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SignMessageDialog.this.mainController.cancel();
            }
        });

        this.addWindowListener(this);

        sMessageTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
            }
        });

        accountTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
                addressContentLabel.setText("");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
                addressContentLabel.setText("");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
                addressContentLabel.setText("");
            }
        });

        indexTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
                addressContentLabel.setText("");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
                addressContentLabel.setText("");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sSignatureTextArea.setText("");
                addressContentLabel.setText("");
            }
        });

        applyResourceBundle();

        addressContentLabel.setText("");
    }
    
    public void applyResourceBundle() {
        setTitle(bundle.getString("SignMessageDialog.title"));

        ((TitledBorder) signPanel.getBorder()).setTitle(bundle.getString("SignMessageDialog.signPanel.border.title"));
        ((TitledBorder) verifyPanel.getBorder()).setTitle(bundle.getString("SignMessageDialog.verifyPanel.border.title"));

        sMessageLabel.setText(bundle.getString("SignMessageDialog.sMessageLabel.text"));
        sSignatureLabel.setText(bundle.getString("SignMessageDialog.sSignatureLabel.text"));
        sAddressLabel.setText(bundle.getString("SignMessageDialog.sAddressLabel.text"));
        signButton.setText(bundle.getString("SignMessageDialog.signButton.text"));
        accountLabel.setText(bundle.getString("SignMessageDialog.accountLabel.text"));
        indexLabel.setText(bundle.getString("SignMessageDialog.indexLabel.text"));
        vMessageLabel.setText(bundle.getString("SignMessageDialog.vMessageLabel.text"));
        vSignatureLabel.setText(bundle.getString("SignMessageDialog.vSignatureLabel.text"));
        vAddressLabel.setText(bundle.getString("SignMessageDialog.vAddressLabel.text"));
        verifyButton.setText(bundle.getString("SignMessageDialog.verifyButton.text"));
        copyAddressButton.setText(bundle.getString("SignMessageDialog.copyAddressButton.text"));
        
        accountTextField.setToolTipText(bundle.getString("SignMessageDialog.accountTextField.toolTipText"));
        indexTextField.setToolTipText(bundle.getString("SignMessageDialog.indexTextField.toolTipText"));
        
        purposeComboBox.removeAllItems();
        purposeComboBox.addItem(bundle.getString("SignMessageDialog.purposeComboBox.receive.text"));
        purposeComboBox.addItem(bundle.getString("SignMessageDialog.purposeComboBox.change.text"));
    }

    public void setXPub(DeterministicKey xpub) {
        int account = xpub.getParent().getParent().getChildNumber().num() + 1;
        int purpose = xpub.getParent().getChildNumber().num();
        int index = xpub.getChildNumber().num();
        String address = xpub.toAddress(MainNetParams.get()).toString();
        accountTextField.setText("" + account);
        purposeComboBox.setSelectedIndex(purpose);
        indexTextField.setText("" + index);
        addressContentLabel.setText(address);
        addressTextField.setText(address);
        accountTextField.setEditable(false);
        purposeComboBox.setEditable(false);
        purposeComboBox.setEnabled(false);
        indexTextField.setEditable(false);
        addressTextField.setEditable(false);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        signPanel = new javax.swing.JPanel();
        sMessageLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sMessageTextArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        sSignatureTextArea = new javax.swing.JTextArea();
        sSignatureLabel = new javax.swing.JLabel();
        sAddressLabel = new javax.swing.JLabel();
        signButton = new javax.swing.JButton();
        accountLabel = new javax.swing.JLabel();
        accountTextField = new javax.swing.JTextField();
        indexLabel = new javax.swing.JLabel();
        indexTextField = new javax.swing.JTextField();
        purposeComboBox = new javax.swing.JComboBox();
        copyAddressButton = new javax.swing.JButton();
        addressContentLabel = new javax.swing.JLabel();
        verifyPanel = new javax.swing.JPanel();
        vMessageLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        vMessageTextArea = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        vSignatureTextArea = new javax.swing.JTextArea();
        vSignatureLabel = new javax.swing.JLabel();
        vAddressLabel = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        verifyButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sign & Verify");
        setResizable(false);

        signPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Sign message "));
        signPanel.setPreferredSize(new java.awt.Dimension(430, 420));

        sMessageLabel.setText("Message");

        sMessageTextArea.setColumns(20);
        sMessageTextArea.setLineWrap(true);
        sMessageTextArea.setRows(5);
        jScrollPane1.setViewportView(sMessageTextArea);

        sSignatureTextArea.setEditable(false);
        sSignatureTextArea.setColumns(20);
        sSignatureTextArea.setLineWrap(true);
        sSignatureTextArea.setRows(5);
        jScrollPane3.setViewportView(sSignatureTextArea);

        sSignatureLabel.setText("Signature");

        sAddressLabel.setText("Address");

        signButton.setText("Sign");
        signButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signButtonActionPerformed(evt);
            }
        });

        accountLabel.setText("Account Index");

        indexLabel.setText("Address Index");

        purposeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Receive", "Change" }));

        copyAddressButton.setText("Copy");
        copyAddressButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyAddressButtonActionPerformed(evt);
            }
        });

        addressContentLabel.setText(" ");

        javax.swing.GroupLayout signPanelLayout = new javax.swing.GroupLayout(signPanel);
        signPanel.setLayout(signPanelLayout);
        signPanelLayout.setHorizontalGroup(
            signPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(signPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3)
                    .addGroup(signPanelLayout.createSequentialGroup()
                        .addGroup(signPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sMessageLabel)
                            .addComponent(sSignatureLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(signPanelLayout.createSequentialGroup()
                        .addComponent(accountLabel)
                        .addGap(5, 5, 5)
                        .addComponent(accountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(purposeComboBox, 0, 103, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(indexLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(indexTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(signPanelLayout.createSequentialGroup()
                        .addComponent(sAddressLabel)
                        .addGap(18, 18, 18)
                        .addComponent(addressContentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(copyAddressButton)))
                .addContainerGap())
            .addGroup(signPanelLayout.createSequentialGroup()
                .addGap(156, 156, 156)
                .addComponent(signButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        signPanelLayout.setVerticalGroup(
            signPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(signPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accountLabel)
                    .addComponent(indexLabel)
                    .addComponent(indexTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(purposeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(signPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sAddressLabel)
                    .addComponent(copyAddressButton)
                    .addComponent(addressContentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sSignatureLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(signButton)
                .addGap(14, 14, 14))
        );

        verifyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Verify message "));

        vMessageLabel.setText("Message");

        vMessageTextArea.setColumns(20);
        vMessageTextArea.setLineWrap(true);
        vMessageTextArea.setRows(5);
        jScrollPane2.setViewportView(vMessageTextArea);

        vSignatureTextArea.setColumns(20);
        vSignatureTextArea.setLineWrap(true);
        vSignatureTextArea.setRows(5);
        jScrollPane4.setViewportView(vSignatureTextArea);

        vSignatureLabel.setText("Signature");

        vAddressLabel.setText("Address");

        verifyButton.setText("Verify");
        verifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout verifyPanelLayout = new javax.swing.GroupLayout(verifyPanel);
        verifyPanel.setLayout(verifyPanelLayout);
        verifyPanelLayout.setHorizontalGroup(
            verifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verifyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(verifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane4)
                    .addGroup(verifyPanelLayout.createSequentialGroup()
                        .addGroup(verifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vMessageLabel)
                            .addComponent(vSignatureLabel)
                            .addComponent(vAddressLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(addressTextField))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, verifyPanelLayout.createSequentialGroup()
                .addContainerGap(180, Short.MAX_VALUE)
                .addComponent(verifyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(168, 168, 168))
        );
        verifyPanelLayout.setVerticalGroup(
            verifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verifyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vAddressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(vSignatureLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(verifyButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(signPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(verifyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(signPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                    .addComponent(verifyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private int getAccountIndex() {
        String accountText = accountTextField.getText().trim();
        if ("".equals(accountText)) {
            throw new IllegalArgumentException(bundle.getString("SignMessageDialog.MessageDialog.emptyAccount"));
        }
        int account;
        try {
            account = Integer.parseInt(accountText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(bundle.getString("SignMessageDialog.MessageDialog.invalidAccount"));
        }
        if (account < 1)
            throw new IllegalArgumentException(bundle.getString("SignMessageDialog.MessageDialog.accountMustGeZero"));
        return account;
    }

    private int getAddressIndex() {
        String indexText = indexTextField.getText().trim();
        if ("".equals(indexText)) {
            throw new IllegalArgumentException(bundle.getString("SignMessageDialog.MessageDialog.emptyIndex"));
        }
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(bundle.getString("SignMessageDialog.MessageDialog.invalidIndex"));
        }
        if (index < 0)
            throw new IllegalArgumentException(bundle.getString("SignMessageDialog.MessageDialog.indexMustGeZero"));
        return index;
    }

    private KeyChain.KeyPurpose getKeyPurpose() {
        KeyChain.KeyPurpose purpose;
        if (purposeComboBox.getSelectedIndex() == 0) {
            purpose = KeyChain.KeyPurpose.RECEIVE_FUNDS;
        } else {
            purpose = KeyChain.KeyPurpose.CHANGE;
        }
        return purpose;
    }

    private void signButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signButtonActionPerformed
        if (device != null) {
            String messageText = sMessageTextArea.getText().trim();
            if ("".equals(messageText)) {
                JOptionPane.showMessageDialog(this, bundle.getString("SignMessageDialog.MessageDialog.emptyMessage"));
                return;
            }
            byte[] message = messageText.getBytes();

            int account;
            int index;
            KeyChain.KeyPurpose purpose;
            try {
                account = this.getAccountIndex();
                index = this.getAddressIndex();
                purpose = this.getKeyPurpose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
                return;
            }

            mainController.signMessage(device, account - 1, purpose, index, message);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceDetached"));
        }
    }//GEN-LAST:event_signButtonActionPerformed

    private void verifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyButtonActionPerformed
        if (device != null) {
            String messageText = vMessageTextArea.getText().trim();
            if ("".equals(messageText)) {
                JOptionPane.showMessageDialog(this, bundle.getString("SignMessageDialog.MessageDialog.emptyMessage"));
                return;
            }
            byte[] message = messageText.getBytes();

            String addressText = addressTextField.getText().trim();
            if ("".equals(addressText)) {
                JOptionPane.showMessageDialog(this, bundle.getString("SignMessageDialog.MessageDialog.emptyAddress"));
                return;
            }
            Address address = null;
            try {
                address = new Address(MainNetParams.get(), addressText);
            } catch (AddressFormatException ex) {
                JOptionPane.showMessageDialog(this, bundle.getString("SignMessageDialog.MessageDialog.invalidAddress"));
                return;
            }

            String signatureText = vSignatureTextArea.getText().trim();
            if ("".equals(signatureText)) {
                JOptionPane.showMessageDialog(this, bundle.getString("SignMessageDialog.MessageDialog.emptySignature"));
                return;
            }
            byte[] signature;
            try {
                signature = Base64.decode(signatureText);
            } catch (DecoderException e) {
                JOptionPane.showMessageDialog(this, bundle.getString("SignMessageDialog.MessageDialog.invalidSignature"));
                return;
            }

            mainController.verifyMessage(device, address, signature, message);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceDetached"));
        }
    }//GEN-LAST:event_verifyButtonActionPerformed

    private void copyAddressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyAddressButtonActionPerformed
        String text = addressContentLabel.getText();
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }//GEN-LAST:event_copyAddressButtonActionPerformed

    @Subscribe
    public void onHardwareWalletEvent(HardwareWalletEvent event) {
        System.out.println(event.getEventType());
        switch (event.getEventType()) {
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
                    @Override
                    public void run() {
                        messageDialog.setVisible(true);
                    }
                });
                break;
            case MESSAGE_SIGNATURE:
                BWalletMessage.MessageSignature messageSignature = (BWalletMessage.MessageSignature) event.getMessage().get();
                byte[] bytes = messageSignature.getSignature().toByteArray();
                String signature = Base64.toBase64String(bytes);
                sSignatureTextArea.setText(signature);
                messageDialog.setVisible(false);
                if (device != null) {
                    mainController.getAddress(device, this.getAccountIndex() - 1, this.getKeyPurpose(), this.getAddressIndex(), false);
                }
                break;
            case ADDRESS:
                BWalletMessage.Address address = (BWalletMessage.Address) event.getMessage().get();
                addressContentLabel.setText(address.getAddress());
                break;
            case SHOW_OPERATION_SUCCEEDED:
                messageDialog.setVisible(false);
                JOptionPane.showMessageDialog(this, bundle.getString("SignMessageDialog.MessageDialog.success"));
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
        } else if (event.getEventType() == MessageEventType.DEVICE_FAILED) {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
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
            java.util.logging.Logger.getLogger(SignMessageDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SignMessageDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SignMessageDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SignMessageDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //Locale.setDefault(Locale.ENGLISH);
                SignMessageDialog dialog = new SignMessageDialog(new javax.swing.JFrame(), true, ResourceBundle.getBundle("com/bdx/bwallet/tools/ui/Bundle"), null, null);
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
    private javax.swing.JLabel accountLabel;
    private javax.swing.JTextField accountTextField;
    private javax.swing.JLabel addressContentLabel;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JButton copyAddressButton;
    private javax.swing.JLabel indexLabel;
    private javax.swing.JTextField indexTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JComboBox purposeComboBox;
    private javax.swing.JLabel sAddressLabel;
    private javax.swing.JLabel sMessageLabel;
    private javax.swing.JTextArea sMessageTextArea;
    private javax.swing.JLabel sSignatureLabel;
    private javax.swing.JTextArea sSignatureTextArea;
    private javax.swing.JButton signButton;
    private javax.swing.JPanel signPanel;
    private javax.swing.JLabel vAddressLabel;
    private javax.swing.JLabel vMessageLabel;
    private javax.swing.JTextArea vMessageTextArea;
    private javax.swing.JLabel vSignatureLabel;
    private javax.swing.JTextArea vSignatureTextArea;
    private javax.swing.JButton verifyButton;
    private javax.swing.JPanel verifyPanel;
    // End of variables declaration//GEN-END:variables
}
