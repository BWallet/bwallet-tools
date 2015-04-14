/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui.utils;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.tools.ui.PINEntryDialog;
import com.google.common.base.Optional;
import com.google.protobuf.Message;
import java.util.ResourceBundle;

/**
 *
 * @author Administrator
 */
public class PINEntryUtils {

    public static PINEntryDialog createDialog(java.awt.Dialog parent, ResourceBundle bundle, Optional<Message> message) {
        PINEntryDialog pinEntryDialog = new PINEntryDialog(parent, true, bundle);
        pinEntryDialog.setLocationRelativeTo(null);
        if (message.isPresent()) {
            BWalletMessage.PinMatrixRequest pinRequest = (BWalletMessage.PinMatrixRequest) message.get();
            String title = bundle.getString("PINEntryDialog.titleLabel.text." + pinRequest.getType().name());
            pinEntryDialog.setPinTitle(title);
        }
        return pinEntryDialog;
    }
    
}
