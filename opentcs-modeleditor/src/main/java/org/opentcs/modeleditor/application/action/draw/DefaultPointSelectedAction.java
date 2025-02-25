// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.application.action.draw;

import static java.util.Objects.requireNonNull;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.gui.JPopupButton;
import org.opentcs.guing.base.model.elements.PointModel;
import org.opentcs.guing.common.components.drawing.figures.LabeledPointFigure;
import org.opentcs.guing.common.components.drawing.figures.PointFigure;
import org.opentcs.guing.common.util.ImageDirectory;

/**
 * This action manages the behaviour when the user selects the point button.
 */
public class DefaultPointSelectedAction
    extends
      AbstractSelectedAction {

  /**
   * The SelectionProperty contains all point types in the model.
   */
  private final PointModel.Type pointType;

  private final Tool tool;
  /**
   * The button this action belongs to.
   */
  private final JPopupButton popupButton;
  /**
   * The Icon the popup button uses when this action is selected.
   */
  private final ImageIcon largeIcon;
  /**
   * The ButtonGroup the popupButton belongs to. It is necessary to know it,
   * because
   * <code>DrawingEditor.setTool()</code> doesn't select or deselect the
   * popupButton, so we have to do it manually.
   */
  private final ButtonGroup group;

  /**
   * Constructor for an action of a button in the toolbar.
   *
   * @param editor The drawing editor
   * @param tool The tool
   * @param popupButton The popup button
   * @param group The button group
   */
  public DefaultPointSelectedAction(
      DrawingEditor editor,
      Tool tool,
      JPopupButton popupButton,
      ButtonGroup group
  ) {
    super(editor);
    this.tool = requireNonNull(tool);
    this.popupButton = requireNonNull(popupButton);
    this.group = requireNonNull(group);

    this.pointType = null;
    this.largeIcon = null;
  }

  /**
   * Constructor for a button inside a drop down menu of another button.
   *
   * @param editor The drawing editor
   * @param tool The tool
   * @param pointType The point type
   * @param popupButton The popup button
   * @param group The button group
   */
  @SuppressWarnings("this-escape")
  public DefaultPointSelectedAction(
      DrawingEditor editor,
      Tool tool,
      PointModel.Type pointType,
      JPopupButton popupButton,
      ButtonGroup group
  ) {
    super(editor);
    this.pointType = requireNonNull(pointType);
    this.tool = requireNonNull(tool);
    this.popupButton = requireNonNull(popupButton);
    this.group = requireNonNull(group);

    this.largeIcon = getLargeImageIconByType(pointType);

    putValue(AbstractAction.NAME, pointType.getDescription());
    putValue(AbstractAction.SHORT_DESCRIPTION, pointType.getHelptext());
    putValue(AbstractAction.SMALL_ICON, getImageIconByType(pointType));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (pointType != null) {
      CreationTool creationTool = (CreationTool) tool;
      LabeledPointFigure lpf = (LabeledPointFigure) creationTool.getPrototype();
      PointFigure pointFigure = lpf.getPresentationFigure();
      pointFigure.getModel().getPropertyType().setValue(pointType);

      popupButton.setText(null);
      popupButton.setToolTipText(pointType.getHelptext());
      popupButton.setIcon(largeIcon);
    }

    getEditor().setTool(tool);
    group.setSelected(popupButton.getModel(), true);
  }

  @Override
  protected void updateEnabledState() {
    setEnabled(getView() != null && getView().isEnabled());
  }

  private ImageIcon getImageIconByType(PointModel.Type pointType) {
    switch (pointType) {
      case HALT:
        return ImageDirectory.getImageIcon("/toolbar/point-halt.22.png");
      case PARK:
        return ImageDirectory.getImageIcon("/toolbar/point-park.22.png");
      default:
        return null;
    }
  }

  private ImageIcon getLargeImageIconByType(PointModel.Type pointType) {
    switch (pointType) {
      case HALT:
        return ImageDirectory.getImageIcon("/toolbar/point-halt-arrow.22.png");
      case PARK:
        return ImageDirectory.getImageIcon("/toolbar/point-park-arrow.22.png");
      default:
        return null;
    }
  }
}
