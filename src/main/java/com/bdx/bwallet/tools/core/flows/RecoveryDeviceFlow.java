package com.bdx.bwallet.tools.core.flows;

import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.HardwareWalletEventType;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;

public class RecoveryDeviceFlow extends AbstractWalletFlow {

    @Override
    protected void internalTransition(WalletClient client, WalletContext context, MessageEvent event) {
        switch (event.getEventType()) {
            case PIN_MATRIX_REQUEST:
                // Device is asking for a PIN matrix to be displayed (user must read
                // the screen carefully)
                HardwareWalletEvents.fireHardwareWalletEvent(
                        HardwareWalletEventType.SHOW_PIN_ENTRY, event.getMessage()
                        .get());
                // Further state transitions will occur after the user has provided
                // the PIN via the service
                break;
            case WORD_REQUEST:
                HardwareWalletEvents.fireHardwareWalletEvent(
                        HardwareWalletEventType.SHOW_WORD_ENTRY);
                break;
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
