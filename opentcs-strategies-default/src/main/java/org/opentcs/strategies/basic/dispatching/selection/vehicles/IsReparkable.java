// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.selection.vehicles;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.OrderConstants;
import org.opentcs.strategies.basic.dispatching.selection.ReparkVehicleSelectionFilter;

/**
 * Filters vehicles that are reparkable.
 */
public class IsReparkable
    implements
      ReparkVehicleSelectionFilter {

  /**
   * The object service.
   */
  private final TCSObjectService objectService;

  /**
   * Creates a new instance.
   *
   * @param objectService The object service.
   */
  @Inject
  public IsReparkable(TCSObjectService objectService) {
    this.objectService = requireNonNull(objectService, "objectService");
  }

  @Override
  public Collection<String> apply(Vehicle vehicle) {
    return reparkable(vehicle) ? new ArrayList<>() : Arrays.asList(getClass().getName());
  }

  private boolean reparkable(Vehicle vehicle) {
    return vehicle.getIntegrationLevel() == Vehicle.IntegrationLevel.TO_BE_UTILIZED
        && vehicle.hasProcState(Vehicle.ProcState.IDLE)
        && vehicle.hasState(Vehicle.State.IDLE)
        && isParkingPosition(vehicle.getCurrentPosition())
        && vehicle.getOrderSequence() == null
        && hasAcceptableOrderTypesForParking(vehicle);
  }

  private boolean isParkingPosition(TCSObjectReference<Point> positionRef) {
    if (positionRef == null) {
      return false;
    }

    Point position = objectService.fetchObject(Point.class, positionRef);
    return position.isParkingPosition();
  }

  private boolean hasAcceptableOrderTypesForParking(Vehicle vehicle) {
    return vehicle.getAcceptableOrderTypes().stream()
        .anyMatch(
            orderType -> orderType.getName().equals(OrderConstants.TYPE_PARK)
                || orderType.getName().equals(OrderConstants.TYPE_ANY)
        );
  }
}
