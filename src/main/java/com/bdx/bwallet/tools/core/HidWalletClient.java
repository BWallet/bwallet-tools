/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core;

import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.wallets.AbstractWallet;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.protobuf.Message;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dean Liu
 */
public class HidWalletClient extends AbstractWalletClient {

    private static final Logger log = LoggerFactory.getLogger(HidWalletClient.class);

    private final AbstractWallet wallet;
    private boolean isValid = false;

    public HidWalletClient(AbstractWallet wallet) {
        Preconditions.checkNotNull(wallet, "'wallet' must be present");
        this.wallet = wallet;
    }

    @Override
    public boolean attach() {
        log.debug("Verifying environment...");
        if (!wallet.attach()) {
            log.error("Problems with the hardware environment will prevent communication with the BWallet.");
            return false;
        }
        // Must be OK to be here
        log.debug("Environment OK");
        return true;
    }

    @Override
    public void softDetach() {
        log.debug("Performing client soft detach...");
        isValid = false;
        wallet.softDetach();
    }

    @Override
    public void hardDetach() {
        log.debug("Performing client hard detach...");
        isValid = false;
        // A hard detach includes a disconnect
        wallet.hardDetach();
    }

    @Override
    public boolean connect() {
        log.debug("Attempting to connect...");
        isValid = wallet.connect();
        if (isValid) {
            MessageEvents.fireMessageEvent(MessageEventType.DEVICE_CONNECTED);
        }
        return isValid;
    }

    @Override
    public void disconnect() {
        // A disconnect has the same behaviour as a soft detach
        softDetach();
    }

    @Override
    protected Optional<MessageEvent> sendMessage(Message message) {
        // Implemented as a blocking message
        return sendMessage(message, 1, TimeUnit.SECONDS);
    }

    @Override
    protected Optional<MessageEvent> sendMessage(Message message, int duration, TimeUnit timeUnit) {
        if (!isValid) {
            log.warn("BWallet is not valid.");
            return Optional.absent();
        }
        // Write the message
        wallet.writeMessage(message);
        return Optional.absent();
    }
}
