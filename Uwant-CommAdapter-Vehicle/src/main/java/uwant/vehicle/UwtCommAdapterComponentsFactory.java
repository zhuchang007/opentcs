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

import uwant.common.telegrams.RequestResponseMatcherCom;
import uwant.common.telegrams.TelegramSender;
import org.opentcs.data.model.Vehicle;
import uwant.common.netty.ChannelManager;

/** @author zhuchang */
public interface UwtCommAdapterComponentsFactory {

  UwtCommAdapter createUwtCommAdapter(Vehicle vehicle, ChannelManager vehicleChannelManager);

  /**
   * Creates a new {@link RequestResponseMatcherCom}.
   * @param vehicleName
   * @param telegramSender Sends telegrams/requests.
   * @return The created {@link RequestResponseMatcherCom}.
   */
  RequestResponseMatcherCom createRequestResponseMatcherCom(String vehicleName, TelegramSender telegramSender);
}
