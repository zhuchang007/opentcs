// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.v1.binding;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * An update for a vehicle's list of allowed order types.
 */
public class PutVehicleAllowedOrderTypesTO {

  @Nonnull
  private List<String> orderTypes;

  @JsonCreator
  public PutVehicleAllowedOrderTypesTO(
      @Nonnull
      @JsonProperty(value = "orderTypes", required = true)
      List<String> orderTypes
  ) {
    this.orderTypes = requireNonNull(orderTypes, "orderTypes");
  }

  @Nonnull
  public List<String> getOrderTypes() {
    return orderTypes;
  }

  public PutVehicleAllowedOrderTypesTO setOrderTypes(
      @Nonnull
      List<String> orderTypes
  ) {
    this.orderTypes = requireNonNull(orderTypes, "orderTypes");
    return this;
  }
}
