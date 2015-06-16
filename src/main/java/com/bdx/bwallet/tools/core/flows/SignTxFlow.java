/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.flows;

import java.io.IOException;

import org.bitcoinj.core.Transaction;

import com.bdx.bwallet.protobuf.BWalletMessage.TxRequest;
import com.bdx.bwallet.protobuf.BWalletType.TxRequestSerializedType;
import com.bdx.bwallet.tools.core.WalletClient;
import com.bdx.bwallet.tools.core.WalletContext;
import com.bdx.bwallet.tools.core.events.HardwareWalletEventType;
import com.bdx.bwallet.tools.core.events.HardwareWalletEvents;
import com.bdx.bwallet.tools.core.events.MessageEvent;

/**
 *
 * @author Administrator
 */
public class SignTxFlow extends AbstractWalletFlow {

    @Override
    protected void internalTransition(WalletClient client, WalletContext context, MessageEvent event) {
        switch (event.getEventType()) {
            case PIN_MATRIX_REQUEST:
                // Device is asking for a PIN matrix to be displayed (user must read the screen carefully)
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_PIN_ENTRY, event.getMessage().get());
                // Further state transitions will occur after the user has provided the PIN via the service
                break;
            case TX_REQUEST:
                // Device is requesting a transaction input or output
                Transaction transaction = context.getTransaction().get();
                TxRequest txRequest = ((TxRequest) event.getMessage().get());

                // Check if we are being given a signature
                TxRequestSerializedType serializedType = txRequest.getSerialized();
                if (serializedType.hasSignatureIndex()) {
                    //log.debug("Received signature index");
                    int signedInputIndex = serializedType.getSignatureIndex();
                    byte[] signature = serializedType.getSignature().toByteArray();
                    context.getSignatures().put(signedInputIndex, signature);
                }
                if (serializedType.hasSerializedTx()) {
                    //log.debug("Received serialized Tx - could be partial");
                    byte[] serializedTx = serializedType.getSerializedTx().toByteArray();
                    try {
                        context.getSerializedTx().write(serializedTx);
                    } catch (IOException e) {
                        // Ignore
                    }
                }
                switch (txRequest.getRequestType()) {
                    case TXFINISHED:
                        HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_OPERATION_SUCCEEDED, event.getMessage().get());
                        break;
                    case TXOUTPUT:
                        break;
                }
                client.txAck(txRequest, transaction, context.getReceivingAddressPathMap(), context.getChangeAddressPathMap());
                break;
            case BUTTON_REQUEST:
                // Device is requesting a button press
                HardwareWalletEvents.fireHardwareWalletEvent(HardwareWalletEventType.SHOW_BUTTON_PRESS, event.getMessage().get());
                client.buttonAck();
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
