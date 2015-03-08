package com.bdx.bwallet.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.hid4java.HidDevice;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.ButtonAck;
import com.bdx.bwallet.protobuf.BWalletMessage.ButtonRequest;
import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.protobuf.BWalletMessage.FirmwareErase;
import com.bdx.bwallet.protobuf.BWalletMessage.FirmwareUpload;
import com.bdx.bwallet.protobuf.BWalletMessage.Initialize;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

public class WriteFirmwareTool extends AbstractTool {

	public WriteFirmwareTool() {
		super();
	}

	@Override
	public void run(CommandLine cl) {
		if (!cl.hasOption("firmware")) {
			System.err.println("Please specify the location of firemware.");
			return ;
		}
		
		String firmware = cl.getOptionValue("firmware");
		byte[] payload = null;
		try {
			byte[] bytes = FileUtils.readFileToByteArray(new File(firmware));
			payload= Hex.decodeHex(new String(bytes).toCharArray());
		} catch (IOException | DecoderException e) {
			System.err.println("Failed to open file : " + e.getMessage());
			return ;
		}
		
		System.out.println("Waitting for device plug...");
		
		while (true) {
			List<HidDevice> devices = this.openAttachedDevices();
			for (HidDevice device : devices) {
				Initialize initialize = BWalletMessage.Initialize.newBuilder().setLanguage("english").build();
				HidMessageUtils.writeMessage(device, initialize);
				
				Features features = (Features) HidMessageUtils.readFromDevice(device);
				if (!features.getBootloaderMode()) {
					System.err.println("Device is not in bootloader mode.");
				} else {
					FirmwareErase firmwareErase = BWalletMessage.FirmwareErase.newBuilder().build();
					HidMessageUtils.writeMessage(device, firmwareErase);
					System.out.println("This action cannot be undone. Please confirm on your device.");
					
					Message message = HidMessageUtils.readFromDevice(device);
					if (isConfirmedAction(message)) {
						System.out.println("Firemware uploading...");
						
						FirmwareUpload firmwareUpload = BWalletMessage.FirmwareUpload.newBuilder().setPayload(ByteString.copyFrom(payload)).build();
						HidMessageUtils.writeMessage(device, firmwareUpload);
						
						@SuppressWarnings("unused")
						ButtonRequest buttonRequest = (ButtonRequest) HidMessageUtils.readFromDevice(device);
						System.out.println("Please confirm the firmware fingerprint.");
						
						ButtonAck buttonAck = BWalletMessage.ButtonAck.newBuilder().build();
						HidMessageUtils.writeMessage(device, buttonAck);
						
						message = HidMessageUtils.readFromDevice(device);
						if (isConfirmedAction(message)) {
							System.err.println("Update was successful! Please unplug the device now.");
						} else {
							BWalletMessage.Failure failure = (BWalletMessage.Failure)message;
							System.err.println("Update failed : " + failure.getMessage());
						}
					} else {
						BWalletMessage.Failure failure = (BWalletMessage.Failure)message;
						System.err.println("Failed to wipe device : " + failure.getMessage());
					}
				}
				//this.closeDevice(device);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean isConfirmedAction(Message message) {
		if (message instanceof BWalletMessage.Success) {
			return true;
		} else if (message instanceof BWalletMessage.Failure){
			return false;
		}
		throw new RuntimeException("Unknown message.");
	}

}
