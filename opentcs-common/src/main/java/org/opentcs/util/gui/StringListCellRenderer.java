// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.util.gui;

import static java.util.Objects.requireNonNull;

import java.awt.Component;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Renders values to JLabels.
 *
 * @param <E> The type of the values to be rendered.
 */
public class StringListCellRenderer<E>
    implements
      ListCellRenderer<E> {

  /**
   * A default renderer for creating the label.
   */
  private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
  /**
   * Returns a String representation of E.
   */
  private final Function<E, String> representer;

  /**
   * Creates an instance.
   *
   * @param representer a string representation provider for the values of the list.
   * Null value as parameter for the representer is possible.
   * The result is set as text of the JLabel.
   */
  public StringListCellRenderer(Function<E, String> representer) {
    this.representer = requireNonNull(representer, "representer");
  }

  @Override
  public Component getListCellRendererComponent(
      JList<? extends E> list,
      E value,
      int index,
      boolean isSelected,
      boolean cellHasFocus
  ) {
    JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
        list,
        value,
        index,
        isSelected,
        cellHasFocus
    );
    label.setText(representer.apply(value));
    return label;
  }
}
