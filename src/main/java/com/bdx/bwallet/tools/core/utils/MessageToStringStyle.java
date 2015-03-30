package com.bdx.bwallet.tools.core.utils;

import com.bdx.bwallet.protobuf.BWalletType;
import com.bdx.bwallet.protobuf.BWalletType.HDNodeType;
import com.google.common.base.Optional;
import com.google.protobuf.ByteString;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bitcoinj.core.Utils;

/**
 * <p>Apache Commons ToStringStyle extension to provide the following to logging:</p>
 * <ul>
 * <li>Hex representation of byte[], ByteString</li>
 * <li>Better representation of custom BWallet message types</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class MessageToStringStyle extends ToStringStyle {

  /**
   * Match the multi-line style
   */
  public MessageToStringStyle() {
    super();
    this.setContentStart("[");
    this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
    this.setFieldSeparatorAtStart(true);
    this.setContentEnd(SystemUtils.LINE_SEPARATOR + "]");
  }

  protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {

    // Avoid writing out certain fields for security reasons
    if (fieldName.equals("xpub")) {
      buffer.append("****");
      return;
    }
    if (fieldName.equals("xpubBytes")) {
      buffer.append("****");
      return;
    }
    if (fieldName.equals("private_key")) {
      buffer.append("****");
      return;
    }
    if (fieldName.equals("public_key")) {
      buffer.append("****");
      return;
    }
    if (fieldName.equals("chain_code")) {
      buffer.append("****");
      return;
    }
    if (fieldName.equals("payload")) {
      buffer.append("****");
      return;
    }

    // Unwrap Optional
    if (value instanceof Optional) {

      value = ((Optional) value).orNull();
    }


    if (value instanceof byte[]) {

      byte[] bytes = (byte[]) value;
      appendHexBytes(buffer, bytes);

    } else if (value instanceof ByteString) {

      byte[] bytes = ((ByteString) value).toByteArray();
      appendHexBytes(buffer, bytes);

    } else if (value instanceof BWalletType.TxRequestDetailsType) {

      BWalletType.TxRequestDetailsType detail = ((BWalletType.TxRequestDetailsType) value);
      appendTxRequestDetailsType(buffer, detail);

    } else if (value instanceof BWalletType.TxRequestSerializedType) {

      BWalletType.TxRequestSerializedType serializedType = ((BWalletType.TxRequestSerializedType) value);
      appendTxRequestSerializedType(buffer, serializedType);

    } else if (value instanceof BWalletType.TransactionType) {

      BWalletType.TransactionType txType = ((BWalletType.TransactionType) value);
      appendTransactionType(buffer, txType);

    } else if (value instanceof HDNodeType) {

      HDNodeType hdNodeType = ((HDNodeType) value);
      appendHDNodeType(buffer, hdNodeType);

    } else {

      buffer.append(value);
    }

  }

  private void appendHexBytes(StringBuffer buffer, byte[] bytes) {
    for (byte b : bytes) {
      buffer.append(String.format(" %02x", b));
    }
  }

  private void appendTransactionType(StringBuffer buffer, BWalletType.TransactionType txType) {

    buffer
      .append("\n    bin_outputs_count: ")
      .append(txType.getBinOutputsCount());
    for (BWalletType.TxOutputBinType txOutputBinType : txType.getBinOutputsList()) {
      appendTxOutputBinType(buffer, txOutputBinType);
    }

    buffer
      .append("\n    inputs_cnt: ")
      .append(txType.getInputsCnt());
    buffer
      .append("\n    inputs_count: ")
      .append(txType.getInputsCount());
    for (BWalletType.TxInputType txInputType : txType.getInputsList()) {
      appendTxInputType(buffer, txInputType);
    }

    buffer
      .append("\n    outputs_cnt: ")
      .append(txType.getOutputsCnt());
    buffer
      .append("\n    outputs_count: ")
      .append(txType.getOutputsCount());
    for (BWalletType.TxOutputType txOutputType : txType.getOutputsList()) {
      appendTxOutputType(buffer, txOutputType);
    }

    buffer
      .append("\n    lock_time: ")
      .append(txType.getLockTime());

    buffer
      .append("\n    version: ")
      .append(txType.getVersion());

  }

  private void appendHDNodeType(StringBuffer buffer, HDNodeType hdNodeType) {

    // Avoid logging some fields for security reasons
    // The original code is left in place to assist debugging

    buffer
      .append("\n    public_key: ****");
    // appendHexBytes(buffer, hdNodeType.getPublicKey().or(new byte[] {}));

    buffer
      .append("\n    private_key: ****");

    buffer
      .append("\n    chain_code: ****");
    //appendHexBytes(buffer, hdNodeType.getChainCode().or(new byte[] {}));

    buffer
      .append("\n    child_num: ")
      .append(Integer.toHexString(hdNodeType.hasChildNum() ? hdNodeType.getChildNum() : -1));

    buffer
      .append("\n    depth: ")
      .append(hdNodeType.getDepth());

    buffer
      .append("\n    fingerprint: ")
      .append(Integer.toHexString(hdNodeType.hasFingerprint() ? hdNodeType.getFingerprint() : -1));

  }

  private void appendTxRequestDetailsType(StringBuffer buffer, BWalletType.TxRequestDetailsType detail) {

    buffer
      .append("\n    has_request_index: ")
      .append(detail.hasRequestIndex());
    buffer
      .append("\n    request_index: ")
      .append(detail.getRequestIndex());

    buffer
      .append("\n    has_tx_hash: ")
      .append(detail.hasTxHash());
    buffer
      .append("\n    tx_hash: ");
    byte[] bytes = detail.getTxHash().toByteArray();
    if (bytes.length > 0) {
      buffer.append(Utils.HEX.encode(bytes));
    }
  }

  private void appendTxRequestSerializedType(StringBuffer buffer, BWalletType.TxRequestSerializedType serialized) {

    buffer
      .append("\n    has_serialized_tx: ")
      .append(serialized.hasSerializedTx());
    buffer
      .append("\n    serialized_tx: ");
    byte[] bytes = serialized.getSerializedTx().toByteArray();
    if (bytes.length > 0) {
      buffer.append(Utils.HEX.encode(bytes));
    }

    buffer
      .append("\n    has_signature_index: ")
      .append(serialized.hasSignatureIndex());
    buffer
      .append("\n    signature_index: ")
      .append(serialized.getSignatureIndex());
    buffer
      .append("\n    has_signature: ")
      .append(serialized.hasSignature());
    buffer
      .append("\n    signature: ");
    bytes = serialized.getSignature().toByteArray();
    if (bytes.length > 0) {
      buffer.append(Utils.HEX.encode(bytes));
    }

  }

  private void appendTxInputType(StringBuffer buffer, BWalletType.TxInputType txInput) {

    buffer
      .append("\n      sequence: ")
      .append(txInput.getSequence());

    for (Integer addressN : txInput.getAddressNList()) {
      buffer
        .append("\n      address_n: ")
        .append(String.format("%02x", addressN));
    }
    byte[] bytes = txInput.getPrevHash().toByteArray();
    if (bytes.length > 0) {
      buffer
        .append("\n      prev_hash: ")
        .append(Utils.HEX.encode(bytes));
    }

    buffer
      .append("\n      prev_index: ")
      .append(txInput.getPrevIndex());

    buffer
      .append("\n      script_type: ")
      .append(txInput.getScriptType());

    bytes = txInput.getScriptSig().toByteArray();
    if (bytes.length > 0) {
      buffer
        .append("\n      script_sig: ")
        .append(Utils.HEX.encode(bytes));
    }
  }

  private void appendTxOutputType(StringBuffer buffer, BWalletType.TxOutputType txOutput) {

    byte[] bytes = txOutput.getAddressBytes().toByteArray();
    if (bytes.length > 0) {
      buffer
        .append("\n      address_bytes: ")
        .append(Utils.HEX.encode(bytes));
    }

    buffer
      .append("\n      address: ")
      .append(txOutput.getAddress());

    buffer
      .append("\n      address_n_count: ")
      .append(txOutput.getAddressNCount());

    for (Integer addressN : txOutput.getAddressNList()) {
      buffer
        .append("\n      address_n: ")
        .append(String.format("%02x", addressN));
    }

    buffer
      .append("\n      amount: ")
      .append(txOutput.getAmount());

    buffer
      .append("\n      script_type: ")
      .append(txOutput.getScriptType());

  }

  private void appendTxOutputBinType(StringBuffer buffer, BWalletType.TxOutputBinType txOutput) {

    byte[] bytes = txOutput.getScriptPubkey().toByteArray();
    if (bytes.length > 0) {
      buffer
        .append("\n      script_pubkey: ")
        .append(Utils.HEX.encode(bytes));
    }

    buffer
      .append("\n      amount: ")
      .append(txOutput.getAmount());

  }
}