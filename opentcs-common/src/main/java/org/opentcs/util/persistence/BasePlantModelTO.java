// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.util.persistence;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * The base class for a plant model transfer object.
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BasePlantModelTO {

  private String version = "";

  /**
   * Creates a new instance.
   */
  public BasePlantModelTO() {
  }

  @XmlAttribute(required = true)
  public String getVersion() {
    return version;
  }

  public BasePlantModelTO setVersion(
      @Nonnull
      String version
  ) {
    requireNonNull(version, "version");
    this.version = version;
    return this;
  }
}
