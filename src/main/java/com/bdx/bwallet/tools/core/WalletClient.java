/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core;

import java.util.List;
import java.util.Map;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.KeyChain;

import com.bdx.bwallet.protobuf.BWalletMessage.TxRequest;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.wallets.Connectable;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

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
            String pin,
            boolean passphraseProtection,
            boolean skipChecksum
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

    Optional<MessageEvent> multiTxAck(TxRequest txRequest, Transaction tx, Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap, Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap, List<DeterministicKey> pubkeys, ImmutableList<ChildNumber> multisigBasePath, int multisigM, Map<Integer, Map<Integer, byte[]>> multisigSignatures);
    
    Optional<MessageEvent> pinMatrixAck(String pin);

    Optional<MessageEvent> cancel();

    Optional<MessageEvent> buttonAck();

    Optional<MessageEvent> applySettings(String language, String label);

    Optional<MessageEvent> applySettings(boolean usePassphrase);
    
    Optional<MessageEvent> applySettings(byte[] homescreen);
    
    public Optional<MessageEvent> getAddress(int account, KeyChain.KeyPurpose keyPurpose, int index, boolean showDisplay);

    public Optional<MessageEvent> getAddress(List<ChildNumber> childNumbers, boolean showDisplay);
    
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
    
    Optional<MessageEvent> testScreen(int delayTime);
    
    Optional<MessageEvent> getAccountLabels(String coinName, boolean all, int index);
    
    Optional<MessageEvent> setAccountLabel(String coinName, int index, String label);
    
    Optional<MessageEvent> removeAccountLabel(String coinName, int index);
}
