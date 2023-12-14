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
import uwant.common.vehicle.telegrams.NodeAction;

/** @author zhuchang */
public class RouteTableEntry {
  private int routeId;
  private int nodeId;
  private Map<Integer, NodeAction> nodeActionsMap = new HashMap<>();

  public RouteTableEntry() {
    routeId = 0;
    nodeId = 0;
    nodeActionsMap.put(1, new NodeAction(0, 0, 0));
    nodeActionsMap.put(2, new NodeAction(0, 0, 0));
    nodeActionsMap.put(3, new NodeAction(0, 0, 0));
    nodeActionsMap.put(4, new NodeAction(0, 0, 0));
  }

  public int getRouteId() {
    return routeId;
  }

  public void setRouteId(int routeId) {
    this.routeId = routeId;
  }

  public int getNodeId() {
    return nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public Map<Integer, NodeAction> getNodeActionsMap() {
    return nodeActionsMap;
  }
}
