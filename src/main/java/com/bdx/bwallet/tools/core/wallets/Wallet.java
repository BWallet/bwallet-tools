/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.wallets;

import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.google.common.base.Optional;
import com.google.protobuf.Message;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Dean Liu
 */
public interface Wallet extends Connectable {
    public Optional<MessageEvent> readMessage(int duration, TimeUnit timeUnit);
    public void writeMessage(Message message);
}
