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
import com.google.common.primitives.Ints;
import uwant.common.telegrams.Response;
import static uwant.common.telegrams.Telegram.getCheckSum;
import static java.util.Objects.requireNonNull;

/** @author zhuchang */
public class NodeActionSetResponse extends Response {
  /** The response type.命令字 */
  public static final byte TYPE = 0x16;

  public NodeActionSetResponse(byte[] telegramData) {
    super(telegramData);
  }
}
