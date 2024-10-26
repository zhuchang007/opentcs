// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.phase.parking;

import com.google.common.base.Strings;
import java.util.function.Function;
import org.opentcs.components.kernel.Dispatcher;
import org.opentcs.data.model.Point;

/**
 * Returns the priority of a parking position, if it has any, or <code>null</code>.
 * <p>
 * A priority is returned if the given point is a parking position and has a property with key
 * {@link Dispatcher#PROPKEY_PARKING_POSITION_PRIORITY} and a numeric (decimal) value as understood
 * by {@link Integer#parseInt(java.lang.String)}.
 * If these prerequisites are not met, <code>null</code> is returned.
 * </p>
 */
public class ParkingPositionToPriorityFunction
    implements
      Function<Point, Integer> {

  /**
   * Creates a new instance.
   */
  public ParkingPositionToPriorityFunction() {
  }

  @Override
  public Integer apply(Point point) {
    if (!point.isParkingPosition()) {
      return null;
    }
    String priorityString = point.getProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY);
    if (Strings.isNullOrEmpty(priorityString)) {
      return null;
    }
    try {
      return Integer.parseInt(priorityString);
    }
    catch (NumberFormatException e) {
      return null;
    }
  }
}
