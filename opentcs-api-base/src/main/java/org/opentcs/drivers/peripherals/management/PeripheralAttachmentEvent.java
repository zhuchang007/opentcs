// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.drivers.peripherals.management;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import java.io.Serializable;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.TCSResourceReference;

/**
 * Instances of this class represent events emitted by/for attaching comm adapters.
 */
public class PeripheralAttachmentEvent
    extends
      PeripheralCommAdapterEvent
    implements
      Serializable {

  /**
   * The location a peripheral comm adapter has been attached to.
   */
  private final TCSResourceReference<Location> location;
  /**
   * The information to the actual attachment.
   */
  private final PeripheralAttachmentInformation attachmentInformation;

  /**
   * Creates a new instance.
   *
   * @param location The location a comm adapter has been attached to.
   * @param attachmentInformation The information to the actual attachment.
   */
  public PeripheralAttachmentEvent(
      @Nonnull
      TCSResourceReference<Location> location,
      @Nonnull
      PeripheralAttachmentInformation attachmentInformation
  ) {
    this.location = requireNonNull(location, "location");
    this.attachmentInformation = requireNonNull(attachmentInformation, "attachmentInformation");
  }

  /**
   * Returns the location a comm adapter has been attached to.
   *
   * @return The location a comm adapter has been attached to.
   */
  public TCSResourceReference<Location> getLocation() {
    return location;
  }

  /**
   * Returns the information to the actual attachment.
   *
   * @return The information to the actual attachment.
   */
  public PeripheralAttachmentInformation getAttachmentInformation() {
    return attachmentInformation;
  }
}
