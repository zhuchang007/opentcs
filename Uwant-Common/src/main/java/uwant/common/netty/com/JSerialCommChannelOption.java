/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.netty.com;

import io.netty.channel.ChannelOption;
import uwant.common.netty.com.JSerialCommChannelConfig.Paritybit;
import uwant.common.netty.com.JSerialCommChannelConfig.Stopbits;

/**
 * Option for configuring a serial port connection
 */
public final class JSerialCommChannelOption<T> extends ChannelOption<T> {

    public static final ChannelOption<Integer> BAUD_RATE = valueOf("BAUD_RATE");
    public static final ChannelOption<Stopbits> STOP_BITS = valueOf("STOP_BITS");
    public static final ChannelOption<Integer> DATA_BITS = valueOf("DATA_BITS");
    public static final ChannelOption<Paritybit> PARITY_BIT = valueOf("PARITY_BIT");
    public static final ChannelOption<Integer> WAIT_TIME = valueOf("WAIT_TIME");
    public static final ChannelOption<Integer> READ_TIMEOUT = valueOf("READ_TIMEOUT");

    @SuppressWarnings({ "unused", "deprecation" })
    private JSerialCommChannelOption() {
        super(null);
    }
}
