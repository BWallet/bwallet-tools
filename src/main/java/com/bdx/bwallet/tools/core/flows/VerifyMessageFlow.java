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
public class VerifyMessageFlow extends AbstractWalletFlow {

    @Override
    protected void internalTransition(WalletClient client, WalletContext context, MessageEvent event) {
        switch (event.getEventType()) {
            case BUTTON_REQUEST:
                // Device is requesting a button press
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_BUTTON_PRESS, event.getMessage().get());
                client.buttonAck();
                break;
            case SUCCESS:
                // Device has successfully wiped
                // No wallet creation required so we're done
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_OPERATION_SUCCEEDED, event.getMessage().get());
                // Ensure the Features are updated
                context.reset();
                break;
            case FAILURE:
                // User has cancelled or operation failed
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_OPERATION_FAILED, event.getMessage().get());
                context.reset();
                break;
            default:
                handleUnexpectedMessageEvent(context, event);
        }
    }
    
}
