/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.events;

import com.google.common.base.Optional;
import com.google.protobuf.Message;
import org.hid4java.HidDevice;

/**
 *
 * @author Administrator
 */
public class MessageEvent {

    private final MessageEventType eventType;

    private final Optional<Message> message;

    private Optional<HidDevice> device;
    
    public MessageEvent(MessageEventType eventType, Optional<Message> message, Optional<HidDevice> device) {
        this.eventType = eventType;
        this.message = message;
        this.device = device;
    }
    
    /**
     * @param eventType The message event type (e.g. INITIALISE, PING etc)
     * @param message The adapted hardware wallet message
     */
    public MessageEvent(MessageEventType eventType, Optional<Message> message) {
        this.eventType = eventType;
        this.message = message;
        this.device = Optional.absent();
    }

    /**
     * @return The low level message type
     */
    public MessageEventType getEventType() {
        return eventType;
    }

    /**
     * @return The adapted low level message if present
     */
    public Optional<Message> getMessage() {
        return message;
    }
    
    public Optional<HidDevice> getDevice() {
        return device;
    }

    public void setDevice(Optional<HidDevice> device) {
        this.device = device;
    }
}
