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
package uwant.common.event;

import java.io.Serializable;
import uwant.common.telegrams.Request;

/** @author zhuchang */
public class SendRequestEvent implements Serializable {

  private static final long serialVersionUID = -588552123127735593L;

  private final String vehicleName;
  private final Request request;
  private final int routeId;
  private final int sendCount;

  public SendRequestEvent(String vehicleName, Request request, int routeId, int sendCount) {
    this.vehicleName = vehicleName;
    this.request = request;
    this.routeId = routeId;
    this.sendCount = sendCount;
  }

  public String getVehicleName() {
    return vehicleName;
  }

  @Override
  public String toString() {
    if (routeId < 0) {
      return request.getHexRawContent() + " >> " + sendCount;
    } else {
      return "Route " + routeId + ": " + request.getHexRawContent() + " >> " + sendCount;
    }
  }
}
