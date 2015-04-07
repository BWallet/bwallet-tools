/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.flows;

/**
 *
 * @author Administrator
 */
public enum ContextUseCase {

    BLANK,
    WIPE_DEVICE,
    LOAD_WALLET,
    PROVIDE_ENTROPY,
    CREATE_WALLET,
    REQUEST_ADDRESS,
    REQUEST_PUBLIC_KEY,
    REQUEST_DETERMINISTIC_HIERARCHY,
    SIMPLE_SIGN_TX,
    SIGN_TX,
    REQUEST_CIPHER_KEY,
    ENCRYPT_MESSAGE,
    DECRYPT_MESSAGE,
    SIGN_MESSAGE,
    VERIFY_MESSAGE,
    CHANGE_PIN,
    RECOVERY_DEVICE,
    FIRMWARE_ERASE,
    FIRMWARE_UPLOAD,
    APPLY_SETTINGS,
    TEST_SCREEN,
    GET_ACCOUNT_LABELS,
    SET_ACCOUNT_LABEL
}
