// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.controlcenter.vehicles;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.TreeMap;
import org.opentcs.components.Lifecycle;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a pool of {@link VehicleEntry}s with an entry for every {@link Vehicle} object in the
 * kernel.
 */
public class VehicleEntryPool
    implements
      Lifecycle {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(VehicleEntryPool.class);
  /**
   * The object service.
   */
  private final TCSObjectService objectService;
  /**
   * The entries of this pool.
   */
  private final Map<String, VehicleEntry> entries = new TreeMap<>();
  /**
   * Whether the pool is initialized or not.
   */
  private boolean initialized;

  /**
   * Creates a new instance.
   *
   * @param objectService The object service.
   */
  @Inject
  public VehicleEntryPool(
      @Nonnull
      TCSObjectService objectService
  ) {
    this.objectService = requireNonNull(objectService, "objectService");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      LOG.debug("Already initialized.");
      return;
    }

    objectService.fetchObjects(Vehicle.class).stream()
        .forEach(vehicle -> entries.put(vehicle.getName(), new VehicleEntry(vehicle)));
    LOG.debug("Initialized vehicle entry pool: {}", entries);
    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      LOG.debug("Not initialized.");
      return;
    }

    entries.clear();
    initialized = false;
  }

  /**
   * Returns all entries in the pool.
   *
   * @return Map of vehicle names to their vehicle entries.
   */
  @Nonnull
  public Map<String, VehicleEntry> getEntries() {
    return entries;
  }

  /**
   * Returns the {@link VehicleEntry} for the given vehicle name.
   *
   * @param vehicleName The vehicle name to get the entry for.
   * @return the vehicle entry for the given vehicle name.
   */
  @Nullable
  public VehicleEntry getEntryFor(
      @Nonnull
      String vehicleName
  ) {
    requireNonNull(vehicleName, "vehicleName");
    return entries.get(vehicleName);
  }
}
