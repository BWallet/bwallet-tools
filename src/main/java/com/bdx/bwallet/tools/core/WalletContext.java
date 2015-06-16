/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.wallet.KeyChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bdx.bwallet.protobuf.BWalletMessage.Features;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.flows.ContextUseCase;
import com.bdx.bwallet.tools.core.flows.WalletFlow;
import com.bdx.bwallet.tools.core.flows.WalletFlows;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * 
 * @author Dean Liu
 */
public class WalletContext {

	private static final Logger log = LoggerFactory.getLogger(WalletContext.class);

	/**
	 * The hardware wallet client handling outgoing messages and generating low
	 * level message events
	 */
	private final WalletClient client;

	private WalletFlow currentFlow = WalletFlows.newBlankFlow();

	private ContextUseCase currentUseCase = ContextUseCase.BLANK;

	private Optional<Features> features = Optional.absent();

	private SettableFuture<Optional<Features>> future = null;

	/**
	 * Provide the transaction forming the basis for the "sign transaction" use
	 * case
	 */
	private Optional<Transaction> transaction = Optional.absent();
	/**
	 * Keep track of all the signatures for the "sign transaction" use case
	 */
	private Map<Integer, byte[]> signatures = Maps.newHashMap();

	/**
	 * Keep track of any transaction serialization bytes coming back from the
	 * device
	 */
	private ByteArrayOutputStream serializedTx = new ByteArrayOutputStream();

	/**
	 * A map keyed on TxInput index and the associated path to the receiving
	 * address on that input This is used during Trezor transaction signing for
	 * fast addressN lookup
	 */
	private Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap;

	/**
	 * A map keyed on TxOutput Address and the associated path to the change
	 * address on that output This is used during Trezor transaction signing for
	 * fast addressN lookup, removes the need to confirm a change address and
	 * ensures the transaction balance is correct on the display
	 */
	private Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap;

	// TODO clear in reset all
	private List<DeterministicKey> multisigPubkeys;
	
	private int multisigM;
	
	private Map<Integer, Map<Integer, byte[]>> multisigSignatures;
	
	private ImmutableList<ChildNumber> multisigBasePath;
	
	public WalletContext(WalletClient client) {
		this.client = client;
		// Ensure the service is subscribed to low level message events from the
		// client
		MessageEvents.subscribe(this);
	}

	/**
	 * @param event
	 *            The low level message event
	 */
	@Subscribe
	public void onMessageEvent(MessageEvent event) {
		log.debug("Received message event: '{}'", event.getEventType().name());
		if (future != null && !future.isDone()) {
			if (event.getEventType() == MessageEventType.FEATURES) {
				features = Optional.of((Features) event.getMessage().get());
				future.set(features);
			}
		} else {
			// Perform a state transition as a result of this event
			try {
				currentFlow.transition(client, this, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public WalletFlow getFlow() {
		return currentFlow;
	}

	public ContextUseCase getCurrentUseCase() {
		return currentUseCase;
	}

	public WalletClient getClient() {
		return client;
	}

	public Optional<Features> getFeatures() {
		return features;
	}

	public boolean connect() {
		return client.connect();
	}

	public ListenableFuture<Optional<Features>> initialise() {
		this.future = SettableFuture.<Optional<Features>> create();
		client.initialise();
		return this.future;
	}

	public void destroy() {
		MessageEvents.unsubscribe(this);
		client.hardDetach();
	}

	/**
	 * <p>
	 * Reset all context state to ensure a fresh context
	 * </p>
	 */
	private void resetAll() {
		currentFlow = WalletFlows.newBlankFlow();
		currentUseCase = ContextUseCase.BLANK;
		features = Optional.absent();
		transaction = Optional.absent();
		signatures = Maps.newHashMap();
		serializedTx = new ByteArrayOutputStream();
		receivingAddressPathMap = Maps.newHashMap();
		changeAddressPathMap = Maps.newHashMap();
	}

	public void resetAllButFeatures() {
		Optional<Features> originalFeatures = this.features;

	    resetAll();

	    this.features = originalFeatures;
	}

	public void beginWipeDeviceUseCase() {
		log.debug("Begin 'wipe device' use case");
		// Track the use case
		currentUseCase = ContextUseCase.WIPE_DEVICE;
		// Set the event receiving state
		currentFlow = WalletFlows.newWipeDeviceFlow();
		// Issue starting message to elicit the event
		client.wipeDevice();
	}

	public void beginFirmwareEraseUseCase() {
		log.debug("Begin 'firmware erase' use case");
		// Track the use case
		currentUseCase = ContextUseCase.FIRMWARE_ERASE;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newFirmwareEraseFlow();
		// Issue starting message to elicit the event
		client.firmwareErase();
	}

	public void beginFirmwareUploadUseCase(byte[] payload) {
		log.debug("Begin 'firmware upload' use case");
		// Track the use case
		currentUseCase = ContextUseCase.FIRMWARE_UPLOAD;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newFirmwareUploadFlow();
		// Issue starting message to elicit the event
		client.firmwareUpload(payload);
	}

	public void beginCreateWalletUseCase(String language, String label, boolean displayRandom, boolean pinProtection, boolean passphraseProtection, int strength) {
		log.debug("Begin 'create wallet' use case");
		// Track the use case
		currentUseCase = ContextUseCase.CREATE_WALLET;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newCreateWalletFlow();
		// Issue starting message to elicit the event
		client.resetDevice(language, label, displayRandom, pinProtection, passphraseProtection, strength);
	}

	public void beginRecoverDeviceUseCase(String language, String label, int wordCount, boolean passphraseProtection, boolean pinProtection,
			boolean enforceWordlist) {
		log.debug("Begin 'recover device' use case");
		// Track the use case
		currentUseCase = ContextUseCase.RECOVERY_DEVICE;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newRecoveryDeviceFlow();
		// Issue starting message to elicit the event
		client.recoverDevice(language, label, wordCount, passphraseProtection, pinProtection, enforceWordlist);
	}

	public void beginApplySettingsUseCase(String language, String label) {
		log.debug("Begin 'apply settings' use case");
		// Track the use case
		currentUseCase = ContextUseCase.APPLY_SETTINGS;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newApplySettingsFlow();
		// Issue starting message to elicit the event
		client.applySettings(language, label);
	}

	public void beginApplySettingsUseCase(boolean usePassphrase) {
		log.debug("Begin 'apply settings' use case");
		// Track the use case
		currentUseCase = ContextUseCase.APPLY_SETTINGS;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newApplySettingsFlow();
		// Issue starting message to elicit the event
		client.applySettings(usePassphrase);
	}

	public void beginApplySettingsUseCase(byte[] homescreen) {
		log.debug("Begin 'apply settings' use case");
		// Track the use case
		currentUseCase = ContextUseCase.APPLY_SETTINGS;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newApplySettingsFlow();
		// Issue starting message to elicit the event
		client.applySettings(homescreen);
	}

	public void beginGetDeterministicHierarchyUseCase(List<ChildNumber> childNumbers) {
		log.debug("Begin 'get deterministic hierarchy' use case");
		// Track the use case
		currentUseCase = ContextUseCase.REQUEST_DETERMINISTIC_HIERARCHY;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newGetDeterministicHierarchyFlow();
		// Issue starting message to elicit the event
		client.getDeterministicHierarchy(childNumbers);
	}

	public void beginSignMessageUseCase(int account, KeyChain.KeyPurpose keyPurpose, int index, byte[] message) {
		log.debug("Begin 'sign message' use case");
		// Track the use case
		currentUseCase = ContextUseCase.SIGN_MESSAGE;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newSignMessageFlow();
		// Issue starting message to elicit the event
		client.signMessage(account, keyPurpose, index, message);
	}

	public void beginVerifyMessageUseCase(Address address, byte[] signature, byte[] message) {
		log.debug("Begin 'verify message' use case");
		// Track the use case
		currentUseCase = ContextUseCase.VERIFY_MESSAGE;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newVerifyMessageFlow();
		// Issue starting message to elicit the event
		client.verifyMessage(address, signature, message);
	}

	public void beginChangePINUseCase(boolean remove) {
		log.debug("Begin 'change pin' use case");
		// Track the use case
		currentUseCase = ContextUseCase.CHANGE_PIN;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newChangePINFlow();
		// Issue starting message to elicit the event
		client.changePIN(remove);
	}

	public void beginTestScreenUseCase(int delayTime) {
		log.debug("Begin 'test screen' use case");
		// Track the use case
		currentUseCase = ContextUseCase.TEST_SCREEN;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newTestScreenFlow();
		// Issue starting message to elicit the event
		client.testScreen(delayTime);
	}

	public void beginGetAccountLabelsUseCase(String coinName, boolean all, int index) {
		log.debug("Begin 'get account labels' use case");
		// Track the use case
		currentUseCase = ContextUseCase.GET_ACCOUNT_LABELS;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newGetAccountLabelsFlow();
		// Issue starting message to elicit the event
		client.getAccountLabels(coinName, all, index);
	}

	public void beginSetAccountLabelUseCase(String coinName, int index, String label) {
		log.debug("Begin 'set account label' use case");
		// Track the use case
		currentUseCase = ContextUseCase.SET_ACCOUNT_LABEL;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newSetAccountLabelFlow();
		// Issue starting message to elicit the event
		client.setAccountLabel(coinName, index, label);
	}

	public void beginRemoveAccountLabelUseCase(String coinName, int index) {
		log.debug("Begin 'set account label' use case");
		// Track the use case
		currentUseCase = ContextUseCase.SET_ACCOUNT_LABEL;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newSetAccountLabelFlow();
		// Issue starting message to elicit the event
		client.removeAccountLabel(coinName, index);
	}

	public void beginGetAddressUseCase(List<ChildNumber> childNumbers, boolean showDisplay) {
		log.debug("Begin 'get address' use case");
		// Track the use case
		currentUseCase = ContextUseCase.REQUEST_ADDRESS;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newGetAddressFlow();
		// Issue starting message to elicit the event
		client.getAddress(childNumbers, showDisplay);
	}

	public void beginGetAddressUseCase(int account, KeyChain.KeyPurpose keyPurpose, int index, boolean showDisplay) {
		log.debug("Begin 'get address' use case");
		// Track the use case
		currentUseCase = ContextUseCase.REQUEST_ADDRESS;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newGetAddressFlow();
		// Issue starting message to elicit the event
		client.getAddress(account, keyPurpose, index, showDisplay);
	}

	public void beginLoadDeviceUseCase(String language, String label, String seedPhrase, String pin, boolean passphraseProtection, boolean skipChecksum) {
		log.debug("Begin 'load device' use case");
		// Track the use case
		currentUseCase = ContextUseCase.LOAD_WALLET;
		// Store the overall context parameters
		// Set the event receiving state
		currentFlow = WalletFlows.newLoadDeviceFlow();
		// Issue starting message to elicit the event
		client.loadDevice(language, label, seedPhrase, pin, passphraseProtection, skipChecksum);
	}

	/**
	 * <p>
	 * Begin the "sign transaction" use case
	 * </p>
	 * 
	 * @param transaction
	 *            The transaction containing the inputs and outputs
	 * @param receivingAddressPathMap
	 *            The map of paths for our receiving addresses
	 * @param changeAddressPathMap
	 *            The map paths for our change address
	 */
	public void beginSignTxUseCase(Transaction transaction, Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap,
			Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap) {

		log.debug("Begin 'sign transaction' use case");

		// Clear relevant information
		resetAllButFeatures();

		// Track the use case
		currentUseCase = ContextUseCase.SIGN_TX;

		// Store the overall context parameters
		this.transaction = Optional.of(transaction);
		this.receivingAddressPathMap = receivingAddressPathMap;
		this.changeAddressPathMap = changeAddressPathMap;

		// Set the event receiving state
		currentFlow = WalletFlows.newSignTxFlow();

		// Issue starting message to elicit the event
		client.signTx(transaction);

	}

	public void beginMultiSignTxUseCase(Transaction transaction, Map<Integer, ImmutableList<ChildNumber>> receivingAddressPathMap,
			Map<Address, ImmutableList<ChildNumber>> changeAddressPathMap, List<DeterministicKey> multisigPubkeys, ImmutableList<ChildNumber> multisigBasePath, 
			int multisigM, Map<Integer, Map<Integer, byte[]>> multisigSignatures) {

		log.debug("Begin 'multi-sign transaction' use case");

		// TODO check multisigPubkeys and multisigM
		
		// Clear relevant information
		resetAllButFeatures();

		// Track the use case
		currentUseCase = ContextUseCase.MULTI_SIGN_TX;

		// Store the overall context parameters
		this.transaction = Optional.of(transaction);
		this.receivingAddressPathMap = receivingAddressPathMap;
		this.changeAddressPathMap = changeAddressPathMap;
		this.multisigPubkeys = multisigPubkeys;
		this.multisigM = multisigM;
		this.multisigSignatures = multisigSignatures;
		this.multisigBasePath = multisigBasePath;
		
		// Set the event receiving state
		currentFlow = WalletFlows.newMultiSignTxFlow();

		// Issue starting message to elicit the event
		client.signTx(transaction);

	}
	
	/**
	 * @return The transaction for the "sign transaction" use case
	 */
	public Optional<Transaction> getTransaction() {
		return transaction;
	}

	/**
	 * <p>
	 * Note: When adding this signature using the builder setScriptSig() you'll
	 * need to append the SIGHASH 0x01 byte
	 * </p>
	 * 
	 * @return The map of ECDSA signatures provided by the device during
	 *         "sign transaction" keyed on the input index
	 */
	public Map<Integer, byte[]> getSignatures() {
		return signatures;
	}

	/**
	 * This value cannot be considered complete until the
	 * "SHOW_OPERATION_SUCCESSFUL" message has been received since it can be
	 * built up over several incoming messages
	 * 
	 * @return The serialized transaction provided by the device during
	 *         "sign transaction"
	 */
	public ByteArrayOutputStream getSerializedTx() {
		return serializedTx;
	}

	/**
	 * @return The map of paths for our receiving addresses on the current
	 *         transaction (key input index, value deterministic path to
	 *         receiving address)
	 */
	public Map<Integer, ImmutableList<ChildNumber>> getReceivingAddressPathMap() {
		return receivingAddressPathMap;
	}

	/**
	 * @return The map of paths for the change address (key address, value
	 *         deterministic path to change address)
	 */
	public Map<Address, ImmutableList<ChildNumber>> getChangeAddressPathMap() {
		return changeAddressPathMap;
	}

	public List<DeterministicKey> getPubkeys() {
		return multisigPubkeys;
	}

	public List<DeterministicKey> getMultisigPubkeys() {
		return multisigPubkeys;
	}

	public int getMultisigM() {
		return multisigM;
	}

	public Map<Integer, Map<Integer, byte[]>> getMultisigSignatures() {
		return multisigSignatures;
	}

	public ImmutableList<ChildNumber> getMultisigBasePath() {
		return multisigBasePath;
	}

}
