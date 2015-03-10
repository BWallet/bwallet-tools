package com.bdx.bwallet.tools;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;
import com.bdx.bwallet.protobuf.BWalletType;
import com.google.protobuf.Message;

public class RecoveryDeviceTool extends AbstractTool {
	
	private Scanner scanner = new Scanner(System.in);
	
	public RecoveryDeviceTool() {
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
					
					System.out.println("pin protection(true/false)");
					String pinProtection = scanner.nextLine();
					
					System.out.println("passphrase protection(true/false)");
					String passphraseProtection = scanner.nextLine();
					
					System.out.println("seed length(12/18/24)");
					String seedLength = scanner.nextLine();
					
					if ("chinese".equalsIgnoreCase(language)) {
						language = "chinese";
					} else {
						language = "english";
					}
					
					if (!seedLength.equals("12") && !seedLength.equals("18") && !seedLength.equals("24")) {
						seedLength = "24";
					}
					
					BWalletMessage.RecoveryDevice recoveryDevice = BWalletMessage.RecoveryDevice
						.newBuilder()
						.setLabel(label)
						.setLanguage(language)
						.setPinProtection(Boolean.valueOf(pinProtection))
						.setPassphraseProtection(Boolean.valueOf(passphraseProtection))
						.setWordCount(Integer.parseInt(seedLength))
						.setEnforceWordlist(true)
						.build();
					
					HidMessageUtils.writeMessage(device, recoveryDevice);
					
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
		if (message instanceof BWalletMessage.PinMatrixRequest) {
			BWalletType.PinMatrixRequestType type = ((BWalletMessage.PinMatrixRequest)message).getType();
			if (type.getNumber() == 2)
				System.out.println("Please enter new PIN");
			else if (type.getNumber() == 3)
				System.out.println("Please re-enter new PIN");
			
			String pin = scanner.nextLine();
			
			BWalletMessage.PinMatrixAck pinMatrixAck = BWalletMessage.PinMatrixAck.newBuilder().setPin(pin).build();
			HidMessageUtils.writeMessage(device, pinMatrixAck);
		} else if (message instanceof BWalletMessage.WordRequest) {
			System.out.println("word");
			String word = scanner.nextLine();
			BWalletMessage.WordAck wordAck = BWalletMessage.WordAck.newBuilder().setWord(word).build();
			HidMessageUtils.writeMessage(device, wordAck);
		} else if (message instanceof BWalletMessage.Success) {
			System.out.println("Device recovery success");
			return ;
		} else if (message instanceof BWalletMessage.Failure) {
			System.out.println("Device recovery failure");
			return ;
		} else {
			throw new RuntimeException("Unkown message");
		}
		message = HidMessageUtils.readFromDevice(device);
		this.handleMessage(device, message);
	}
	
	public static void main(String [] args) {
		RecoveryDeviceTool tool = new RecoveryDeviceTool();
		tool.run(null);
	}
}
