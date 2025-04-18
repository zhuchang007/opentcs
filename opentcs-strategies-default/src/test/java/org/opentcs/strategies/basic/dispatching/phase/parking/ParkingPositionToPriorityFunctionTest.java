// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.phase.parking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.components.kernel.Dispatcher;
import org.opentcs.data.model.Point;

/**
 */
class ParkingPositionToPriorityFunctionTest {

  private ParkingPositionToPriorityFunction priorityFunction;

  @BeforeEach
  void setUp() {
    priorityFunction = new ParkingPositionToPriorityFunction();
  }

  @Test
  void returnsNullForNonParkingPosition() {
    Point point = new Point("Some point")
        .withType(Point.Type.HALT_POSITION)
        .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "1");

    assertThat(priorityFunction.apply(point), is(nullValue()));
  }

  @Test
  void returnsNullForParkingPositionWithoutPriorityProperty() {
    Point point = new Point("Some point").withType(Point.Type.PARK_POSITION);

    assertThat(priorityFunction.apply(point), is(nullValue()));
  }

  @Test
  void returnsNullForParkingPositionWithNonDecimalProperty() {
    Point point = new Point("Some point")
        .withType(Point.Type.PARK_POSITION)
        .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "abc");

    assertThat(priorityFunction.apply(point), is(nullValue()));
  }

  @Test
  void returnsPriorityForParkingPositionWithDecimalProperty() {
    Point point = new Point("Some point")
        .withType(Point.Type.PARK_POSITION)
        .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "23");

    assertThat(priorityFunction.apply(point), is(23));
  }

}
