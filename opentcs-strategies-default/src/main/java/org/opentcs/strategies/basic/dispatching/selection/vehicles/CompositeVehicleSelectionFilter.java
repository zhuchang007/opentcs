// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.selection.vehicles;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.opentcs.data.model.Vehicle;
import org.opentcs.strategies.basic.dispatching.selection.VehicleSelectionFilter;

/**
 * A collection of {@link VehicleSelectionFilter}s.
 */
public class CompositeVehicleSelectionFilter
    implements
      VehicleSelectionFilter {

  /**
   * The {@link VehicleSelectionFilter}s.
   */
  private final Set<VehicleSelectionFilter> filters;

  @Inject
  public CompositeVehicleSelectionFilter(Set<VehicleSelectionFilter> filters) {
    this.filters = requireNonNull(filters, "filters");
  }

  @Override
  public Collection<String> apply(Vehicle vehicle) {
    return filters.stream()
        .flatMap(filter -> filter.apply(vehicle).stream())
        .collect(Collectors.toList());
  }
}
