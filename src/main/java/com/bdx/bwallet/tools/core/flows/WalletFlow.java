package com.bdx.bwallet.tools.core.flows;

import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.MessageEvent;

public interface WalletFlow {
        void transition(WalletClient client, WalletContext context, MessageEvent event);
}
