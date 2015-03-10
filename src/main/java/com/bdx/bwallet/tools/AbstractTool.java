package com.bdx.bwallet.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;
import org.hid4java.HidDeviceInfo;
import org.hid4java.HidException;
import org.hid4java.HidServices;

import com.google.protobuf.Message;

public abstract class AbstractTool implements Tool {

	private HidServices hidServices = null;
	
	public AbstractTool() {
		try {
			hidServices = new HidServices(false);
		} catch (HidException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void run(CommandLine cl) {
	}

	public HidServices getHidServices() {
		return hidServices;
	}

	protected List<HidDevice> openAttachedDevices() {
		List<HidDevice> devices = new ArrayList<HidDevice>();
		for (HidDeviceInfo hidDeviceInfo : hidServices.getAttachedHidDevices()) {
			if (hidDeviceInfo.getVendorId() == VENDOR_ID && hidDeviceInfo.getProductId() == PRODUCT_ID) {
				if (hidDeviceInfo.getSerialNumber() != null) {
					HidDevice device = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, hidDeviceInfo.getSerialNumber().toString());
					if (device != null)
						devices.add(device);
				}
			}
		}
		return devices;
	}
	
	protected void handleMessage(HidDevice device, Message message) {
	}
	
	protected void closeDevice(HidDevice device) {
		if (device != null)
			device.close();
	}
	
}
