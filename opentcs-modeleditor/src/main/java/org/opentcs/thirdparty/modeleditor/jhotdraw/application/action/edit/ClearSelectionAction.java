// SPDX-FileCopyrightText: The original authors of JHotDraw and all its contributors
// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.edit;

import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.SMALL_ICON;
import static org.opentcs.modeleditor.util.I18nPlantOverviewModeling.MENU_PATH;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import org.opentcs.guing.common.components.EditableComponent;
import org.opentcs.guing.common.util.ImageDirectory;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * Clears (de-selects) the selected region.
 * This action acts on the last {@code JTextComponent} which had the focus
 * when the {@code ActionEvent} was generated.
 * This action is called when the user selects the "Clear Selection" item
 * in the Edit menu. The menu item is automatically created by the application.
 *
 * @author Werner Randelshofer.
 */
public class ClearSelectionAction
    extends
      org.jhotdraw.app.action.edit.AbstractSelectionAction {

  /**
   * This action's ID.
   */
  public static final String ID = "edit.clearSelection";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);

  /**
   * Creates a new instance which acts on the currently focused component.
   */
  public ClearSelectionAction() {
    this(null);
  }

  /**
   * Creates a new instance which acts on the specified component.
   *
   * @param target The target of the action. Specify null for the currently
   * focused component.
   */
  @SuppressWarnings("this-escape")
  public ClearSelectionAction(JComponent target) {
    super(target);

    putValue(NAME, BUNDLE.getString("clearSelectionAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("clearSelectionAction.shortDescription"));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("shift ctrl A"));

    ImageIcon icon = ImageDirectory.getImageIcon("/menu/edit-clear-2.png");
    putValue(SMALL_ICON, icon);
    putValue(LARGE_ICON_KEY, icon);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    JComponent cTarget = target;
    Component cFocusOwner
        = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();

    if (cTarget == null && (cFocusOwner instanceof JComponent)) {
      cTarget = (JComponent) cFocusOwner;
    }

    if (cTarget != null && cTarget.isEnabled()) {
      if (cTarget instanceof EditableComponent) {
        ((EditableComponent) cTarget).clearSelection();
      }
      else if (cTarget instanceof JTextComponent) {
        JTextComponent tc = ((JTextComponent) cTarget);
        tc.select(tc.getSelectionStart(), tc.getSelectionStart());
      }
      else {
        cTarget.getToolkit().beep();
      }
    }
  }

  @Override
  protected void updateEnabled() {
    if (target != null) {
      setEnabled(target.isEnabled());
    }
  }
}
