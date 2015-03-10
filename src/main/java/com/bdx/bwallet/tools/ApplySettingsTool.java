package com.bdx.bwallet.tools;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;
import com.google.protobuf.Message;

public class ApplySettingsTool extends AbstractTool {
	
	private Scanner scanner = new Scanner(System.in);
	
	public ApplySettingsTool() {
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
				}  else if (!features.getInitialized()){
					System.err.println("Device is not initialized. ");
				} else {
					System.out.println("label");
					String label = scanner.nextLine();
					
					System.out.println("language(english/chinese)");
					String language = scanner.nextLine();
					
					if ("chinese".equalsIgnoreCase(language)) {
						language = "chinese";
					} else {
						language = "english";
					}
					
					BWalletMessage.ApplySettings applySettings = BWalletMessage.ApplySettings
							.newBuilder()
							.setLabel(label)
							.setLanguage(language)
							.build();
					HidMessageUtils.writeMessage(device, applySettings);
					
					Message message = HidMessageUtils.readFromDevice(device);
					this.handleMessage(device, message);
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
			System.err.println("Please confirm the action on your device.");
			BWalletMessage.ButtonAck buttonAck = BWalletMessage.ButtonAck.newBuilder().build();
			HidMessageUtils.writeMessage(device, buttonAck);
		} else if (message instanceof BWalletMessage.Success) {
			System.out.println("Label and language was successfully changed.");
			return ;
		} else if (message instanceof BWalletMessage.Failure) {
			System.out.println("Apply settings cancelled");
			return ;
		}
		message = HidMessageUtils.readFromDevice(device);
		this.handleMessage(device, message);
	}
	
	public static void main(String [] args) {
		ApplySettingsTool tool = new ApplySettingsTool();
		tool.run(null);
	}
}
