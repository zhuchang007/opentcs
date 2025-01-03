// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching;

import static java.util.Objects.requireNonNull;
import static org.opentcs.util.Assertions.checkArgument;

import java.util.List;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.TransportOrder;

/**
 * Contains information for a potential assignment of a transport order to a vehicle.
 */
public class AssignmentCandidate {

  /**
   * The vehicle.
   */
  private final Vehicle vehicle;
  /**
   * The transport order.
   */
  private final TransportOrder transportOrder;
  /**
   * The route/drive orders to be executed upon assignment.
   */
  private final List<DriveOrder> driveOrders;
  /**
   * The completeRoutingCosts for processing the whole order with the vehicle.
   */
  private final long completeRoutingCosts;

  /**
   * Creates a new instance.
   *
   * @param vehicle The vehicle that would be assigned to the transport order.
   * @param transportOrder The transport order that would be assigned to the vehicle.
   * @param driveOrders The drive orders containing the computed route the vehicle would take.
   * May not be empty and the route of each drive order may not be null.
   */
  public AssignmentCandidate(
      Vehicle vehicle,
      TransportOrder transportOrder,
      List<DriveOrder> driveOrders
  ) {
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.transportOrder = requireNonNull(transportOrder, "transportOrder");
    this.driveOrders = requireNonNull(driveOrders, "driveOrders");
    checkArgument(!driveOrders.isEmpty(), "driveOrders is empty");
    driveOrders.forEach(
        driveOrder -> checkArgument(
            driveOrder.getRoute() != null,
            "a drive order's route is null"
        )
    );
    this.completeRoutingCosts = cumulatedCosts(driveOrders);
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public TransportOrder getTransportOrder() {
    return transportOrder;
  }

  public List<DriveOrder> getDriveOrders() {
    return driveOrders;
  }

  /**
   * Returns the costs for travelling only the first drive order/reaching the first destination.
   *
   * @return The costs for travelling only the first drive order.
   */
  public long getInitialRoutingCosts() {
    return driveOrders.get(0).getRoute().getCosts();
  }

  /**
   * Returns the costs for travelling all drive orders.
   *
   * @return The costs for travelling all drive orders.
   */
  public long getCompleteRoutingCosts() {
    return completeRoutingCosts;
  }

  private static long cumulatedCosts(List<DriveOrder> driveOrders) {
    return driveOrders.stream().mapToLong(driveOrder -> driveOrder.getRoute().getCosts()).sum();
  }
}
