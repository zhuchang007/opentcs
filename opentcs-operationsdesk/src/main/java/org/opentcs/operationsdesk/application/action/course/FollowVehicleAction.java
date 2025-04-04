// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.operationsdesk.application.action.course;

import static java.util.Objects.requireNonNull;
import static org.opentcs.operationsdesk.util.I18nPlantOverviewOperating.VEHICLEPOPUP_PATH;

import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Inject;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import org.opentcs.guing.base.model.elements.VehicleModel;
import org.opentcs.guing.common.components.drawing.OpenTCSDrawingEditor;
import org.opentcs.guing.common.components.drawing.OpenTCSDrawingView;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 */
public class FollowVehicleAction
    extends
      AbstractAction {

  /**
   * Automatically moves the drawing so a vehicle is always visible.
   */
  public static final String ID = "course.vehicle.follow";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(VEHICLEPOPUP_PATH);
  /**
   * The vehicle.
   */
  private final VehicleModel vehicleModel;
  /**
   * The drawing editor.
   */
  private final OpenTCSDrawingEditor drawingEditor;

  /**
   * Creates a new instance.
   *
   * @param vehicle The selected vehicle.
   * @param drawingEditor The application's drawing editor.
   */
  @Inject
  @SuppressWarnings("this-escape")
  public FollowVehicleAction(
      @Assisted
      VehicleModel vehicle,
      OpenTCSDrawingEditor drawingEditor
  ) {
    this.vehicleModel = requireNonNull(vehicle, "vehicle");
    this.drawingEditor = requireNonNull(drawingEditor, "drawingEditor");

    putValue(NAME, BUNDLE.getString("followVehicleAction.name"));
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem) evt.getSource();
    OpenTCSDrawingView drawingView = drawingEditor.getActiveView();

    if (drawingView != null) {
      if (checkBox.isSelected()) {
        drawingView.followVehicle(vehicleModel);
      }
      else {
        drawingView.stopFollowVehicle();
      }
    }
  }

}
