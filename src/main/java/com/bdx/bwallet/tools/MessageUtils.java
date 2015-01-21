package com.bdx.bwallet.tools;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.bdx.bwallet.protobuf.BWalletMessage;

public class MessageUtils {

	/**
	* @param prefix The logging prefix (usually ">" for write and "<" for read)
	* @param count  The packet count
	* @param buffer The buffer containing the packet to log
	*/
	public static void logPacket(String prefix, int count, byte[] buffer) {
		String s = prefix + " Packet [" + count + "]:";
		for (byte b : buffer) {
			s += String.format(" %02x", b);
		}
		//System.out.println(s);
	}

	/**
	 * <p>
	 * Format a protobuf message as a byte buffer filled with HID packets
	 * </p>
	 * 
	 * @param message
	 *            The protobuf message
	 * 
	 * @return A byte buffer containing a set of HID packets
	 */
	public static ByteBuffer formatAsHIDPackets(Message message) {

		int msgSize = message.getSerializedSize();
		String msgName = getMessageName(message);
		int msgId = BWalletMessage.MessageType.valueOf("MessageType_" + msgName).getNumber();

		// Create the header
		ByteBuffer messageBuffer = ByteBuffer.allocate(204800 * 2);	// 32768 is to small

		// Marker bytes
		messageBuffer.put((byte) '#');
		messageBuffer.put((byte) '#');

		// Header code
		messageBuffer.put((byte) ((msgId >> 8) & 0xFF));
		messageBuffer.put((byte) (msgId & 0xFF));

		// Message size
		messageBuffer.put((byte) ((msgSize >> 24) & 0xFF));
		messageBuffer.put((byte) ((msgSize >> 16) & 0xFF));
		messageBuffer.put((byte) ((msgSize >> 8) & 0xFF));
		messageBuffer.put((byte) (msgSize & 0xFF));

		// Message payload
		messageBuffer.put(message.toByteArray());

		// Packet padding
		while (messageBuffer.position() % 63 > 0) {
			messageBuffer.put((byte) 0);
		}

		return messageBuffer;
	}

	public static Message.Builder getMessageBuilder(String type) {
		try {
			Class<?> c = Class.forName("com.bdx.bwallet.protobuf.BWalletMessage$" + type);
			Method m = c.getMethod("newBuilder", new Class[] {});
			Message.Builder builder = (Builder) m.invoke(c, new Object[] {});
			return builder;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Unknown message type: " + type);
		}
	}

	/**
	 * @param type
	 *            The message type
	 * @param buffer
	 *            The buffer containing the protobuf message
	 * 
	 * @return The low level message event containing the data if it could be
	 *         parsed and adapted
	 */
	public static Message parse(BWalletMessage.MessageType type, byte[] buffer) {
		// TODO Using reflect to implement
		
		//System.out.println("MessageUtils [info] Parsing '" + type + "' (" + buffer.length + " bytes):");

		logPacket("<>", 0, buffer);

		try {
			Message message;

			switch (type) {
			case MessageType_Initialize:
				message = BWalletMessage.Initialize.parseFrom(buffer);
				break;
			case MessageType_Ping:
				message = BWalletMessage.Ping.parseFrom(buffer);
				break;
			case MessageType_Success:
				message = BWalletMessage.Success.parseFrom(buffer);
				break;
			case MessageType_Failure:
				message = BWalletMessage.Failure.parseFrom(buffer);
				break;
			case MessageType_ChangePin:
				message = BWalletMessage.ChangePin.parseFrom(buffer);
				break;
			case MessageType_WipeDevice:
				message = BWalletMessage.WipeDevice.parseFrom(buffer);
				break;
			case MessageType_FirmwareErase:
				message = BWalletMessage.FirmwareErase.parseFrom(buffer);
				break;
			case MessageType_FirmwareUpload:
				message = BWalletMessage.FirmwareUpload.parseFrom(buffer);
				break;
			case MessageType_GetEntropy:
				message = BWalletMessage.GetEntropy.parseFrom(buffer);
				break;
			case MessageType_Entropy:
				message = BWalletMessage.Entropy.parseFrom(buffer);
				break;
			case MessageType_GetPublicKey:
				message = BWalletMessage.GetPublicKey.parseFrom(buffer);
				break;
			case MessageType_PublicKey:
				message = BWalletMessage.PublicKey.parseFrom(buffer);
				break;
			case MessageType_LoadDevice:
				message = BWalletMessage.LoadDevice.parseFrom(buffer);
				break;
			case MessageType_ResetDevice:
				message = BWalletMessage.ResetDevice.parseFrom(buffer);
				break;
			case MessageType_SignTx:
				message = BWalletMessage.SignTx.parseFrom(buffer);
				break;
			case MessageType_SimpleSignTx:
				message = BWalletMessage.SimpleSignTx.parseFrom(buffer);
				break;
			case MessageType_Features:
				message = BWalletMessage.Features.parseFrom(buffer);
				break;
			case MessageType_PinMatrixRequest:
				message = BWalletMessage.PinMatrixRequest.parseFrom(buffer);
				break;
			case MessageType_PinMatrixAck:
				message = BWalletMessage.PinMatrixAck.parseFrom(buffer);
				break;
			case MessageType_Cancel:
				message = BWalletMessage.Cancel.parseFrom(buffer);
				break;
			case MessageType_TxRequest:
				message = BWalletMessage.TxRequest.parseFrom(buffer);
				break;
			case MessageType_TxAck:
				message = BWalletMessage.TxAck.parseFrom(buffer);
				break;
			case MessageType_CipherKeyValue:
				message = BWalletMessage.CipherKeyValue.parseFrom(buffer);
				break;
			case MessageType_ClearSession:
				message = BWalletMessage.ClearSession.parseFrom(buffer);
				break;
			case MessageType_ApplySettings:
				message = BWalletMessage.ApplySettings.parseFrom(buffer);
				break;
			case MessageType_ButtonRequest:
				message = BWalletMessage.ButtonRequest.parseFrom(buffer);
				break;
			case MessageType_ButtonAck:
				message = BWalletMessage.ButtonAck.parseFrom(buffer);
				break;
			case MessageType_GetAddress:
				message = BWalletMessage.GetAddress.parseFrom(buffer);
				break;
			case MessageType_Address:
				message = BWalletMessage.Address.parseFrom(buffer);
				break;
			case MessageType_EntropyRequest:
				message = BWalletMessage.EntropyRequest.parseFrom(buffer);
				break;
			case MessageType_EntropyAck:
				message = BWalletMessage.EntropyAck.parseFrom(buffer);
				break;
			case MessageType_SignMessage:
				message = BWalletMessage.SignMessage.parseFrom(buffer);
				break;
			case MessageType_VerifyMessage:
				message = BWalletMessage.VerifyMessage.parseFrom(buffer);
				break;
			case MessageType_MessageSignature:
				message = BWalletMessage.MessageSignature.parseFrom(buffer);
				break;
			case MessageType_EncryptMessage:
				message = BWalletMessage.EncryptMessage.parseFrom(buffer);
				break;
			case MessageType_DecryptMessage:
				message = BWalletMessage.DecryptMessage.parseFrom(buffer);
				break;
			case MessageType_PassphraseRequest:
				message = BWalletMessage.PassphraseRequest.parseFrom(buffer);
				break;
			case MessageType_PassphraseAck:
				message = BWalletMessage.PassphraseAck.parseFrom(buffer);
				break;
			case MessageType_EstimateTxSize:
				message = BWalletMessage.EstimateTxSize.parseFrom(buffer);
				break;
			case MessageType_TxSize:
				message = BWalletMessage.TxSize.parseFrom(buffer);
				break;
			case MessageType_RecoveryDevice:
				message = BWalletMessage.RecoveryDevice.parseFrom(buffer);
				break;
			case MessageType_WordRequest:
				message = BWalletMessage.WordRequest.parseFrom(buffer);
				break;
			case MessageType_WordAck:
				message = BWalletMessage.WordAck.parseFrom(buffer);
				break;
			case MessageType_DebugLinkDecision:
				message = BWalletMessage.DebugLinkDecision.parseFrom(buffer);
				break;
			case MessageType_DebugLinkGetState:
				message = BWalletMessage.DebugLinkGetState.parseFrom(buffer);
				break;
			case MessageType_DebugLinkState:
				message = BWalletMessage.DebugLinkState.parseFrom(buffer);
				break;
			case MessageType_DebugLinkStop:
				message = BWalletMessage.DebugLinkStop.parseFrom(buffer);
				break;
			case MessageType_DebugLinkLog:
				message = BWalletMessage.DebugLinkLog.parseFrom(buffer);
				break;
			default:
				throw new IllegalStateException("Unknown message type: " + type.name());
			}

			return message;

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			//System.out.println("MessageUtils [error] Could not parse message");
		}

		// Must have failed to be here
		return null;
	}
	
	public static String getMessageName(Message message) {
		String msgName = message.getClass().getSimpleName();
		
		// fix the bug after obfuscation, e.g. BWalletMessage$Initialize -> Initialize
		int dolsIndex = msgName.indexOf("$");
		if (dolsIndex > 0) {
			msgName = msgName.substring(dolsIndex + 1, msgName.length());
		}
		
		return msgName;
	}

}
