// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.drivers.peripherals.management;

import javax.swing.JPanel;
import org.opentcs.drivers.peripherals.PeripheralProcessModel;

/**
 * A base class for panels associated with peripheral comm adapters.
 */
public abstract class PeripheralCommAdapterPanel
    extends
      JPanel {

  /**
   * Returns the title for this comm adapter panel.
   * The default implementation returns the accessible name from the panel's accessible context.
   *
   * @return The title for this comm adapter panel.
   */
  public String getTitle() {
    return getAccessibleContext().getAccessibleName();
  }

  /**
   * Notifies a comm adapter panel that the corresponding process model changed.
   * The comm adapter panel may want to update the content its representing.
   *
   * @param processModel The new process model.
   */
  public abstract void processModelChanged(PeripheralProcessModel processModel);
}
