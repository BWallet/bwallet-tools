/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.flows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class WalletFlows {

    private static final Logger log = LoggerFactory.getLogger(WalletFlows.class);

    public static BlankFlow newBlankFlow() {
        log.debug("Transitioning to 'blank' flow");
        return new BlankFlow();
    }

    public static WipeDeviceFlow newWipeDeviceFlow() {
        log.debug("Transitioning to 'wipe device' flow");
        return new WipeDeviceFlow();
    }
    
    public static FirmwareEraseFlow newFirmwareEraseFlow() {
        log.debug("Transitioning to 'wipe device' flow");
        return new FirmwareEraseFlow();
    }
    
    public static FirmwareUploadFlow newFirmwareUploadFlow() {
        log.debug("Transitioning to 'firmware update' flow");
        return new FirmwareUploadFlow();
    }
    
    public static CreateWalletFlow newCreateWalletFlow() {
        log.debug("Transitioning to 'create wallet' flow");
        return new CreateWalletFlow();
    }

    public static RecoveryDeviceFlow newRecoveryDeviceFlow() {
        log.debug("Transitioning to 'recovery device' flow");
        return new RecoveryDeviceFlow();
    }
    
    public static ApplySettingsFlow newApplySettingsFlow() {
        log.debug("Transitioning to 'apply settings' flow");
        return new ApplySettingsFlow();
    }
    
    public static GetDeterministicHierarchyFlow newGetDeterministicHierarchyFlow() {
        log.debug("Transitioning to 'get public key' flow");
        return new GetDeterministicHierarchyFlow();
    }
    
    public static SignMessageFlow newSignMessageFlow() {
        log.debug("Transitioning to 'sign message' flow");
        return new SignMessageFlow();
    }
    
    public static VerifyMessageFlow newVerifyMessageFlow() {
        log.debug("Transitioning to 'verify message' flow");
        return new VerifyMessageFlow();
    }
    
    public static ChangePINFlow newChangePINFlow() {
        log.debug("Transitioning to 'change pin' flow");
        return new ChangePINFlow();
    }
    
    public static TestScreenFlow newTestScreenFlow() {
        log.debug("Transitioning to 'test screen' flow");
        return new TestScreenFlow();
    }
    
    public static GetAccountLabelsFlow newGetAccountLabelsFlow() {
        log.debug("Transitioning to 'get account labels' flow");
        return new GetAccountLabelsFlow();
    }
    
    public static SetAccountLabelFlow newSetAccountLabelFlow() {
        log.debug("Transitioning to 'set account label' flow");
        return new SetAccountLabelFlow();
    }
    
    public static GetAddressFlow newGetAddressFlow() {
        log.debug("Transitioning to 'get address' flow");
        return new GetAddressFlow();
    }
    
    public static LoadDeviceFlow newLoadDeviceFlow() {
        log.debug("Transitioning to 'load device' flow");
        return new LoadDeviceFlow();
    }
}
