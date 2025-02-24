// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.phase.parking;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import org.opentcs.components.kernel.services.InternalTransportOrderService;
import org.opentcs.data.model.Vehicle;
import org.opentcs.strategies.basic.dispatching.DefaultDispatcherConfiguration;
import org.opentcs.strategies.basic.dispatching.DriveOrderRouteAssigner;
import org.opentcs.strategies.basic.dispatching.TransportOrderUtil;
import org.opentcs.strategies.basic.dispatching.selection.candidates.CompositeAssignmentCandidateSelectionFilter;
import org.opentcs.strategies.basic.dispatching.selection.vehicles.CompositeParkVehicleSelectionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates parking orders for idle vehicles not already at a parking position considering all
 * parking positions.
 */
public class ParkIdleVehiclesPhase
    extends
      AbstractParkingPhase {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ParkIdleVehiclesPhase.class);
  /**
   * A filter for selecting vehicles that may be parked.
   */
  private final CompositeParkVehicleSelectionFilter vehicleSelectionFilter;

  @Inject
  public ParkIdleVehiclesPhase(
      InternalTransportOrderService orderService,
      ParkingPositionSupplier parkingPosSupplier,
      CompositeAssignmentCandidateSelectionFilter assignmentCandidateSelectionFilter,
      TransportOrderUtil transportOrderUtil,
      DefaultDispatcherConfiguration configuration,
      CompositeParkVehicleSelectionFilter vehicleSelectionFilter,
      DriveOrderRouteAssigner driveOrderRouteAssigner
  ) {
    super(
        orderService,
        parkingPosSupplier,
        assignmentCandidateSelectionFilter,
        transportOrderUtil,
        configuration,
        driveOrderRouteAssigner
    );
    this.vehicleSelectionFilter = requireNonNull(vehicleSelectionFilter, "vehicleSelectionFilter");
  }

  @Override
  public void run() {
    if (!getConfiguration().parkIdleVehicles()) {
      return;
    }

    LOG.debug("Looking for vehicles to send to parking positions...");

    getOrderService().fetchObjects(Vehicle.class).stream()
        .filter(vehicle -> vehicleSelectionFilter.apply(vehicle).isEmpty())
        .forEach(vehicle -> createParkingOrder(vehicle));
  }
}
