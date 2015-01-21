package com.bdx.bwallet.tools;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.hid4java.HidDevice;

import com.google.protobuf.Message;
import com.bdx.bwallet.protobuf.BWalletMessage;

public class HidMessageUtils {

	private static final int PACKET_LENGTH = 64;

	protected static int writeToDevice(HidDevice device, byte[] buffer) {
		assert device != null;
		assert buffer != null;

		//System.out.println("HidMessageUtils [debug] Writing buffer to HID pipe...");

		int bytesSent = device.write(buffer, PACKET_LENGTH, (byte) 0x00);
		if (bytesSent != buffer.length) {
			//System.out.println("HidMessageUtils [warn] Invalid packet size sent. Expected: " + buffer.length + " Actual: " + bytesSent);
		}

		//System.out.println("HidMessageUtils [debug] Wrote " + bytesSent + " bytes to USB pipe.");

		return bytesSent;
	}

	public static void writeMessage(HidDevice device, Message message) {
		assert device != null;
		assert message != null;

		ByteBuffer messageBuffer = MessageUtils.formatAsHIDPackets(message);

		int packets = messageBuffer.position() / 63;
		//System.out.println("HidMessageUtils [debug] Writing " + packets + " packets");
		messageBuffer.rewind();

		// HID requires 64 byte packets with 63 bytes of payload
		for (int i = 0; i < packets; i++) {

			byte[] buffer = new byte[64];
			buffer[0] = 63; // Length
			messageBuffer.get(buffer, 1, 63); // Payload

			// Describe the packet
			String s = "Packet [" + i + "]: ";
			for (int j = 0; j < 64; j++) {
				s += String.format(" %02x", buffer[j]);
			}

			//System.out.println("HidMessageUtils [debug] > " + s);

			writeToDevice(device, buffer);
		}
	}

	protected static Message readFromDevice(HidDevice device) {
		assert device != null;

		//System.out.println("HidMessageUtils [debug] Reading from hardware device");

		ByteBuffer messageBuffer = ByteBuffer.allocate(32768);

		BWalletMessage.MessageType type;
		int msgSize;
		int received;

		// Keep reading until synchronized on "##"
		for (;;) {
			byte[] buffer = new byte[PACKET_LENGTH];

			received = device.read(buffer);

			//System.out.println("HidMessageUtils [debug] < " + received + " bytes");

			if (received == -1) {
				return null;
			}

			MessageUtils.logPacket("<", 0, buffer);

			if (received < 9) {
				continue;
			}

			// Synchronize the buffer on start of new message ('?' is ASCII 63)
			if (buffer[0] != (byte) '?' || buffer[1] != (byte) '#' || buffer[2] != (byte) '#') {
				// Reject packet
				//System.out.println("HidMessageUtils [debug] Rejecting message (not synchronized)");
				continue;
			}

			// Evaluate the header information (short, int)
			type = BWalletMessage.MessageType.valueOf((buffer[3] << 8 & 0xFF) + buffer[4]);
			msgSize = ((buffer[5] & 0xFF) << 24) + ((buffer[6] & 0xFF) << 16) + ((buffer[7] & 0xFF) << 8) + (buffer[8] & 0xFF);

			// Treat remainder of packet as the protobuf message payload
			messageBuffer.put(buffer, 9, buffer.length - 9);

			break;
		}

		//System.out.println("HidMessageUtils [debug] < Type: '" + type.name() + "' Message size: '" + msgSize + "' bytes");

		int packet = 0;
		while (messageBuffer.position() < msgSize) {

			byte[] buffer = new byte[PACKET_LENGTH];
			received = device.read(buffer);
			packet++;

			//System.out.println("HidMessageUtils [debug] < (cont) " + received + " bytes");
			MessageUtils.logPacket("<", packet, buffer);

			if (buffer[0] != (byte) '?') {
				//System.out.println("HidMessageUtils [warn] < Malformed packet length. Expected: '3f' Actual: '" + String.format("%02x", buffer[0])
						//+ "'. Ignoring.");
				continue;
			}

			// Append the packet payload to the message buffer
			messageBuffer.put(buffer, 1, buffer.length - 1);
		}

		//System.out.println("HidMessageUtils [debug] Packet complete");

		// Parse the message
		return MessageUtils.parse(type, Arrays.copyOfRange(messageBuffer.array(), 0, msgSize));
	}

}
