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

/** @author zhuchang */
public class NodeAction {
  private int actionId;
  private int actionParam1;
  private int actionParam2;

  public NodeAction(int actionId, int actionParam1, int actionParam2) {
    this.actionId = actionId;
    this.actionParam1 = actionParam1;
    this.actionParam2 = actionParam2;
  }

  public int getActionId() {
    return actionId;
  }

  public int getActionParam1() {
    return actionParam1;
  }

  public int getActionParam2() {
    return actionParam2;
  }

  public void setActionId(int actionId) {
    this.actionId = actionId;
  }

  public void setActionParam1(int actionParam1) {
    this.actionParam1 = actionParam1;
  }

  public void setActionParam2(int actionParam2) {
    this.actionParam2 = actionParam2;
  }

  public enum NodeActionType {
    DETECTION(0, "检测"),
    FORWARD(1, "前进"),
    BACKWARD(2, "后退"),
    TURN_LEFT(3, "左转"),
    TURN_RIGHT(4, "右转"),
    PATROL_FORWARD(5, "前巡"),
    PATROL_BACKWARD(6, "后巡"),
    BRANCH_MIDDLE(7, "分叉直行"),
    BRANCH_LEFT(8, "分叉左转"),
    BRANCH_RIGHT(9, "分叉右转"),
    STOP_OR_DELAY(10, "停止/延时"),
    SPEED_SET(11, "速度指定"),
    CARD_READ_SHIELD(12, "读卡屏蔽"),
    GEAR_3(13, "3档"),
    GEAR_4(14, "4档"),
    GEAR_5(15, "5档"),
    ALARM(16, "报警"),
    TURN_TO_LINE(17, "转到线条"),
    JACK_UP_SWITCH(18, "顶升开关"),
    AVOID_SWITCH(19, "避障开关"),
    QUICK_STOP(20, "急停"),
    SWITCH_ROUTE(21, "切换路线"),
    TRANSPLANT(22, "升降移栽"),
    RESTORE_PATROL(23, "恢复巡线"),
    TURN_RIGHT_90_DEGREE(24, "右转90度"),
    TURN_RIGHT_180_DEGREE(25, "右转180度"),
    OUTPUT(26, "输出"),
    DATA_UPLOAD(27, "数据上传"),
    NODE_JUMP(28, "节点跳转"),
    TURN_LEFT_90_DEGREE(29, "左转90度"),
    TURN_LEFT_180_DEGREE(30, "做换180度"),
    TURN_RIGHT_DEGREE_SET(31, "右转指定度数"),
    TURN_LEFT_DEGREE_SET(32, "左转指定度数"),
    VEHICLE_CHANGE_OVER(33, "车体换向"),
    LOGICAL_CHANGE_OVER(34, "逻辑换向"),
    WAIT(35, "等待");

    private int value;
    private String name;

    private NodeActionType(int value, String name) {
      this.value = value;
      this.name = name;
    }

    public int getValue() {
      return value;
    }

    public String getName() {
      return name;
    }
  }
}
