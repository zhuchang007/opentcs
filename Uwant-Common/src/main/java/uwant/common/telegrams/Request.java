/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.telegrams;

/**
 * A request represents a telegram sent from the control system to vehicle control and expects
 * a response with the same id to match.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class Request
    extends Telegram {
  public Request() {
  }

  public Request(byte[] telegramData) {
    super(telegramData);
  }

  protected void encodeTelegramHead(int addr, int agvId) {
    this.addr = addr;
    this.agvId = agvId;
    rawContent[0] = (byte) ((this.addr >> 8) & 0xff);
    rawContent[1] = (byte) (this.addr & 0xff);
    rawContent[2] = (byte) (this.agvId & 0xff);
  }

  protected void encodeTelegramTail() {
    // End of the request
    rawContent[CHECKSUM_POS] = getCheckSum(rawContent, 3, CHECKSUM_POS);
    rawContent[TELEGRAM_LENGTH - 1] = (byte) (0xff - rawContent[2]);
  }
}
