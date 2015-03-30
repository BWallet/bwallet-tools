/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core;

import com.bdx.bwallet.protobuf.BWalletMessage.TxRequest;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.wallets.Connectable;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.KeyChain;

/**
 *
 * @author Administrator
 */
public interface WalletClient extends Connectable {

    Optional<MessageEvent> initialise();

    Optional<MessageEvent> ping();

    Optional<MessageEvent> clearSession();

    Optional<MessageEvent> changePIN(boolean remove);

    Optional<MessageEvent> wipeDevice();

    Optional<MessageEvent> firmwareErase();

    Optional<MessageEvent> firmwareUpload(byte[] payload);

    Optional<MessageEvent> getEntropy();

    Optional<MessageEvent> loadDevice(
            String language,
            String label,
            String seedPhrase,
            String pin
    );

    Optional<MessageEvent> resetDevice(
            String language,
            String label,
            boolean displayRandom,
            boolean pinProtection,
            boolean passphraseProtection,
            int strength
    );

    Optional<MessageEvent> recoverDevice(
            String language,
            String label,
            int wordCount,
            boolean passphraseProtection,
            boolean pinProtection,
            boolean enforceWordlist
    );

    Optional<MessageEvent> wordAck(String word);

    public Optional<MessageEvent> signTx(Transaction tx);

    public Optional<MessageEvent> simpleSignTx(Transaction tx);

    Optional<MessageEvent> txAck(TxRequest txRequest, Transaction tx, Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap, Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap);

    Optional<MessageEvent> pinMatrixAck(String pin);

    Optional<MessageEvent> cancel();

    Optional<MessageEvent> buttonAck();

    Optional<MessageEvent> applySettings(String language, String label);

    public Optional<MessageEvent> getAddress(int account, KeyChain.KeyPurpose keyPurpose, int index, boolean showDisplay);

    Optional<MessageEvent> getPublicKey(int account, KeyChain.KeyPurpose keyPurpose, int index);

    Optional<MessageEvent> getDeterministicHierarchy(List<ChildNumber> childNumbers);

    Optional<MessageEvent> entropyAck(byte[] entropy);

    Optional<MessageEvent> signMessage(int account, KeyChain.KeyPurpose keyPurpose, int index, byte[] message);

    Optional<MessageEvent> verifyMessage(Address address, byte[] signature, byte[] message);

    Optional<MessageEvent> encryptMessage(byte[] pubKey, byte[] message, boolean displayOnly);

    Optional<MessageEvent> decryptMessage(int account, KeyChain.KeyPurpose keyPurpose, int index, byte[] message);

    Optional<MessageEvent> cipherKeyValue(
            int account,
            KeyChain.KeyPurpose keyPurpose,
            int index,
            byte[] key,
            byte[] keyValue,
            boolean isEncrypting,
            boolean askOnDecrypt,
            boolean askOnEncrypt
    );

    Optional<MessageEvent> estimateTxSize(Transaction tx);
    
    Optional<MessageEvent> passphraseAck(String passphrase);
}
