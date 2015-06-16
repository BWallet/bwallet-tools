/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.flows;

import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.HardwareWalletEventType;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;

/**
 *
 * @author Administrator
 */
public class GetAddressFlow extends AbstractWalletFlow {

    @Override
    protected void internalTransition(WalletClient client, WalletContext context, MessageEvent event) {
        switch (event.getEventType()) {
            case PIN_MATRIX_REQUEST:
                // Device is asking for a PIN matrix to be displayed (user must read the screen carefully)
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_PIN_ENTRY, event.getMessage().get());
                // Further state transitions will occur after the user has provided the PIN via the service
                break;
            case PASSPHRASE_REQUEST:
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_PASSPHRASE_ENTRY, event.getMessage().get());
                break;
            case BUTTON_REQUEST:
                // Device is asking for button press (address display, confirmation of reset etc)
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_BUTTON_PRESS, event.getMessage().get());
                client.buttonAck();
                break;
            case ADDRESS:
                // Device has completed the operation and provided an address
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.ADDRESS, event.getMessage().get());
                context.resetAllButFeatures();
                break;
            case FAILURE:
                // User has cancelled or operation failed
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_OPERATION_FAILED, event.getMessage().get());
                context.resetAllButFeatures();
                break;
            default:
                handleUnexpectedMessageEvent(context, event);
        }
    }

}
