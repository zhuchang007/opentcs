// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.application.action;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.tool.Tool;

/**
 * A listener if a tool was (de)selected.
 */
public final class ToolButtonListener
    implements
      ItemListener {

  private final Tool tool;
  private final DrawingEditor editor;

  /**
   * Creates a new instance.
   *
   * @param tool The tool
   * @param editor The drawing editor
   */
  public ToolButtonListener(Tool tool, DrawingEditor editor) {
    this.tool = Objects.requireNonNull(tool, "tool is null");
    this.editor = Objects.requireNonNull(editor, "editor is null");
  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getStateChange() == ItemEvent.SELECTED) {
      editor.setTool(tool);
    }
  }
}
