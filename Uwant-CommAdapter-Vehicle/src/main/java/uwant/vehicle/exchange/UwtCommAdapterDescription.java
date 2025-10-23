/**
 * Copyright (c) The openTCS Authors.
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uwant.vehicle.exchange;

import static uwant.vehicle.exchange.I18nUwantComAdapter.BUNDLE_PATH;

import java.util.ResourceBundle;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

/**
 * uwt commAdapter Description.
 *
 * @author zhuchang
 */
public class UwtCommAdapterDescription
    extends
      VehicleCommAdapterDescription {


  public UwtCommAdapterDescription() {
  }

  @Override
  public String getDescription() {
    return ResourceBundle.getBundle(BUNDLE_PATH).getString("UwtCommAdapterFactoryDescription");
  }

  @Override
  public boolean isSimVehicleCommAdapter() {
    return false;
  }
}
