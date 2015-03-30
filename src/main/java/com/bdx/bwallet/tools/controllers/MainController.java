/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.controllers;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.tools.core.HidWalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.WalletService;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvent;
import com.bdx.bwallet.tools.core.events.HardwareWalletEventType;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.wallets.HidWallet;
import com.bdx.bwallet.tools.model.Device;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;
import org.hid4java.HidDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dean Liu
 */
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private final WalletService walletService = new WalletService();

    private final Map<String, WalletContext> contexts = new HashMap<>();

    public MainController() {
        MessageEvents.subscribe(this);
        HardwareWalletEvents.subscribe(this);
    }

    public void unsubscribe() {
        HardwareWalletEvents.unsubscribe(this);
    }

    public List<HidDevice> getDevices() {
        return walletService.getDevices();
    }

    public void cancel() {
        walletService.requestCancel();
    }

    public void providePin(String pinPositions) {
        walletService.providePIN(pinPositions);
    }

    public void provideEntropy(byte[] entropy) {
        walletService.provideEntropy(entropy);
    }

    public void provideWord(String word) {
        walletService.provideWord(word);
    }

    public void providePassphrase(String passphrase) {
        walletService.providePassphrase(passphrase);
    }
    
    public void wipeDevice(Device device) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.wipeDevice();
        }
    }

    public void firmwareErase(Device device) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.firmwareErase();
        }
    }

    public void firmwareUpload(Device device, final File firmware) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            try {
                byte[] bytes = FileUtils.readFileToByteArray(firmware);
                byte[] payload = Hex.decodeHex(new String(bytes).toCharArray());
                walletService.firmwareUpload(payload);
            } catch (IOException | DecoderException ex) {
                BWalletMessage.Failure failure = BWalletMessage.Failure.newBuilder().setMessage(ex.getMessage()).build();
                HardwareWalletEvents.fireHardwareWalletEvent(
                        HardwareWalletEventType.SHOW_OPERATION_FAILED, failure);
            }
        }
    }

    public void createWallet(Device device, final String language, final String label, final boolean displayRandom, final boolean pinProtection, final boolean passphraseProtection, final int strength) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.createWallet(language, label, displayRandom, pinProtection, passphraseProtection, strength);
        }
    }

    public void recoverDevice(Device device, final String language, final String label, final int wordCount, final boolean passphraseProtection, final boolean pinProtection, final boolean enforceWordlist) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.recoverDevice(language, label, wordCount, passphraseProtection, pinProtection, enforceWordlist);
        }
    }

    public void applySettings(Device device, final String language, final String label) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.applySettings(language, label);
        }
    }

    public void getDeterministicHierarchy(Device device, List<ChildNumber> childNumbers) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.getDeterministicHierarchy(childNumbers);
        }
    }
    
    public void signMessage(Device device, int account, KeyChain.KeyPurpose keyPurpose, int index, byte[] message) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.signMessage(account, keyPurpose, index, message);
        }
    }
    
    public void verifyMessage(Device device, Address address, byte[] signature, byte[] message) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.verifyMessage(address, signature, message);
        }
    }
    
    public void changePIN(Device device, boolean remove) {
        final WalletContext context = this.getContext(device);
        if (context != null) {
            walletService.setContext(context);
            walletService.changePIN(remove);
        }
    }
    
    public WalletContext getContext(Device device) {
        WalletContext context = contexts.get(device.getPath());
        if (context == null) {
            context = this.createContext(device);
            if (context.connect()) {
                try {
                    ListenableFuture<Optional<BWalletMessage.Features>> future = context.initialise();
                    future.get(5, TimeUnit.SECONDS);
                    contexts.put(device.getPath(), context);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    java.util.logging.Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                context = null;
            }
        }
        return context;
    }

    protected WalletContext createContext(Device device) {
        HidWallet wallet = new HidWallet(Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.fromNullable(device.getSerialNumber()));
        HidWalletClient client = new HidWalletClient(wallet);
        WalletContext context = new WalletContext(client);
        return context;
    }

    public byte[] generateEntropy() {
        return walletService.generateEntropy();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.getEventType() == MessageEventType.DEVICE_DETACHED) {
            HidDevice hidDevice = event.getDevice().get();
            WalletContext context = contexts.remove(hidDevice.getPath());
            System.out.println(hidDevice.getPath());
            System.out.println(context);
            if (context != null) {
                context.destroy();
                System.out.println("yyy");
            }
        }
    }

    @Subscribe
    public void onHardwareWalletEvent(final HardwareWalletEvent event) {
        log.debug("Received hardware event: '{}'", event.getEventType().name());
        //System.out.println(event.getEventType());
        // Quick check for relevancy
        switch (event.getEventType()) {
            case SHOW_DEVICE_DETACHED:
                // TODO 
                break;
            default:
                // The AbstractHardwareWalletWizard handles everything when a wizard is showing
                return;
        }

    }
}
