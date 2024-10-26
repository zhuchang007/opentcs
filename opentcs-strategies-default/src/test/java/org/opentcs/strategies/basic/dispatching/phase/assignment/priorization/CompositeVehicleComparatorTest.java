// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.phase.assignment.priorization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.theInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opentcs.data.model.Vehicle;
import org.opentcs.strategies.basic.dispatching.DefaultDispatcherConfiguration;
import org.opentcs.strategies.basic.dispatching.priorization.CompositeVehicleComparator;
import org.opentcs.strategies.basic.dispatching.priorization.vehicle.VehicleComparatorIdleFirst;

/**
 */
class CompositeVehicleComparatorTest {

  private CompositeVehicleComparator comparator;
  private DefaultDispatcherConfiguration configuration;
  private Map<String, Comparator<Vehicle>> availableComparators;

  @BeforeEach
  void setUp() {
    configuration = Mockito.mock(DefaultDispatcherConfiguration.class);
    availableComparators = new HashMap<>();

  }

  @Test
  void sortNamesUpForOtherwiseEqualInstances() {
    Mockito.when(configuration.vehiclePriorities())
        .thenReturn(List.of());
    comparator = new CompositeVehicleComparator(configuration, availableComparators);

    Vehicle candidate1 = new Vehicle("AA");
    Vehicle candidate2 = new Vehicle("CC");
    Vehicle candidate3 = new Vehicle("AB");

    List<Vehicle> list = new ArrayList<>();
    list.add(candidate1);
    list.add(candidate2);
    list.add(candidate3);

    Collections.sort(list, comparator);

    assertThat(list.get(0), is(theInstance(candidate1)));
    assertThat(list.get(1), is(theInstance(candidate3)));
    assertThat(list.get(2), is(theInstance(candidate2)));
  }

  @Test
  void sortsByNameAndEnergylevel() {
    Mockito.when(configuration.vehiclePriorities())
        .thenReturn(List.of());
    comparator = new CompositeVehicleComparator(configuration, availableComparators);

    Vehicle candidate1 = new Vehicle("AA").withEnergyLevel(1);
    Vehicle candidate2 = new Vehicle("CC").withEnergyLevel(2);
    Vehicle candidate3 = new Vehicle("BB").withEnergyLevel(2);

    List<Vehicle> list = new ArrayList<>();
    list.add(candidate1);
    list.add(candidate2);
    list.add(candidate3);

    Collections.sort(list, comparator);

    assertThat(list.get(0), is(theInstance(candidate3)));
    assertThat(list.get(1), is(theInstance(candidate2)));
    assertThat(list.get(2), is(theInstance(candidate1)));
  }

  @Test
  void sortsByNameEnergylevelRoutingCost() {

    Mockito.when(configuration.vehiclePriorities())
        .thenReturn(List.of("IDLE_FIRST"));
    availableComparators.put(
        "IDLE_FIRST",
        new VehicleComparatorIdleFirst()
    );

    comparator = new CompositeVehicleComparator(configuration, availableComparators);

    Vehicle candidate1 = new Vehicle("AA").withEnergyLevel(30).withState(Vehicle.State.EXECUTING);
    Vehicle candidate2 = new Vehicle("BB").withEnergyLevel(30).withState(Vehicle.State.IDLE);
    Vehicle candidate3 = new Vehicle("CC").withEnergyLevel(60).withState(Vehicle.State.IDLE);
    Vehicle candidate4 = new Vehicle("DD").withEnergyLevel(60).withState(Vehicle.State.IDLE);

    List<Vehicle> list = new ArrayList<>();
    list.add(candidate1);
    list.add(candidate2);
    list.add(candidate3);
    list.add(candidate4);

    Collections.sort(list, comparator);

    assertThat(list.get(0), is(theInstance(candidate3)));
    assertThat(list.get(1), is(theInstance(candidate4)));
    assertThat(list.get(2), is(theInstance(candidate2)));
    assertThat(list.get(3), is(theInstance(candidate1)));
  }
}
