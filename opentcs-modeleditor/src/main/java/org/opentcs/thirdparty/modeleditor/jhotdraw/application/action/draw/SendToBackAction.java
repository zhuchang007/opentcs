// SPDX-FileCopyrightText: The original authors of JHotDraw and all its contributors
// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.draw;

import static javax.swing.Action.SMALL_ICON;
import static org.opentcs.modeleditor.util.I18nPlantOverviewModeling.TOOLBAR_PATH;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.opentcs.guing.common.util.ImageDirectory;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * SendToBackAction.
 *
 * @author Werner Randelshofer
 */
public class SendToBackAction
    extends
      AbstractSelectedAction {

  /**
   * This action's ID.
   */
  public static final String ID = "edit.sendToBack";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(TOOLBAR_PATH);

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor
   */
  @SuppressWarnings("this-escape")
  public SendToBackAction(DrawingEditor editor) {
    super(editor);

    putValue(NAME, BUNDLE.getString("sendToBackAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("sendToBackAction.shortDescription"));
    putValue(SMALL_ICON, ImageDirectory.getImageIcon("/toolbar/object-order-back.png"));

    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    final DrawingView view = getView();
    final Collection<Figure> figures = new ArrayList<>(view.getSelectedFigures());
    sendToBack(view, figures);
    fireUndoableEditHappened(new AbstractUndoableEdit() {
      @Override
      public String getPresentationName() {
        return ResourceBundleUtil.getBundle(TOOLBAR_PATH)
            .getString("sendToBackAction.undo.presentationName");
      }

      @Override
      public void redo()
          throws CannotRedoException {
        super.redo();
        SendToBackAction.sendToBack(view, figures);
      }

      @Override
      public void undo()
          throws CannotUndoException {
        super.undo();
        BringToFrontAction.bringToFront(view, figures);
      }
    });
  }

  public static void sendToBack(DrawingView view, Collection<Figure> figures) {
    Drawing drawing = view.getDrawing();

    for (Figure figure : figures) {
      drawing.sendToBack(figure);
    }
  }
}
