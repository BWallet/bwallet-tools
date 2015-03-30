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
            case PASSPHRASE_REQUEST:
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_PASSPHRASE_ENTRY, event.getMessage().get());
                break;
            case PUBLIC_KEY:
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.DETERMINISTIC_HIERARCHY, event.getMessage().get());
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
