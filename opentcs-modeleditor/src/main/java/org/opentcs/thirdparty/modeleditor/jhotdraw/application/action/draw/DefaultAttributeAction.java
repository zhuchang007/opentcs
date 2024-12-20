// SPDX-FileCopyrightText: The original authors of JHotDraw and all its contributors
// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.draw;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.undo.CompositeEdit;

/**
 * DefaultAttributeAction.
 *
 * @author Werner Randelshofer
 */
public class DefaultAttributeAction
    extends
      AbstractSelectedAction {

  private AttributeKey[] keys;
  private Map<AttributeKey, Object> fixedAttributes;

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor
   * @param key The attribute key
   */
  public DefaultAttributeAction(
      DrawingEditor editor,
      AttributeKey key
  ) {

    this(editor, key, null, null);
  }

  public DefaultAttributeAction(
      DrawingEditor editor,
      AttributeKey key,
      Map<AttributeKey, Object> fixedAttributes
  ) {

    this(editor, new AttributeKey[]{key}, null, null, fixedAttributes);
  }

  public DefaultAttributeAction(
      DrawingEditor editor,
      AttributeKey[] keys
  ) {

    this(editor, keys, null, null);
  }

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor.
   * @param key The attribute key.
   * @param icon The icon.
   */
  public DefaultAttributeAction(DrawingEditor editor, AttributeKey key, Icon icon) {
    this(editor, key, null, icon);
  }

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor.
   * @param key The attribute key.
   * @param name The name.
   */
  public DefaultAttributeAction(DrawingEditor editor, AttributeKey key, String name) {
    this(editor, key, name, null);
  }

  public DefaultAttributeAction(DrawingEditor editor, AttributeKey key, String name, Icon icon) {
    this(editor, new AttributeKey[]{key}, name, icon);
  }

  public DefaultAttributeAction(
      DrawingEditor editor,
      AttributeKey[] keys,
      String name,
      Icon icon
  ) {

    this(editor, keys, name, icon, new HashMap<AttributeKey, Object>());
  }

  @SuppressWarnings("this-escape")
  public DefaultAttributeAction(
      DrawingEditor editor,
      AttributeKey[] keys,
      String name,
      Icon icon,
      Map<AttributeKey, Object> fixedAttributes
  ) {

    super(editor);
    this.keys = keys.clone();
    putValue(AbstractAction.NAME, name);
    putValue(AbstractAction.SMALL_ICON, icon);
    setEnabled(true);
    editor.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DefaultAttributeAction.this.keys[0].getKey())) {
          putValue("attribute_" + DefaultAttributeAction.this.keys[0].getKey(), evt.getNewValue());
        }
      }
    });
    this.fixedAttributes = fixedAttributes;
    updateEnabledState();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (getView() != null && getView().getSelectionCount() > 0) {
      CompositeEdit edit = new CompositeEdit("");
      fireUndoableEditHappened(edit);
      changeAttribute();
      fireUndoableEditHappened(edit);
    }
  }

  @SuppressWarnings("unchecked")
  public void changeAttribute() {
    CompositeEdit edit = new CompositeEdit("attributes");
    fireUndoableEditHappened(edit);
    DrawingEditor editor = getEditor();

    for (Figure figure : getView().getSelectedFigures()) {
      figure.willChange();
      for (AttributeKey key : keys) {
        figure.set(key, editor.getDefaultAttribute(key));
      }

      for (Map.Entry<AttributeKey, Object> entry : fixedAttributes.entrySet()) {
        figure.set(entry.getKey(), entry.getValue());

      }

      figure.changed();
    }

    fireUndoableEditHappened(edit);
  }

  public void selectionChanged(FigureSelectionEvent evt) {
    //setEnabled(getView().getSelectionCount() > 0);
  }
}
