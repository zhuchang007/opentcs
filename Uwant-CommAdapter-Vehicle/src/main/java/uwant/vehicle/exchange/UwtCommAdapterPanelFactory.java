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

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import org.opentcs.access.KernelServicePortal;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanelFactory;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author zhuchang */
public class UwtCommAdapterPanelFactory implements VehicleCommAdapterPanelFactory {

  private static final Logger LOG = LoggerFactory.getLogger(UwtCommAdapterPanelFactory.class);

  private final AdapterPanelComponentsFactory componentsFactory;

  /** The service portal. */
  private final KernelServicePortal servicePortal;

  private boolean initialized;

  @Inject
  public UwtCommAdapterPanelFactory(
      AdapterPanelComponentsFactory componentsFactory, KernelServicePortal servicePortal) {
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    this.servicePortal = requireNonNull(servicePortal, "servicePortal");
  }

  @Override
  public List<VehicleCommAdapterPanel> getPanelsFor(
      @Nonnull VehicleCommAdapterDescription description,
      @Nonnull TCSObjectReference<Vehicle> vehicle,
      @Nonnull VehicleProcessModelTO processModel) {
    if (!providesPanelsFor(description, processModel)) {
      LOG.debug("Cannot provide panels for '{}' with '{}'.", description, processModel);
      return new ArrayList<>();
    }
    List<VehicleCommAdapterPanel> panels = new ArrayList<>();
    panels.add(componentsFactory.createCOMPanel(vehicle, servicePortal.getVehicleService(), processModel));
    panels.add(componentsFactory.createRoutePanel(servicePortal.getVehicleService(), processModel));
    return panels;
  }

  /**
   * Checks whether this factory can provide comm adapter panels for the given description and the
   * given type of process model.
   *
   * @param description The description to check for.
   * @param processModel The process model.
   * @return {@code true} if, and only if, this factory can provide comm adapter panels for the
   *     given description and the given type of process model.
   */
  private boolean providesPanelsFor(
      VehicleCommAdapterDescription description, VehicleProcessModelTO processModel) {
    return (description instanceof UwtCommAdapterDescription)
        && (processModel instanceof UwtProcessModelTO);
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
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
    if (!isInitialized()) {
      return;
    }

    initialized = false;
  }
}
