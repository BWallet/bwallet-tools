package com.bdx.bwallet.tools;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;
import com.google.protobuf.Message;

public class WipeDeviceTool extends AbstractTool {
	
	public WipeDeviceTool() {
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
				if (features.getBootloaderMode()) {
					System.err.println("Device is in bootloader mode.");
				} else if (!features.getInitialized()){
					System.err.println("Device is already wiped. ");
				} else {
					BWalletMessage.WipeDevice wipeDevice = BWalletMessage.WipeDevice.newBuilder().build();
					HidMessageUtils.writeMessage(device, wipeDevice);
					
					Message message = HidMessageUtils.readFromDevice(device);
					handleMessage(device, message);
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	protected void handleMessage(HidDevice device, Message message) {
		if (message instanceof BWalletMessage.ButtonRequest) {
			System.err.println("This action cannot be undone. Please confirm on your device.");
			BWalletMessage.ButtonAck buttonAck = BWalletMessage.ButtonAck.newBuilder().build();
			HidMessageUtils.writeMessage(device, buttonAck);
		} else if (message instanceof BWalletMessage.Success) {
			System.out.println("Device wiped");
			return ;
		} else if (message instanceof BWalletMessage.Failure) {
			System.out.println("Wipe cancelled");
			return ;
		}
		message = HidMessageUtils.readFromDevice(device);
		this.handleMessage(device, message);
	}
	
	public static void main(String [] args) {
		WipeDeviceTool tool = new WipeDeviceTool();
		tool.run(null);
	}
}
