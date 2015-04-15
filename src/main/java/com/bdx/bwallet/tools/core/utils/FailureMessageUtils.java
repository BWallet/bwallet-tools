/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.utils;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.google.common.base.Optional;
import com.google.protobuf.Message;
import java.util.ResourceBundle;

/**
 *
 * @author Administrator
 */
public class FailureMessageUtils {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("com/bdx/bwallet/tools/core/FailureMessage");
    
    public static String extract(Optional<Message> message) {
        String msg = "Operation failed";
        if (message.isPresent()) {
            BWalletMessage.Failure failure = (BWalletMessage.Failure) message.get();
            msg = failure.getMessage();
        }
        String translated;
        try {
            translated = BUNDLE.getString(msg);
        } catch (Exception ex) {
            translated = msg;
            System.out.println(msg);
        }
        return translated;
    }

}
