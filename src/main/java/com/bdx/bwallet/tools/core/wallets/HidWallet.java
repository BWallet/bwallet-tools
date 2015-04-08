/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.wallets;

import com.bdx.bwallet.protobuf.BWalletMessage;
import com.bdx.bwallet.tools.core.concurrent.SafeExecutors;
import com.bdx.bwallet.tools.core.events.MessageEvent;
import com.bdx.bwallet.tools.core.events.MessageEventType;
import com.bdx.bwallet.tools.core.events.MessageEvents;
import com.bdx.bwallet.tools.core.utils.MessageUtils;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Message;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.hid4java.HidDevice;
import org.hid4java.HidException;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Administrator
 */
public class HidWallet extends AbstractWallet {

    public static final Integer VENDOR_ID = 0x534c;
    public static final Integer PRODUCT_ID = 0x01;
    
    private static final int PACKET_LENGTH = 64;

    private static final Logger log = LoggerFactory.getLogger(HidWallet.class);

    private Optional<Integer> vendorId = Optional.absent();
    private Optional<Integer> productId = Optional.absent();
    private Optional<String> serialNumber = Optional.absent();

    /**
     * The located device
     */
    private Optional<HidDevice> locatedDevice = Optional.absent();

    /**
     * The USB HID entry point
     */
    private final HidServices hidServices;

    /**
     * Monitor the USB HID read buffer and handle the firing of low level
     * messages when a message is found A new one is required after a detach
     */
    private ExecutorService monitorHidExecutorService;

    /**
     * Default constructor for use with dynamic binding
     */
    public HidWallet() {
        this(Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<String>absent());
    }

    /**
     * <p>
     * Create a new instance of a USB-based BWallet device (standard)</p>
     *
     * @param vendorId The vendor ID (default is 0x534c)
     * @param productId The product ID (default is 0x01)
     * @param serialNumber The device serial number (default is to accept any)
     */
    public HidWallet(
            Optional<Integer> vendorId,
            Optional<Integer> productId,
            Optional<String> serialNumber) {
        this.vendorId = vendorId.isPresent() ? vendorId : Optional.of(VENDOR_ID);
        this.productId = productId.isPresent() ? productId : Optional.of(PRODUCT_ID);
        this.serialNumber = serialNumber;
        try {
            // Get the USB services and dump information about them
            hidServices = HidManager.getHidServices();
            //hidServices.addHidServicesListener(this);
        } catch (HidException e) {
            log.error("Failed to create client due to USB services problem", e);
            throw new IllegalStateException("Failed to create client due to HID services problem", e);
        }
    }

    @Override
    public boolean attach() {
        // Ensure we close any earlier connections
        if (locatedDevice.isPresent()) {
            softDetach();
        }
        // Explore all attached HID devices
        locatedDevice = Optional.fromNullable(
                hidServices.getHidDevice(
                        vendorId.get(),
                        productId.get(),
                        serialNumber.orNull()
                )
        );
        if (!locatedDevice.isPresent()) {
            log.info("Device not attached");
        }
        // Must be OK to be here
        return true;
    }

    @Override
    public void softDetach() {
        if (locatedDevice != null && locatedDevice.isPresent()) {
            log.debug("Closing device on HID API...");
            locatedDevice.get().close();
        } else {
            // Already closed
            return;
        }
        log.debug("Removing device reference");
        locatedDevice = Optional.absent();
        log.debug("Shutdown HID monitoring");
        monitorHidExecutorService.shutdownNow();
        try {
            monitorHidExecutorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Could not cleanly shutdown the low level monitor executor service during soft detach");
        }
        log.info("Detached from BWallet. HID events remain available.");
    }

    @Override
    public void hardDetach() {
        softDetach();
        log.debug("Shutdown HID events");
        //hidServices.removeUsbServicesListener(this);
        log.debug("Release resources");
        //hidServices.stop();
        log.info("Hard detach complete. HID events are stopped.");
        // Let everyone know
        //MessageEvents.fireMessageEvent(MessageEventType.DEVICE_DETACHED_HARD);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean connect() {
        // Check if the verify environment located a BWallet
        if (!locatedDevice.isPresent()) {
            log.debug("Suspect recently detached device. Attempting to locate...");
      // Explore all attached devices including hubs to verify USB library is working
            // and to determine initial state
            attach();
            if (!locatedDevice.isPresent()) {
                log.debug("Failed to locate. Device must be detached.");
                MessageEvents.fireMessageEvent(MessageEventType.DEVICE_FAILED);
                return false;
            }
        }
        log.info("Located a BWallet device.");
        // Ensure any pre-existing monitors are terminated
        if (monitorHidExecutorService != null && !monitorHidExecutorService.isShutdown()) {
            log.warn("Low level monitor executor service is still running. Device not detached properly. Attempting clean shutdown of executor service...");
            monitorHidExecutorService.shutdownNow();
            try {
                monitorHidExecutorService.awaitTermination(1, TimeUnit.SECONDS);
                log.info("Clean shutdown OK");
            } catch (InterruptedException e) {
                log.error("Could not cleanly shutdown the low level monitor executor service during connect");
            }
        }
        // Start polling the HID read buffer looking for messages
        monitorHidExecutorService = SafeExecutors.newSingleThreadExecutor("monitor-hid");
        monitorHidExecutorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        while (!(monitorHidExecutorService.isShutdown() || monitorHidExecutorService.isTerminated())) {
                            // Wait for 10 seconds for a response (this is so that the monitorExecutorService can shut down cleanly)
                            Optional<MessageEvent> messageEvent = readMessage(10, TimeUnit.SECONDS);
                            if (messageEvent.isPresent()) {
                                if (MessageEventType.DEVICE_FAILED.equals(messageEvent.get().getEventType())) {
                                    // Stop reading messages on this thread for a short while to allow recovery time
                                    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                                } else {
                                    // Fire the event
                                    messageEvent.get().setDevice(locatedDevice);
                                    MessageEvents.fireMessageEvent(messageEvent.get());
                                }
                            }
                        }
                    }
                });
        // Allow time for the read monitor thread to start
        Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
        // Must be OK to be here
        return true;
    }

    @Override
    public String toString() {
        if (locatedDevice.isPresent()) {
            return "USB BWallet Version 1: " + locatedDevice.get().getId();
        }
        return "Not attached";
    }

    @Override
    protected int writeToDevice(byte[] buffer) {
        Preconditions.checkNotNull(buffer, "'buffer' must be present");
        Preconditions.checkNotNull(locatedDevice, "Device is not located");
        Preconditions.checkState(locatedDevice.isPresent(), "Device is not connected");
        log.debug("Writing buffer to HID pipe...");
        int bytesSent = locatedDevice.get().write(
                buffer,
                PACKET_LENGTH,
                (byte) 0x00
        );
        log.debug("Wrote {} bytes to USB pipe.", bytesSent);
        return bytesSent;
    }

    @Override
    protected synchronized Optional<MessageEvent> readFromDevice(int duration, TimeUnit timeUnit) {
        log.debug("Reading from hardware device");
        if (!locatedDevice.isPresent()) {
            log.warn("Attempting to read from a device that is not present");
            // Avoid excessive failure logging
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            return Optional.absent();
        }
        ByteBuffer messageBuffer = ByteBuffer.allocate(32768);
        BWalletMessage.MessageType type;
        int msgSize;
        int received;
        // Keep reading until synchronized on "##"
        for (;;) {
            byte[] buffer = new byte[PACKET_LENGTH];
      // Check for timeout against the read operation
            // This allows the executing thread to terminate in a timely manner without
            // a response from the device
            if (duration != 0) {
                received = locatedDevice.get().read(buffer, (int) timeUnit.toMillis(duration));
            } else {
                received = locatedDevice.get().read(buffer);
            }
            // There is a security risk to raising this logging level beyond trace
            log.trace("< {} bytes", received);
            if (received == -1) {
                // Hardware problem
                return Optional.of(
                        new MessageEvent(
                                MessageEventType.DEVICE_FAILED,
                                Optional.<Message>absent()
                        ));
            }
            if (received == 0) {
                return Optional.absent();
            }
            MessageUtils.logPacket("<", 0, buffer);
            if (received < 9) {
                continue;
            }
            // Synchronize the buffer on start of new message ('?' is ASCII 63)
            if (buffer[0] != (byte) '?' || buffer[1] != (byte) '#' || buffer[2] != (byte) '#') {
                // Reject packet
                log.debug("Rejecting message (not synchronized)");
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
            byte[] buffer = new byte[PACKET_LENGTH];
            received = locatedDevice.get().read(buffer);
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
        return Optional.of(MessageUtils.parse(type, Arrays.copyOfRange(messageBuffer.array(), 0, msgSize)));
    }
}
