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

import uwant.common.telegrams.Response;

/** @author zhuchang */
public class ActionResponse
    extends
      Response {

  /** The response type.命令字 */
  public static final byte TYPE = 0x03;

  public ActionResponse(byte[] telegramData) {
    super(telegramData);
    this.commandType = TYPE;
  }
}
