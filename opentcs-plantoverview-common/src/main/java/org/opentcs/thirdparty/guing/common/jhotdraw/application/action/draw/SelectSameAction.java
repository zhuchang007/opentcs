// SPDX-FileCopyrightText: The original authors of JHotDraw and all its contributors
// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.thirdparty.guing.common.jhotdraw.application.action.draw;

import static javax.swing.Action.SMALL_ICON;
import static org.opentcs.guing.common.util.I18nPlantOverview.MODELVIEW_PATH;

import java.util.HashSet;
import java.util.Set;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.opentcs.guing.common.util.ImageDirectory;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * SelectSameAction.
 *
 * @author Werner Randelshofer
 */
public class SelectSameAction
    extends
      AbstractSelectedAction {

  /**
   * This action's ID.
   */
  public static final String ID = "edit.selectSame";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MODELVIEW_PATH);

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor
   */
  @SuppressWarnings("this-escape")
  public SelectSameAction(DrawingEditor editor) {
    super(editor);

    putValue(NAME, BUNDLE.getString("selectSameAction.name"));
    putValue(SMALL_ICON, ImageDirectory.getImageIcon("/menu/kcharselect.png"));

    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    selectSame();
  }

  public void selectSame() {
    Set<Class<?>> selectedClasses = new HashSet<>();

    for (Figure selected : getView().getSelectedFigures()) {
      selectedClasses.add(selected.getClass());
    }

    for (Figure f : getDrawing().getChildren()) {
      if (selectedClasses.contains(f.getClass())) {
        getView().addToSelection(f);
      }
    }
  }
}
