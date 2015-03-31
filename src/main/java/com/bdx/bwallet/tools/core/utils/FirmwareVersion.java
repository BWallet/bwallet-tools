/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.utils;

import com.bdx.bwallet.protobuf.BWalletMessage.Features;

/**
 *
 * @author Administrator
 */
public class FirmwareVersion {

    private int major;
    private int minor;
    private int patch;

    public FirmwareVersion(Features features) {
        this.major = features.getMajorVersion();
        this.minor = features.getMinorVersion();
        this.patch = features.getPatchVersion();
    }

    public FirmwareVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public boolean ge(int major, int minor, int patch) {
        if (this.major > major) {
            return true;
        } else if (this.major < major) {
            return false;
        } else {
            if (this.minor > minor) {
                return true;
            } else if (this.minor < minor) {
                return false;
            } else {
                if (this.patch >= patch) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String toString() {
        String b = this.major + "." + this.minor + "." + this.patch;
        return b;
    }
}
