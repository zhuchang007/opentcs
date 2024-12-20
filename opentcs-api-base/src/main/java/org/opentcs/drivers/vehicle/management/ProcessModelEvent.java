// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.drivers.vehicle.management;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import java.io.Serializable;
import org.opentcs.drivers.vehicle.VehicleProcessModel;

/**
 * Instances of this class represent events emitted by/for changes on {@link VehicleProcessModel}s.
 */
public class ProcessModelEvent
    extends
      CommAdapterEvent
    implements
      Serializable {

  /**
   * The attribute's name that changed in the process model.
   */
  private final String attributeChanged;
  /**
   * A serializable representation of the corresponding process model.
   */
  private final VehicleProcessModelTO updatedProcessModel;

  /**
   * Creates a new instance.
   *
   * @param attributeChanged The attribute's name that changed.
   * @param updatedProcessModel A serializable representation of the corresponding process model.
   */
  public ProcessModelEvent(
      @Nonnull
      String attributeChanged,
      @Nonnull
      VehicleProcessModelTO updatedProcessModel
  ) {
    this.attributeChanged = requireNonNull(attributeChanged, "attributeChanged");
    this.updatedProcessModel = requireNonNull(updatedProcessModel, "updatedProcessModel");
  }

  /**
   * Returns the attribute's name that changed in the process model.
   *
   * @return The attribute's name that changed in the process model.
   */
  public String getAttributeChanged() {
    return attributeChanged;
  }

  /**
   * Returns a serializable representation of the corresponding process model.
   *
   * @return A serializable representation of the corresponding process model.
   */
  public VehicleProcessModelTO getUpdatedProcessModel() {
    return updatedProcessModel;
  }
}
