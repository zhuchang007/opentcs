// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.application;

import java.util.Objects;
import org.opentcs.access.Kernel;

/**
 * Defines the plant overview's potential modes of operation.
 */
public enum OperationMode {

  /**
   * For cases in which the mode of operation has not been defined, yet.
   */
  UNDEFINED,
  /**
   * Used when modelling a driving course.
   */
  MODELLING,
  /**
   * Used when operating a plant/system.
   */
  OPERATING;

  /**
   * Returns the equivalent operation mode to the given kernel state.
   *
   * @param state The kernel state.
   * @return The equivalent operation mode to the given kernel state.
   */
  public static OperationMode equivalent(Kernel.State state) {
    if (Objects.equals(state, Kernel.State.MODELLING)) {
      return MODELLING;
    }
    else if (Objects.equals(state, Kernel.State.OPERATING)) {
      return OPERATING;
    }
    else {
      return UNDEFINED;
    }
  }

  public static Kernel.State equivalent(OperationMode mode) {
    if (Objects.equals(mode, MODELLING)) {
      return Kernel.State.MODELLING;
    }
    else if (Objects.equals(mode, OPERATING)) {
      return Kernel.State.OPERATING;
    }
    else {
      return null;
    }
  }
}
