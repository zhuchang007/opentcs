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
import static uwant.vehicle.telegrams.I18nTelegrams.BUNDLE_PATH;

/** @author zhuchang */
public class SendRequestFailedEvent implements Serializable {

  private static final long serialVersionUID = 2293760729294843025L;
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_PATH);

  private final Request request;
  private final String vehicleName;

  public SendRequestFailedEvent(String vehicleName, Request request) {
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
    return BUNDLE.getString("SendRequestFailedEvent.Text");
  }
}
