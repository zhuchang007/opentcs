// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.base.components.properties.type;

import java.util.ArrayList;
import org.opentcs.guing.base.model.ModelComponent;

/**
 * A property for percentages.
 */
public class PercentProperty
    extends
      AbstractQuantity<PercentProperty.Unit> {

  /**
   * Creates a new instance.
   *
   * @param model The model component.
   */
  public PercentProperty(ModelComponent model) {
    this(model, false);
  }

  /**
   * Creates a new instance.
   *
   * @param model The model component.
   * @param isInteger Whether only the integer part of the value is relevant.
   */
  public PercentProperty(ModelComponent model, boolean isInteger) {
    this(model, Double.NaN, Unit.PERCENT, isInteger);
  }

  /**
   * Creates a new instance with value.
   *
   * @param model The model component.
   * @param value The property's value.
   * @param unit The unit in which the value is measured.
   * @param isInteger Whether only the integer part of the value is relevant.
   */
  @SuppressWarnings("this-escape")
  public PercentProperty(ModelComponent model, double value, Unit unit, boolean isInteger) {
    super(model, value, unit, Unit.class, new ArrayList<Relation<Unit>>());
    setInteger(isInteger);
  }

  @Override // Property
  public Object getComparableValue() {
    return String.valueOf(fValue) + getUnit();
  }

  @Override
  protected void initValidRange() {
    validRange.setMin(0).setMax(100);
  }

  /**
   * The supported units.
   */
  public enum Unit {

    /**
     * Percent.
     */
    PERCENT("%");

    private final String displayString;

    Unit(String displayString) {
      this.displayString = displayString;
    }

    @Override
    public String toString() {
      return displayString;
    }
  }
}
