// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.application.menus.menubar;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.opentcs.components.plantoverview.PlantModelImporter;
import org.opentcs.guing.common.application.GuiManager;
import org.opentcs.modeleditor.application.action.file.ImportPlantModelAction;
import org.opentcs.modeleditor.util.I18nPlantOverviewModeling;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 */
public class FileImportMenu
    extends
      JMenu {

  private static final ResourceBundleUtil LABELS
      = ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.MENU_PATH);

  @Inject
  @SuppressWarnings("this-escape")
  public FileImportMenu(
      Set<PlantModelImporter> importers,
      GuiManager guiManager
  ) {
    super(LABELS.getString("fileImportMenu.text"));
    requireNonNull(importers, "importers");
    requireNonNull(guiManager, "guiManager");

    for (PlantModelImporter importer : importers) {
      add(new JMenuItem(new ImportPlantModelAction(importer, guiManager)));
    }
  }
}
