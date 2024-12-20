// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.util;

import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/**
 * Provides methods to configure the naming convention for model elements.
 */
@ConfigurationPrefix(ElementNamingSchemeConfiguration.PREFIX)
public interface ElementNamingSchemeConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "elementnamingscheme";

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new point element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_point_0"
  )
  String pointPrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new point element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_point_1"
  )
  String pointNumberPattern();

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new path element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "1_path_0"
  )
  String pathPrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new path element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "1_path_1"
  )
  String pathNumberPattern();

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new location type element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "2_loctype_0"
  )
  String locationTypePrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new location type element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "2_loctype_1"
  )
  String locationTypeNumberPattern();

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new location element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "3_loc_0"
  )
  String locationPrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new location element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "3_loc_1"
  )
  String locationNumberPattern();

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new link element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "4_link_0"
  )
  String linkPrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new link element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "4_link_1"
  )
  String linkNumberPattern();

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new block.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "5_block_0"
  )
  String blockPrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new block.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "5_block_1"
  )
  String blockNumberPattern();

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new layout element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "7_layout_0"
  )
  String layoutPrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new layout element.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "7_layout_1"
  )
  String layoutNumberPattern();

  @ConfigurationEntry(
      type = "String",
      description = "The default prefix for a new vehicle.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "8_vehicle_0"
  )
  String vehiclePrefix();

  @ConfigurationEntry(
      type = "String",
      description = "The numbering pattern for a new vehicle.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "8_vehicle_1"
  )
  String vehicleNumberPattern();

}
