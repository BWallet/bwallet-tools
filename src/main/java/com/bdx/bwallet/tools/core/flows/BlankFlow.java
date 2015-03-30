/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.flows;

import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.MessageEvent;

/**
 *
 * @author Dean Liu
 */
public class BlankFlow extends AbstractWalletFlow{

    @Override
    protected void internalTransition(WalletClient client, WalletContext context, MessageEvent event) {
        // TODO log warning here
        System.out.println(event.getEventType());
    }
    
}
