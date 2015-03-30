/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.protobuf.BWalletMessage.TxRequest;
import com.bdx.bwallet.protobuf.BWalletType;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.utils.TransactionUtils;
import com.bdx.bwallet.tools.core.utils.MessageUtils;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.KeyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dean Liu
 */
public abstract class AbstractWalletClient implements WalletClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractWalletClient.class);

    @Override
    public Optional<MessageEvent> initialise() {
        return sendMessage(
                BWalletMessage.Initialize
                .newBuilder()
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> ping() {
        return sendMessage(
                BWalletMessage.Ping
                .newBuilder()
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> clearSession() {
        return sendMessage(
                BWalletMessage.ClearSession
                .newBuilder()
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> changePIN(boolean remove) {

        return sendMessage(
                BWalletMessage.ChangePin
                .newBuilder()
                .setRemove(remove)
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> wipeDevice() {
        return sendMessage(
                BWalletMessage.WipeDevice
                .newBuilder()
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> firmwareErase() {
        return sendMessage(
                BWalletMessage.FirmwareErase
                .newBuilder()
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> firmwareUpload(byte[] payload) {
        return sendMessage(
                BWalletMessage.FirmwareUpload
                .newBuilder()
                .setPayload(ByteString.copyFrom(payload))
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> getEntropy() {
        return sendMessage(
                BWalletMessage.GetEntropy
                .newBuilder()
                .build()
        );
    }

    // TODO Support use of seed node
    @Override
    public Optional<MessageEvent> loadDevice(
            String language,
            String label,
            String seedPhrase,
            String pin
    ) {

        // A load normally takes about 10 seconds to complete
        return sendMessage(
                BWalletMessage.LoadDevice
                .newBuilder()
                .setLanguage(language)
                .setLabel(label)
                .setMnemonic(seedPhrase)
                //        .setNode(nodeType)
                .setPin(pin)
                .build()
        );

    }

    @Override
    public Optional<MessageEvent> resetDevice(
            String language,
            String label,
            boolean displayRandom,
            boolean pinProtection,
            boolean passphraseProtection,
            int strength
    ) {

        return sendMessage(
                BWalletMessage.ResetDevice
                .newBuilder()
                .setLanguage(language)
                .setLabel(label)
                .setDisplayRandom(displayRandom)
                .setStrength(strength)
                .setPinProtection(pinProtection)
                .setPassphraseProtection(passphraseProtection)
                .build()
        );

    }

    @Override
    public Optional<MessageEvent> recoverDevice(
            String language,
            String label,
            int wordCount,
            boolean passphraseProtection,
            boolean pinProtection,
            boolean enforceWordlist
    ) {
        return sendMessage(
                BWalletMessage.RecoveryDevice
                .newBuilder()
                .setLanguage(language)
                .setLabel(label)
                .setWordCount(wordCount)
                .setPassphraseProtection(passphraseProtection)
                .setPinProtection(pinProtection)
                .setEnforceWordlist(enforceWordlist)
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> wordAck(String word) {
        return sendMessage(
                BWalletMessage.WordAck
                .newBuilder()
                .setWord(word)
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> signTx(Transaction tx) {

        return sendMessage(
                BWalletMessage.SignTx
                .newBuilder()
                .setCoinName("Bitcoin")
                .setInputsCount(tx.getInputs().size())
                .setOutputsCount(tx.getOutputs().size())
                .build()
        );

    }

    @Override
    public Optional<MessageEvent> simpleSignTx(Transaction tx) {

        BWalletMessage.SimpleSignTx.Builder builder = BWalletMessage.SimpleSignTx.newBuilder();
        builder.setCoinName("Bitcoin");

        // Explore the current tx inputs
        for (TransactionInput input : tx.getInputs()) {

            // Build a TxInputType message
            int prevIndex = (int) input.getOutpoint().getIndex();
            byte[] prevHash = input.getOutpoint().getHash().getBytes();

            // No multisig support in MBHD yet
            BWalletType.InputScriptType inputScriptType = BWalletType.InputScriptType.SPENDADDRESS;

            BWalletType.TxInputType txInputType = BWalletType.TxInputType
                    .newBuilder()
                    .setSequence((int) input.getSequenceNumber())
                    .setScriptSig(ByteString.copyFrom(input.getScriptSig().getProgram()))
                    .setScriptType(inputScriptType)
                    .setPrevIndex(prevIndex)
                    .setPrevHash(ByteString.copyFrom(prevHash))
                    .build();

            builder.addInputs(txInputType);
        }

        // Explore the current tx outputs
        for (TransactionOutput output : tx.getOutputs()) {

            // Build a TxOutputType message
            // Address
            Address address = output.getAddressFromP2PKHScript(MainNetParams.get());
            if (address == null) {
                throw new IllegalArgumentException("'address' must be present");
            }
            // Is it pay-to-script-hash (P2SH) or pay-to-address (P2PKH)?
            final BWalletType.OutputScriptType outputScriptType;
            if (address.isP2SHAddress()) {
                outputScriptType = BWalletType.OutputScriptType.PAYTOSCRIPTHASH;
            } else {
                outputScriptType = BWalletType.OutputScriptType.PAYTOADDRESS;
            }

            BWalletType.TxOutputType txOutputType = BWalletType.TxOutputType
                    .newBuilder()
                    .setAddress(String.valueOf(address))
                    .setAmount(output.getValue().value)
                    .setScriptType(outputScriptType)
                    .build();

            builder.addOutputs(txOutputType);
        }

        // Explore the current tx inputs
        for (TransactionInput input : tx.getInputs()) {

            // Get the previous Tx
            Transaction prevTx = input.getOutpoint().getConnectedOutput().getParentTransaction();

            // No multisig support in MBHD yet
            BWalletType.TransactionType.Builder prevBuilder = BWalletType.TransactionType.newBuilder();

            // Explore the current tx inputs
            for (TransactionInput prevInput : prevTx.getInputs()) {

                // Build a TxInputType message
                int prevIndex = (int) prevInput.getOutpoint().getIndex();
                byte[] prevHash = prevInput.getOutpoint().getHash().getBytes();

                // No multisig support in MBHD yet
                BWalletType.InputScriptType inputScriptType = BWalletType.InputScriptType.SPENDADDRESS;

                BWalletType.TxInputType txInputType = BWalletType.TxInputType
                        .newBuilder()
                        .setSequence((int) prevInput.getSequenceNumber())
                        .setScriptSig(ByteString.copyFrom(prevInput.getScriptSig().getProgram()))
                        .setScriptType(inputScriptType)
                        .setPrevIndex(prevIndex)
                        .setPrevHash(ByteString.copyFrom(prevHash))
                        .build();

                prevBuilder.addInputs(txInputType);
            }

            // Explore the current tx outputs
            for (TransactionOutput prevOutput : prevTx.getOutputs()) {

                // Build a TxOutputType message
                BWalletType.TxOutputBinType txOutputBinType = BWalletType.TxOutputBinType
                        .newBuilder()
                        .setAmount(prevOutput.getValue().value)
                        .setScriptPubkey(ByteString.copyFrom(prevOutput.getScriptPubKey().getProgram()))
                        .build();

                prevBuilder.addBinOutputs(txOutputBinType);
            }

            builder.addTransactions(prevBuilder.build());
        }

        // Send the message
        return sendMessage(builder.build());
    }

    @Override
    public Optional<MessageEvent> txAck(
            TxRequest txRequest,
            Transaction tx,
            Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap,
            Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap) {

        BWalletType.TransactionType txType = null;

        // Get the transaction hash (if present)
        Optional<byte[]> txHash = Optional.of(txRequest.getDetails().getTxHash().toByteArray());

        // Assume we're working with the current (child) transaction to start with
        Optional<Transaction> requestedTx = Optional.of(tx);

        // Check if the requested transaction is different to the current
        boolean binOutputType = txHash.isPresent();
        if (binOutputType) {
            // Need to look up a transaction by hash
            requestedTx = TransactionUtils.getTransactionByHash(tx, txHash.get());

            // Check if the transaction was found
            if (!requestedTx.isPresent()) {
                log.error("Device requested unknown hash: {}", Utils.HEX.encode(txHash.get()));
                throw new IllegalArgumentException("Device requested unknown hash.");
            }

        }

        // Have the required transaction at this point
        switch (txRequest.getRequestType()) {
            case TXMETA:
                txType = MessageUtils.buildTxMetaResponse(requestedTx);
                break;
            case TXINPUT:
                txType = MessageUtils.buildTxInputResponse(txRequest, requestedTx, binOutputType, receivingAddressPathMap);
                break;
            case TXOUTPUT:
                txType = MessageUtils.buildTxOutputResponse(txRequest, requestedTx, binOutputType, changeAddressPathMap);
                break;
            case TXFINISHED:
                log.info("TxSign workflow complete.");
                break;
            default:
                log.error("Unknown TxReturnType: {}", txRequest.getRequestType().name());
                return Optional.absent();
        }

        // Must have fully processed the message to be here
        if (txType != null) {
            return sendMessage(
                    BWalletMessage.TxAck
                    .newBuilder()
                    .setTx(txType)
                    .build()
            );
        }

        log.info("No TxAck required.");

        return Optional.absent();

    }

    @Override
    public Optional<MessageEvent> pinMatrixAck(String pin) {
        return sendMessage(
                BWalletMessage.PinMatrixAck
                .newBuilder()
                .setPin(pin)
                .build(),
                // No immediate response expected
                2, TimeUnit.SECONDS
        );
    }

    @Override
    public Optional<MessageEvent> buttonAck() {
        return sendMessage(
                BWalletMessage.ButtonAck
                .newBuilder()
                .build(),
                // No immediate response expected
                2, TimeUnit.SECONDS
        );
    }

    public Optional<MessageEvent> cancel() {
        return sendMessage(
                BWalletMessage.Cancel
                .newBuilder()
                .build(),
                // No immediate response expected
                2, TimeUnit.SECONDS);
    }

    @Override
    public Optional<MessageEvent> getAddress(
            int account,
            KeyChain.KeyPurpose keyPurpose,
            int index,
            boolean showDisplay
    ) {

        return sendMessage(BWalletMessage.GetAddress
                .newBuilder()
                // Build the chain code
                .addAllAddressN(MessageUtils.buildAddressN(account, keyPurpose, index))
                .setCoinName("Bitcoin")
                .setShowDisplay(showDisplay)
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> getPublicKey(
            int account,
            KeyChain.KeyPurpose keyPurpose,
            int index
    ) {

        return sendMessage(BWalletMessage.GetPublicKey
                .newBuilder()
                // Build the chain code
                .addAllAddressN(MessageUtils.buildAddressN(account, keyPurpose, index))
                .build()
        );

    }

    @Override
    public Optional<MessageEvent> getDeterministicHierarchy(List<ChildNumber> childNumbers) {

        List<Integer> addressN = Lists.newArrayList();
        for (ChildNumber childNumber : childNumbers) {
            addressN.add(childNumber.getI());
        }

        return sendMessage(
                BWalletMessage.GetPublicKey
                .newBuilder()
                .addAllAddressN(addressN)
                .build()
        );

    }

    @Override
    public Optional<MessageEvent> applySettings(String language, String label) {

        return sendMessage(
                BWalletMessage.ApplySettings
                .newBuilder()
                .setLanguage(language)
                .setLabel(label)
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> entropyAck(byte[] entropy) {
        return sendMessage(
                BWalletMessage.EntropyAck
                .newBuilder()
                .setEntropy(ByteString.copyFrom(entropy))
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> signMessage(
            int account,
            KeyChain.KeyPurpose keyPurpose,
            int index,
            byte[] message
    ) {
        return sendMessage(BWalletMessage.SignMessage
                .newBuilder()
                // Build the chain code
                .addAllAddressN(MessageUtils.buildAddressN(account, keyPurpose, index))
                .setCoinName("Bitcoin")
                .setMessage(ByteString.copyFrom(message))
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> verifyMessage(
            Address address,
            byte[] signature,
            byte[] message
    ) {
        return sendMessage(
                BWalletMessage.VerifyMessage
                .newBuilder()
                .setAddress(address.toString())
                .setSignature(ByteString.copyFrom(signature))
                .setMessage(ByteString.copyFrom(message))
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> encryptMessage(
            byte[] pubKey,
            byte[] message,
            boolean displayOnly
    ) {
        return sendMessage(
                BWalletMessage.EncryptMessage
                .newBuilder()
                .setPubkey(ByteString.copyFrom(pubKey))
                .setMessage(ByteString.copyFrom(message))
                .setDisplayOnly(displayOnly)
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> decryptMessage(
            int account,
            KeyChain.KeyPurpose keyPurpose,
            int index,
            byte[] message
    ) {
        return sendMessage(BWalletMessage.DecryptMessage
                .newBuilder()
                // Build the chain code
                .addAllAddressN(MessageUtils.buildAddressN(account, keyPurpose, index))
                .setMessage(ByteString.copyFrom(message))
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> cipherKeyValue(
            int account,
            KeyChain.KeyPurpose keyPurpose,
            int index,
            byte[] key,
            byte[] keyValue,
            boolean isEncrypting,
            boolean askOnDecrypt,
            boolean askOnEncrypt
    ) {
        return sendMessage(BWalletMessage.CipherKeyValue
                .newBuilder()
                // Build the chain code
                .addAllAddressN(MessageUtils.buildAddressN(account, keyPurpose, index))
                .setAskOnDecrypt(askOnDecrypt)
                .setAskOnEncrypt(askOnEncrypt)
                .setEncrypt(isEncrypting)
                .setKeyBytes(ByteString.copyFrom(key))
                .setValue(ByteString.copyFrom(keyValue))
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> estimateTxSize(Transaction tx) {

        int inputsCount = tx.getInputs().size();
        int outputsCount = tx.getOutputs().size();

        return sendMessage(
                BWalletMessage.EstimateTxSize
                .newBuilder()
                .setCoinName("Bitcoin")
                .setInputsCount(inputsCount)
                .setOutputsCount(outputsCount)
                .build()
        );
    }

    @Override
    public Optional<MessageEvent> passphraseAck(String passphrase) {
        return sendMessage(
                BWalletMessage.PassphraseAck
                .newBuilder()
                .setPassphrase(passphrase)
                .build()
        );
    }
    
    @Override
    public Optional<MessageEvent> testScreen(int delayTime) {
        return sendMessage(
                BWalletMessage.TestScreen
                .newBuilder()
                .setDelayTime(delayTime)
                .build()
        );
    }
    
    /**
     * <p>
     * Send a message to the device that should have a near-immediate (under 5
     * second) response.</p>
     * <p>
     * If the response times out a FAILURE message should be generated.</p>
     *
     * @param message The message to send to the hardware wallet
     *
     * @return An optional low level message event, present only in blocking
     * implementations
     */
    protected abstract Optional<MessageEvent> sendMessage(Message message);

    /**
     * <p>
     * Send a message to the device with an arbitrary response duration.</p>
     * <p>
     * If the response times out a FAILURE message should be generated.</p>
     *
     * @param message The message to send to the hardware wallet
     * @param duration The duration to wait before returning
     * @param timeUnit The time unit
     *
     * @return An optional low level message event, present only in blocking
     * implementations
     */
    protected abstract Optional<MessageEvent> sendMessage(Message message, int duration, TimeUnit timeUnit);
}
