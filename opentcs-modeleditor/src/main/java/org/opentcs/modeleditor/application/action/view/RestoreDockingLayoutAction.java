// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.application.action.view;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.opentcs.modeleditor.application.OpenTCSView;

/**
 * Action for resetting the docking layout.
 */
public class RestoreDockingLayoutAction
    extends
      AbstractAction {

  /**
   * This action's ID.
   */
  public static final String ID = "openTCS.restoreDockingLayout";
  private final OpenTCSView view;

  /**
   * Creates a new instance.
   *
   * @param view The openTCS view
   */
  public RestoreDockingLayoutAction(OpenTCSView view) {
    this.view = view;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    view.resetWindowArrangement();
  }

}
