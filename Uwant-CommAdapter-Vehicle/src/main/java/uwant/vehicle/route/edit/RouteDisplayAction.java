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
package uwant.vehicle.route.edit;

import java.util.HashMap;
import java.util.Map;

/** @author zhuchang */
public class RouteDisplayAction {
  private int actionId;
  private String actionName;
  private boolean param1Edit;
  private boolean param2Edit;
  private final Map<Integer, String> param1 = new HashMap<>();
  private final Map<Integer, String> param2 = new HashMap<>();

  /** @return the actionId */
  public int getActionId() {
    return actionId;
  }

  /** @param actionId the actionId to set */
  public void setActionId(int actionId) {
    this.actionId = actionId;
  }

  /** @return the actionName */
  public String getActionName() {
    return actionName;
  }

  /** @param actionName the actionName to set */
  public void setActionName(String actionName) {
    this.actionName = actionName;
  }

  /** @return the param1Edit */
  public boolean isParam1Edit() {
    return param1Edit;
  }

  /** @param param1Edit the param1Edit to set */
  public void setParam1Edit(boolean param1Edit) {
    this.param1Edit = param1Edit;
  }

  /** @return the param2Edit */
  public boolean isParam2Edit() {
    return param2Edit;
  }

  /** @param param2Edit the param2Edit to set */
  public void setParam2Edit(boolean param2Edit) {
    this.param2Edit = param2Edit;
  }

  /** @return the param1 */
  public Map<Integer, String> getParam1() {
    return param1;
  }

  /** @return the param2 */
  public Map<Integer, String> getParam2() {
    return param2;
  }

  @Override
  public String toString() {
    String str =
        "actionId:"
            + actionId
            + " actionName:"
            + actionName
            + " param1Edit:"
            + param1Edit
            + " param2Edit:"
            + param2Edit;

    for (Map.Entry<Integer, String> entry : param1.entrySet()) {
      str += " param1_id:" + entry.getKey();
      str += " param1:" + entry.getValue();
    }
    for (Map.Entry<Integer, String> entry : param2.entrySet()) {
      str += " param2_id:" + entry.getKey();
      str += " param2:" + entry.getValue();
    }
    return str;
  }
}
