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
package uwant.vehicle.exchange;

import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import uwant.vehicle.route.edit.RoutePanel;

/** @author zhuchang */
public interface AdapterPanelComponentsFactory {
  UwtPanel createCOMPanel(TCSObjectReference<Vehicle> vehicle, VehicleService vehicleService, VehicleProcessModelTO processModel);

  RoutePanel createRoutePanel(VehicleService vehicleService, VehicleProcessModelTO processModel);

}
