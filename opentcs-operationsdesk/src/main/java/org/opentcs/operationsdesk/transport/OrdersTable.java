// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.operationsdesk.transport;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A table for transport orders.
 */
public class OrdersTable
    extends
      JTable {

  /**
   * Creates a new instance of OrdersTable.
   *
   * @param tableModel das Tabellenmodell
   */
  @SuppressWarnings("this-escape")
  public OrdersTable(TableModel tableModel) {
    super(tableModel);

    setRowSelectionAllowed(true);
    setFocusable(false);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public TableCellEditor getCellEditor(int row, int column) {
    TableModel tableModel = getModel();
    Object value = tableModel.getValueAt(row, column);
    TableCellEditor editor = getDefaultEditor(value.getClass());

    return editor;
  }

  @Override
  public TableCellRenderer getCellRenderer(int row, int column) {
    TableModel tableModel = getModel();
    Object value = tableModel.getValueAt(row, column);
    TableCellRenderer renderer = getDefaultRenderer(value.getClass());

    return renderer;
  }
}
