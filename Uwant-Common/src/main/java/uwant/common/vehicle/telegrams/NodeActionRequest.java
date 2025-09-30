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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import org.w3c.dom.Node;
import uwant.common.telegrams.Request;

/**
 * @author zhuchang
 */
@SuppressWarnings("this-escape")
public class NodeActionRequest
    extends
      Request {

  /**
   * The Request type.命令字
   */
  public static final byte TYPE = 0x16;

  public NodeActionRequest(int addr, int agvId, int nodeId, List<NodeAction> listNodeAction) {
    this.addr = addr;
    this.agvId = agvId;
    encodeTelegramContent(nodeId, listNodeAction);
  }

  public NodeActionRequest(byte[] telegram) {
    super(telegram);
  }

  private void encodeTelegramContent(int nodeId, List<NodeAction> listNodeAction) {
    checkArgument(listNodeAction.size() < 5, "listNodeAction larger than 4!");

    encodeTelegramHead(this.addr, this.agvId);
    // Payload of the telegram
    rawContent[2] = (byte) (agvId & 0xff);
    rawContent[3] = TYPE;
    rawContent[4] = (byte) (nodeId & 0xff);
    for (int i = 0; i < listNodeAction.size(); i++) {
      rawContent[5 + 3 * i] = (byte) (listNodeAction.get(i).getActionId() & 0xff);
      rawContent[6 + 3 * i] = (byte) (listNodeAction.get(i).getActionParam1() & 0xff);
      rawContent[7 + 3 * i] = (byte) (listNodeAction.get(i).getActionParam2() & 0xff);
    }
    encodeTelegramTail();
  }
}
