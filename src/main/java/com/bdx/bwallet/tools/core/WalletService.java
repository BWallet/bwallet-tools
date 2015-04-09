/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core;

import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.wallets.HidWallet;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.protobuf.Message;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.bitcoinj.core.Address;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;
import org.hid4java.HidDevice;
import org.hid4java.HidException;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dean Liu
 */
public class WalletService implements HidServicesListener {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final HidServices hidServices;

    /**
     * The current hardware wallet context
     */
    private WalletContext context;

    public WalletService() {
        try {
            // Get the USB services and dump information about them
            hidServices = HidManager.getHidServices();
            hidServices.addHidServicesListener(this);
        } catch (HidException e) {
            log.error("Failed to create client due to USB services problem", e);
            throw new IllegalStateException("Failed to create client due to HID services problem", e);
        }
    }

    public WalletContext getContext() {
        return context;
    }

    public void setContext(WalletContext context) {
        Preconditions.checkNotNull(context, "'context' must be present");
        this.context = context;
    }

    public void requestCancel() {
        // Let the state changes occur as a result of the internal messages
        context.getClient().cancel();
    }

    public void providePIN(String pin) {
        context.getClient().pinMatrixAck(pin);
    }

    public void provideEntropy(byte[] entropy) {
        context.getClient().entropyAck(entropy);
    }
    
    public void provideWord(String word) {
        context.getClient().wordAck(word);
    }
    
    public void providePassphrase(String passphrase) {
        context.getClient().passphraseAck(passphrase);
    }
    
    public void wipeDevice() {
        // Set the FSM context
        context.beginWipeDeviceUseCase();
    }

    public void firmwareErase() {
        context.beginFirmwareEraseUseCase();
    }

    public void firmwareUpload(byte[] payload) {
        context.beginFirmwareUploadUseCase(payload);
    }

    public void createWallet(String language,
            String label,
            boolean displayRandom,
            boolean pinProtection,
            boolean passphraseProtection, 
            int strength) {
        context.beginCreateWalletUseCase(language, label, displayRandom, pinProtection, passphraseProtection, strength);
    }

    public void recoverDevice(String language,
            String label,
            int wordCount,
            boolean passphraseProtection,
            boolean pinProtection,
            boolean enforceWordlist) {
        context.beginRecoverDeviceUseCase(language, label, wordCount, passphraseProtection, pinProtection, enforceWordlist);
    }
    
    public void applySettings(String language, String labe) {
        context.beginApplySettingsUseCase(language, labe);
    }
    
    public void applySettings(boolean usePassphrase) {
        context.beginApplySettingsUseCase(usePassphrase);
    }
    
    public void applySettings(byte[] homescreen) {
        context.beginApplySettingsUseCase(homescreen);
    }
    
    public void getDeterministicHierarchy(List<ChildNumber> childNumbers) {
        context.beginGetDeterministicHierarchyUseCase(childNumbers);
    }
    
    public void getAddress(List<ChildNumber> childNumbers) {
        context.beginGetAddressUseCase(childNumbers);
    }
    
    public void signMessage(int account, KeyChain.KeyPurpose keyPurpose, int index, byte[] message) {
        context.beginSignMessageUseCase(account, keyPurpose, index, message);
    }
    
    public void verifyMessage(Address address, byte[] signature, byte[] message) {
        context.beginVerifyMessageUseCase(address, signature, message);
    }
    
    public void changePIN(boolean remove) {
        context.beginChangePINUseCase(remove);
    }
    
    public void testScreen(int delayTime) {
        context.beginTestScreenUseCase(delayTime);
    }
    
    public void getAccountLabels(String coinName, boolean all, int index) {
        context.beginGetAccountLabelsUseCase(coinName, all, index);
    }
    
    public void setAccountLabel(String coinName, int index, String label) {
        context.beginSetAccountLabelUseCase(coinName, index, label);
    }
    
    public void removeAccountLabel(String coinName, int index) {
        context.beginRemoveAccountLabelUseCase(coinName, index);
    }
    
    /**
     * @return 32 bytes (256 bits) of entropy generated locally
     */
    public byte[] generateEntropy() {
        // Initialize a secure random number generator using
        // the OWASP recommended method
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        // Generate random bytes
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public List<HidDevice> getDevices() {
        List<HidDevice> devices = new ArrayList();
        List<HidDevice> attachedHidDevices = hidServices.getAttachedHidDevices();
        for (HidDevice hidDevice : attachedHidDevices) {
            if (HidWallet.VENDOR_ID == hidDevice.getVendorId()
                && HidWallet.PRODUCT_ID == hidDevice.getProductId()) {
                devices.add(hidDevice);
            }
        }
        return devices;
    }

    @Override
    public void hidDeviceAttached(HidServicesEvent event) {
        HidDevice attachedDevice = event.getHidDevice();

        int attachedVendorId = (int) attachedDevice.getVendorId();
        int attachedProductId = (int) attachedDevice.getProductId();

        // Check if it is a device we're interested in that was attached
        if (HidWallet.VENDOR_ID.equals(attachedVendorId)
                && HidWallet.PRODUCT_ID.equals(attachedProductId)) {
            // Inform others of this event
            MessageEvent messageEvent = new MessageEvent(MessageEventType.DEVICE_ATTACHED, Optional.<Message>absent(), Optional.of(attachedDevice));
            MessageEvents.fireMessageEvent(messageEvent);
        }
    }

    @Override
    public void hidDeviceDetached(HidServicesEvent event) {
        HidDevice attachedDevice = event.getHidDevice();

        int detachedVendorId = (int) attachedDevice.getVendorId();
        int detachedProductId = (int) attachedDevice.getProductId();

        // Check if it is a device we're interested in that was detached
        if (HidWallet.VENDOR_ID.equals(detachedVendorId)
                && HidWallet.PRODUCT_ID.equals(detachedProductId)) {
            // Inform others of this event
            MessageEvent messageEvent = new MessageEvent(MessageEventType.DEVICE_DETACHED, Optional.<Message>absent(), Optional.of(attachedDevice));
            MessageEvents.fireMessageEvent(messageEvent);
        }
    }

    @Override
    public void hidFailure(HidServicesEvent event) {
        MessageEvents.fireMessageEvent(MessageEventType.DEVICE_FAILED);
    }

}
