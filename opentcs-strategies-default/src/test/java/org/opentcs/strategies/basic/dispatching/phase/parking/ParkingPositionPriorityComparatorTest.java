// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.phase.parking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.components.kernel.Dispatcher;
import org.opentcs.data.model.Point;

/**
 */
class ParkingPositionPriorityComparatorTest {

  private ParkingPositionPriorityComparator comparator;

  @BeforeEach
  void setUp() {
    comparator = new ParkingPositionPriorityComparator(new ParkingPositionToPriorityFunction());
  }

  @Test
  void prefersPrioritizedParkingPositions() {
    Point pointWithoutPrio
        = new Point("Point without prio")
            .withType(Point.Type.PARK_POSITION);
    Point pointWithPrio
        = new Point("Point with prio")
            .withType(Point.Type.PARK_POSITION)
            .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "1");

    // Let's try it the one way, ...
    assertThat(
        comparator.compare(pointWithPrio, pointWithoutPrio),
        lessThan(0)
    );
    // ...and the other way.
    assertThat(
        comparator.compare(pointWithoutPrio, pointWithPrio),
        greaterThan(0)
    );
  }

  @Test
  void prefersSmallerPriorityIntegers() {
    Point pointWithLowerPrioValue
        = new Point("Point with lower prio value")
            .withType(Point.Type.PARK_POSITION)
            .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "23");
    Point pointWithHigherPrioValue
        = new Point("Point with higher prio value")
            .withType(Point.Type.PARK_POSITION)
            .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "142");

    // Let's try it the one way, ...
    assertThat(
        comparator.compare(pointWithLowerPrioValue, pointWithHigherPrioValue),
        lessThan(0)
    );
    // ...and the other way.
    assertThat(
        comparator.compare(pointWithHigherPrioValue, pointWithLowerPrioValue),
        greaterThan(0)
    );
  }

  @Test
  void treatsSamePrioritiesEqually() {
    Point point1
        = new Point("Point 1, with prio 4")
            .withType(Point.Type.PARK_POSITION)
            .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "4");
    Point point2
        = new Point("Point 2, also with prio 4")
            .withType(Point.Type.PARK_POSITION)
            .withProperty(Dispatcher.PROPKEY_PARKING_POSITION_PRIORITY, "4");

    // Let's try it the one way, ...
    assertThat(
        comparator.compare(point1, point2),
        is(0)
    );
    // ...and the other way.
    assertThat(
        comparator.compare(point2, point1),
        is(0)
    );
  }
}
