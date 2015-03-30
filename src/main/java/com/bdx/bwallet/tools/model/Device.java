/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.model;

import org.hid4java.HidDevice;

/**
 *
 * @author Dean Liu
 */
public class Device {

    private final HidDevice hidDevice;

    public Device(HidDevice hidDevice) {
        this.hidDevice = hidDevice;
    }

    public String getPath() {
        return hidDevice.getPath();
    }

    /**
     * @return The Vendor ID
     */
    public short getVendorId() {
        return hidDevice.getVendorId();
    }

    /**
     * @return The product ID
     */
    public short getProductId() {
        return hidDevice.getProductId();
    }

    /**
     * @return The serial number (wide string)
     */
    public String getSerialNumber() {
        return hidDevice.getSerialNumber();
    }

    /**
     * @return The release number
     */
    public int getReleaseNumber() {
        return hidDevice.getReleaseNumber();
    }

    /**
     * @return The manufacturer string
     */
    public String getManufacturer() {
        return hidDevice.getManufacturer();
    }

    /**
     * @return The product info for this Device/Interface (Windows/Mac only)
     */
    public String getProduct() {
        return hidDevice.getProduct();
    }

    /**
     * @return The usage page for this Device/Interface (Windows/Mac only)
     */
    public int getUsagePage() {
        return hidDevice.getUsagePage();
    }

    /**
     * @return The usage number
     */
    public int getUsage() {
        return hidDevice.getUsage();
    }

    /**
     * @return The USB interface number
     */
    public int getInterfaceNumber() {
        return hidDevice.getInterfaceNumber();
    }

    /**
     * @return A unique device ID made up from vendor ID, product ID and serial
     * number
     */
    public String getId() {
        return hidDevice.getId();
    }

    @Override
    public String toString() {
        return this.getPath();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Device device = (Device) o;
        return this.getPath().equals(device.getPath());
    }

    public int hashCode() {
        return this.getPath().hashCode();
    }

}
