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
import java.util.List;
import static com.google.common.base.Preconditions.checkArgument;

/** @author zhuchang */
public class NodeActionSetRequest extends Request {

  /** The Request type.命令字 */
  public static final byte TYPE = 0x16;
  /** The expected length of a telegram of this type. */
  public static final int TELEGRAM_LENGTH = 22;
  /** The position of the checksum byte. */
  public static final int CHECKSUM_POS = TELEGRAM_LENGTH - 2;

  public NodeActionSetRequest(int addr, int agvId, int nodeId, List<NodeAction> listNodeAction) {
    super(TELEGRAM_LENGTH);
    encodeTelegramContent(addr, agvId, nodeId, listNodeAction);
  }

  @Override
  public void updateRequestContent(int telegramId) {}

  private void encodeTelegramContent(
      int addr, int agvId, int nodeId, List<NodeAction> listNodeAction) {
    checkArgument(listNodeAction.size() < 5, "listNodeAction larger than 4!");
    id = agvId;
    // Start of each telegram
    rawContent[0] = (byte) ((addr >> 8) & 0xff);
    rawContent[1] = (byte) (addr & 0xff);

    // Payload of the telegram
    rawContent[2] = (byte) (id & 0xff);
    rawContent[3] = TYPE;
    rawContent[4] = (byte) (nodeId & 0xff);

    for (int i = 0; i < listNodeAction.size(); i++) {
      rawContent[5 + 3 * i] = (byte) (listNodeAction.get(i).getActionId() & 0xff);
      rawContent[6 + 3 * i] = (byte) (listNodeAction.get(i).getActionParam1() & 0xff);
      rawContent[7 + 3 * i] = (byte) (listNodeAction.get(i).getActionParam2() & 0xff);
    }

    // End of each telegram
    rawContent[CHECKSUM_POS] = getCheckSum(rawContent, 3, CHECKSUM_POS);
    rawContent[TELEGRAM_LENGTH - 1] = (byte) (0xff - rawContent[2]);
  }
}
