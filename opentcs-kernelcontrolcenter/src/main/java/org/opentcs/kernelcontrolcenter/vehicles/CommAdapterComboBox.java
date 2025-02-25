// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernelcontrolcenter.vehicles;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.JComboBox;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

/**
 * A wide combobox which sets the selected item when receiving an update event from a
 * {@link LocalVehicleEntry}.
 */
public class CommAdapterComboBox
    extends
      JComboBox<VehicleCommAdapterDescription>
    implements
      PropertyChangeListener {

  /**
   * Creates a new instance.
   */
  public CommAdapterComboBox() {
  }

  @Override
  public VehicleCommAdapterDescription getSelectedItem() {
    return (VehicleCommAdapterDescription) super.getSelectedItem();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (!(evt.getSource() instanceof LocalVehicleEntry)) {
      return;
    }

    LocalVehicleEntry entry = (LocalVehicleEntry) evt.getSource();
    if (Objects.equals(entry.getAttachedCommAdapterDescription(), getModel().getSelectedItem())) {
      return;
    }

    super.setSelectedItem(entry.getAttachedCommAdapterDescription());
  }

}
