// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.base.components.properties.type;

import org.opentcs.guing.base.model.ModelComponent;

/**
 * An attribute for coordinates.
 * Examples: 1 mm, 20 cm, 3.4 m, 17.98 km
 */
public class CoordinateProperty
    extends
      LengthProperty {

  /**
   * Creates a new instance of CoordinateProperty.
   *
   * @param model Point- or LocationModel.
   */
  public CoordinateProperty(ModelComponent model) {
    this(model, 0, Unit.MM);
  }

  /**
   * Creates a new instance of CoordinateProperty.
   *
   * @param model Point- or LocationModel.
   * @param value The initial value.
   * @param unit The initial unit.
   */
  public CoordinateProperty(ModelComponent model, double value, Unit unit) {
    super(model, value, unit);
  }

  @Override
  protected void initValidRange() {
    validRange.setMin(Double.NEGATIVE_INFINITY);
  }
}
