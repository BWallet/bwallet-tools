/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.tools.controllers.MainController;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.concurrent.SafeExecutors;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvent;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.model.Device;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.protobuf.ByteString;
import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import org.hid4java.HidDevice;
import org.spongycastle.util.encoders.Hex;

/**
 *
 * @author Administrator
 */
public class MainUI extends javax.swing.JFrame {

    private final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

    private MainController mainController = new MainController();

    private final String buyUrl = "https://bidingxing.com/bwallet";

    /**
     * Creates new form MainUI
     */
    public MainUI() {
        initComponents();

        MessageEvents.subscribe(this);
        HardwareWalletEvents.subscribe(this);

        DefaultListModel<Device> listModel = new DefaultListModel();
        devicesList.setModel(listModel);
        List<HidDevice> hidDevices = mainController.getDevices();
        for (HidDevice hidDevice : hidDevices) {
            Device device = new Device(hidDevice);
            listModel.addElement(device);
        }
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        DefaultListModel<Device> listModel = (DefaultListModel) devicesList.getModel();
        if (event.getEventType() == MessageEventType.DEVICE_ATTACHED) {
            System.out.println("MainUI onMessageEvent : " + event.getEventType());
            HidDevice hidDevice = event.getDevice().get();
            System.out.println("MainUI onMessageEvent : " + hidDevice.toString());
            Device device = new Device(hidDevice);
            listModel.addElement(device);
        } else if (event.getEventType() == MessageEventType.DEVICE_DETACHED) {
            HidDevice hidDevice = event.getDevice().get();
            Device device = new Device(hidDevice);
            listModel.removeElement(device);
        }
    }

    @Subscribe
    public void onHardwareWalletEvent(HardwareWalletEvent event) {
        //log.debug("{} Received hardware event: '{}'.", this, event.getEventType().name());
        switch (event.getEventType()) {
            case SHOW_DEVICE_FAILED:
                //handleDeviceFailed(event);
                break;
            case SHOW_DEVICE_READY:
                //handleDeviceReady(event);
                break;
            case SHOW_DEVICE_DETACHED:
                //handleDeviceDetached(event);
                break;
            case SHOW_DEVICE_STOPPED:
                //handleDeviceStopped(event);
                break;
            case SHOW_PIN_ENTRY:
                //handlePINEntry(event);
                break;
            case SHOW_BUTTON_PRESS:
                //handleButtonPress(event);
                break;
            case SHOW_OPERATION_SUCCEEDED:
                //handleOperationSucceeded(event);
                break;
            case SHOW_OPERATION_FAILED:
                //handleOperationFailed(event);
                break;
            case PROVIDE_ENTROPY:
                //handleProvideEntropy(event);
                break;
            case ADDRESS:
                //handleReceivedAddress(event);
                break;
            case PUBLIC_KEY:
                //handleReceivedPublicKey(event);
                break;
            case DETERMINISTIC_HIERARCHY:
                //handleReceivedDeterministicHierarchy(event);
                break;
            case MESSAGE_SIGNATURE:
                //handleReceivedMessageSignature(event);
                break;
            case SHOW_WORD_ENTRY:
                break;
            default:
                //log.warn("Unknown hardware wallet event type: {}", event.getEventType().name());
                break;
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

        devicesPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        devicesList = new javax.swing.JList();
        buyButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        resetDeviceButton = new javax.swing.JButton();
        recoveryDeviceButton = new javax.swing.JButton();
        wipeDeviceButton = new javax.swing.JButton();
        getPublicKeyButton = new javax.swing.JButton();
        getBlHashButton = new javax.swing.JButton();
        accountInfoButton = new javax.swing.JButton();
        updateFirmwareButton = new javax.swing.JButton();
        applySettingsButton = new javax.swing.JButton();
        changePinButton = new javax.swing.JButton();
        testScreenButton = new javax.swing.JButton();
        signAndVerifyButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BWallet Tools");
        setResizable(false);

        devicesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(" Devices "));

        devicesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(devicesList);

        javax.swing.GroupLayout devicesPanelLayout = new javax.swing.GroupLayout(devicesPanel);
        devicesPanel.setLayout(devicesPanelLayout);
        devicesPanelLayout.setHorizontalGroup(
            devicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, devicesPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        devicesPanelLayout.setVerticalGroup(
            devicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(devicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        buyButton.setText("Buy BWallet");
        buyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyButtonActionPerformed(evt);
            }
        });

        aboutButton.setText("About");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });

        resetDeviceButton.setText("Device Setup");
        resetDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDeviceButtonActionPerformed(evt);
            }
        });

        recoveryDeviceButton.setText("Recovery Device");
        recoveryDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recoveryDeviceButtonActionPerformed(evt);
            }
        });

        wipeDeviceButton.setText("Wipe Device");
        wipeDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wipeDeviceButtonActionPerformed(evt);
            }
        });

        getPublicKeyButton.setText("Get Public Key");
        getPublicKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getPublicKeyButtonActionPerformed(evt);
            }
        });

        getBlHashButton.setText("Get Bootloader Hash");
        getBlHashButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getBlHashButtonActionPerformed(evt);
            }
        });

        accountInfoButton.setText("Get Account Info");

        updateFirmwareButton.setText("Update Firmware");
        updateFirmwareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateFirmwareButtonActionPerformed(evt);
            }
        });

        applySettingsButton.setText("Apply Settings");
        applySettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applySettingsButtonActionPerformed(evt);
            }
        });

        changePinButton.setText("Change/Remove PIN");
        changePinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePinButtonActionPerformed(evt);
            }
        });

        testScreenButton.setText("Test Screen");

        signAndVerifyButton.setText("Sign & Verify");
        signAndVerifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signAndVerifyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(aboutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(getPublicKeyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(getBlHashButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(accountInfoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(applySettingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(changePinButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(testScreenButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(resetDeviceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(recoveryDeviceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(wipeDeviceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(updateFirmwareButton, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addComponent(signAndVerifyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(devicesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buyButton)
                    .addComponent(aboutButton))
                .addGap(18, 18, 18)
                .addComponent(devicesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetDeviceButton)
                    .addComponent(recoveryDeviceButton)
                    .addComponent(wipeDeviceButton)
                    .addComponent(updateFirmwareButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applySettingsButton)
                    .addComponent(changePinButton)
                    .addComponent(testScreenButton)
                    .addComponent(signAndVerifyButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getPublicKeyButton)
                    .addComponent(getBlHashButton)
                    .addComponent(accountInfoButton))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void recoveryDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recoveryDeviceButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            RecoveryDeviceDialog recoveryDeviceDialog = new RecoveryDeviceDialog(this, true, mainController, device);
            recoveryDeviceDialog.setLocationRelativeTo(null);
            recoveryDeviceDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_recoveryDeviceButtonActionPerformed

    private void wipeDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wipeDeviceButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WipeDeviceDialog wipeDeviceDialog = new WipeDeviceDialog(this, true, mainController, device);
            wipeDeviceDialog.setLocationRelativeTo(null);
            wipeDeviceDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_wipeDeviceButtonActionPerformed

    public Device getSelectDevice() {
        return (Device) devicesList.getSelectedValue();
    }

    private void getBlHashButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getBlHashButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                BootloaderHashDialog bootloaderHashDialog = new BootloaderHashDialog(this, true);
                
                Optional<BWalletMessage.Features> features = context.getFeatures();
                if (features.isPresent()) {
                    ByteString hash = features.get().getBootloaderHash();
                    if (hash != null) {
                        byte[] bytes = hash.toByteArray();
                        String hex = Hex.toHexString(bytes);
                        bootloaderHashDialog.setContent(hex);
                    }
                }
                bootloaderHashDialog.setLocationRelativeTo(null);
                bootloaderHashDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_getBlHashButtonActionPerformed

    private void buyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyButtonActionPerformed
        try {
            this.openWebpage(new URL(buyUrl));
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buyButtonActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_aboutButtonActionPerformed

    private void updateFirmwareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateFirmwareButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            FirmwareUpdateDialog updateFirmwareDialog = new FirmwareUpdateDialog(this, true, mainController, device);
            updateFirmwareDialog.setLocationRelativeTo(null);
            updateFirmwareDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_updateFirmwareButtonActionPerformed

    private void resetDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDeviceButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            CreateWalletDialog createWalletDialog = new CreateWalletDialog(this, true, mainController, device);
            createWalletDialog.setLocationRelativeTo(null);
            createWalletDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_resetDeviceButtonActionPerformed

    private void applySettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySettingsButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                Optional<BWalletMessage.Features> features = context.getFeatures();
                Optional<String> language = Optional.absent();
                Optional<String> label = Optional.absent();
                if (features.isPresent()) {
                    language = Optional.fromNullable(features.get().getLanguage());
                    label = Optional.fromNullable(features.get().getLabel());
                }

                ApplySettingsDialog applySettingsDialog = new ApplySettingsDialog(this, true, mainController, device, language, label);
                applySettingsDialog.setLocationRelativeTo(null);
                applySettingsDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_applySettingsButtonActionPerformed

    private void getPublicKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPublicKeyButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            GetPublicKeyDialog getPublicKeyDialog = new GetPublicKeyDialog(this, true, mainController, device);
            getPublicKeyDialog.setLocationRelativeTo(null);
            getPublicKeyDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_getPublicKeyButtonActionPerformed

    private void changePinButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePinButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            ChangePINDialog changePINDialog = new ChangePINDialog(this, true, mainController, device);
            changePINDialog.setLocationRelativeTo(null);
            changePINDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_changePinButtonActionPerformed

    private void signAndVerifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signAndVerifyButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            SignMessageDialog signMessageDialog = new SignMessageDialog(this, true, mainController, device);
            signMessageDialog.setLocationRelativeTo(null);
            signMessageDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a device.");
        }
    }//GEN-LAST:event_signAndVerifyButtonActionPerformed

    protected void openWebpage(URI uri) {
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
            }
        }
    }

    protected void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainUI mainUI = new MainUI();
                mainUI.setLocationRelativeTo(null);
                mainUI.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JButton accountInfoButton;
    private javax.swing.JButton applySettingsButton;
    private javax.swing.JButton buyButton;
    private javax.swing.JButton changePinButton;
    private javax.swing.JList devicesList;
    private javax.swing.JPanel devicesPanel;
    private javax.swing.JButton getBlHashButton;
    private javax.swing.JButton getPublicKeyButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton recoveryDeviceButton;
    private javax.swing.JButton resetDeviceButton;
    private javax.swing.JButton signAndVerifyButton;
    private javax.swing.JButton testScreenButton;
    private javax.swing.JButton updateFirmwareButton;
    private javax.swing.JButton wipeDeviceButton;
    // End of variables declaration//GEN-END:variables
}
