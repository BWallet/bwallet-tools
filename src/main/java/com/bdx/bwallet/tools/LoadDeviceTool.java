package com.bdx.bwallet.tools;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;
import com.google.protobuf.Message;

public class LoadDeviceTool extends AbstractTool {

	private Scanner scanner = new Scanner(System.in);
	
	public LoadDeviceTool() {
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
				} else if (features.getInitialized()){
					System.err.println("Device is already initialized. Use Wipe first.");
				} else {
					System.out.println("label");
					String label = scanner.nextLine();
					
					System.out.println("language(english/chinese)");
					String language = scanner.nextLine();
					
					System.out.println("recovery seed");
					String mnemonic = scanner.nextLine();
					
					System.out.println("pin");
					String pin = scanner.nextLine();
					
					System.out.println("passphrase protection(true/false)");
					String passphraseProtection = scanner.nextLine();
					
					if ("chinese".equalsIgnoreCase(language)) {
						language = "chinese";
					} else {
						language = "english";
					}
					
					BWalletMessage.LoadDevice loadDevice = BWalletMessage.LoadDevice
						.newBuilder()
						.setMnemonic(mnemonic)
						.setLabel(label)
						.setLanguage(language)
						.setPassphraseProtection(Boolean.valueOf(passphraseProtection))
						.setPin(pin)
						.build();
					
					HidMessageUtils.writeMessage(device, loadDevice);
					
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
		if (message == null)
			return ;
		if (message instanceof BWalletMessage.ButtonRequest) {
			System.out.println("Please confirm the action on your device.");
			BWalletMessage.ButtonAck buttonAck = BWalletMessage.ButtonAck.newBuilder().build();
			HidMessageUtils.writeMessage(device, buttonAck);
		} else if (message instanceof BWalletMessage.Success) {
			System.out.println("Device load success");
			return ;
		} else if (message instanceof BWalletMessage.Failure) {
			System.out.println("Device load failure");
			return ;
		} else {
			throw new RuntimeException("Unkown message");
		}
		message = HidMessageUtils.readFromDevice(device);
		this.handleMessage(device, message);
	}
	
	public static void main(String [] args) {
		LoadDeviceTool tool = new LoadDeviceTool();
		tool.run(null);
	}
	
}
