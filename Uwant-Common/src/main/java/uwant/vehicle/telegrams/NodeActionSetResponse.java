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

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.primitives.Ints;
import uwant.common.telegrams.Response;
import static uwant.common.telegrams.Telegram.getCheckSum;
import static java.util.Objects.requireNonNull;

/** @author zhuchang */
public class NodeActionSetResponse extends Response {
  /** The response type.命令字 */
  public static final byte TYPE = 0x16;
  /** The expected length of a telegram of this type. */
  public static final int TELEGRAM_LENGTH = 22;
  /** The position of the checksum byte. */
  public static final int CHECKSUM_POS = TELEGRAM_LENGTH - 2;

  public NodeActionSetResponse(byte[] telegramData) {
    super(TELEGRAM_LENGTH);
    requireNonNull(telegramData, "telegramData");
    checkArgument(telegramData.length == TELEGRAM_LENGTH);

    System.arraycopy(telegramData, 0, rawContent, 0, TELEGRAM_LENGTH);
    decodeTelegramContent();
  }

  private void decodeTelegramContent() {
    addr = (rawContent[0] << 8) | (rawContent[1]);
    id = Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[2]) - 0x80;
    isOK = Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[4]) == 1;
  }

  public static boolean isNodeActionSetResponse(byte[] telegramData) {
    requireNonNull(telegramData, "data");

    boolean result = true;
    if (telegramData.length != TELEGRAM_LENGTH) {
      result = false;
    } else if (telegramData[3] != TYPE) {
      result = false;
    } else if (getCheckSum(telegramData, 3, CHECKSUM_POS) != telegramData[CHECKSUM_POS]) {
      result = false;
    }
    return result;
  }
}
