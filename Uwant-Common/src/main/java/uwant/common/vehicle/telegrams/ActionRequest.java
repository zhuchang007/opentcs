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
package uwant.common.vehicle.telegrams;

import static uwant.common.telegrams.Telegram.getCheckSum;

import uwant.common.telegrams.Request;

/** @author zhuchang */
@SuppressWarnings("this-escape")
public class ActionRequest
    extends
      Request {

  /** The command type.命令字 */
  public static final byte TYPE = 0x03;

  public ActionRequest(int addr, int agvId, Action action, int speed) {
    this.addr = addr;
    this.agvId = agvId;
    encodeTelegramContent(action, speed);
  }

  public ActionRequest(byte[] telegramData) {
    super(telegramData);
  }

  private void encodeTelegramContent(Action action, int speed) {
    encodeTelegramHead(this.addr, this.agvId);
    // Payload of the request
    this.commandType = TYPE;
    rawContent[3] = this.commandType;
    rawContent[4] = (byte) (action.value() & 0xff);
    rawContent[5] = (byte) (speed & 0xff);
    encodeTelegramTail();
  }

  public enum Action {
    STOP(0),
    FORWARD(1),
    BACKWARD(2),
    TURN_LEFT(3),
    TURN_RIGHT(4),
    PATROL_FORWARD(5),
    PATROL_BACKWARD(6),
    QUICK_STOP(0xff);

    private int value = 0;

    Action(int value) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static Action valueOf(int action) {
      switch (action) {
        case 1:
          return FORWARD;
        case 2:
          return BACKWARD;
        case 3:
          return TURN_LEFT;
        case 4:
          return TURN_RIGHT;
        case 5:
          return PATROL_FORWARD;
        case 6:
          return PATROL_BACKWARD;
        case 0xff:
          return QUICK_STOP;
        default:
          return STOP;
      }
    }
  }
}
