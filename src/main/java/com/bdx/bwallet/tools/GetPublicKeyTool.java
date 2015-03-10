package com.bdx.bwallet.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;
import com.google.protobuf.Message;

public class GetPublicKeyTool extends AbstractTool {
	
	static final int HARDENED_BIT = 0x80000000;
	
	private Scanner scanner = new Scanner(System.in);
	
	public GetPublicKeyTool() {
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
					System.out.println("Path e.g 44'/0'/0'");
					String path = scanner.nextLine();
					
					List<Integer> addressN = new ArrayList<Integer>();
					if (path != null && !path.equals("")) {
						String [] array = path.split("/");
						for (String s : array) {
							int n = 0;
							if (s.endsWith("'")) {
								n = Integer.parseInt(s.substring(0, s.length() - 1));
								n = n | HARDENED_BIT;
							} else {
								n = Integer.parseInt(s);
							}
							addressN.add(n);
						}
					}
					
					BWalletMessage.GetPublicKey getPublicKey = BWalletMessage.GetPublicKey
							.newBuilder().addAllAddressN(addressN)
							.build();
					HidMessageUtils.writeMessage(device, getPublicKey);
					
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
		if (message instanceof BWalletMessage.PublicKey) {
			BWalletMessage.PublicKey publicKey = (BWalletMessage.PublicKey)message;
			System.out.println("Public key:" + publicKey.getXpub());
		}
	}
	
	public static void main(String [] args) {
		GetPublicKeyTool tool = new GetPublicKeyTool();
		tool.run(null);
	}
	
}
