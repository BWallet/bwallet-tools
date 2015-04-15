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
import com.bdx.bwallet.tools.model.Language;
import com.bdx.bwallet.tools.ui.utils.PINEntryUtils;
import com.google.common.eventbus.Subscribe;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.hid4java.HidDevice;

/**
 *
 * @author Administrator
 */
public class CreateWalletDialog extends javax.swing.JDialog implements WindowListener {

    private ResourceBundle bundle;

    private MainController mainController;

    private JDialog messageDialog;

    private Device device;

    private boolean recoveryStarted = false;
    private int recoveryWords = 0;
    private int recoveryWordsDone = 0;
    private int recoveryCurrentWord = 1;

    /**
     * Creates new form CreateWalletDialog
     */
    public CreateWalletDialog(java.awt.Frame parent, boolean modal, ResourceBundle bundle, MainController mainController, Device device) {
        super(parent, modal);
        initComponents();

        DefaultComboBoxModel languageComboBoxModel = (DefaultComboBoxModel) languageComboBox.getModel();
        languageComboBoxModel.removeAllElements();
        languageComboBoxModel.addElement(new Language("english", "English"));
        languageComboBoxModel.addElement(new Language("chinese", "中文"));

        JOptionPane messagePanel = new JOptionPane(bundle.getString("MessageDialog.confirmAction"), JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null,
                new Object[]{}, null);
        messageDialog = messagePanel.createDialog(this, bundle.getString("CreateWalletDialog.title"));
        messageDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        messageDialog.setSize(720, 150);
        messageDialog.setLocationRelativeTo(null);
        messageDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CreateWalletDialog.this.mainController.cancel();
            }
        });     

        this.mainController = mainController;
        this.device = device;

        this.addWindowListener(this);
        lengthSlider.addChangeListener(new ChangeListener() {
            private boolean isUserTrigger = true;

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting() && isUserTrigger) {
                    if (source.getValue() != 24) {
                        int reply = JOptionPane.showConfirmDialog(CreateWalletDialog.this, CreateWalletDialog.this.bundle.getString("CreateWalletDialog.MessageDialog.confirmChangeSeedLength"), "", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.NO_OPTION) {
                            isUserTrigger = false;
                            source.setValue(24);
                            isUserTrigger = true;
                        }
                    }
                }
            }
        });

        this.bundle = bundle;
        applyResourceBundle();
    }

    public void applyResourceBundle() {
        setTitle(bundle.getString("CreateWalletDialog.title")); 
        
        ((TitledBorder)basicPanel.getBorder()).setTitle(bundle.getString("CreateWalletDialog.basicPanel.border.title"));
        ((TitledBorder)advancedPanel.getBorder()).setTitle(bundle.getString("CreateWalletDialog.advancedPanel.border.title"));
        
        labelLabel.setText(bundle.getString("CreateWalletDialog.labelLabel.text")); 
        languageLabel.setText(bundle.getString("CreateWalletDialog.languageLabel.text")); 
        labelTextField.setText(bundle.getString("CreateWalletDialog.labelTextField.text")); 
        continueButton.setText(bundle.getString("CreateWalletDialog.continueButton.text")); 
        lengthLabel.setText(bundle.getString("CreateWalletDialog.lengthLabel.text")); 
        len12Label.setText(bundle.getString("CreateWalletDialog.len12Label.text")); 
        len18Label.setText(bundle.getString("CreateWalletDialog.len18Label.text")); 
        len24Label.setText(bundle.getString("CreateWalletDialog.len24Label.text")); 
        pinLabel.setText(bundle.getString("CreateWalletDialog.pinLabel.text")); 
        passphraseLabel.setText(bundle.getString("CreateWalletDialog.passphraseLabel.text")); 
        pinCheckBox.setText(bundle.getString("CreateWalletDialog.pinCheckBox.text")); 
        passphraseCheckBox.setText(bundle.getString("CreateWalletDialog.passphraseCheckBox.text")); 
        
        labelTextField.setToolTipText(bundle.getString("CreateWalletDialog.labelTextField.toolTipText"));
        languageComboBox.setToolTipText(bundle.getString("CreateWalletDialog.languageComboBox.toolTipText"));
        lengthSlider.setToolTipText(bundle.getString("CreateWalletDialog.lengthSlider.toolTipText"));
        pinCheckBox.setToolTipText(bundle.getString("CreateWalletDialog.pinCheckBox.toolTipText"));
        passphraseCheckBox.setToolTipText(bundle.getString("CreateWalletDialog.passphraseCheckBox.toolTipText"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        basicPanel = new javax.swing.JPanel();
        labelLabel = new javax.swing.JLabel();
        languageLabel = new javax.swing.JLabel();
        labelTextField = new javax.swing.JTextField();
        languageComboBox = new javax.swing.JComboBox();
        continueButton = new javax.swing.JButton();
        advancedPanel = new javax.swing.JPanel();
        lengthLabel = new javax.swing.JLabel();
        lengthSlider = new javax.swing.JSlider();
        len12Label = new javax.swing.JLabel();
        len18Label = new javax.swing.JLabel();
        len24Label = new javax.swing.JLabel();
        pinLabel = new javax.swing.JLabel();
        passphraseLabel = new javax.swing.JLabel();
        pinCheckBox = new javax.swing.JCheckBox();
        passphraseCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Device Setup");
        setResizable(false);

        basicPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Basic settings "));

        labelLabel.setText("Device label");

        languageLabel.setText("Device language");

        labelTextField.setText("My BWallet");

        languageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "English", "中文" }));
        languageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout basicPanelLayout = new javax.swing.GroupLayout(basicPanel);
        basicPanel.setLayout(basicPanelLayout);
        basicPanelLayout.setHorizontalGroup(
            basicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(basicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(basicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(languageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(languageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                    .addComponent(labelTextField))
                .addContainerGap())
        );
        basicPanelLayout.setVerticalGroup(
            basicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(basicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(languageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        continueButton.setText("Continue");
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });

        advancedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Advanced settings "));

        lengthLabel.setText("Recovery seed length");

        lengthSlider.setMaximum(24);
        lengthSlider.setMinimum(12);
        lengthSlider.setMinorTickSpacing(6);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setSnapToTicks(true);

        len12Label.setText("12 words");
        len12Label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        len18Label.setText("18 words");
        len18Label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        len24Label.setText("24 words");
        len24Label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        pinLabel.setText("PIN protection");

        passphraseLabel.setText("Additional passphrase encryption");

        pinCheckBox.setSelected(true);
        pinCheckBox.setText("Enable PIN protection");
        pinCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pinCheckBoxActionPerformed(evt);
            }
        });

        passphraseCheckBox.setText("Enable passphrase encryption");
        passphraseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passphraseCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout advancedPanelLayout = new javax.swing.GroupLayout(advancedPanel);
        advancedPanel.setLayout(advancedPanelLayout);
        advancedPanelLayout.setHorizontalGroup(
            advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(advancedPanelLayout.createSequentialGroup()
                        .addComponent(len12Label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(len18Label)
                        .addGap(191, 191, 191)
                        .addComponent(len24Label))
                    .addGroup(advancedPanelLayout.createSequentialGroup()
                        .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passphraseCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pinCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lengthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lengthSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passphraseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        advancedPanelLayout.setVerticalGroup(
            advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lengthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lengthSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(advancedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(len24Label)
                        .addComponent(len18Label, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(len12Label, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pinCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passphraseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passphraseCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(basicPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addComponent(advancedPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(continueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(241, 241, 241))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(basicPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(advancedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(continueButton)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void languageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_languageComboBoxActionPerformed

    private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
        if (device != null) {
            Language language = (Language) ((DefaultComboBoxModel) languageComboBox.getModel()).getSelectedItem();
            String label = labelTextField.getText();
            boolean pinProtection = pinCheckBox.isSelected();
            boolean passphraseProtection = passphraseCheckBox.isSelected();
            int seedLength = lengthSlider.getValue();

            recoveryStarted = false;
            recoveryWords = seedLength;
            recoveryWordsDone = 0;
            recoveryCurrentWord = 1;

            mainController.createWallet(device, language.getValue(), label, false, pinProtection, passphraseProtection, (seedLength / 6) * 64);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceDetached"));
        }
    }//GEN-LAST:event_continueButtonActionPerformed

    private void pinCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pinCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pinCheckBoxActionPerformed

    private void passphraseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passphraseCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passphraseCheckBoxActionPerformed

    protected String processOrdinal(String input) {
        if (StringUtils.isBlank(input))
            return input;
        String prefix = "ordinal(";
        int start = input.indexOf(prefix);
        int end = 0;
        if (start > 0) {
            char[] chars = input.toCharArray();
            String numberStr = "";
            for (int i = start + prefix.length(); i < chars.length; i++) {
                if (chars[i] == ')') {
                    end = i;
                    break;
                }
                numberStr += chars[i];
            }
            String output = input.substring(0, start);
            output += ordinal(Integer.parseInt(numberStr));
            output += input.substring(end + 1, input.length());
            return output;
        } else 
            return input;
    }
    
    protected String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];
        }
    }

    @Subscribe
    public void onHardwareWalletEvent(HardwareWalletEvent event) {
        System.out.println(event.getEventType());
        String msg = "";
        switch (event.getEventType()) {
            case SHOW_BUTTON_PRESS:
                if (!recoveryStarted) {
                    // First write
                    recoveryStarted = true;
                    msg = MessageFormat.format(bundle.getString("CreateWalletDialog.messageDialog.writeSeedFirst"), new Object[]{recoveryWords});
                    msg += MessageFormat.format(bundle.getString("CreateWalletDialog.messageDialog.writeSeed"), new Object[]{recoveryCurrentWord});
                } else {
                    recoveryWordsDone = recoveryWordsDone + 1;
                    recoveryCurrentWord = recoveryCurrentWord + 1;
                    if (recoveryWordsDone < recoveryWords) {
                        // Write
                        msg = MessageFormat.format(bundle.getString("CreateWalletDialog.messageDialog.writeSeed"), new Object[]{recoveryCurrentWord});
                    } else if (recoveryWordsDone == recoveryWords) {
                        // First check
                        recoveryCurrentWord = 1;
                        msg = MessageFormat.format(bundle.getString("CreateWalletDialog.messageDialog.checkSeedFirst"), new Object[]{recoveryWords});
                        msg += MessageFormat.format(bundle.getString("CreateWalletDialog.messageDialog.checkSeed"), new Object[]{recoveryCurrentWord});
                    } else if (recoveryWordsDone < 2 * recoveryWords - 1) {
                        // Check
                        msg = MessageFormat.format(bundle.getString("CreateWalletDialog.messageDialog.checkSeed"), new Object[]{recoveryCurrentWord});
                    } else {
                        // Last check
                        msg = MessageFormat.format(bundle.getString("CreateWalletDialog.messageDialog.checkSeed"), new Object[]{recoveryCurrentWord});
                    }
                }
                msg = processOrdinal(msg);
                ((JOptionPane) messageDialog.getContentPane().getComponent(0)).setMessage(msg);
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        messageDialog.setVisible(true);
                    }
                });
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
            case PROVIDE_ENTROPY:
                byte[] entropy = mainController.generateEntropy();
                mainController.provideEntropy(entropy);
                break;
            case SHOW_OPERATION_SUCCEEDED:
                messageDialog.setVisible(false);
                JOptionPane.showMessageDialog(this, bundle.getString("CreateWalletDialog.MessageDialog.success"));
                this.dispose();
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
            java.util.logging.Logger.getLogger(CreateWalletDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateWalletDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateWalletDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateWalletDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CreateWalletDialog dialog = new CreateWalletDialog(new javax.swing.JFrame(), true, ResourceBundle.getBundle("com/bdx/bwallet/tools/ui/Bundle"), null, null);
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
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JPanel basicPanel;
    private javax.swing.JButton continueButton;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JLabel len12Label;
    private javax.swing.JLabel len18Label;
    private javax.swing.JLabel len24Label;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JSlider lengthSlider;
    private javax.swing.JCheckBox passphraseCheckBox;
    private javax.swing.JLabel passphraseLabel;
    private javax.swing.JCheckBox pinCheckBox;
    private javax.swing.JLabel pinLabel;
    // End of variables declaration//GEN-END:variables
}
