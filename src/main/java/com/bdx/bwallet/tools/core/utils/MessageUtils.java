package com.bdx.bwallet.tools.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.KeyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.TxRequest;
import com.bdx.bwallet.protobuf.BWalletType;
import com.bdx.bwallet.protobuf.BWalletType.HDNodePathType;
import com.bdx.bwallet.protobuf.BWalletType.HDNodeType;
import com.bdx.bwallet.protobuf.BWalletType.InputScriptType;
import com.bdx.bwallet.protobuf.BWalletType.MultisigRedeemScriptType;
import com.bdx.bwallet.protobuf.BWalletType.OutputScriptType;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>
 * Utility class to provide the following to applications:</p>
 * <ul>
 * <li>Various BWalletMessage related operations</li>
 * </ul>
 *
 * @since 0.0.1  
 */
public final class MessageUtils {

    private static final Logger log = LoggerFactory.getLogger(MessageUtils.class);

    /**
     * Utilities should not have public constructors
     */
    private MessageUtils() {
    }

    /**
     * @param type The message type
     * @param buffer The buffer containing the protobuf message
     *
     * @return The low level message event containing the data if it could be
     * parsed and adapted
     */
    public static MessageEvent parse(BWalletMessage.MessageType type, byte[] buffer) {

        log.info("Parsing '{}' ({} bytes):", type, buffer.length);

        logPacket("<>", 0, buffer);

        try {
            Message message;
            MessageEventType messageEventType;

            switch (type) {
                case MessageType_Initialize:
                    message = BWalletMessage.Initialize.parseFrom(buffer);
                    messageEventType = MessageEventType.INITALISE;
                    break;
                case MessageType_Ping:
                    message = BWalletMessage.Ping.parseFrom(buffer);
                    messageEventType = MessageEventType.PING;
                    break;
                case MessageType_Success:
                    message = BWalletMessage.Success.parseFrom(buffer);
                    messageEventType = MessageEventType.SUCCESS;
                    break;
                case MessageType_Failure:
                    message = BWalletMessage.Failure.parseFrom(buffer);
                    messageEventType = MessageEventType.FAILURE;
                    break;
                case MessageType_ChangePin:
                    message = BWalletMessage.ChangePin.parseFrom(buffer);
                    messageEventType = MessageEventType.CHANGE_PIN;
                    break;
                case MessageType_WipeDevice:
                    message = BWalletMessage.WipeDevice.parseFrom(buffer);
                    messageEventType = MessageEventType.WIPE_DEVICE;
                    break;
                case MessageType_FirmwareErase:
                    message = BWalletMessage.FirmwareErase.parseFrom(buffer);
                    messageEventType = MessageEventType.FIRMWARE_ERASE;
                    break;
                case MessageType_FirmwareUpload:
                    message = BWalletMessage.FirmwareUpload.parseFrom(buffer);
                    messageEventType = MessageEventType.FIRMWARE_UPLOAD;
                    break;
                case MessageType_GetEntropy:
                    message = BWalletMessage.GetEntropy.parseFrom(buffer);
                    messageEventType = MessageEventType.GET_ENTROPY;
                    break;
                case MessageType_Entropy:
                    message = BWalletMessage.Entropy.parseFrom(buffer);
                    messageEventType = MessageEventType.ENTROPY;
                    break;
                case MessageType_GetPublicKey:
                    message = BWalletMessage.GetPublicKey.parseFrom(buffer);
                    messageEventType = MessageEventType.GET_PUBLIC_KEY;
                    break;
                case MessageType_PublicKey:
                    message = BWalletMessage.PublicKey.parseFrom(buffer);
                    messageEventType = MessageEventType.PUBLIC_KEY;
                    break;
                case MessageType_LoadDevice:
                    message = BWalletMessage.LoadDevice.parseFrom(buffer);
                    messageEventType = MessageEventType.LOAD_DEVICE;
                    break;
                case MessageType_ResetDevice:
                    message = BWalletMessage.ResetDevice.parseFrom(buffer);
                    messageEventType = MessageEventType.RESET_DEVICE;
                    break;
                case MessageType_SignTx:
                    message = BWalletMessage.SignTx.parseFrom(buffer);
                    messageEventType = MessageEventType.SIGN_TX;
                    break;
                case MessageType_SimpleSignTx:
                    message = BWalletMessage.SimpleSignTx.parseFrom(buffer);
                    messageEventType = MessageEventType.SIMPLE_SIGN_TX;
                    break;
                case MessageType_Features:
                    message = BWalletMessage.Features.parseFrom(buffer);
                    messageEventType = MessageEventType.FEATURES;
                    break;
                case MessageType_PinMatrixRequest:
                    message = BWalletMessage.PinMatrixRequest.parseFrom(buffer);
                    messageEventType = MessageEventType.PIN_MATRIX_REQUEST;
                    break;
                case MessageType_PinMatrixAck:
                    message = BWalletMessage.PinMatrixAck.parseFrom(buffer);
                    messageEventType = MessageEventType.PIN_MATRIX_ACK;
                    break;
                case MessageType_Cancel:
                    message = BWalletMessage.Cancel.parseFrom(buffer);
                    messageEventType = MessageEventType.CANCEL;
                    break;
                case MessageType_TxRequest:
                    message = BWalletMessage.TxRequest.parseFrom(buffer);
                    messageEventType = MessageEventType.TX_REQUEST;
                    break;
                case MessageType_TxAck:
                    message = BWalletMessage.TxAck.parseFrom(buffer);
                    messageEventType = MessageEventType.TX_ACK;
                    break;
                case MessageType_CipherKeyValue:
                    message = BWalletMessage.CipherKeyValue.parseFrom(buffer);
                    messageEventType = MessageEventType.CIPHER_KEY_VALUE;
                    break;
                case MessageType_CipheredKeyValue:
                    message = BWalletMessage.CipheredKeyValue.parseFrom(buffer);
                    messageEventType = MessageEventType.CIPHERED_KEY_VALUE;
                    break;
                case MessageType_ClearSession:
                    message = BWalletMessage.ClearSession.parseFrom(buffer);
                    messageEventType = MessageEventType.CLEAR_SESSION;
                    break;
                case MessageType_ApplySettings:
                    message = BWalletMessage.ApplySettings.parseFrom(buffer);
                    messageEventType = MessageEventType.APPLY_SETTINGS;
                    break;
                case MessageType_ButtonRequest:
                    message = BWalletMessage.ButtonRequest.parseFrom(buffer);
                    messageEventType = MessageEventType.BUTTON_REQUEST;
                    break;
                case MessageType_ButtonAck:
                    message = BWalletMessage.ButtonAck.parseFrom(buffer);
                    messageEventType = MessageEventType.BUTTON_ACK;
                    break;
                case MessageType_GetAddress:
                    message = BWalletMessage.GetAddress.parseFrom(buffer);
                    messageEventType = MessageEventType.GET_ADDRESS;
                    break;
                case MessageType_Address:
                    message = BWalletMessage.Address.parseFrom(buffer);
                    messageEventType = MessageEventType.ADDRESS;
                    break;
                case MessageType_EntropyRequest:
                    message = BWalletMessage.EntropyRequest.parseFrom(buffer);
                    messageEventType = MessageEventType.ENTROPY_REQUEST;
                    break;
                case MessageType_EntropyAck:
                    message = BWalletMessage.EntropyAck.parseFrom(buffer);
                    messageEventType = MessageEventType.ENTROPY_ACK;
                    break;
                case MessageType_SignMessage:
                    message = BWalletMessage.SignMessage.parseFrom(buffer);
                    messageEventType = MessageEventType.SIGN_MESSAGE;
                    break;
                case MessageType_VerifyMessage:
                    message = BWalletMessage.VerifyMessage.parseFrom(buffer);
                    messageEventType = MessageEventType.VERIFY_MESSAGE;
                    break;
                case MessageType_MessageSignature:
                    message = BWalletMessage.MessageSignature.parseFrom(buffer);
                    messageEventType = MessageEventType.MESSAGE_SIGNATURE;
                    break;
                case MessageType_EncryptMessage:
                    message = BWalletMessage.EncryptMessage.parseFrom(buffer);
                    messageEventType = MessageEventType.ENCRYPT_MESSAGE;
                    break;
                case MessageType_EncryptedMessage:
                    message = BWalletMessage.EncryptedMessage.parseFrom(buffer);
                    messageEventType = MessageEventType.ENCRYPTED_MESSAGE;
                    break;
                case MessageType_DecryptMessage:
                    message = BWalletMessage.DecryptMessage.parseFrom(buffer);
                    messageEventType = MessageEventType.DECRYPT_MESSAGE;
                    break;
                case MessageType_DecryptedMessage:
                    message = BWalletMessage.DecryptedMessage.parseFrom(buffer);
                    messageEventType = MessageEventType.DECRYPTED_MESSAGE;
                    break;
                case MessageType_PassphraseRequest:
                    message = BWalletMessage.PassphraseRequest.parseFrom(buffer);
                    messageEventType = MessageEventType.PASSPHRASE_REQUEST;
                    break;
                case MessageType_PassphraseAck:
                    message = BWalletMessage.PassphraseAck.parseFrom(buffer);
                    messageEventType = MessageEventType.PASSPHRASE_ACK;
                    break;
                case MessageType_EstimateTxSize:
                    message = BWalletMessage.EstimateTxSize.parseFrom(buffer);
                    messageEventType = MessageEventType.ESTIMATE_TX_SIZE;
                    break;
                case MessageType_TxSize:
                    message = BWalletMessage.TxSize.parseFrom(buffer);
                    messageEventType = MessageEventType.TX_SIZE;
                    break;
                case MessageType_RecoveryDevice:
                    message = BWalletMessage.RecoveryDevice.parseFrom(buffer);
                    messageEventType = MessageEventType.RECOVER_DEVICE;
                    break;
                case MessageType_WordRequest:
                    message = BWalletMessage.WordRequest.parseFrom(buffer);
                    messageEventType = MessageEventType.WORD_REQUEST;
                    break;
                case MessageType_WordAck:
                    message = BWalletMessage.WordAck.parseFrom(buffer);
                    messageEventType = MessageEventType.WORD_ACK;
                    break;
                case MessageType_DebugLinkDecision:
                    message = BWalletMessage.DebugLinkDecision.parseFrom(buffer);
                    messageEventType = MessageEventType.DEBUG_LINK_DECISION;
                    break;
                case MessageType_DebugLinkGetState:
                    message = BWalletMessage.DebugLinkGetState.parseFrom(buffer);
                    messageEventType = MessageEventType.DEBUG_LINK_GET_STATE;
                    break;
                case MessageType_DebugLinkState:
                    message = BWalletMessage.DebugLinkState.parseFrom(buffer);
                    messageEventType = MessageEventType.DEBUG_LINK_STATE;
                    break;
                case MessageType_DebugLinkStop:
                    message = BWalletMessage.DebugLinkStop.parseFrom(buffer);
                    messageEventType = MessageEventType.DEBUG_LINK_STOP;
                    break;
                case MessageType_DebugLinkLog:
                    message = BWalletMessage.DebugLinkLog.parseFrom(buffer);
                    messageEventType = MessageEventType.DEBUG_LINK_LOG;
                    break;
                case MessageType_GetAccountLabels:
                    message = BWalletMessage.GetAccountLabels.parseFrom(buffer);
                    messageEventType = MessageEventType.GET_ACCOUNT_LABELS;
                    break;
                case MessageType_AccountLabels:
                    message = BWalletMessage.AccountLabels.parseFrom(buffer);
                    messageEventType = MessageEventType.ACCOUNT_LABELS;
                    break;
                default:
                    throw new IllegalStateException("Unknown message type: " + type.name());
            }

            // Must be OK to be here
            log.trace("< Message:\n{}", ToStringBuilder.reflectionToString(message, new MessageToStringStyle()));

            // Wrap the type and message into an event
            return new MessageEvent(messageEventType, Optional.of(message));

        } catch (InvalidProtocolBufferException e) {
            log.error("Could not parse message", e);
        }

        // Must have failed to be here
        return null;

    }

    /**
     * @param prefix The logging prefix (usually ">" for write and "<" for read)
     * @param
     * count The packet count
     * @param buffer The buffer containing the packet to log
     */
    @SuppressFBWarnings(value = {"SBSC_USE_STRINGBUFFER_CONCATENATION"}, justification = "Only occurs at trace")
    public static void logPacket(String prefix, int count, byte[] buffer) {

    // Only do work if required
        // There is a security issue to revealing this information for certain packets
        // so be cautious in raising it in Production
        if (log.isTraceEnabled()) {
            String s = prefix + " Packet [" + count + "]:";
            for (byte b : buffer) {
                s += String.format(" %02x", b);
            }
            log.trace("{}", s);
        }

    }

    /**
     * <p>
     * Write a BWallet protocol buffer message to an OutputStream</p>
     *
     * @param message The protocol buffer message to read
     * @param out The data output stream (must be open)
     *
     * @throws java.io.IOException If the device disconnects during IO
     */
    @SuppressFBWarnings(value = {"SBSC_USE_STRINGBUFFER_CONCATENATION"}, justification = "Only occurs at trace")
    public static void writeAsHIDPackets(Message message, OutputStream out) throws IOException {

        // The message presented as a collection of HID packets
        ByteBuffer messageBuffer = formatAsHIDPackets(message);

        int packets = messageBuffer.position() / 63;
        log.info("Writing {} packets", packets);
        messageBuffer.rewind();

        // HID requires 64 byte packets with 63 bytes of payload
        for (int i = 0; i < packets; i++) {

            byte[] buffer = new byte[64];
            buffer[0] = 63; // Length
            messageBuffer.get(buffer, 1, 63); // Payload

            if (log.isTraceEnabled()) {
                // Describe the packet
                String s = "> Packet [" + i + "]: ";
                for (int j = 0; j < 64; j++) {
                    s += String.format(" %02x", buffer[j]);
                }

                log.trace(s);
            }

            out.write(buffer);

            // Flush to ensure bytes are available immediately
            out.flush();

        }

    }

    /**
     * <p>
     * Format a BWallet protobuf message as a byte buffer filled with HID
     * packets</p>
     *
     * @param message The BWallet protobuf message
     *
     * @return A byte buffer containing a set of HID packets
     */
    public static ByteBuffer formatAsHIDPackets(Message message) {

        int msgSize = message.getSerializedSize();
        String msgName = message.getClass().getSimpleName();
        int msgId = BWalletMessage.MessageType.valueOf("MessageType_" + msgName).getNumber();

        // There is a security risk to raising this logging level beyond trace
        log.trace("> Message: {}, ({} bytes)", ToStringBuilder.reflectionToString(message, new MessageToStringStyle()), msgSize);

        // Create the header
        ByteBuffer messageBuffer = ByteBuffer.allocate(204800); // 32768 is to small

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

    /**
     * <p>
     * Parse the contents of the input stream into a BWallet protobuf message</p>
     *
     * @param in The input stream containing BWallet HID packets
     *
     * @return The adapted Core message
     */
    public static MessageEvent parseAsHIDPackets(InputStream in) throws IOException {

        ByteBuffer messageBuffer = ByteBuffer.allocate(32768);

        BWalletMessage.MessageType type;
        int msgSize;
        int received;

        // Keep reading until synchronized on "##"
        for (;;) {
            byte[] buffer = new byte[64];

            received = in.read(buffer);

            if (received == -1) {
                throw new IOException("Read buffer is closed");
            }

            // There is a security risk to raising this logging level beyond trace
            log.trace("< {} bytes", received);
            MessageUtils.logPacket("<", 0, buffer);

            if (received < 9) {
                continue;
            }

            // Synchronize the buffer on start of new message ('?' is ASCII 63)
            if (buffer[0] != (byte) '?' || buffer[1] != (byte) '#' || buffer[2] != (byte) '#') {
                // Reject packet
                continue;
            }

            // Evaluate the header information (short, int)
            type = BWalletMessage.MessageType.valueOf((buffer[3] << 8 & 0xFF) + buffer[4]);
            msgSize = ((buffer[5] & 0xFF) << 24) + ((buffer[6] & 0xFF) << 16) + ((buffer[7] & 0xFF) << 8) + (buffer[8] & 0xFF);

            // Treat remainder of packet as the protobuf message payload
            messageBuffer.put(buffer, 9, buffer.length - 9);

            break;
        }

        // There is a security risk to raising this logging level beyond trace
        log.trace("< Type: '{}' Message size: '{}' bytes", type.name(), msgSize);

        int packet = 0;
        while (messageBuffer.position() < msgSize) {

            byte[] buffer = new byte[64];
            received = in.read(buffer);
            packet++;

            // There is a security risk to raising this logging level beyond trace
            log.trace("< (cont) {} bytes", received);
            MessageUtils.logPacket("<", packet, buffer);

            if (buffer[0] != (byte) '?') {
                log.warn("< Malformed packet length. Expected: '3f' Actual: '{}'. Ignoring.", String.format("%02x", buffer[0]));
                continue;
            }

            // Append the packet payload to the message buffer
            messageBuffer.put(buffer, 1, buffer.length - 1);
        }

        log.debug("Packet complete");

        // Parse the message
        return MessageUtils.parse(type, Arrays.copyOfRange(messageBuffer.array(), 0, msgSize));

    }

    /**
     * @param requestedTx The requested tx
     *
     * @return A BWallet transaction type containing an overall description of
     * the current transaction
     */
    public static BWalletType.TransactionType buildTxMetaResponse(Optional<Transaction> requestedTx) {

        int inputCount = requestedTx.get().getInputs().size();
        // TxOutputBinType and TxOutputType counts are the same so ignore hash flag
        int outputCount = requestedTx.get().getOutputs().size();

        // Provide details about the requested transaction
        return BWalletType.TransactionType
                .newBuilder()
                .setVersion((int) requestedTx.get().getVersion())
                .setLockTime((int) requestedTx.get().getLockTime())
                .setInputsCnt(inputCount)
                .setOutputsCnt(outputCount)
                .build();

    }

    /**
     * @param txRequest The BWallet request
     * @param requestedTx The requested tx (either current or a previous one
     * providing inputs)
     * @param binOutputType True if the requested tx is a parent (the receiving
     * address map does not apply)
     * @param receivingAddressPathMap A map of paths for rapid address lookup
     * (called AddressN in BWallet protobuf)
     *
     * @return A BWallet transaction type containing a description of an input
     */
    public static BWalletType.TransactionType buildTxInputResponse(
            TxRequest txRequest,
            Optional<Transaction> requestedTx,
            boolean binOutputType,
            Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap
    ) {

        final Optional<Integer> requestIndex = Optional.fromNullable(txRequest.getDetails().getRequestIndex());
        if (!requestIndex.isPresent()) {
            log.warn("Request index is not present for TxInput");
            return null;
        }

        // Get the transaction input indicated by the request index
        TransactionInput input = requestedTx.get().getInput(requestIndex.get());

        List<Integer> addressN = Lists.newArrayList();
        if (!binOutputType) {
            // We are the current transaction so look up the path of the receiving address
            ImmutableList<ChildNumber> receivingAddressPath = receivingAddressPathMap.get(requestIndex.get());
            Preconditions.checkNotNull(receivingAddressPath, "The receiving address path has no entry for index " + requestIndex.get() + ". Signing will fail.");
            addressN = MessageUtils.buildAddressN(receivingAddressPath);
        }

    // Must be OK to be here
        // Build a TxInputType message
        int prevIndex = (int) input.getOutpoint().getIndex();
        byte[] prevHash = input.getOutpoint().getHash().getBytes();

        // No multisig support in MBHD yet
        BWalletType.InputScriptType inputScriptType = BWalletType.InputScriptType.SPENDADDRESS;

        BWalletType.TxInputType txInputType = BWalletType.TxInputType
                .newBuilder()
                .addAllAddressN(addressN)
                .setSequence((int) input.getSequenceNumber())
                .setScriptSig(ByteString.copyFrom(input.getScriptSig().getProgram()))
                .setScriptType(inputScriptType)
                .setPrevIndex(prevIndex)
                .setPrevHash(ByteString.copyFrom(prevHash))
                .build();

        return BWalletType.TransactionType
                .newBuilder()
                .addInputs(txInputType)
                .build();

    }

    public static BWalletType.TransactionType buildTxInputResponseForMultisig(
            TxRequest txRequest,
            Optional<Transaction> requestedTx,
            boolean binOutputType,
            Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap, 
            List<DeterministicKey> pubkeys, 
            ImmutableList<ChildNumber> basePath, 
            int m, Map<Integer, Map<Integer, byte[]>> signatures
    ) {

        final Optional<Integer> requestIndex = Optional.fromNullable(txRequest.getDetails().getRequestIndex());
        if (!requestIndex.isPresent()) {
            log.warn("Request index is not present for TxInput");
            return null;
        }

        // Get the transaction input indicated by the request index
        TransactionInput input = requestedTx.get().getInput(requestIndex.get());

        List<Integer> addressN = Lists.newArrayList();
        // No multisig support in MBHD yet
        BWalletType.InputScriptType inputScriptType = BWalletType.InputScriptType.SPENDADDRESS;
        
        int prevIndex = (int) input.getOutpoint().getIndex();
        byte[] prevHash = input.getOutpoint().getHash().getBytes();
        
        BWalletType.TxInputType txInputType = null;
        
        
        if (!binOutputType) {
            // We are the current transaction so look up the path of the receiving address
            ImmutableList<ChildNumber> receivingAddressPath = receivingAddressPathMap.get(requestIndex.get());
            Preconditions.checkNotNull(receivingAddressPath, "The receiving address path has no entry for index " + requestIndex.get() + ". Signing will fail.");
            addressN = MessageUtils.buildAddressN(receivingAddressPath);
            
            List<HDNodePathType> nodePaths = pubkeysToNodePaths(pubkeys, addressN);
            
            List<ChildNumber> path = Lists.newArrayList(basePath);
            path.addAll(receivingAddressPath);
            addressN = MessageUtils.buildAddressN(path);
            
            MultisigRedeemScriptType.Builder multisigRedeemScriptBuilder = MultisigRedeemScriptType.newBuilder()
            		.addAllPubkeys(nodePaths)
            		.setM(m);
            Map<Integer, byte[]> partySignatures = signatures.get(requestIndex.get());
            for (int i = 0; i < pubkeys.size(); i++) {
            	if (partySignatures != null && partySignatures.get(i) != null) {
            		multisigRedeemScriptBuilder.addSignatures(ByteString.copyFrom(partySignatures.get(i)));
                } else {
                	multisigRedeemScriptBuilder.addSignatures(ByteString.copyFrom(new byte[0]));
                }
            }
            MultisigRedeemScriptType multisigRedeemScript = multisigRedeemScriptBuilder.build();
            inputScriptType = InputScriptType.SPENDMULTISIG;
            txInputType = BWalletType.TxInputType
                    .newBuilder()
                    .addAllAddressN(addressN)
                    .setSequence((int) input.getSequenceNumber())
                    .setScriptSig(ByteString.copyFrom(input.getScriptSig().getProgram()))
                    .setScriptType(inputScriptType)
                    .setPrevIndex(prevIndex)
                    .setPrevHash(ByteString.copyFrom(prevHash))
                    .setMultisig(multisigRedeemScript)
                    .build();
        } else {
        	txInputType = BWalletType.TxInputType
                    .newBuilder()
                    .addAllAddressN(addressN)
                    .setSequence((int) input.getSequenceNumber())
                    .setScriptSig(ByteString.copyFrom(input.getScriptSig().getProgram()))
                    .setScriptType(inputScriptType)
                    .setPrevIndex(prevIndex)
                    .setPrevHash(ByteString.copyFrom(prevHash))
                    .build();
        }

        // Must be OK to be here
        // Build a TxInputType message

        return BWalletType.TransactionType
                .newBuilder()
                .addInputs(txInputType)
                .build();

    }
    
    public static List<HDNodePathType> pubkeysToNodePaths(List<DeterministicKey> pubkeys, List<Integer> addressN) {
    	List<HDNodePathType> nodePaths = new ArrayList<HDNodePathType>();
    	for (int i = 0; i < pubkeys.size(); i++) {
    		DeterministicKey key = pubkeys.get(i);
    		String hex = Hex.encodeHexString(key.serializePublic(MainNetParams.get()));
    		byte[] bytes = null;
    		try {
				bytes = Hex.decodeHex(hex.substring(10, 18).toCharArray());
			} catch (DecoderException e) {
				throw new IllegalStateException(e);
			}
    		HDNodeType node = HDNodeType.newBuilder().setChainCode(ByteString.copyFrom(key.getChainCode()))
					.setChildNum(key.getChildNumber().getI())
					.setDepth(key.getPath().size())
					//.setFingerprint(key.getFingerprint())
					.setFingerprint(ByteBuffer.wrap(bytes).getInt())
					.setPublicKey(ByteString.copyFrom(key.getPubKey())).build();
			HDNodePathType nodePath = HDNodePathType.newBuilder()
					.setNode(node).addAllAddressN(addressN).build();
			nodePaths.add(nodePath);
    	}
    	return nodePaths;
    }
    
    /**
     * @param txRequest The BWallet request
     * @param requestedTx The requested tx (either current or a previous one
     * providing inputs)
     * @param changeAddressPathMap A map of paths for rapid address lookup
     * (called AddressN in BWallet protobuf)
     *
     * @return A BWallet transaction type containing a description of an output
     */
    public static BWalletType.TransactionType buildTxOutputResponse(
            TxRequest txRequest,
            Optional<Transaction> requestedTx,
            boolean binOutputType,
            Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap) {

        Preconditions.checkNotNull(changeAddressPathMap, "'changeAddressPathMap' must be present");

        final Optional<Integer> requestIndex = Optional.fromNullable(txRequest.getDetails().getRequestIndex());
        if (!requestIndex.isPresent()) {
            log.warn("Request index is not present for TxOutput");
            return null;
        }

        // Get the transaction output indicated by the request index
        TransactionOutput output = requestedTx.get().getOutput(requestIndex.get());

        if (binOutputType) {

      // Build a TxOutputBinType representing a previous transaction
            // Require the output script program
            byte[] scriptPubKey = output.getScriptPubKey().getProgram();

            BWalletType.TxOutputBinType txOutputBinType = BWalletType.TxOutputBinType
                    .newBuilder()
                    .setAmount(output.getValue().value)
                    .setScriptPubkey(ByteString.copyFrom(scriptPubKey))
                    .build();

            return BWalletType.TransactionType
                    .newBuilder()
                    .addBinOutputs(txOutputBinType)
                    .build();

        }

    // Build a TxOutputType representing the current transaction
        // Address
        Address address = output.getAddressFromP2PKHScript(MainNetParams.get());
        
        // TODO xxx
        if (address == null) {
        	System.out.println("getAddressFromP2PKHScript is null, try getAddressFromP2SH");
        	address = output.getAddressFromP2SH(MainNetParams.get());
        }
        
        if (address == null) {
            throw new IllegalArgumentException("TxOutput " + requestIndex + " has no address.");
        }

        // Is it pay-to-script-hash (P2SH) or pay-to-address (P2PKH)?
        final BWalletType.OutputScriptType outputScriptType;
        if (address.isP2SHAddress()) {
            outputScriptType = BWalletType.OutputScriptType.PAYTOSCRIPTHASH;
        } else {
            outputScriptType = BWalletType.OutputScriptType.PAYTOADDRESS;
        }

        final BWalletType.TxOutputType txOutputType;

    // Check for change addresses
        if (changeAddressPathMap.containsKey(address)) {

            Iterable<? extends Integer> addressN = buildAddressN(changeAddressPathMap.get(address));

            // Known change address so it won't trigger a sign confirmation
            txOutputType = BWalletType.TxOutputType
                    .newBuilder()
                    .addAllAddressN(addressN)
                    .setAmount(output.getValue().value)
                    .setScriptType(outputScriptType)
                    .build();

        } else {

            // Unknown address so can expect a sign confirmation
            txOutputType = BWalletType.TxOutputType
                    .newBuilder()
                    .setAddress(String.valueOf(address))
                    .setAmount(output.getValue().value)
                    .setScriptType(outputScriptType)
                    .build();

        }

        return BWalletType.TransactionType
                .newBuilder()
                .addOutputs(txOutputType)
                .build();

    }

    public static BWalletType.TransactionType buildTxOutputResponseForMultisig(
            TxRequest txRequest,
            Optional<Transaction> requestedTx,
            boolean binOutputType,
            Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap, 
            List<DeterministicKey> pubkeys, 
            ImmutableList<ChildNumber> basePath, 
            int m, Map<Integer, Map<Integer, byte[]>> signatures) {

        Preconditions.checkNotNull(changeAddressPathMap, "'changeAddressPathMap' must be present");

        final Optional<Integer> requestIndex = Optional.fromNullable(txRequest.getDetails().getRequestIndex());
        if (!requestIndex.isPresent()) {
            log.warn("Request index is not present for TxOutput");
            return null;
        }

        // Get the transaction output indicated by the request index
        TransactionOutput output = requestedTx.get().getOutput(requestIndex.get());

        if (binOutputType) {
        	// Build a TxOutputBinType representing a previous transaction
            // Require the output script program
            byte[] scriptPubKey = output.getScriptPubKey().getProgram();
            BWalletType.TxOutputBinType txOutputBinType = BWalletType.TxOutputBinType
                    .newBuilder()
                    .setAmount(output.getValue().value)
                    .setScriptPubkey(ByteString.copyFrom(scriptPubKey))
                    .build();
            return BWalletType.TransactionType
                    .newBuilder()
                    .addBinOutputs(txOutputBinType)
                    .build();
        }

        // Build a TxOutputType representing the current transaction
        // Address
        Address address = output.getAddressFromP2PKHScript(MainNetParams.get());
        
        if (address == null) {
        	log.warn("Output is not pay-to-address (P2PKH)");
        	address = output.getAddressFromP2SH(MainNetParams.get());
        }
        
        if (address == null) {
        	log.warn("Output is not pay-to-script-hash (P2SH).");
            throw new IllegalArgumentException("TxOutput " + requestIndex + " has no address.");
        }

        // Is it pay-to-script-hash (P2SH) or pay-to-address (P2PKH)?
        BWalletType.OutputScriptType outputScriptType;
        if (address.isP2SHAddress()) {
            outputScriptType = BWalletType.OutputScriptType.PAYTOSCRIPTHASH;
        } else {
            outputScriptType = BWalletType.OutputScriptType.PAYTOADDRESS;
        }

        final BWalletType.TxOutputType txOutputType;

        // Check for change addresses
        if (changeAddressPathMap.containsKey(address)) {
        	ImmutableList<ChildNumber> changeAddressPath = changeAddressPathMap.get(address);
            List<Integer> addressN = buildAddressN(changeAddressPath);
            
            List<HDNodePathType> nodePaths = pubkeysToNodePaths(pubkeys, addressN);
            
            List<ChildNumber> path = Lists.newArrayList(basePath);
            path.addAll(changeAddressPath);
            addressN = MessageUtils.buildAddressN(path);
            
            MultisigRedeemScriptType.Builder multisigRedeemScriptBuilder = MultisigRedeemScriptType.newBuilder()
            		.addAllPubkeys(nodePaths)
            		.setM(m);
            // TODO add signature ?
            /*
            Map<Integer, byte[]> partySignatures = signatures.get(requestIndex.get());
            for (int i = 0; i < pubkeys.size(); i++) {
            	if (partySignatures != null && partySignatures.get(i) != null) {
            		multisigRedeemScriptBuilder.addSignatures(ByteString.copyFrom(partySignatures.get(i)));
                } else {
                	multisigRedeemScriptBuilder.addSignatures(ByteString.copyFrom(new byte[0]));
                }
            }
            */
            MultisigRedeemScriptType multisigRedeemScript = multisigRedeemScriptBuilder.build();
            outputScriptType = OutputScriptType.PAYTOMULTISIG;
            txOutputType = BWalletType.TxOutputType
                    .newBuilder()
                    .addAllAddressN(addressN)
                    .setAmount(output.getValue().value)
                    .setScriptType(outputScriptType)
                    .setMultisig(multisigRedeemScript)
                    .build();

        } else {
            // Unknown address so can expect a sign confirmation
            txOutputType = BWalletType.TxOutputType
                    .newBuilder()
                    .setAddress(String.valueOf(address))
                    .setAmount(output.getValue().value)
                    .setScriptType(outputScriptType)
                    .build();
        }

        return BWalletType.TransactionType
                .newBuilder()
                .addOutputs(txOutputType)
                .build();
    }
    
    /**
     * <p>
     * Build an AddressN chain code structure</p>
     *
     * @param account The plain account number (0 gives maximum compatibility)
     * @param keyPurpose The key purpose
     * (RECEIVE_FUNDS,CHANGE,REFUND,AUTHENTICATION etc)
     * @param index The plain index of the required address
     *
     * @return The list representing the chain code (only a simple chain is
     * currently supported)
     */
    public static List<Integer> buildAddressN(int account, KeyChain.KeyPurpose keyPurpose, int index) {
        int keyPurposeAddressN = 0;
        switch (keyPurpose) {
            case RECEIVE_FUNDS:
            case REFUND:
                keyPurposeAddressN = 0;
                break;
            case CHANGE:
            case AUTHENTICATION:
                keyPurposeAddressN = 1;
                break;
        }

        return Lists.newArrayList(
                44 | ChildNumber.HARDENED_BIT,
                ChildNumber.HARDENED_BIT,
                account | ChildNumber.HARDENED_BIT,
                keyPurposeAddressN,
                index
        );
    }

    /**
     * <p>
     * Build an AddressN chain code structure</p>
     *
     * @param receivingAddressPath The Bitcoinj receiving address path
     *
     * @return The list representing the chain code (only a simple chain is
     * currently supported)
     */
    public static List<Integer> buildAddressN(List<ChildNumber> receivingAddressPath) {

        List<Integer> addressN = Lists.newArrayList();

        for (ChildNumber childNumber : receivingAddressPath) {

            addressN.add(childNumber.getI());

        }

        return addressN;
    }

}
