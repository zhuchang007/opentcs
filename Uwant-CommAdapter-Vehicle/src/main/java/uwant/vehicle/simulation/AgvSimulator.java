/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.simulation;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import uwant.common.telegrams.Telegram;
import uwant.common.vehicle.telegrams.*;

public class AgvSimulator
    implements
      SerialPortPacketListener {

  public AgvSimulator() {
  }

  @Override
  public int getListeningEvents() {
    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
  }

  @Override
  public int getPacketSize() {
    return Telegram.TELEGRAM_LENGTH;
  }

  private static SerialPort comPort;

  @Override
  public void serialEvent(SerialPortEvent event) {
    byte[] recvTelegram = event.getReceivedData();

    if (recvTelegram.length != Telegram.TELEGRAM_LENGTH) {
      System.out.println("Receive data wrong length! ");
      return;
    }

    if (((recvTelegram[2] ^ recvTelegram[Telegram.TELEGRAM_LENGTH - 1]) & 0xff) != 0xff) {
      System.out.println("Receive head and tail do not equals to '0xff'! ");
      return;
    }

    if (recvTelegram[3] == StateResponse.TYPE) {
    }
    else if (recvTelegram[3] == ActionRequest.TYPE) {
      ActionRequest actionRequest = new ActionRequest(recvTelegram);
      System.out.println("receive ActionRequest: " + actionRequest.getHexRawContent());
      byte[] newData = new byte[Telegram.TELEGRAM_LENGTH];
      newData[0] = recvTelegram[0];
      newData[1] = recvTelegram[1];
      newData[2] = (byte) (recvTelegram[2] ^ 0x80);
      newData[3] = ActionResponse.TYPE;
      newData[4] = 1;
      newData[20] = (byte) (newData[3] + newData[4]);
      newData[21] = (byte) (0xff - newData[2]);
      ActionResponse actionResponse = new ActionResponse(newData);
      try {
        TimeUnit.MILLISECONDS.sleep(200);
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      comPort.writeBytes(actionResponse.getRawContent(), Telegram.TELEGRAM_LENGTH);
      System.out.println("send actionResponse: " + actionResponse.getHexRawContent());
    }
    else if (recvTelegram[3] == NodeActionRequest.TYPE) {
      NodeActionRequest nodeActionRequest = new NodeActionRequest(recvTelegram);
      System.out.println("receive nodeActionRequest: " + nodeActionRequest.getHexRawContent());
      byte[] newData = new byte[Telegram.TELEGRAM_LENGTH];
      newData[0] = recvTelegram[0];
      newData[1] = recvTelegram[1];
      newData[2] = (byte) (recvTelegram[2] ^ 0x80);
      newData[3] = NodeActionResponse.TYPE;
      newData[4] = 1;
      newData[20] = (byte) (newData[3] + newData[4]);
      newData[21] = (byte) (0xff - newData[2]);
      NodeActionResponse nodeActionResponse = new NodeActionResponse(newData);
      try {
        TimeUnit.MILLISECONDS.sleep(200);
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      comPort.writeBytes(nodeActionResponse.getRawContent(), Telegram.TELEGRAM_LENGTH);
      System.out.println("send nodeActionResponse: " + nodeActionResponse.getHexRawContent());
    }
  }

  public static void main(String[] args)
      throws InterruptedException {
    AgvSimulator agvSimulator = new AgvSimulator();

    byte[] rawStateResponse = new byte[22];
    rawStateResponse[0] = 0x00;
    rawStateResponse[1] = 0x01;
    // 小车编号
    rawStateResponse[2] = (byte) (0x80 + 0x01);
    // 命令字
    rawStateResponse[3] = 0x01;
    // 数据
    rawStateResponse[7] = 0x01;
    rawStateResponse[8] = 0x01;
    // 校验和
    rawStateResponse[20] = Telegram.getCheckSum(rawStateResponse, 3, 19);
    // 帧尾
    rawStateResponse[21] = (byte) (0xff - rawStateResponse[2]);

    StateResponse stateResponse = new StateResponse(rawStateResponse);

    SerialPort[] comPorts = SerialPort.getCommPorts();
//    for (int i = 0; i < comPorts.length; i++) {
//      System.out.println("串口名称" + comPorts[i].getSystemPortName() +"\n");
//    }
    comPort = comPorts[3];
    comPort.setBaudRate(57600);
    comPort.openPort();
    comPort.addDataListener(agvSimulator);

//    comPort.removeDataListener();
//    comPort.closePort();

    System.out.println("send stateResponse every second：" + stateResponse.getHexRawContent());
    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    executorService.scheduleAtFixedRate(() -> {
      comPort.writeBytes(stateResponse.getRawContent(), Telegram.TELEGRAM_LENGTH);
    }, 0, 1000, TimeUnit.MILLISECONDS);

    while (true) {
      Thread.sleep(1000);
    }
  }

}
