/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.netty.com;

import com.fazecast.jSerialComm.SerialPort;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.OioByteStreamChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * A channel to a serial device using the jSerialComm library.
 */
public class JSerialCommChannel extends OioByteStreamChannel {


  private static final JSerialCommDeviceAddress LOCAL_ADDRESS = new JSerialCommDeviceAddress("localhost");

  private final JSerialCommChannelConfig config;

  private boolean open = true;
  private JSerialCommDeviceAddress deviceAddress;
  private SerialPort serialPort;

  public JSerialCommChannel() {
    super(null);
    config = new DefaultJSerialCommChannelConfig(this);
  }

  @Override
  public JSerialCommChannelConfig config() {
    return config;
  }

  @Override
  public boolean isOpen() {
    return open;
  }

  @Override
  protected AbstractUnsafe newUnsafe() {
    return new JSCUnsafe();
  }

  @Override
  protected void doConnect(SocketAddress remoteAddress,
                           SocketAddress localAddress)
      throws Exception {
    JSerialCommDeviceAddress remote = (JSerialCommDeviceAddress) remoteAddress;
    SerialPort commPort = SerialPort.getCommPort(remote.value());
    if (!commPort.openPort()) {
      throw new IOException("Could not open port: " + remote.value());
    }

    commPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING,
                                config().getOption(JSerialCommChannelOption.READ_TIMEOUT), 0);

    deviceAddress = remote;
    serialPort = commPort;
  }

  protected void doInit()
      throws Exception {
    serialPort.setComPortParameters(
      config().getOption(JSerialCommChannelOption.BAUD_RATE),
      config().getOption(JSerialCommChannelOption.DATA_BITS),
      config().getOption(JSerialCommChannelOption.STOP_BITS).value(),
      config().getOption(JSerialCommChannelOption.PARITY_BIT).value()
    );

    activate(serialPort.getInputStream(), serialPort.getOutputStream());
  }

  @Override
  public JSerialCommDeviceAddress localAddress() {
    return (JSerialCommDeviceAddress) super.localAddress();
  }

  @Override
  public JSerialCommDeviceAddress remoteAddress() {
    return (JSerialCommDeviceAddress) super.remoteAddress();
  }

  @Override
  protected JSerialCommDeviceAddress localAddress0() {
    return LOCAL_ADDRESS;
  }

  @Override
  protected JSerialCommDeviceAddress remoteAddress0() {
    return deviceAddress;
  }

  @Override
  protected void doBind(SocketAddress localAddress)
      throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doDisconnect()
      throws Exception {
    doClose();
  }

  @Override
  protected void doClose()
      throws Exception {
    open = false;
    try {
       super.doClose();
    }
    finally {
      if (serialPort != null) {
        serialPort.closePort();
        serialPort = null;
      }
    }
  }

  @Override
  protected boolean isInputShutdown() {
    return !open;
  }

  @Override
  protected ChannelFuture shutdownInput() {
    return newFailedFuture(new UnsupportedOperationException("shutdownInput"));
  }


  private final class JSCUnsafe extends AbstractUnsafe {
    @Override
    public void connect(
      final SocketAddress remoteAddress,
      final SocketAddress localAddress,
      final ChannelPromise promise) {
      if (!promise.setUncancellable() || !isOpen()) {
        return;
      }

      try {
        final boolean wasActive = isActive();
        doConnect(remoteAddress, localAddress);

        int waitTime = config().getOption(JSerialCommChannelOption.WAIT_TIME);
        if (waitTime > 0) {
            eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                  try {
                    doInit();
                    safeSetSuccess(promise);
                    if (!wasActive && isActive()) {
                        pipeline().fireChannelActive();
                    }
                  }
                  catch (Exception t) {
                    safeSetFailure(promise, t);
                    closeIfClosed();
                  }
                }
           }, waitTime, TimeUnit.MILLISECONDS);
          }
          else {
            doInit();
            safeSetSuccess(promise);
            if (!wasActive && isActive()) {
              pipeline().fireChannelActive();
            }
          }
      }
      catch (Exception t) {
        safeSetFailure(promise, t);
        closeIfClosed();
      }
    }
  }
}
