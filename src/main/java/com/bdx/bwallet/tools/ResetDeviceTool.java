package com.bdx.bwallet.tools;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;
import com.bdx.bwallet.protobuf.BWalletType;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

public class ResetDeviceTool extends AbstractTool {

	private Scanner scanner = new Scanner(System.in);
	
	private boolean recoveryStarted = false;
	private int recoveryWords = 0;
	private int recoveryWordsDone = 0;
	private int recoveryCurrentWord = 1;
	
	public ResetDeviceTool() {
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
					
					recoveryStarted = false;
					recoveryWords = Integer.parseInt(seedLength);
					recoveryWordsDone = 0;
					recoveryCurrentWord = 1;
					
					BWalletMessage.ResetDevice resetDevice = BWalletMessage.ResetDevice
						.newBuilder()
						.setLabel(label)
						.setLanguage(language)
						.setPinProtection(Boolean.valueOf(pinProtection))
						.setPassphraseProtection(Boolean.valueOf(passphraseProtection))
						.setStrength((Integer.parseInt(seedLength) / 6) * 64)
						.build();
					
					HidMessageUtils.writeMessage(device, resetDevice);
					
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
		} else if (message instanceof BWalletMessage.EntropyRequest) {
			byte[] bytes = new byte[32];
			new Random().nextBytes(bytes);
			ByteString entropy = ByteString.copyFrom(bytes);
			BWalletMessage.EntropyAck entropyAck = BWalletMessage.EntropyAck.newBuilder().setEntropy(entropy).build();
			HidMessageUtils.writeMessage(device, entropyAck);
		} else if (message instanceof BWalletMessage.ButtonRequest) {
			if (!recoveryStarted) {
				// First write
	            recoveryStarted = true;
	            System.out.println("We will now show you " + recoveryWords + " words that will allow you to recover your accounts in case you lose your device. Please write down all these words carefully.");
	            System.out.println("Please write down the " + ordinal(recoveryCurrentWord) + " word of your recovery seed.");
			} else {
	        	recoveryWordsDone = recoveryWordsDone + 1;
	            recoveryCurrentWord = recoveryCurrentWord + 1;
	            if (recoveryWordsDone < recoveryWords) {
	            	// Write
	            	System.out.println("Please write down the " + ordinal(recoveryCurrentWord) + " word of your recovery seed.");
	            } else if (recoveryWordsDone == recoveryWords) {
	            	// First check
	                recoveryCurrentWord = 1;
	                System.out.println("We will now show you the " + recoveryWords + " words again. Please check carefully that you wrote them down correctly.");
	                System.out.println("Please check the " + ordinal(recoveryCurrentWord) + " word of your recovery seed.");
	            } else if (recoveryWordsDone < 2 * recoveryWords - 1) {
	            	// Check
	            	System.out.println("Please check the " + ordinal(recoveryCurrentWord) + " word of your recovery seed.");
	            } else {
	            	// Last check
	            }
	        }
			
			BWalletMessage.ButtonAck buttonAck = BWalletMessage.ButtonAck.newBuilder().build();
			HidMessageUtils.writeMessage(device, buttonAck);
		} else if (message instanceof BWalletMessage.Success) {
			System.out.println("Device reset success");
			return ;
		} else if (message instanceof BWalletMessage.Failure) {
			System.out.println("Device reset failure");
			return ;
		} else {
			throw new RuntimeException("Unkown message");
		}
		message = HidMessageUtils.readFromDevice(device);
		this.handleMessage(device, message);
	}
	
	public String ordinal(int i) {
	    String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	    switch (i % 100) {
	    case 11:
	    case 12:
	    case 13:
	        return i + "th";
	    default:
	        return i + sufixes[i % 10];

	    }
	}
	
	public static void main(String [] args) {
		ResetDeviceTool tool = new ResetDeviceTool();
		tool.run(null);
	}

}
