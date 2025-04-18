// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.application.menus.menubar;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import javax.swing.JMenu;
import org.opentcs.modeleditor.application.action.ViewActionMap;
import org.opentcs.modeleditor.util.I18nPlantOverviewModeling;
import org.opentcs.thirdparty.guing.common.jhotdraw.application.action.edit.DeleteAction;
import org.opentcs.thirdparty.guing.common.jhotdraw.application.action.edit.SelectAllAction;
import org.opentcs.thirdparty.guing.common.jhotdraw.application.action.edit.UndoRedoManager;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;
import org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.edit.ClearSelectionAction;
import org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.edit.CopyAction;
import org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.edit.CutAction;
import org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.edit.DuplicateAction;
import org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.edit.PasteAction;

/**
 * The application's "Edit" menu.
 */
public class EditMenu
    extends
      JMenu {

//  private final JMenuItem menuItemCopy;
//  private final JMenuItem menuItemCut;
//  private final JMenuItem menuItemDuplicate;
//  private final JMenuItem menuItemPaste;
  /**
   * Creates a new instance.
   *
   * @param actionMap The application's action map.
   */
  @Inject
  @SuppressWarnings("this-escape")
  public EditMenu(ViewActionMap actionMap) {
    requireNonNull(actionMap, "actionMap");

    final ResourceBundleUtil labels
        = ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.MENU_PATH);

    this.setText(labels.getString("editMenu.text"));
    this.setToolTipText(labels.getString("editMenu.tooltipText"));
    this.setMnemonic('E');

    // Undo, Redo
    add(actionMap.get(UndoRedoManager.UNDO_ACTION_ID));
    add(actionMap.get(UndoRedoManager.REDO_ACTION_ID));
    addSeparator();
    // Cut, Copy, Paste, Duplicate
//    menuItemCut = menuEdit.add(actionMap.get(CutAction.ID));
//    menuItemCopy = menuEdit.add(actionMap.get(CopyAction.ID));
//    menuItemPaste = menuEdit.add(actionMap.get(PasteAction.ID));
//    menuItemDuplicate = menuEdit.add(actionMap.get(DuplicateAction.ID));
    // Delete
    add(actionMap.get(DeleteAction.ID));
    add(actionMap.get(CopyAction.ID));
    add(actionMap.get(PasteAction.ID));
    add(actionMap.get(DuplicateAction.ID));
    add(actionMap.get(CutAction.ID));
    addSeparator();
    // Select all, Clear selection
    add(actionMap.get(SelectAllAction.ID));
    add(actionMap.get(ClearSelectionAction.ID));
  }

}
