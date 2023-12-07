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
package uwant.vehicle.telegrams;

import uwant.common.telegrams.Request;
import static uwant.common.telegrams.Telegram.getCheckSum;

/** @author zhuchang */
public class ActionRequest extends Request {

  /** The Request type.命令字 */
  public static final byte TYPE = 0x03;
  /** The expected length of a telegram of this type. */
  public static final int TELEGRAM_LENGTH = 22;
  /** The position of the checksum byte. */
  public static final int CHECKSUM_POS = TELEGRAM_LENGTH - 2;

  public ActionRequest(int addr, int agvId, Action action, int speed) {
    super(TELEGRAM_LENGTH);
    encodeTelegramContent(addr, agvId, action, speed);
  }

  @Override
  public void updateRequestContent(int telegramId) {}

  private void encodeTelegramContent(int addr, int agvId, Action action, int speed) {
    id = agvId;
    // Start of each telegram
    rawContent[0] = (byte) ((addr >> 8) & 0xff);
    rawContent[1] = (byte) (addr & 0xff);

    // Payload of the telegram
    rawContent[2] = (byte) (id & 0xff);
    rawContent[3] = TYPE;

    // byte[] tmpWord = Ints.toByteArray(id);
    rawContent[4] = (byte) (action.value() & 0xff);
    rawContent[5] = (byte) (speed & 0xff);

    // End of each telegram
    rawContent[CHECKSUM_POS] = getCheckSum(rawContent, 3, CHECKSUM_POS);
    rawContent[TELEGRAM_LENGTH - 1] = (byte) (0xff - rawContent[2]);
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

    private Action(int value) {
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
