package com.bdx.bwallet.tools.core.flows;

import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.HardwareWalletEventType;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;

public class LoadDeviceFlow extends AbstractWalletFlow {

    @Override
    protected void internalTransition(WalletClient client, WalletContext context, MessageEvent event) {
        switch (event.getEventType()) {
            case BUTTON_REQUEST:
                // Device is requesting a button press
                HardwareWalletEvents.fireHardwareWalletEvent(
                        HardwareWalletEventType.SHOW_BUTTON_PRESS, event
                        .getMessage().get());
                client.buttonAck();
                break;
            case SUCCESS:
                // Device has completed the operation and changed/removed the PIN
                HardwareWalletEvents.fireHardwareWalletEvent(
                        HardwareWalletEventType.SHOW_OPERATION_SUCCEEDED, event
                        .getMessage().get());
                context.reset();
                break;
            case FAILURE:
                // User has cancelled or operation failed
                HardwareWalletEvents.fireHardwareWalletEvent(
                        HardwareWalletEventType.SHOW_OPERATION_FAILED, event
                        .getMessage().get());
                context.reset();
                break;
            default:
                handleUnexpectedMessageEvent(context, event);
        }
    }
}
