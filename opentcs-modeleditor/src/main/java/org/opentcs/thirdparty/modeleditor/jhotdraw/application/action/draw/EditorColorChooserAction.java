// SPDX-FileCopyrightText: The original authors of JHotDraw and all its contributors
// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.draw;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.EditorColorIcon;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.opentcs.modeleditor.util.I18nPlantOverviewModeling;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * EditorColorChooserAction.
 * <p>
 * The behavior for choosing the initial color of
 * the JColorChooser matches with
 * {@link EditorColorIcon }.
 *
 * @author Werner Randelshofer
 */
public class EditorColorChooserAction
    extends
      AttributeAction {

  protected AttributeKey<Color> key;

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor
   * @param key The attribute key
   * @param name The name
   * @param icon The icon
   * @param fixedAttributes The fixed attributes
   */
  @SuppressWarnings("this-escape")
  public EditorColorChooserAction(
      DrawingEditor editor,
      AttributeKey<Color> key,
      String name,
      Icon icon,
      Map<AttributeKey, Object> fixedAttributes
  ) {

    super(editor, fixedAttributes, name, icon);
    this.key = key;
    putValue(AbstractAction.NAME, name);
    putValue(
        Action.SHORT_DESCRIPTION,
        ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.TOOLBAR_PATH)
            .getString("editorColorChooserAction.shortDescription")
    );
    putValue(AbstractAction.SMALL_ICON, icon);
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    Color initialColor = getInitialColor();
    ResourceBundleUtil labels
        = ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.TOOLBAR_PATH);
    Color chosenColor
        = JColorChooser.showDialog(
            (Component) e.getSource(),
            labels.getString("editorColorChooserAction.dialog_colorSelection.title"),
            initialColor
        );

    if (chosenColor != null) {
      HashMap<AttributeKey, Object> attr = new HashMap<>(attributes);
      attr.put(key, chosenColor);
      applyAttributesTo(attr, getView().getSelectedFigures());
    }
  }

  public void selectionChanged(FigureSelectionEvent evt) {
    //setEnabled(getView().getSelectionCount() > 0);
  }

  protected Color getInitialColor() {
    Color initialColor = getEditor().getDefaultAttribute(key);

    if (initialColor == null) {
      initialColor = Color.red;
    }

    return initialColor;
  }
}
