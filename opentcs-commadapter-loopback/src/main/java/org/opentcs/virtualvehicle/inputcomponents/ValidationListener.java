// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.virtualvehicle.inputcomponents;

import java.util.EventListener;

/**
 * A listener interface for {@link ValidationEvent ValidationEvents}.
 */
public interface ValidationListener
    extends
      EventListener {

  /**
   * Should be called when the state of validation changed.
   *
   * @param e The ValidationEvent containing validation information.
   */
  void validityChanged(ValidationEvent e);
}
