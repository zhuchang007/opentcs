/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.telegrams;

import java.io.Serializable;
import static java.util.Objects.requireNonNull;

/**
 * The base class for all telegram types used for communication with the vehicle.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public abstract class Telegram implements Serializable {

  /** The default value for a telegram's id. */
  public static final int ID_DEFAULT = 0;
  /** The telegram's raw content as sent via the network. */
  protected final byte[] rawContent;
  /** The identifier for a specific telegram instance. */
  protected int id;

  /**
   * Creates a new instance.
   *
   * @param telegramLength The telegram's length
   */
  public Telegram(int telegramLength) {
    this.rawContent = new byte[telegramLength];
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
    return bytesToHexString(rawContent);
  }

  /**
   * Returns the identifier for this specific telegram instance. In uwt this id as agv id.
   *
   * @return The identifier for this specific telegram instance.
   */
  public int getId() {
    return id;
  }

  // tag::documentation_checksumComp[]
  /**
   * Computes a checksum for the given raw content of a telegram.
   *
   * @param rawContent A telegram's raw content.
   * @return The checksum computed for the given raw content.
   */
  public static byte getCheckSum(byte[] rawContent) {
    requireNonNull(rawContent, "rawContent");

    int cs = 0;
    for (int i = 0; i < rawContent[1]; i++) {
      cs ^= rawContent[2 + i];
    }
    return (byte) cs;
  }

  public static byte getCheckSum(byte[] rawContent, int startPos, int endPos) {
    requireNonNull(rawContent, "rawContent");

    int cs = 0;
    for (int i = startPos; i < endPos; i++) {
      cs += rawContent[i];
    }
    return (byte) cs;
  }

  public static String bytesToHexString(byte[] bArray) {
    StringBuilder sb = new StringBuilder(bArray.length);
    String sTemp;
    // i从第3个字节开始，前面2个字节（串口模块地址）不显示
    for (int i = 2; i < bArray.length; i++) {
      sTemp = Integer.toHexString(0xFF & bArray[i]);
      if (sTemp.length() < 2) {
        sb.append(0);
      }
      sb.append(sTemp.toUpperCase());
      sb.append(" ");
    }
    return sb.toString();
  }
  // end::documentation_checksumComp[]
}
