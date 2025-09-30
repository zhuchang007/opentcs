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
package uwant.vehicle;

import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/** @author zhuchang */
@ConfigurationPrefix(UwtCommAdapterConfiguration.PREFIX)
public interface UwtCommAdapterConfiguration {
  /** This configuration's prefix. */
  String PREFIX = "uwant.com.commAdapter";

  @ConfigurationEntry(
      type = "String",
      description = "the mode of communication adapter,COM or TCP",
      orderKey = "0_commMode"
  )
  String commMode();

  @ConfigurationEntry(type = "String", description = "The Com Name.", orderKey = "0_comName")
  String comName();

  @ConfigurationEntry(type = "Integer", description = "Com baudRate.", orderKey = "0_comBaudRate")
  int comBaudRate();

  @ConfigurationEntry(
      type = "Integer",
      description = "TCP listening Port",
      orderKey = "0_tcpPort"
  )
  int tcpPort();
}
