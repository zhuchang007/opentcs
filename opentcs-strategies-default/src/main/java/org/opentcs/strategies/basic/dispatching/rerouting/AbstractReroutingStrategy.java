// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.rerouting;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.strategies.basic.dispatching.DriveOrderRouteAssigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation of {@link ReroutingStrategy} defining the basic rerouting algorithm.
 */
public abstract class AbstractReroutingStrategy
    implements
      ReroutingStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractReroutingStrategy.class);
  private final TCSObjectService objectService;
  private final DriveOrderMerger driveOrderMerger;
  private final DriveOrderRouteAssigner driveOrderRouteAssigner;

  /**
   * Creates a new instance.
   *
   * @param objectService The object service to use.
   * @param driveOrderMerger Used to restore drive order history for a newly computed route.
   * @param driveOrderRouteAssigner Assigns routes to drive orders.
   */
  protected AbstractReroutingStrategy(
      TCSObjectService objectService,
      DriveOrderMerger driveOrderMerger,
      DriveOrderRouteAssigner driveOrderRouteAssigner
  ) {
    this.objectService = requireNonNull(objectService, "objectService");
    this.driveOrderMerger = requireNonNull(driveOrderMerger, "driveOrderMerger");
    this.driveOrderRouteAssigner = requireNonNull(
        driveOrderRouteAssigner,
        "driveOrderRouteAssigner"
    );
  }

  @Override
  public Optional<List<DriveOrder>> reroute(Vehicle vehicle) {
    TransportOrder currentTransportOrder = objectService.fetchObject(
        TransportOrder.class,
        vehicle.getTransportOrder()
    );

    LOG.debug("{}: Determining the reroute source...", vehicle.getName());
    Optional<Point> optRerouteSource = determineRerouteSource(vehicle);
    if (optRerouteSource.isEmpty()) {
      LOG.warn(
          "{}: Could not determine the reroute source. Not trying to reroute.",
          vehicle.getName()
      );
      return Optional.empty();
    }
    Point rerouteSource = optRerouteSource.get();

    // Get all unfinished drive order of the transport order the vehicle is processing.
    List<DriveOrder> unfinishedOrders = getUnfinishedDriveOrders(currentTransportOrder);

    // Try to get a new route for the unfinished drive orders from the reroute source.
    Optional<List<DriveOrder>> optOrders = tryReroute(vehicle, unfinishedOrders, rerouteSource);

    if (optOrders.isEmpty()) {
      return Optional.empty();
    }

    List<DriveOrder> newDriveOrders = optOrders.get();
    LOG.debug(
        "Found a new route for {} from point {}: {}",
        vehicle.getName(),
        rerouteSource.getName(),
        newDriveOrders
    );
    restoreCurrentDriveOrderHistory(newDriveOrders, vehicle, currentTransportOrder, rerouteSource);

    return Optional.of(newDriveOrders);
  }

  protected TCSObjectService getObjectService() {
    return objectService;
  }

  /**
   * Determines the {@link Point} that should be the source point for the rerouting.
   *
   * @param vehicle The vehicle to determine the reroute source point for.
   * @return The {@link Point} wrapped in an {@link Optional} or {@link Optional#empty()}, if a
   * source point for the rerouting could not be determined.
   */
  protected abstract Optional<Point> determineRerouteSource(Vehicle vehicle);

  /**
   * Returns a list of drive orders that haven't been finished for the given transport order, yet.
   *
   * @param order The transport order to get unfinished drive orders from.
   * @return The list of unfinished drive orders.
   */
  private List<DriveOrder> getUnfinishedDriveOrders(TransportOrder order) {
    List<DriveOrder> result = new ArrayList<>();
    result.add(order.getCurrentDriveOrder());
    result.addAll(order.getFutureDriveOrders());
    return result;
  }

  /**
   * Tries to reroute the given vehicle for the given drive orders.
   *
   * @param vehicle The vehicle to reroute.
   * @param driveOrders The drive orders for which to get a new route.
   * @param sourcePoint The source point to reroute from.
   * @return If rerouting is possible, an {@link Optional} containing the rerouted list of drive
   * orders, otherwise {@link Optional#empty()}.
   */
  private Optional<List<DriveOrder>> tryReroute(
      Vehicle vehicle,
      List<DriveOrder> driveOrders,
      Point sourcePoint
  ) {
    LOG.debug(
        "Trying to reroute drive orders for {} from {}. Current drive orders: {}",
        vehicle.getName(),
        sourcePoint,
        driveOrders
    );
    TransportOrder vehicleOrder = objectService.fetchObject(
        TransportOrder.class,
        vehicle.getTransportOrder()
    );

    return driveOrderRouteAssigner.tryAssignRoutes(
        new TransportOrder("reroute-dummy", driveOrders)
            .withProperties(vehicleOrder.getProperties()),
        vehicle,
        sourcePoint
    );
  }

  private void restoreCurrentDriveOrderHistory(
      List<DriveOrder> newDriveOrders,
      Vehicle vehicle,
      TransportOrder originalOrder,
      Point rerouteSource
  ) {
    // If the vehicle is currently not processing a (drive) order or waiting to get the next
    // drive order (i.e. if it's idle) there is nothing to be restored.
    if (vehicle.hasProcState(Vehicle.ProcState.IDLE)) {
      return;
    }

    // XXX Is a distinction even necessary here, or could the else-part be performed in general?
    if (isPointDestinationOfOrder(rerouteSource, originalOrder.getCurrentDriveOrder())) {
      // The current drive order could not get rerouted, because the vehicle already
      // received all commands for it. Therefore we want to keep the original drive order.
      newDriveOrders.set(0, originalOrder.getCurrentDriveOrder());
    }
    else {
      // Restore the current drive order's history
      DriveOrder newCurrentOrder
          = driveOrderMerger.mergeDriveOrders(
              originalOrder.getCurrentDriveOrder(),
              newDriveOrders.get(0),
              originalOrder,
              originalOrder.getCurrentRouteStepIndex(),
              vehicle
          );
      newDriveOrders.set(0, newCurrentOrder);
    }
  }

  private boolean isPointDestinationOfOrder(Point point, DriveOrder order) {
    if (point == null || order == null) {
      return false;
    }
    if (order.getRoute() == null) {
      return false;
    }
    return Objects.equals(point, order.getRoute().getFinalDestinationPoint());
  }
}
