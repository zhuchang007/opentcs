/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uwant.vehicle.netty.com;

import static java.util.Objects.requireNonNull;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import com.google.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.slf4j.LoggerFactory;
import uwant.common.netty.ChannelManager;
import uwant.common.netty.tcp.ConnectionEventListener;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.Response;
import uwant.common.telegrams.Telegram;
import uwant.common.vehicle.telegrams.ActionResponse;
import uwant.common.vehicle.telegrams.NodeActionResponse;
import uwant.common.vehicle.telegrams.StateResponse;
import uwant.vehicle.UwtCommAdapterConfiguration;

/**
 * @author zhuchang
 */
public class ComChannelManager
    implements
      ChannelManager {

  /**
   * This class's Logger.
   */
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ComChannelManager.class);
  private String comPortName = "COM1";
  private int comBaudRate = 57600;

  private static SerialPort serialPort;

  private final Set<ConnectionEventListener<Response>> listeners = Collections.synchronizedSet(
      new HashSet<>()
  );

  /**
   * Whether this component is initialized or not.
   */
  private boolean initialized;

  @Inject
  public ComChannelManager(
      @Nonnull
      UwtCommAdapterConfiguration comCommAdapterConfiguration
  ) {
    requireNonNull(comCommAdapterConfiguration, "comCommAdapterConfiguration");
    this.comPortName = comCommAdapterConfiguration.comName();
    this.comBaudRate = comCommAdapterConfiguration.comBaudRate();
  }

  @Override
  public void initialize() {
    if (initialized) {
      return;
    }
    if (connect()) {
      initialized = true;
    }
  }

  /**
   * Initiates a connection (attempt) to the remote host and port.
   */
  public boolean connect() {
    if (isConnected()) {
      LOG.debug("Already connected, doing nothing.");
      return true;
    }
    boolean existComPort = false;
    SerialPort[] serialPorts = SerialPort.getCommPorts();
    for (SerialPort port : serialPorts) {
      if (port.getSystemPortName().equals(this.comPortName)) {
        existComPort = true;
        serialPort = port;
      }
    }
    if (!existComPort) {
      LOG.info("The COM to be opened doesn't exist! Please modify the configuration file.");
      return false;
    }
    serialPort.setBaudRate(comBaudRate);
    serialPort.openPort();
    serialPort.addDataListener(new PacketListener());
    return true;
  }

  public void disconnect() {
    if (isConnected()) {
      serialPort.removeDataListener();
      serialPort.closePort();
    }
  }

  @Override
  public void send(int agvId, Request telegram) {
    if (isConnected()) {
      serialPort.writeBytes(telegram.getRawContent(), Telegram.TELEGRAM_LENGTH);
    }
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  public boolean isConnected() {
    return serialPort != null && serialPort.isOpen();
  }

  @Override
  public void terminate() {
    if (!initialized) {
      return;
    }

    listeners.clear();
    disconnect();
    initialized = false;
  }

  @Override
  public void addListener(int agvId, ConnectionEventListener<Response> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(int agvId, ConnectionEventListener<Response> listener) {
    listeners.remove(listener);
  }

  @Override
  public int getListenerCount() {
    return listeners.size();
  }

  private final class PacketListener
      implements
        SerialPortPacketListener {
    @Override
    public int getListeningEvents() {
      return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public int getPacketSize() {
      return Telegram.TELEGRAM_LENGTH;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
      byte[] recvTelegram = event.getReceivedData();
      if (recvTelegram.length != Telegram.TELEGRAM_LENGTH) {
        LOG.info("Receive data wrong length! ");
        return;
      }

      if (((recvTelegram[2] ^ recvTelegram[Telegram.TELEGRAM_LENGTH - 1]) & 0xff) != 0xff) {
        LOG.info("Receive head and tail do not equals to '0xff'! ");
        return;
      }

      if (recvTelegram[3] == StateResponse.TYPE) {
        // 每个Vehicle的CommAdapter都处理
        listeners.forEach(
            (responseHandler) -> {
              responseHandler.onIncomingTelegram(new StateResponse(recvTelegram));
            }
        );
      }
      else if (recvTelegram[3] == ActionResponse.TYPE) {
        listeners.forEach(
            (responseHandler) -> {
              responseHandler.onIncomingTelegram(new ActionResponse(recvTelegram));
            }
        );
      }
      else if (recvTelegram[3] == NodeActionResponse.TYPE) {
        listeners.forEach(
            (responseHandler) -> {
              responseHandler.onIncomingTelegram(new NodeActionResponse(recvTelegram));
            }
        );
      }
    }
  }
}
