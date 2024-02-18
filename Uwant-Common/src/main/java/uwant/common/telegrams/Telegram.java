/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.telegrams;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.Serializable;
import static java.util.Objects.requireNonNull;

/**
 * The base class for all telegram types used for communication with the vehicle.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class Telegram implements Serializable {

  /** The expected length of a telegram of this type. */
  public static final int TELEGRAM_LENGTH = 22;
  /** The position of the checksum byte. */
  public static final int CHECKSUM_POS = TELEGRAM_LENGTH - 2;

  /** The telegram's raw content as sent via the network. */
  protected final byte[] rawContent;
  /** The identifier for a specific telegram instance. */
  protected int addr;
  protected int agvId;

  protected byte commandType;

  /**
   * Creates a new instance.
   */
  public Telegram() {
    this.rawContent = new byte[TELEGRAM_LENGTH];
  }

  public Telegram(byte[] telegramData) {
    this();
    requireNonNull(telegramData, "telegramData");
    checkArgument(telegramData.length == TELEGRAM_LENGTH);
    checkArgument(getCheckSum(telegramData, 3, CHECKSUM_POS) == telegramData[CHECKSUM_POS]);
    System.arraycopy(telegramData, 0, rawContent, 0, TELEGRAM_LENGTH);
  }

  /**
   * Returns this telegram's actual raw content.
   *
   * @return This telegram's actual raw content.
   */
  public byte[] getRawContent() {
    return rawContent;
  }

  public String getHexRawContent() {
    return HexConvert.bytesToHex(rawContent);
  }

  /**
   * Returns the identifier for this specific telegram instance. In uwt this id as agv id.
   *
   * @return The identifier for this specific telegram instance.
   */
  public int getAgvId() {
    return agvId;
  }

  public int getAddr() {
    return addr;
  }

  public int getCommandType() {return commandType;}

  public static byte getCheckSum(byte[] rawContent, int startPos, int endPos) {
    requireNonNull(rawContent, "rawContent");

    int cs = 0;
    for (int i = startPos; i < endPos; i++) {
      cs += rawContent[i];
    }
    return (byte) cs;
  }
}
