/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uwant.vehicle.route.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.vehicle.exchange.I18nUwantComAdapter;

/** @author zhuchang */
public class RouteTableModel extends AbstractTableModel {
  /** This class's logger. */
  private static final Logger LOG = LoggerFactory.getLogger(RouteTableModel.class);
  /** This class's resource bundle. */
  private static final ResourceBundle BUNDLE =
      ResourceBundle.getBundle(I18nUwantComAdapter.BUNDLE_PATH);
  /** The column names. */
  public static final String[] COLUMN_NAMES =
      new String[] {
        BUNDLE.getString("RoutTableModel.column_routeId.headerText"),
        BUNDLE.getString("RoutTableModel.column_nodeId.headerText"),
        BUNDLE.getString("RoutTableModel.column_action1_Id.headerText"),
        BUNDLE.getString("RoutTableModel.column_action1_param1.headerText"),
        BUNDLE.getString("RoutTableModel.column_action1_param2.headerText"),
        BUNDLE.getString("RoutTableModel.column_action2_Id.headerText"),
        BUNDLE.getString("RoutTableModel.column_action2_param1.headerText"),
        BUNDLE.getString("RoutTableModel.column_action2_param2.headerText"),
        BUNDLE.getString("RoutTableModel.column_action3_Id.headerText"),
        BUNDLE.getString("RoutTableModel.column_action3_param1.headerText"),
        BUNDLE.getString("RoutTableModel.column_action3_param2.headerText"),
        BUNDLE.getString("RoutTableModel.column_action4_Id.headerText"),
        BUNDLE.getString("RoutTableModel.column_action4_param1.headerText"),
        BUNDLE.getString("RoutTableModel.column_action4_param2.headerText")
      };
  /** The column classes. */
  private static final Class<?>[] COLUMN_CLASSES =
      new Class<?>[] {
        Integer.class, // 路线编号
        Integer.class, // 节点编号
        Integer.class, // action1
        Integer.class,
        Integer.class,
        Integer.class, // action2
        Integer.class,
        Integer.class,
        Integer.class, // action3
        Integer.class,
        Integer.class,
        Integer.class, // action4
        Integer.class,
        Integer.class,
      };

  public static final int ROUTID_COLUMN = 0;
  public static final int NODEID_COLUMN = 1;
  public static final int ACTION1_ID_COLUMN = 2;
  public static final int ACTION1_PARAM1_COLUMN = 3;
  public static final int ACTION1_PARAM2_COLUMN = 4;
  public static final int ACTION2_ID_COLUMN = 5;
  public static final int ACTION2_PARAM1_COLUMN = 6;
  public static final int ACTION2_PARAM2_COLUMN = 7;
  public static final int ACTION3_ID_COLUMN = 8;
  public static final int ACTION3_PARAM1_COLUMN = 9;
  public static final int ACTION3_PARAM2_COLUMN = 10;
  public static final int ACTION4_ID_COLUMN = 11;
  public static final int ACTION4_PARAM1_COLUMN = 12;
  public static final int ACTION4_PARAM2_COLUMN = 13;

  public static final String NODEID_COLUMN_IDENTIFIER = COLUMN_NAMES[NODEID_COLUMN];
  public static final String ACTION1_ID_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION1_ID_COLUMN];
  public static final String ACTION1_PARAM1_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION1_PARAM1_COLUMN];
  public static final String ACTION1_PARAM2_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION1_PARAM2_COLUMN];
  public static final String ACTION2_ID_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION2_ID_COLUMN];
  public static final String ACTION2_PARAM1_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION2_PARAM1_COLUMN];
  public static final String ACTION2_PARAM2_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION2_PARAM2_COLUMN];
  public static final String ACTION3_ID_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION3_ID_COLUMN];
  public static final String ACTION3_PARAM1_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION3_PARAM1_COLUMN];
  public static final String ACTION3_PARAM2_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION3_PARAM2_COLUMN];
  public static final String ACTION4_ID_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION4_ID_COLUMN];
  public static final String ACTION4_PARAM1_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION4_PARAM1_COLUMN];
  public static final String ACTION4_PARAM2_COLUMN_IDENTIFIER = COLUMN_NAMES[ACTION4_PARAM2_COLUMN];

  private final List<RouteTableEntry> entries = new ArrayList<>();
  private DefaultListModel<List<RouteTableEntry>> routeListModel;
  /**
   * Adds a new entry to this model.
   *
   * @param newEntry The new entry.
   */
  public void addData(RouteTableEntry newEntry) {
    entries.add(newEntry);
    fireTableRowsInserted(entries.size(), entries.size());
  }

  public void clear() {
    fireTableRowsDeleted(0, entries.size());
    entries.clear();
  }

  public void delData(RouteTableEntry delEntry) {
    entries.remove(delEntry);
    fireTableRowsDeleted(entries.indexOf(delEntry), entries.indexOf(delEntry));

    Collections.list(routeListModel.elements()).stream()
        .filter(u -> u.get(0).getRouteId() == delEntry.getRouteId())
        .forEach(
            u -> {
              u.remove(delEntry);
              if (u.isEmpty()) {
                routeListModel.removeElement(u);
              }
            });
  }

  public void delData(int row) {
    delData(entries.get(row));
  }

  public void setRouteListModel(DefaultListModel<List<RouteTableEntry>> routeListModel) {
    this.routeListModel = routeListModel;
  }

  @Override
  public String getColumnName(int columnIndex) {
    try {
      return COLUMN_NAMES[columnIndex];
    } catch (ArrayIndexOutOfBoundsException exc) {
      LOG.warn("Invalid columnIndex", exc);
      return "Invalid column index " + columnIndex;
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return COLUMN_CLASSES[columnIndex];
  }

  @Override
  public int getRowCount() {
    return entries.size();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_NAMES.length;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex >= entries.size()) {
      return null;
    }

    RouteTableEntry entry = entries.get(rowIndex);

    switch (columnIndex) {
      case ROUTID_COLUMN:
        return entry.getRouteId();
      case NODEID_COLUMN:
        return entry.getNodeId();
      case ACTION1_ID_COLUMN:
        return entry.getNodeActionsMap().get(1).getActionId();
      case ACTION1_PARAM1_COLUMN:
        return entry.getNodeActionsMap().get(1).getActionParam1();
      case ACTION1_PARAM2_COLUMN:
        return entry.getNodeActionsMap().get(1).getActionParam2();
      case ACTION2_ID_COLUMN:
        return entry.getNodeActionsMap().get(2).getActionId();
      case ACTION2_PARAM1_COLUMN:
        return entry.getNodeActionsMap().get(2).getActionParam1();
      case ACTION2_PARAM2_COLUMN:
        return entry.getNodeActionsMap().get(2).getActionParam2();
      case ACTION3_ID_COLUMN:
        return entry.getNodeActionsMap().get(3).getActionId();
      case ACTION3_PARAM1_COLUMN:
        return entry.getNodeActionsMap().get(3).getActionParam1();
      case ACTION3_PARAM2_COLUMN:
        return entry.getNodeActionsMap().get(3).getActionParam2();
      case ACTION4_ID_COLUMN:
        return entry.getNodeActionsMap().get(4).getActionId();
      case ACTION4_PARAM1_COLUMN:
        return entry.getNodeActionsMap().get(4).getActionParam1();
      case ACTION4_PARAM2_COLUMN:
        return entry.getNodeActionsMap().get(4).getActionParam2();
      default:
        LOG.warn("Unhandled column index: {}", columnIndex);
        return "Invalid column index " + columnIndex;
    }
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    switch (columnIndex) {
      case ROUTID_COLUMN:
        return false;
      default:
        return true;
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (rowIndex >= entries.size() || aValue == null) {
      return;
    }

    RouteTableEntry routeTableEntry = entries.get(rowIndex);

    switch (columnIndex) {
      case ROUTID_COLUMN:
        break;
      case NODEID_COLUMN:
        routeTableEntry.setNodeId((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION1_ID_COLUMN:
        routeTableEntry.getNodeActionsMap().get(1).setActionId((Integer) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION1_PARAM1_COLUMN:
        routeTableEntry.getNodeActionsMap().get(1).setActionParam1((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION1_PARAM2_COLUMN:
        routeTableEntry.getNodeActionsMap().get(1).setActionParam2((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION2_ID_COLUMN:
        routeTableEntry.getNodeActionsMap().get(2).setActionId((Integer) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION2_PARAM1_COLUMN:
        routeTableEntry.getNodeActionsMap().get(2).setActionParam1((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION2_PARAM2_COLUMN:
        routeTableEntry.getNodeActionsMap().get(2).setActionParam2((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION3_ID_COLUMN:
        routeTableEntry.getNodeActionsMap().get(3).setActionId((Integer) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION3_PARAM1_COLUMN:
        routeTableEntry.getNodeActionsMap().get(3).setActionParam1((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION3_PARAM2_COLUMN:
        routeTableEntry.getNodeActionsMap().get(3).setActionParam2((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION4_ID_COLUMN:
        routeTableEntry.getNodeActionsMap().get(4).setActionId((Integer) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION4_PARAM1_COLUMN:
        routeTableEntry.getNodeActionsMap().get(4).setActionParam1((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case ACTION4_PARAM2_COLUMN:
        routeTableEntry.getNodeActionsMap().get(4).setActionParam2((Integer) aValue);
        // fireTableCellUpdated(rowIndex, columnIndex);
        break;
      default:
        LOG.warn("Unhandled column index: {}", columnIndex);
        break;
    }
  }
}
