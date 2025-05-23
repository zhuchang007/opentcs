// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.components.properties.table;

import static java.util.Objects.requireNonNull;

import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Inject;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.opentcs.guing.base.components.properties.type.AbstractComplexProperty;
import org.opentcs.guing.common.components.dialogs.DetailsDialogContent;
import org.opentcs.guing.common.components.dialogs.StandardDetailsDialog;

/**
 * A cell editor for a complex property.
 */
public class ComplexPropertyCellEditor
    extends
      javax.swing.AbstractCellEditor
    implements
      javax.swing.table.TableCellEditor {

  /**
   * The button for showing the details dialog.
   */
  private final JButton fButton = new JButton();
  // CHECKSTYLE:OFF (Getting this declaration shorter is difficult with automatic formatting.)
  /**
   * Provides the appropriate dialog content for a given property.
   */
  private final Map<Class<? extends AbstractComplexProperty>, Provider<DetailsDialogContent>> contentMap;
  // CHECKSTYLE:ON
  /**
   * A parent for dialogs created by this instance.
   */
  private final JPanel dialogParent;
  /**
   * The property being edited.
   */
  private AbstractComplexProperty fProperty;

  /**
   * Creates a new instance.
   *
   * @param contentMap Provides the appropriate content for a given property.
   * @param dialogParent A parent for dialogs created by this instance.
   */
  @Inject
  public ComplexPropertyCellEditor(
      Map<Class<? extends AbstractComplexProperty>, Provider<DetailsDialogContent>> contentMap,
      @Assisted
      JPanel dialogParent
  ) {
    this.contentMap = requireNonNull(contentMap, "contentMap");
    this.dialogParent = requireNonNull(dialogParent, "dialogParent");

    fButton.setFont(new Font("Dialog", Font.PLAIN, 12));
    fButton.setBorder(null);
    fButton.setHorizontalAlignment(JButton.LEFT);
    fButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showDialog();
      }
    });
  }

  @Override
  public Object getCellEditorValue() {
    return fProperty;
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column
  ) {

    fProperty = (AbstractComplexProperty) value;
    fButton.setText(fProperty.toString());
    fButton.setBackground(table.getBackground());

    return fButton;
  }

  /**
   * Shows the dialog for editing the property.
   */
  private void showDialog() {
    DetailsDialogContent content = contentMap.get(fProperty.getClass()).get();

    StandardDetailsDialog detailsDialog
        = new StandardDetailsDialog(dialogParent, true, content);
    detailsDialog.setLocationRelativeTo(dialogParent);

    detailsDialog.getDialogContent().setProperty(fProperty);
    detailsDialog.activate();
    detailsDialog.setVisible(true);

    if (detailsDialog.getReturnStatus() == StandardDetailsDialog.RET_OK) {
      stopCellEditing();
    }
    else {
      cancelCellEditing();
    }
  }
}
