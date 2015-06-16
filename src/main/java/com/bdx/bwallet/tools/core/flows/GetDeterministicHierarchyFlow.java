package com.bdx.bwallet.tools.core.flows;

import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.HardwareWalletEventType;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;

public class GetDeterministicHierarchyFlow extends AbstractWalletFlow {

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
            case PUBLIC_KEY:
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.DETERMINISTIC_HIERARCHY, event.getMessage().get());
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
