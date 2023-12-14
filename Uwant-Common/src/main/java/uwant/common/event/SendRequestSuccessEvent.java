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
import java.util.ResourceBundle;

import uwant.common.telegrams.Request;
import static uwant.common.vehicle.telegrams.I18nTelegrams.BUNDLE_PATH;

/** @author zhuchang */
public class SendRequestSuccessEvent implements Serializable {

  private static final long serialVersionUID = 1202325446186224803L;
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_PATH);
  private final String vehicleName;
  private final Request request;

  public SendRequestSuccessEvent(String vehicleName, Request request) {
    this.vehicleName = vehicleName;
    this.request = request;
  }

  public String getVehicleName() {
    return vehicleName;
  }

  public Request getRequest() {
    return request;
  }

  @Override
  public String toString() {
    return BUNDLE.getString("SendRequestSuccessEvent.Text");
  }
}
