// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.commadapter.peripheral.loopback;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.TCSResourceReference;
import org.opentcs.drivers.peripherals.PeripheralCommAdapterDescription;
import org.opentcs.drivers.peripherals.PeripheralProcessModel;
import org.opentcs.drivers.peripherals.management.PeripheralCommAdapterPanel;
import org.opentcs.drivers.peripherals.management.PeripheralCommAdapterPanelFactory;

/**
 * A factory for creating {@link LoopbackPeripheralCommAdapterPanel} instances.
 */
public class LoopbackPeripheralCommAdapterPanelFactory
    implements
      PeripheralCommAdapterPanelFactory {

  /**
   * The panel components factory to use.
   */
  private final LoopbackPeripheralAdapterPanelComponentsFactory panelComponentsFactory;
  /**
   * Indicates whether this component is initialized or not.
   */
  private boolean initialized;

  @Inject
  public LoopbackPeripheralCommAdapterPanelFactory(
      LoopbackPeripheralAdapterPanelComponentsFactory panelComponentsFactory
  ) {
    this.panelComponentsFactory = requireNonNull(panelComponentsFactory, "panelComponentsFactory");
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

  @Override
  public List<PeripheralCommAdapterPanel> getPanelsFor(
      @Nonnull
      PeripheralCommAdapterDescription description,
      @Nonnull
      TCSResourceReference<Location> location,
      @Nonnull
      PeripheralProcessModel processModel
  ) {
    requireNonNull(description, "description");
    requireNonNull(location, "location");
    requireNonNull(processModel, "processModel");

    if (!providesPanelsFor(description, processModel)) {
      return new ArrayList<>();
    }

    return Arrays.asList(
        panelComponentsFactory
            .createPanel((LoopbackPeripheralProcessModel) processModel)
    );
  }

  private boolean providesPanelsFor(
      PeripheralCommAdapterDescription description,
      PeripheralProcessModel processModel
  ) {
    return (description instanceof LoopbackPeripheralCommAdapterDescription)
        && (processModel instanceof LoopbackPeripheralProcessModel);
  }
}
