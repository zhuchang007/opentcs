// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.phase.assignment.priorization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.theInstance;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.opentcs.data.order.Route.Step;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.strategies.basic.dispatching.AssignmentCandidate;
import org.opentcs.strategies.basic.dispatching.priorization.candidate.CandidateComparatorByDeadline;

/**
 * Unit tests for {@link CandidateComparatorByDeadline}.
 */
class CandidateComparatorByDeadlineTest {

  private CandidateComparatorByDeadline comparator;

  @BeforeEach
  void setUp() {
    comparator = new CandidateComparatorByDeadline();
  }

  @Test
  void sortEarlyDeadlinesUp() {
    Instant deadline = Instant.now();
    AssignmentCandidate candidate1 = candidateWithDeadline(deadline.plusMillis(50));
    AssignmentCandidate candidate2 = candidateWithDeadline(deadline.plusMillis(200));
    AssignmentCandidate candidate3 = candidateWithDeadline(deadline.plusMillis(100));

    List<AssignmentCandidate> list = new ArrayList<>();
    list.add(candidate1);
    list.add(candidate2);
    list.add(candidate3);

    Collections.sort(list, comparator);

    assertThat(list.get(0), is(theInstance(candidate1)));
    assertThat(list.get(1), is(theInstance(candidate3)));
    assertThat(list.get(2), is(theInstance(candidate2)));
  }

  private AssignmentCandidate candidateWithDeadline(Instant time) {
    TransportOrder deadlinedOrder = new TransportOrder("Some order", new ArrayList<>())
        .withDeadline(time);
    Step dummyStep
        = new Route.Step(
            null, new Point("Point1"), new Point("Point2"), Vehicle.Orientation.FORWARD, 1, 10
        );
    Route route = new Route(Arrays.asList(dummyStep));
    List<DriveOrder> driveOrders = List.of(
        new DriveOrder(
            "some-drive-order", new DriveOrder.Destination(new Point("Point2").getReference())
        )
            .withRoute(route)
    );

    return new AssignmentCandidate(
        new Vehicle("Vehicle1"),
        deadlinedOrder,
        driveOrders
    );
  }

}
