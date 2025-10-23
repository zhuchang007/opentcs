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

import static java.util.Objects.requireNonNull;

import com.google.inject.Inject;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.VehicleCommAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.netty.ChannelManager;
import uwant.vehicle.exchange.UwtCommAdapterDescription;

/** @author zhuchang */
public class UwtCommAdapterFactory
    implements
      VehicleCommAdapterFactory {

  private static final Logger LOG = LoggerFactory.getLogger(UwtCommAdapterFactory.class);

  private final UwtCommAdapterComponentsFactory componentsFactory;
  private final ChannelManager vehicleChannelManager;
  private boolean initialized;

  @Inject
  public UwtCommAdapterFactory(
      UwtCommAdapterComponentsFactory componentsFactory, ChannelManager vehicleChannelManager
  ) {
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    this.vehicleChannelManager = requireNonNull(vehicleChannelManager, "vehicleChannelManager");
  }

  @Override
  public void initialize() {
    if (initialized) {
      return;
    }
    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!initialized) {
      return;
    }
    initialized = false;
  }

  @Override
  public VehicleCommAdapterDescription getDescription() {
    return new UwtCommAdapterDescription();
  }


  @Override
  public boolean providesAdapterFor(Vehicle vehicle) {
    return true;
  }

  @Override
  public VehicleCommAdapter getAdapterFor(Vehicle vehicle) {
    requireNonNull(vehicle, "vehicle");
    if (!providesAdapterFor(vehicle)) {
      return null;
    }

    return componentsFactory.createUwtCommAdapter(vehicle, vehicleChannelManager);
  }
}
