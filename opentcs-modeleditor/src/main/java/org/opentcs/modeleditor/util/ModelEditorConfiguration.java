// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.util;

import org.opentcs.components.plantoverview.LocationTheme;
import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;
import org.opentcs.guing.common.exchange.ApplicationPortalProviderConfiguration;

/**
 * Provides methods to configure the Model Editor application.
 */
@ConfigurationPrefix(ModelEditorConfiguration.PREFIX)
public interface ModelEditorConfiguration
    extends
      ApplicationPortalProviderConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "modeleditor";

  @ConfigurationEntry(
      type = "String",
      description = {"The plant overview application's locale, as a BCP 47 language tag.",
          "Examples: 'en', 'de', 'zh'"},
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_init_0"
  )
  String locale();

  @ConfigurationEntry(
      type = "Class name",
      description = {
          "The name of the class to be used for the location theme.",
          "Must be a class extending org.opentcs.components.plantoverview.LocationTheme"
      },
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "3_themes_0"
  )
  Class<? extends LocationTheme> locationThemeClass();
}
