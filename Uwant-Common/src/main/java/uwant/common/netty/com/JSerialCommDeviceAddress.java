/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.netty.com;

import java.net.SocketAddress;

/**
 * A {@link SocketAddress} subclass to wrap the serial port address of a jSerialComm
 * device (e.g. COM1, /dev/ttyUSB0).
 */
public class JSerialCommDeviceAddress
    extends
      SocketAddress {

  private static final long serialVersionUID = -2907820090993709523L;

  private final String value;

  /**
   * Creates a JSerialCommDeviceAddress representing the address of the serial port.
   *
   * @param value the address of the device (e.g. COM1, /dev/ttyUSB0, ...)
   */
  public JSerialCommDeviceAddress(String value) {
    this.value = value;
  }

  /**
   * @return The serial port address of the device (e.g. COM1, /dev/ttyUSB0, ...)
   */
  public String value() {
    return value;
  }
}
