/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.simulation;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import uwant.common.netty.tcp.ConnectionEventListener;
import uwant.common.netty.tcp.TcpClientChannelManager;
import uwant.common.telegrams.Response;
import uwant.common.telegrams.Telegram;
import uwant.common.vehicle.telegrams.ActionResponse;
import uwant.common.vehicle.telegrams.NodeActionSetResponse;
import uwant.common.vehicle.telegrams.StateResponse;

import java.util.Arrays;
import java.util.List;

public class AgvSimulator implements ConnectionEventListener<byte[]> {
  private TcpClientChannelManager<byte[], byte[]> vehicleChannelManager;

  public AgvSimulator() {
    vehicleChannelManager =
        new TcpClientChannelManager<>(this, this::getChannelHandlers, 5000, false);
    vehicleChannelManager.initialize();
    // vehicleChannelManager.connect("127.0.0.1", 2000);
    // vehicleChannelManager.scheduleConnect("127.0.0.1", 2000, 1000);
  }

  public void connect() {
    vehicleChannelManager.connect("127.0.0.1", 2000);
  }

  @Override
  public void onIncomingTelegram(byte[] paramT) {
    if (paramT.length != StateResponse.TELEGRAM_LENGTH) return;
    Response response;
    switch (paramT[3]) {
      case StateResponse.TYPE:
        response = new StateResponse(paramT);
        break;
      case ActionResponse.TYPE:
        response = new ActionResponse(paramT);
        break;
      case NodeActionSetResponse.TYPE:
        response = new NodeActionSetResponse(paramT);
        break;
      default:
        System.out.println("Unknown Type!");
        response = null;
        break;
    }
    if (response != null) {
      System.out.println("Incoming request: " + response.getHexRawContent());
    }
  }

  @Override
  public void onConnect(Object object) {
    System.out.println("connect successful!");
  }

  @Override
  public void onFailedConnectionAttempt() {}

  @Override
  public void onDisconnect() {}

  @Override
  public void onIdle() {}

  public void sendTelegram(byte[] request) {
    if (!isVehicleConnected()) {
      return;
    }
    vehicleChannelManager.send(request);
  }

  public synchronized boolean isVehicleConnected() {
    return vehicleChannelManager != null && vehicleChannelManager.isConnected();
  }

  private List<ChannelHandler> getChannelHandlers() {
    return Arrays.asList(
        // new LengthFieldBasedFrameDecoder(getMaxTelegramLength(), 1, 1, 2, 0),
        new FixedLengthFrameDecoder(NodeActionSetResponse.TELEGRAM_LENGTH),
        // new VehicleTelegramDecoder(this),
        new TelegramDecoderClient(this),
        new TelegramEncoderClient());
  }

  public static void main(String[] args) throws InterruptedException {
    AgvSimulator agvSimulator = new AgvSimulator();
    int count = 3;
    while (count-- > 0) {
      agvSimulator.connect();
      Thread.sleep(1000);
    }
    Thread.sleep(1000);
    byte[] stateResponse = new byte[20];
    // 小车编号
    stateResponse[0] = (byte) (0x80 + 0x01);
    // 命令字
    stateResponse[1] = 0x01;
    // 数据
    stateResponse[4] = 0x02;
    stateResponse[5] = 0x03;
    stateResponse[6] = 0x04;
    // 校验和
    stateResponse[18] = Telegram.getCheckSum(stateResponse, 1, 18);
    // 帧尾
    stateResponse[19] = (byte) (0xff - stateResponse[0]);

    StateResponse stateResponse1 = new StateResponse(stateResponse);
    System.out.println("发送：" + stateResponse1.getHexRawContent());

    while (true) {
      agvSimulator.sendTelegram(stateResponse1.getRawContent());
      Thread.sleep(500);
    }
    //    while (true) {
    //      System.out.println(tcpClient.isVehicleConnected() + "\r\n");
    //    }
  }
}
