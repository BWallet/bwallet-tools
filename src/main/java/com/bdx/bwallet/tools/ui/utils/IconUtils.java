/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui.utils;

import javax.swing.ImageIcon;

/**
 *
 * @author Administrator
 */
public class IconUtils {

    public static ImageIcon createImageIcon(String path,
            String description) {
        java.net.URL imgURL = IconUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
