package com.bdx.bwallet.tools;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.codec.binary.Hex;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;

public class FirmwareHashTool extends AbstractTool {
	
	public FirmwareHashTool() {
		super();
	}
	
	@Override
	public void run(CommandLine cl) {
		while (true) {
			List<HidDevice> devices = this.openAttachedDevices();
			for (HidDevice device : devices) {
				Initialize initialize = BWalletMessage.Initialize.newBuilder().setLanguage("english").build();
				HidMessageUtils.writeMessage(device, initialize);
				
				Features features = (Features) HidMessageUtils.readFromDevice(device);
				if (!features.getBootloaderMode()) {
					System.err.println("Device is not in bootloader mode.");
				} else {
					//byte[] bytes = features.getBootloaderHash().toByteArray();
					//System.out.println("Bootloader hash:" + Hex.encodeHexString(bytes));
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	public static void main(String [] args) {
		FirmwareHashTool tool = new FirmwareHashTool();
		tool.run(null);
	}
}
