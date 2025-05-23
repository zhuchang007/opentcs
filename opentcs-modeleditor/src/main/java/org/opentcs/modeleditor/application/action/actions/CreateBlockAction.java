// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.application.action.actions;

import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.SMALL_ICON;
import static org.opentcs.modeleditor.util.I18nPlantOverviewModeling.TOOLBAR_PATH;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.opentcs.guing.common.application.GuiManagerModeling;
import org.opentcs.guing.common.util.ImageDirectory;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * An action to trigger the creation of a block.
 */
public class CreateBlockAction
    extends
      AbstractAction {

  /**
   * This action class's ID.
   */
  public static final String ID = "openTCS.createBlock";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(TOOLBAR_PATH);
  /**
   * The GUI manager instance we're working with.
   */
  private final GuiManagerModeling guiManager;

  /**
   * Creates a new instance.
   *
   * @param guiManager The GUI manager instance we're working with.
   */
  @SuppressWarnings("this-escape")
  public CreateBlockAction(GuiManagerModeling guiManager) {
    this.guiManager = guiManager;

    putValue(NAME, BUNDLE.getString("createBlockAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("createBlockAction.shortDescription"));

    ImageIcon iconSmall = ImageDirectory.getImageIcon("/toolbar/blockdevice-3.16.png");
    ImageIcon iconLarge = ImageDirectory.getImageIcon("/toolbar/blockdevice-3.22.png");
    putValue(SMALL_ICON, iconSmall);
    putValue(LARGE_ICON_KEY, iconLarge);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    guiManager.createBlockModel();
  }
}
