/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui.utils;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;

/**
 *
 * @author Administrator
 */
public class BIP44PathUtils {
    
    public static String getAccountPath(DeterministicKey accountKey) {
        String path = "m/44'/0'";
        for (ChildNumber childNumber : accountKey.getPath()) {
            path = path + "/" + childNumber.num();
            if (childNumber.isHardened()) {
                path = path + "'";
            }
        }
        return path;
    }
    
}
