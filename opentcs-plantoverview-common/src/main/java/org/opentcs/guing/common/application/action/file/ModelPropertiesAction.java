// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.application.action.file;

import static java.util.Objects.requireNonNull;
import static org.opentcs.guing.common.util.I18nPlantOverview.MENU_PATH;

import jakarta.inject.Inject;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.opentcs.customizations.plantoverview.ApplicationFrame;
import org.opentcs.data.ObjectPropConstants;
import org.opentcs.guing.common.persistence.ModelManager;
import org.opentcs.guing.common.util.I18nPlantOverview;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * Shows a message window with some of the currently loaded model's properties.
 */
public class ModelPropertiesAction
    extends
      AbstractAction {

  /**
   * This action's ID.
   */
  public static final String ID = "file.modelProperties";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);
  /**
   * The parent component for dialogs shown by this action.
   */
  private final Component dialogParent;
  /**
   * Provides the current system model.
   */
  private final ModelManager modelManager;

  @Inject
  @SuppressWarnings("this-escape")
  public ModelPropertiesAction(
      @ApplicationFrame
      Component dialogParent,
      ModelManager modelManager
  ) {
    this.dialogParent = requireNonNull(dialogParent, "dialogParent");
    this.modelManager = requireNonNull(modelManager, "modelManager");

    putValue(NAME, BUNDLE.getString("modelPropertiesAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("modelPropertiesAction.shortDescription"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ResourceBundleUtil bundle
        = ResourceBundleUtil.getBundle(I18nPlantOverview.MODELPROPERTIES_PATH);

    JOptionPane.showMessageDialog(
        dialogParent,
        "<html><p><b>" + modelManager.getModel().getName() + "</b><br>"
            + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfPoints")
            + numberOfPoints()
            + "<br>"
            + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfPaths")
            + numberOfPaths()
            + "<br>"
            + bundle.getString(
                "modelPropertiesAction.optionPane_properties.message.numberOfLocations"
            )
            + numberOfLocations()
            + "<br>"
            + bundle.getString(
                "modelPropertiesAction.optionPane_properties.message.numberOfLocationTypes"
            )
            + numberOfLocationTypes()
            + "<br>"
            + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfBlocks")
            + numberOfBlocks()
            + "<br>"
            + bundle.getString(
                "modelPropertiesAction.optionPane_properties.message.numberOfVehicles"
            )
            + numberOfVehicles()
            + "<br>"
            + "<br>"
            + bundle.getString("modelPropertiesAction.optionPane_properties.message.lastModified")
            + lastModified()
            + "</p></html>"
    );

  }

  private String lastModified() {
    return modelManager.getModel().getPropertyMiscellaneous().getItems().stream()
        .filter(kvp -> Objects.equals(kvp.getKey(), ObjectPropConstants.MODEL_FILE_LAST_MODIFIED))
        .findAny()
        .map(kvp -> kvp.getValue())
        .orElse("?");
  }

  private int numberOfPoints() {
    return modelManager.getModel().getPointModels().size();
  }

  private int numberOfPaths() {
    return modelManager.getModel().getPathModels().size();
  }

  private int numberOfLocations() {
    return modelManager.getModel().getLocationModels().size();
  }

  private int numberOfLocationTypes() {
    return modelManager.getModel().getLocationTypeModels().size();
  }

  private int numberOfBlocks() {
    return modelManager.getModel().getBlockModels().size();
  }

  private int numberOfVehicles() {
    return modelManager.getModel().getVehicleModels().size();
  }
}
