/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.dispatching.selection;

import java.util.Collection;
import java.util.function.Function;
import org.opentcs.data.model.Vehicle;

/**
 * A filter for {@link Vehicle}s.
 * Returns a collection of reasons for filtering the vehicle.
 * If the returned collection is empty, no reason to filter it was encountered.
 */
public interface VehicleSelectionFilter
    extends
      Function<Vehicle, Collection<String>> {
}
