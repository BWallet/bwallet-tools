package com.bdx.bwallet.tools.core.flows;

import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.HardwareWalletEventType;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWalletFlow implements WalletFlow {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected void handleUnexpectedMessageEvent(WalletContext context, MessageEvent event) {
        log.warn("Unexpected message event '{}'", event.getEventType().name());
        HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_OPERATION_FAILED, event.getMessage().get());
        context.resetAllButFeatures();
    }

    @Override
    public void transition(WalletClient client, WalletContext context, MessageEvent event) {
        switch (event.getEventType()) {
            case DEVICE_DETACHED:
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_DEVICE_DETACHED);
                return;
        }
        System.out.println(event.getEventType());
        internalTransition(client, context, event);
    }

    protected abstract void internalTransition(WalletClient client, WalletContext context, MessageEvent event);
}
