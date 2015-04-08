/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.ui.utils;

import com.google.common.base.Optional;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Administrator
 */
public class LabelUtils {

    /**
     * @param image The optional image
     *
     * @return A new label with the image or a placeholder if not present
     */
    public static JLabel newImageLabel(Optional<BufferedImage> image) {
        if (image.isPresent()) {
            ImageIcon icon = new ImageIcon(image.get());
            JLabel label = new JLabel(icon);
            return label;
        }
        // Fall back to a default image
        JLabel label = newBlankLabel();
        return label;
    }

    /**
     * @return A new blank label with default styling
     */
    public static JLabel newBlankLabel() {
        JLabel label = new JLabel("");
        return label;
    }
}
