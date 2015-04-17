/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.tools.controllers.MainController;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.utils.FirmwareVersion;
import com.bdx.bwallet.tools.model.Device;
import com.bdx.bwallet.tools.ui.utils.BrowserUtils;
import com.bdx.bwallet.tools.ui.utils.IconUtils;
import com.bdx.bwallet.tools.ui.utils.UrlUtils;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultEditorKit;
import org.hid4java.HidDevice;
import org.spongycastle.util.encoders.Hex;

/**
 *
 * @author Administrator
 */
public final class MainFrame extends javax.swing.JFrame {

    static final String SITE_URL = "http://mybwallet.com";
    static final String HELP_URL = SITE_URL + "/docs/help/zh/";
    static final String FAQ_URL = SITE_URL + "/docs/faq/zh/";
    static final String RESOURCES_URL = SITE_URL + "/resources";
    static final String BUY_URL = "https://bidingxing.com/bwallet";

    private final MainController mainController = new MainController();

    private ResourceBundle bundle;

    /**
     * Creates new form MainUI3
     */
    public MainFrame() {
        initComponents();

        splitPane.setDividerLocation(1.0 / 3.0);
        splitPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation(1.0 / 3.0);
            }
        });

        DefaultListModel<Device> listModel = new DefaultListModel();
        devicesList.setModel(listModel);
        List<HidDevice> hidDevices = mainController.getDevices();
        for (HidDevice hidDevice : hidDevices) {
            Device device = new Device(hidDevice);
            listModel.addElement(device);
        }

        MessageEvents.subscribe(this);

        loadResourceBundle();
        applyResourceBundle();
    }

    public void loadResourceBundle() {
        bundle = ResourceBundle.getBundle("com/bdx/bwallet/tools/ui/Bundle");
    }

    public void applyResourceBundle() {
        setTitle(bundle.getString("MainFrame.title")); 

        devicesLabel.setText(bundle.getString("MainFrame.devicesLabel.text")); 

        ((TitledBorder)setupPanel.getBorder()).setTitle(bundle.getString("MainFrame.setupPanel.border.title"));
        ((TitledBorder)settingPanel.getBorder()).setTitle(bundle.getString("MainFrame.settingPanel.border.title"));
        ((TitledBorder)accountPanel.getBorder()).setTitle(bundle.getString("MainFrame.accountPanel.border.title"));
        ((TitledBorder)miscPanel.getBorder()).setTitle(bundle.getString("MainFrame.miscPanel.border.title"));
                
        resetDeviceButton.setText(bundle.getString("MainFrame.resetDeviceButton.text")); 
        recoveryDeviceButton.setText(bundle.getString("MainFrame.recoveryDeviceButton.text")); 
        wipeDeviceButton.setText(bundle.getString("MainFrame.wipeDeviceButton.text")); 
        deviceInfoButton.setText(bundle.getString("MainFrame.deviceInfoButton.text"));
        
        applySettingsButton.setText(bundle.getString("MainFrame.applySettingsButton.text")); 
        passphraseSettingButton.setText(bundle.getString("MainFrame.passphraseSettingButton.text")); 
        homescreenSettingButton.setText(bundle.getString("MainFrame.homescreenSettingButton.text")); 
        accountLabelSettingButton.setText(bundle.getString("MainFrame.accountLabelSettingButton.text")); 
        changePinButton.setText(bundle.getString("MainFrame.changePinButton.text")); 
        
        accountDetailsButton.setText(bundle.getString("MainFrame.accountDetailsButton.text")); 
        signAndVerifyButton.setText(bundle.getString("MainFrame.signAndVerifyButton.text")); 
        getPublicKeyButton.setText(bundle.getString("MainFrame.getPublicKeyButton.text")); 
       
        updateFirmwareButton.setText(bundle.getString("MainFrame.updateFirmwareButton.text")); 
        getBlHashButton.setText(bundle.getString("MainFrame.getBlHashButton.text")); 
        testScreenButton.setText(bundle.getString("MainFrame.testScreenButton.text")); 
        
        fileMenu.setText(bundle.getString("MainFrame.fileMenu.text")); 
        exitMenuItem.setText(bundle.getString("MainFrame.exitMenuItem.text")); 
        helpMenu.setText(bundle.getString("MainFrame.helpMenu.text")); 
        contentsMenuItem.setText(bundle.getString("MainFrame.contentsMenuItem.text")); 
        faqMenuItem.setText(bundle.getString("MainFrame.faqMenuItem.text")); 
        resourcesMenuItem.setText(bundle.getString("MainFrame.resourcesMenuItem.text")); 
        websiteMenuItem.setText(bundle.getString("MainFrame.websiteMenuItem.text")); 
        buyMenuItem.setText(bundle.getString("MainFrame.buyMenuItem.text")); 
        aboutMenuItem.setText(bundle.getString("MainFrame.aboutMenuItem.text")); 
        languageMenu.setText(bundle.getString("MainFrame.languageMenu.text"));
        
        String language = Locale.getDefault().getLanguage();
        if (language.equals("zh")) {
            chineseMenuItem.setText(bundle.getString("MainFrame.chineseMenuItem.text.checked"));
            englishMenuItem.setText(bundle.getString("MainFrame.englishMenuItem.text"));
        } else {
            chineseMenuItem.setText(bundle.getString("MainFrame.chineseMenuItem.text"));
            englishMenuItem.setText(bundle.getString("MainFrame.englishMenuItem.text.checked"));
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

    public Device getSelectDevice() {
        return (Device) devicesList.getSelectedValue();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        devicesLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        devicesList = new javax.swing.JList();
        rightPanel = new javax.swing.JPanel();
        setupPanel = new javax.swing.JPanel();
        resetDeviceButton = new javax.swing.JButton();
        recoveryDeviceButton = new javax.swing.JButton();
        wipeDeviceButton = new javax.swing.JButton();
        deviceInfoButton = new javax.swing.JButton();
        settingPanel = new javax.swing.JPanel();
        applySettingsButton = new javax.swing.JButton();
        changePinButton = new javax.swing.JButton();
        passphraseSettingButton = new javax.swing.JButton();
        accountLabelSettingButton = new javax.swing.JButton();
        homescreenSettingButton = new javax.swing.JButton();
        accountPanel = new javax.swing.JPanel();
        accountDetailsButton = new javax.swing.JButton();
        signAndVerifyButton = new javax.swing.JButton();
        getPublicKeyButton = new javax.swing.JButton();
        miscPanel = new javax.swing.JPanel();
        updateFirmwareButton = new javax.swing.JButton();
        getBlHashButton = new javax.swing.JButton();
        testScreenButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        languageMenu = new javax.swing.JMenu();
        englishMenuItem = new javax.swing.JMenuItem();
        chineseMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        faqMenuItem = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JPopupMenu.Separator();
        resourcesMenuItem = new javax.swing.JMenuItem();
        websiteMenuItem = new javax.swing.JMenuItem();
        separator2 = new javax.swing.JPopupMenu.Separator();
        buyMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BWallet Tools");

        devicesLabel.setText("Devices");

        jScrollPane1.setViewportView(devicesList);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addComponent(devicesLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(devicesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
        );

        splitPane.setLeftComponent(leftPanel);

        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.Y_AXIS));

        setupPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Setup"));
        setupPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        resetDeviceButton.setText("Device Setup");
        resetDeviceButton.setPreferredSize(new java.awt.Dimension(160, 30));
        resetDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDeviceButtonActionPerformed(evt);
            }
        });
        setupPanel.add(resetDeviceButton);

        recoveryDeviceButton.setText("Recovery Device");
        recoveryDeviceButton.setPreferredSize(new java.awt.Dimension(160, 30));
        recoveryDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recoveryDeviceButtonActionPerformed(evt);
            }
        });
        setupPanel.add(recoveryDeviceButton);

        wipeDeviceButton.setText("Wipe Device");
        wipeDeviceButton.setPreferredSize(new java.awt.Dimension(160, 30));
        wipeDeviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wipeDeviceButtonActionPerformed(evt);
            }
        });
        setupPanel.add(wipeDeviceButton);

        deviceInfoButton.setText("Device Info");
        deviceInfoButton.setPreferredSize(new java.awt.Dimension(160, 30));
        deviceInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceInfoButtonActionPerformed(evt);
            }
        });
        setupPanel.add(deviceInfoButton);

        rightPanel.add(setupPanel);

        settingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Settings"));
        settingPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        applySettingsButton.setText("Basic Settings");
        applySettingsButton.setPreferredSize(new java.awt.Dimension(160, 30));
        applySettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applySettingsButtonActionPerformed(evt);
            }
        });
        settingPanel.add(applySettingsButton);

        changePinButton.setText("PIN Setting");
        changePinButton.setPreferredSize(new java.awt.Dimension(160, 30));
        changePinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePinButtonActionPerformed(evt);
            }
        });
        settingPanel.add(changePinButton);

        passphraseSettingButton.setText("Passphrase Setting");
        passphraseSettingButton.setPreferredSize(new java.awt.Dimension(160, 30));
        passphraseSettingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passphraseSettingButtonActionPerformed(evt);
            }
        });
        settingPanel.add(passphraseSettingButton);

        accountLabelSettingButton.setText("Account Label Setting");
        accountLabelSettingButton.setPreferredSize(new java.awt.Dimension(160, 30));
        accountLabelSettingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountLabelSettingButtonActionPerformed(evt);
            }
        });
        settingPanel.add(accountLabelSettingButton);

        homescreenSettingButton.setText("Homescreen Setting");
        homescreenSettingButton.setPreferredSize(new java.awt.Dimension(160, 30));
        homescreenSettingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homescreenSettingButtonActionPerformed(evt);
            }
        });
        settingPanel.add(homescreenSettingButton);

        rightPanel.add(settingPanel);

        accountPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Account"));
        accountPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        accountDetailsButton.setText("Get Account Details");
        accountDetailsButton.setPreferredSize(new java.awt.Dimension(160, 30));
        accountDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountDetailsButtonActionPerformed(evt);
            }
        });
        accountPanel.add(accountDetailsButton);

        signAndVerifyButton.setText("Sign & Verify");
        signAndVerifyButton.setPreferredSize(new java.awt.Dimension(160, 30));
        signAndVerifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signAndVerifyButtonActionPerformed(evt);
            }
        });
        accountPanel.add(signAndVerifyButton);

        getPublicKeyButton.setText("Get Public Key");
        getPublicKeyButton.setPreferredSize(new java.awt.Dimension(160, 30));
        getPublicKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getPublicKeyButtonActionPerformed(evt);
            }
        });
        accountPanel.add(getPublicKeyButton);

        rightPanel.add(accountPanel);

        miscPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Misc"));
        miscPanel.setToolTipText("");
        miscPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        updateFirmwareButton.setText("Update Firmware");
        updateFirmwareButton.setPreferredSize(new java.awt.Dimension(160, 30));
        updateFirmwareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateFirmwareButtonActionPerformed(evt);
            }
        });
        miscPanel.add(updateFirmwareButton);

        getBlHashButton.setText("Get Bootloader Hash");
        getBlHashButton.setPreferredSize(new java.awt.Dimension(160, 30));
        getBlHashButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getBlHashButtonActionPerformed(evt);
            }
        });
        miscPanel.add(getBlHashButton);

        testScreenButton.setText("Test Screen");
        testScreenButton.setPreferredSize(new java.awt.Dimension(160, 30));
        testScreenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testScreenButtonActionPerformed(evt);
            }
        });
        miscPanel.add(testScreenButton);

        rightPanel.add(miscPanel);

        splitPane.setRightComponent(rightPanel);

        fileMenu.setText("File");

        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        languageMenu.setText("Language");
        languageMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageMenuActionPerformed(evt);
            }
        });

        englishMenuItem.setText("English");
        englishMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                englishMenuItemActionPerformed(evt);
            }
        });
        languageMenu.add(englishMenuItem);

        chineseMenuItem.setText("中文");
        chineseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chineseMenuItemActionPerformed(evt);
            }
        });
        languageMenu.add(chineseMenuItem);

        menuBar.add(languageMenu);

        helpMenu.setText("Help");

        contentsMenuItem.setText("Help Contents");
        contentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentsMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(contentsMenuItem);

        faqMenuItem.setText("FAQ");
        faqMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faqMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(faqMenuItem);
        helpMenu.add(separator1);

        resourcesMenuItem.setText("Resources");
        resourcesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resourcesMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(resourcesMenuItem);

        websiteMenuItem.setText("MyBWallet.com");
        websiteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                websiteMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(websiteMenuItem);
        helpMenu.add(separator2);

        buyMenuItem.setText("Buy BWallet");
        buyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(buyMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void resetDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDeviceButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            CreateWalletDialog createWalletDialog = new CreateWalletDialog(this, true, bundle, mainController, device);
            createWalletDialog.setLocationRelativeTo(null);
            createWalletDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_resetDeviceButtonActionPerformed

    private void recoveryDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recoveryDeviceButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            RecoveryDeviceDialog recoveryDeviceDialog = new RecoveryDeviceDialog(this, true, bundle, mainController, device);
            recoveryDeviceDialog.setLocationRelativeTo(null);
            recoveryDeviceDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_recoveryDeviceButtonActionPerformed

    private void wipeDeviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wipeDeviceButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WipeDeviceDialog wipeDeviceDialog = new WipeDeviceDialog(this, true, bundle, mainController, device);
            wipeDeviceDialog.setLocationRelativeTo(null);
            wipeDeviceDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_wipeDeviceButtonActionPerformed

    private void applySettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySettingsButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                try {
                    ListenableFuture<Optional<BWalletMessage.Features>> future = context.initialise();
                    future.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceInitialiseFailed"));
                    return;
                }

                Optional<BWalletMessage.Features> features = context.getFeatures();
                Optional<String> language = Optional.absent();
                Optional<String> label = Optional.absent();
                if (features.isPresent()) {
                    language = Optional.fromNullable(features.get().getLanguage());
                    label = Optional.fromNullable(features.get().getLabel());
                }

                ApplySettingsDialog applySettingsDialog = new ApplySettingsDialog(this, true, bundle, mainController, device, language, label);
                applySettingsDialog.setLocationRelativeTo(null);
                applySettingsDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_applySettingsButtonActionPerformed

    private void passphraseSettingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passphraseSettingButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                try {
                    ListenableFuture<Optional<BWalletMessage.Features>> future = context.initialise();
                    future.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceInitialiseFailed"));
                    return;
                }

                BWalletMessage.Features features = context.getFeatures().get();
                FirmwareVersion firmwareVersion = new FirmwareVersion(features);
                if (!firmwareVersion.ge(1, 3, 1)) {
                    JOptionPane.showMessageDialog(this, MessageFormat.format(bundle.getString("MessageDialog.unsupportedFirmware"), new Object[]{firmwareVersion.toString(), "1.3.1"}));
                    return;
                }

                boolean disable = features.getPassphraseProtection();
                ApplyPassphraseSettingDialog applyPassphraseSettingsDialog = new ApplyPassphraseSettingDialog(this, true, bundle, mainController, device, disable);
                applyPassphraseSettingsDialog.setLocationRelativeTo(null);
                applyPassphraseSettingsDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_passphraseSettingButtonActionPerformed

    private void homescreenSettingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homescreenSettingButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                try {
                    ListenableFuture<Optional<BWalletMessage.Features>> future = context.initialise();
                    future.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceInitialiseFailed"));
                    return;
                }

                BWalletMessage.Features features = context.getFeatures().get();
                FirmwareVersion firmwareVersion = new FirmwareVersion(features);
                if (!firmwareVersion.ge(1, 3, 1)) {
                    JOptionPane.showMessageDialog(this, MessageFormat.format(bundle.getString("MessageDialog.unsupportedFirmware"), new Object[]{firmwareVersion.toString(), "1.3.1"}));
                    return;
                }

                ApplyHomescreenSettingDialog applyHomescreenSettingsDialog = new ApplyHomescreenSettingDialog(this, true, bundle, mainController, device);
                applyHomescreenSettingsDialog.setLocationRelativeTo(null);
                applyHomescreenSettingsDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_homescreenSettingButtonActionPerformed

    private void accountLabelSettingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountLabelSettingButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                try {
                    ListenableFuture<Optional<BWalletMessage.Features>> future = context.initialise();
                    future.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceInitialiseFailed"));
                    return;
                }

                BWalletMessage.Features features = context.getFeatures().get();
                FirmwareVersion firmwareVersion = new FirmwareVersion(features);
                if (!firmwareVersion.ge(1, 3, 1)) {
                    JOptionPane.showMessageDialog(this, MessageFormat.format(bundle.getString("MessageDialog.unsupportedFirmware"), new Object[]{firmwareVersion.toString(), "1.3.1"}));
                    return;
                }

                AccountLabelDialog accountLabelDialog = new AccountLabelDialog(this, true, bundle, mainController, device);
                accountLabelDialog.setLocationRelativeTo(null);
                accountLabelDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_accountLabelSettingButtonActionPerformed

    private void changePinButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePinButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            ChangePINDialog changePINDialog = new ChangePINDialog(this, true, bundle, mainController, device);
            changePINDialog.setLocationRelativeTo(null);
            changePINDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_changePinButtonActionPerformed

    private void accountDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountDetailsButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            AccountDetailsDialog accountDetailsDialog = new AccountDetailsDialog(this, true, bundle, mainController, device);
            accountDetailsDialog.setLocationRelativeTo(null);
            accountDetailsDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_accountDetailsButtonActionPerformed

    private void signAndVerifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signAndVerifyButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            SignMessageDialog signMessageDialog = new SignMessageDialog(this, true, bundle, mainController, device);
            signMessageDialog.setLocationRelativeTo(null);
            signMessageDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_signAndVerifyButtonActionPerformed

    private void getPublicKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPublicKeyButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            GetPublicKeyDialog getPublicKeyDialog = new GetPublicKeyDialog(this, true, bundle, mainController, device);
            getPublicKeyDialog.setLocationRelativeTo(null);
            getPublicKeyDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_getPublicKeyButtonActionPerformed

    private void updateFirmwareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateFirmwareButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            FirmwareUpdateDialog updateFirmwareDialog = new FirmwareUpdateDialog(this, true, bundle, mainController, device);
            updateFirmwareDialog.setLocationRelativeTo(null);
            updateFirmwareDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_updateFirmwareButtonActionPerformed

    private void getBlHashButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getBlHashButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                BootloaderHashDialog bootloaderHashDialog = new BootloaderHashDialog(this, true, bundle);

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
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_getBlHashButtonActionPerformed

    private void testScreenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testScreenButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            TestScreenDialog testScreenDialog = new TestScreenDialog(this, true, bundle, mainController, device);
            testScreenDialog.setLocationRelativeTo(null);
            testScreenDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_testScreenButtonActionPerformed

    private void contentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentsMenuItemActionPerformed
        try {
            BrowserUtils.openWebpage(UrlUtils.getHelpUrl(Locale.getDefault()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_contentsMenuItemActionPerformed

    private void faqMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faqMenuItemActionPerformed
        try {
            BrowserUtils.openWebpage(UrlUtils.getFaqUrl(Locale.getDefault()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_faqMenuItemActionPerformed

    private void resourcesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resourcesMenuItemActionPerformed
        try {
            BrowserUtils.openWebpage(UrlUtils.getResourcesUrl(Locale.getDefault()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_resourcesMenuItemActionPerformed

    private void websiteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_websiteMenuItemActionPerformed
        try {
            BrowserUtils.openWebpage(UrlUtils.getSiteUrl(Locale.getDefault()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_websiteMenuItemActionPerformed

    private void buyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyMenuItemActionPerformed
        try {
            BrowserUtils.openWebpage(UrlUtils.getBuyUrl(Locale.getDefault()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buyMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutDialog aboutDialog = new AboutDialog(this, true, bundle);
        aboutDialog.setLocationRelativeTo(null);
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void languageMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageMenuActionPerformed
    }//GEN-LAST:event_languageMenuActionPerformed

    private void englishMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_englishMenuItemActionPerformed
        Locale.setDefault(new Locale("en","US"));
        this.loadResourceBundle();
        this.applyResourceBundle();
    }//GEN-LAST:event_englishMenuItemActionPerformed

    private void chineseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chineseMenuItemActionPerformed
        Locale.setDefault(new Locale("zh","CN"));
        this.loadResourceBundle();
        this.applyResourceBundle();
    }//GEN-LAST:event_chineseMenuItemActionPerformed

    private void deviceInfoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deviceInfoButtonActionPerformed
        Device device = getSelectDevice();
        if (device != null) {
            WalletContext context = mainController.getContext(device);
            if (context != null) {
                try {
                    ListenableFuture<Optional<BWalletMessage.Features>> future = context.initialise();
                    future.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceInitialiseFailed"));
                    return;
                }

                Optional<BWalletMessage.Features> features = context.getFeatures();
                DeviceInfoDialog deviceInfoDialog = new DeviceInfoDialog(this, true, bundle);
                deviceInfoDialog.setLocationRelativeTo(null);
                deviceInfoDialog.displayFeatures(features);
                deviceInfoDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.deviceOpenFaild"));
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("MessageDialog.noneDeviceSelected"));
        }
    }//GEN-LAST:event_deviceInfoButtonActionPerformed

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);

            im = (InputMap) UIManager.get("TextArea.focusInputMap");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame ui = new MainFrame();
                List<Image> icons = new ArrayList<Image>();
                icons.add(IconUtils.createImageIcon("/images/bwallet-tools16.png", "").getImage());
                icons.add(IconUtils.createImageIcon("/images/bwallet-tools32.png", "").getImage());
                ui.setIconImages(icons);
                ui.setLocationRelativeTo(null);
                ui.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton accountDetailsButton;
    private javax.swing.JButton accountLabelSettingButton;
    private javax.swing.JPanel accountPanel;
    private javax.swing.JButton applySettingsButton;
    private javax.swing.JMenuItem buyMenuItem;
    private javax.swing.JButton changePinButton;
    private javax.swing.JMenuItem chineseMenuItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JButton deviceInfoButton;
    private javax.swing.JLabel devicesLabel;
    private javax.swing.JList devicesList;
    private javax.swing.JMenuItem englishMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem faqMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton getBlHashButton;
    private javax.swing.JButton getPublicKeyButton;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton homescreenSettingButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu languageMenu;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel miscPanel;
    private javax.swing.JButton passphraseSettingButton;
    private javax.swing.JButton recoveryDeviceButton;
    private javax.swing.JButton resetDeviceButton;
    private javax.swing.JMenuItem resourcesMenuItem;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPopupMenu.Separator separator1;
    private javax.swing.JPopupMenu.Separator separator2;
    private javax.swing.JPanel settingPanel;
    private javax.swing.JPanel setupPanel;
    private javax.swing.JButton signAndVerifyButton;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JButton testScreenButton;
    private javax.swing.JButton updateFirmwareButton;
    private javax.swing.JMenuItem websiteMenuItem;
    private javax.swing.JButton wipeDeviceButton;
    // End of variables declaration//GEN-END:variables

}
