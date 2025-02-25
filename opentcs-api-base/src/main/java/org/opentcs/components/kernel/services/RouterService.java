// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.components.kernel.services;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.components.kernel.Router;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.TCSResourceReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.Route;
import org.opentcs.util.annotations.ScheduledApiChange;

/**
 * Provides methods concerning the {@link Router}.
 */
public interface RouterService {

  /**
   * Notifies the router that the topology has changed with respect to the given paths and needs to
   * be re-evaluated.
   * <p>
   * If called within the kernel application, this method is supposed to be called only on the
   * kernel executor thread.
   * </p>
   *
   * @param refs References to paths that have changed in the routing topology. An empty set of
   * path references results in the router updating the entire routing topology.
   * @throws KernelRuntimeException In case there is an exception executing this method.
   */
  void updateRoutingTopology(Set<TCSObjectReference<Path>> refs)
      throws KernelRuntimeException;

  /**
   * Computes routes for the given vehicle from a source point to a set of destination points.
   * <p>
   * If called within the kernel application, this method is supposed to be called only on the
   * kernel executor thread.
   * </p>
   *
   * @param vehicleRef A reference to the vehicle to calculate the routes for.
   * @param sourcePointRef A reference to the source point.
   * @param destinationPointRefs A set of references to the destination points.
   * @param resourcesToAvoid A set of references to resources that are to be avoided.
   * @return A map of destination points to the corresponding computed routes or {@code null}, if
   * no route could be determined for a specific destination point.
   * @throws KernelRuntimeException In case there is an exception executing this method.
   * @deprecated Use {@link #computeRoutes(TCSObjectReference, TCSObjectReference, Set, Set, int)}
   * instead.
   */
  @Deprecated
  Map<TCSObjectReference<Point>, Route> computeRoutes(
      TCSObjectReference<Vehicle> vehicleRef,
      TCSObjectReference<Point> sourcePointRef,
      Set<TCSObjectReference<Point>> destinationPointRefs,
      Set<TCSResourceReference<?>> resourcesToAvoid
  )
      throws KernelRuntimeException;

  /**
   * Computes possible routes for the given vehicle from a source point to a set of destination
   * points.
   * <p>
   * If called within the kernel application, this method is supposed to be called only on the
   * kernel executor thread.
   * </p>
   *
   * @param vehicleRef A reference to the vehicle to calculate the routes for.
   * @param sourcePointRef A reference to the source point.
   * @param destinationPointRefs A set of references to the destination points.
   * @param resourcesToAvoid A set of references to resources that are to be avoided.
   * @param maxRoutesPerDestinationPoint The maximum number of routes to compute for each
   * destination point.
   * @return A map of destination points to a corresponding set of computed routes. If no routes
   * could be determined for a specific destination point, the corresponding set is empty.
   * @throws KernelRuntimeException In case there is an exception executing this method.
   */
  @ScheduledApiChange(when = "7.0", details = "Default implementation will be removed.")
  default Map<TCSObjectReference<Point>, Set<Route>> computeRoutes(
      TCSObjectReference<Vehicle> vehicleRef,
      TCSObjectReference<Point> sourcePointRef,
      Set<TCSObjectReference<Point>> destinationPointRefs,
      Set<TCSResourceReference<?>> resourcesToAvoid,
      int maxRoutesPerDestinationPoint
  )
      throws KernelRuntimeException {
    return computeRoutes(vehicleRef, sourcePointRef, destinationPointRefs, resourcesToAvoid)
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> Optional.ofNullable(entry.getValue()).map(Set::of).orElse(Set.of())
            )
        );
  }
}
