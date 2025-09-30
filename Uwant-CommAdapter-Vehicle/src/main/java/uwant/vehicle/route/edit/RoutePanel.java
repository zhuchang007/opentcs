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

import static java.util.Objects.requireNonNull;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.JTextComponent;
import javax.swing.text.NumberFormatter;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.ServiceCallWrapper;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.CallWrapper;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.gui.BoundsPopupMenuListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.vehicle.telegrams.NodeActionRequest;
import uwant.vehicle.exchange.AdapterPanelComponentsFactory;
import uwant.vehicle.exchange.UwtProcessModelTO;
import uwant.vehicle.exchange.commands.SendRequestCommand;

/** @author zhuchang */
@SuppressWarnings({"deprecation", "unchecked", "rawtypes", "this-escape"})
public class RoutePanel
    extends
      VehicleCommAdapterPanel {

  private static final Logger LOG = LoggerFactory.getLogger(RoutePanel.class);

  public static final String CONFIG_XML_FILE = "./config/uwtRouteTableConfig.xml";

  DefaultListModel<List<RouteTableEntry>> routeListModel;
  RouteTableModel routeTableModel;
  Map<Integer, RouteDisplayAction> mapRouteDisplayAction;
  RouteTableEntryXML routeTableEntryXML = new RouteTableEntryXML();

  {
    // 初始化表格模型
    routeListModel = new DefaultListModel<>();
    routeTableModel = new RouteTableModel();
    routeTableModel.setRouteListModel(routeListModel);

    // 初始化mapRouteDisplayAction
    File tabelConfigXMLFile = new File(CONFIG_XML_FILE);
    if (!tabelConfigXMLFile.exists()) {
      try {
        tabelConfigXMLFile.createNewFile();
      }
      catch (IOException e) {
      }
    }
    RouteTableConfigXML routeTableConfigXML = new RouteTableConfigXML(tabelConfigXMLFile);
    routeTableConfigXML.createUwtConfigXMLFile();
    mapRouteDisplayAction = routeTableConfigXML.parseUwtConfigXMLFile();
  }

  /** The vehicle service used for interaction with the comm adapter. */
  private final VehicleService vehicleService;
  /** The call wrapper to use for service calls. */
  private final CallWrapper callWrapper;

  UwtProcessModelTO processModel;

  private final String vehicleName;

  /**
   * Creates new form RoutePanel
   *
   * @param vehicleService
   * @param callWrapper
   * @param adapterPanelComponentsFactory
   * @param eventBus
   * @param processModel
   */
  @Inject
  public RoutePanel(
      @Assisted
      VehicleService vehicleService,
      @ServiceCallWrapper
      CallWrapper callWrapper,
      @Nonnull
      AdapterPanelComponentsFactory adapterPanelComponentsFactory,
      @Nonnull
      @ApplicationEventBus
      EventBus eventBus,
      @Assisted
      VehicleProcessModelTO processModel
  ) {
    initComponents();

    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.callWrapper = requireNonNull(callWrapper, "callWrapper");

    sendRouteButton.setEnabled(false);
    this.processModel = (UwtProcessModelTO) processModel;
    if (this.processModel != null && this.processModel.isCommAdapterConnected()) {
      SwingUtilities.invokeLater(
          () -> {
            sendRouteButton.setEnabled(true);
          }
      );
    }
    else {
      SwingUtilities.invokeLater(
          () -> {
            sendRouteButton.setEnabled(false);
          }
      );
    }
    this.vehicleName = processModel.getName();

    SwingUtilities.invokeLater(
        new Runnable() {
          @Override
          public void run() {
            // 路线表格加入模型，设置每个单元格的展现形式
            routeTable.setModel(routeTableModel);
            routeTable.setDefaultRenderer(Integer.class, new RouteTableCellRenderer());

            // 给每列ACTION_ID_COLUMN添加下拉框
            String[] actionIdColumnIdentifiers = new String[]{
                RouteTableModel.ACTION1_ID_COLUMN_IDENTIFIER,
                RouteTableModel.ACTION2_ID_COLUMN_IDENTIFIER,
                RouteTableModel.ACTION3_ID_COLUMN_IDENTIFIER,
                RouteTableModel.ACTION4_ID_COLUMN_IDENTIFIER
            };
            for (String actionIdColumnIdentifier : actionIdColumnIdentifiers) {
              initActionIdColumnComboBox(actionIdColumnIdentifier);
            }

            // 添加param1和param2的下拉框，侦听对应的ACTION_ID_COLUMN单元格，单元格内容变化时，实现联动
            routeTableModel.addTableModelListener(
                new TableModelListener() {
                  @Override
                  public void tableChanged(TableModelEvent e) {
                    int type = e.getType(); // 获取事件类型(增、删、改等)
                    int col = e.getColumn();
                    int row = e.getFirstRow();

                    if (type == TableModelEvent.INSERT) {
                      initParamComboBox(row - 1, RouteTableModel.ACTION1_ID_COLUMN);
                      initParamComboBox(row - 1, RouteTableModel.ACTION2_ID_COLUMN);
                      initParamComboBox(row - 1, RouteTableModel.ACTION3_ID_COLUMN);
                      initParamComboBox(row - 1, RouteTableModel.ACTION4_ID_COLUMN);
                    }
                    else if (type == TableModelEvent.UPDATE) {
                      switch (col) {
                        case RouteTableModel.ACTION1_ID_COLUMN:
                        case RouteTableModel.ACTION2_ID_COLUMN:
                        case RouteTableModel.ACTION3_ID_COLUMN:
                        case RouteTableModel.ACTION4_ID_COLUMN:
                          initParamComboBox(row, col);
                          break;
                        default:
                          break;
                      }
                    }
                  }

                  private void initParamComboBox(int row, int col) {
                    if (routeTable.getValueAt(row, col) != null) {
                      // 添加param1的下拉框
                      JComboBox<Integer> actionComboParam1 = new JComboBox<>();
                      actionComboParam1.setEditor(new MyComboBoxEditor());
                      RouteDisplayAction routeDisplayAction = mapRouteDisplayAction.get(
                          (Integer) routeTable.getValueAt(row, col)
                      );
                      Boolean isParam1Edit = routeDisplayAction.isParam1Edit();
                      actionComboParam1.setEditable(isParam1Edit);
                      Map<Integer, String> param1 = routeDisplayAction.getParam1();
                      param1.forEach((k, v) -> actionComboParam1.addItem(k));
                      actionComboParam1.setRenderer(new ParamListCellRenderer(param1));
                      actionComboParam1.addPopupMenuListener(new BoundsPopupMenuListener());
                      routeTable
                          .getColumn(RouteTableModel.COLUMN_NAMES[col + 1])
                          .setCellEditor(new MyCellEditor(actionComboParam1));
                      // 添加param2的下拉框
                      JComboBox<Integer> actionComboParam2 = new JComboBox<>();
                      actionComboParam2.setEditor(new MyComboBoxEditor());
                      Boolean isParam2Edit = routeDisplayAction.isParam2Edit();
                      actionComboParam2.setEditable(isParam2Edit);
                      Map<Integer, String> param2 = mapRouteDisplayAction
                          .get((Integer) routeTable.getValueAt(row, col))
                          .getParam2();
                      param2.forEach((k, v) -> actionComboParam2.addItem(k));
                      actionComboParam2.setRenderer(new ParamListCellRenderer(param2));
                      actionComboParam2.addPopupMenuListener(new BoundsPopupMenuListener());
                      routeTable
                          .getColumn(RouteTableModel.COLUMN_NAMES[col + 2])
                          .setCellEditor(new MyCellEditor(actionComboParam2));
                    }
                  }
                }
            );

            routeList.setModel(routeListModel);
            routeList.setCellRenderer(new RouteListModelCellRenderer());
          }
        }
    );
  }

  private void initActionIdColumnComboBox(String actionIdColumnIdentifier) {
    final JComboBox<RouteDisplayAction> nodeActionCombo = new JComboBox<>();
    // 设置下拉框的展现形式
    nodeActionCombo.setRenderer(
        new ListCellRenderer<RouteDisplayAction>() {
          private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

          @Override
          public Component getListCellRendererComponent(
              JList<? extends RouteDisplayAction> list,
              RouteDisplayAction value,
              int index,
              boolean isSelected,
              boolean cellHasFocus
          ) {

            JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus
            );
            if (value != null) {
              label.setText(value.getActionName());
            }
            else {
              label.setText(" ");
            }
            return label;
          }
        }
    );
    // 添加下拉框的列表元素
    mapRouteDisplayAction.forEach((k, v) -> nodeActionCombo.addItem(v));
    // 下拉框边界正对Table Cell
    nodeActionCombo.addPopupMenuListener(new BoundsPopupMenuListener());
    // 把下拉框关联到actionId表格列上
    routeTable
        .getColumn(actionIdColumnIdentifier)
        .setCellEditor(
            new MyCellEditor(nodeActionCombo) {
              @Override
              public Object getCellEditorValue() {
                return ((RouteDisplayAction) super.getCellEditorValue()).getActionId();
              }
            }
        );
  }

  @Override
  public void processModelChange(String attributeChanged, VehicleProcessModelTO newProcessModel) {
    if (!(newProcessModel instanceof UwtProcessModelTO)) {
      return;
    }
    this.processModel = (UwtProcessModelTO) newProcessModel;
  }

  private void sendAdapterCommand(AdapterCommand command) {
    try {
      callWrapper.call(
          () -> vehicleService.sendCommAdapterCommand(processModel.getVehicleRef(), command)
      );
    }
    catch (Exception ex) {
      LOG.warn("Error sending comm adapter command '{}'", command, ex);
    }
  }

  private void sendRoutes(List<List<RouteTableEntry>> routes) {
    int addr = processModel.getCurrentState().getAddr();
    int agvId = processModel.getCurrentState().getAgvId();

    routes.forEach(
        e -> {
          // sendTextArea.append("发送路线" + e.get(0).getRouteId() + ":\n");
          e.forEach(
              r -> {
                NodeActionRequest nodeActionRequest = new NodeActionRequest(
                    addr, agvId, r.getNodeId(), new ArrayList<>(r.getNodeActionsMap().values())
                );
                sendAdapterCommand(new SendRequestCommand(nodeActionRequest, r.getRouteId()));
              }
          );
        }
    );
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    routeListScrollPane = new javax.swing.JScrollPane();
    routeList = new javax.swing.JList<>();
    routeTableScrollPane = new javax.swing.JScrollPane();
    routeTable = new javax.swing.JTable();
    buttonsPanel = new javax.swing.JPanel();
    addRouteButton = new javax.swing.JButton();
    delRouteButton = new javax.swing.JButton();
    addNodeButton = new javax.swing.JButton();
    delNodeButton = new javax.swing.JButton();
    sendRouteButton = new javax.swing.JButton();
    saveRouteButton = new javax.swing.JButton();
    importRouteButton = new javax.swing.JButton();

    setPreferredSize(new java.awt.Dimension(485, 450));
    setLayout(new java.awt.BorderLayout());

    jPanel1.setLayout(new java.awt.BorderLayout());

    routeListScrollPane.setPreferredSize(new java.awt.Dimension(50, 132));

    routeList.setToolTipText("");
    routeList.setPreferredSize(new java.awt.Dimension(50, 0));
    routeList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        routeListValueChanged(evt);
      }
    });
    routeListScrollPane.setViewportView(routeList);

    jPanel1.add(routeListScrollPane, java.awt.BorderLayout.LINE_START);

    routeTable.setModel(
        new javax.swing.table.DefaultTableModel(
            new Object[][]{

            },
            new String[]{

            }
        )
    );
    routeTableScrollPane.setViewportView(routeTable);

    jPanel1.add(routeTableScrollPane, java.awt.BorderLayout.CENTER);

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(
        "i18n/uwant/vehicle/commadapter/Bundle"
    ); // NOI18N
    addRouteButton.setText(bundle.getString("RoutePanel.addRouteButton")); // NOI18N
    addRouteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addRouteButtonActionPerformed(evt);
      }
    });

    delRouteButton.setText(bundle.getString("RoutePanel.delRouteButton")); // NOI18N
    delRouteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        delRouteButtonActionPerformed(evt);
      }
    });

    addNodeButton.setText(bundle.getString("RoutePanel.addNodeButton")); // NOI18N
    addNodeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addNodeButtonActionPerformed(evt);
      }
    });

    delNodeButton.setText(bundle.getString("RoutePanel.delNodeButton")); // NOI18N
    delNodeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        delNodeButtonActionPerformed(evt);
      }
    });

    sendRouteButton.setText(bundle.getString("RoutePanel.sendRouteButton")); // NOI18N
    sendRouteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sendRouteButtonActionPerformed(evt);
      }
    });

    saveRouteButton.setText(bundle.getString("RoutePanel.saveRouteButton")); // NOI18N
    saveRouteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveRouteButtonActionPerformed(evt);
      }
    });

    importRouteButton.setText(bundle.getString("RoutePanel.importRouteButton")); // NOI18N
    importRouteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        importRouteButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
    buttonsPanel.setLayout(buttonsPanelLayout);
    buttonsPanelLayout.setHorizontalGroup(
        buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                buttonsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        buttonsPanelLayout.createParallelGroup(
                            javax.swing.GroupLayout.Alignment.TRAILING
                        )
                            .addGroup(
                                buttonsPanelLayout.createSequentialGroup()
                                    .addComponent(addRouteButton)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED
                                    )
                                    .addComponent(delRouteButton)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED
                                    )
                                    .addComponent(addNodeButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(delNodeButton)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED
                                    )
                                    .addComponent(sendRouteButton)
                            )
                            .addGroup(
                                javax.swing.GroupLayout.Alignment.LEADING, buttonsPanelLayout
                                    .createSequentialGroup()
                                    .addComponent(saveRouteButton)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED
                                    )
                                    .addComponent(importRouteButton)
                            )
                    )
                    .addContainerGap(102, Short.MAX_VALUE)
            )
    );
    buttonsPanelLayout.setVerticalGroup(
        buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                buttonsPanelLayout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addGroup(
                        buttonsPanelLayout.createParallelGroup(
                            javax.swing.GroupLayout.Alignment.BASELINE
                        )
                            .addComponent(addRouteButton)
                            .addComponent(delRouteButton)
                            .addComponent(addNodeButton)
                            .addComponent(delNodeButton)
                            .addComponent(sendRouteButton)
                    )
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        buttonsPanelLayout.createParallelGroup(
                            javax.swing.GroupLayout.Alignment.BASELINE
                        )
                            .addComponent(saveRouteButton)
                            .addComponent(importRouteButton)
                    )
                    .addContainerGap(16, Short.MAX_VALUE)
            )
    );

    jPanel1.add(buttonsPanel, java.awt.BorderLayout.PAGE_END);

    add(jPanel1, java.awt.BorderLayout.CENTER);

    getAccessibleContext().setAccessibleName(bundle.getString("RoutTableModel.accessibleName")); // NOI18N
  }// </editor-fold>//GEN-END:initComponents

  private void addRouteButtonActionPerformed(
      java.awt.event.ActionEvent evt
  ) { // GEN-FIRST:event_addRouteButtonActionPerformed
    // TODO add your handling code here:
    Optional<Integer> maxValue = Collections.list(routeListModel.elements()).stream()
        .map(u -> u.get(0).getRouteId())
        .max(Integer::compare);

    int maxRouteId = maxValue.isPresent() ? maxValue.get() : 0;

    RouteAddPanel routePanel = new RouteAddPanel();
    routePanel.setRoutId(++maxRouteId);
    StandardDialog standardDialog = new StandardDialog(this, true, routePanel, "增加路线");
    standardDialog.setVisible(true);
    if (standardDialog.getReturnStatus() == StandardDialog.RET_OK) {
      int routeId = routePanel.getRouteId();
      int nodeCount = routePanel.getNodeCount();

      if (routeId < 0
          || Collections.list(routeListModel.elements()).stream()
              .map(u -> u.get(0).getRouteId())
              .anyMatch(u -> u == routeId)
          || nodeCount < 1) {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(
            "uwant/opentcs/vehicle/commadapter/Bundle"
        ); // NOI18N
        JOptionPane.showMessageDialog(
            this, bundle.getString("RoutePanel.routeAddErrorMessageDialog")
        );
        return;
      }

      List<RouteTableEntry> routeTableEntryList = new ArrayList<>();
      for (int i = 0; i < nodeCount; i++) {
        RouteTableEntry routeTableEntry = new RouteTableEntry();
        routeTableEntry.setRouteId(routeId);
        routeTableEntryList.add(routeTableEntry);
      }
      // routelist.add(routeTableEntryList);
      routeListModel.addElement(routeTableEntryList);
      routeList.setSelectedValue(routeTableEntryList, true);
    }
  } // GEN-LAST:event_addRouteButtonActionPerformed

  private void delRouteButtonActionPerformed(
      java.awt.event.ActionEvent evt
  ) { // GEN-FIRST:event_delRouteButtonActionPerformed
    // TODO add your handling code here:
    List<List<RouteTableEntry>> routeTableEntryList = routeList.getSelectedValuesList();
    routeTableEntryList.forEach(
        (List<RouteTableEntry> entryList) -> routeListModel.removeElement(entryList)
    );
  } // GEN-LAST:event_delRouteButtonActionPerformed

  private void addNodeButtonActionPerformed(
      java.awt.event.ActionEvent evt
  ) { // GEN-FIRST:event_addNodeButtonActionPerformed
    // TODO add your handling code here:
    if (routeTableModel.getRowCount() == 0) {
      return;
    }
    RouteTableEntry routeTableEntry = new RouteTableEntry();
    routeTableEntry.setRouteId(
        (int) routeTableModel.getValueAt(routeTableModel.getRowCount() - 1, 0)
    );
    routeTableModel.addData(routeTableEntry);
    Collections.list(routeListModel.elements()).stream()
        .filter(u -> u.get(0).getRouteId() == routeTableEntry.getRouteId())
        .forEach(u -> u.add(routeTableEntry));
  } // GEN-LAST:event_addNodeButtonActionPerformed

  private void delNodeButtonActionPerformed(
      java.awt.event.ActionEvent evt
  ) { // GEN-FIRST:event_delNodeButtonActionPerformed
    // TODO add your handling code here:
    if (routeTableModel.getRowCount() == 0) {
      return;
    }

    int[] selectedRows = routeTable.getSelectedRows();
    Arrays.sort(selectedRows);
    for (int i = selectedRows.length - 1; i > -1; i--) {
      routeTableModel.delData(selectedRows[i]);
    }
  } // GEN-LAST:event_delNodeButtonActionPerformed

  private void sendRouteButtonActionPerformed(
      java.awt.event.ActionEvent evt
  ) { // GEN-FIRST:event_sendRouteButtonActionPerformed
    // TODO add your handling code here:
    List<List<RouteTableEntry>> routes = routeList.getSelectedValuesList();
    sendRoutes(routes);
  } // GEN-LAST:event_sendRouteButtonActionPerformed

  private void routeListValueChanged(
      javax.swing.event.ListSelectionEvent evt
  ) { // GEN-FIRST:event_routeListValueChanged
    // TODO add your handling code here:
    if (evt.getValueIsAdjusting()) {
      return;
    }
    List<List<RouteTableEntry>> routeTableEntryList = routeList.getSelectedValuesList();
    routeTableModel.clear();
    routeTableEntryList.forEach(
        (List<RouteTableEntry> entryList) -> {
          entryList.forEach(entry -> routeTableModel.addData(entry));
        }
    );
  } // GEN-LAST:event_routeListValueChanged

  private void saveRouteButtonActionPerformed(
      java.awt.event.ActionEvent evt
  ) { // GEN-FIRST:event_saveRouteButtonActionPerformed
    // TODO add your handling code here:
    if (routeListModel.getSize() == 0) {
      return;
    }
    routeTableEntryXML.saveRouteFile(Collections.list(routeListModel.elements()));
  } // GEN-LAST:event_saveRouteButtonActionPerformed

  private void importRouteButtonActionPerformed(
      java.awt.event.ActionEvent evt
  ) { // GEN-FIRST:event_importRouteButtonActionPerformed
    // TODO add your handling code here:
    routeListModel.clear();
    List<RouteTableEntry> allRouteTableEntries = routeTableEntryXML.importRouteFile();
    if (allRouteTableEntries == null) {
      return;
    }
    Map<Integer, List<RouteTableEntry>> routeMap = allRouteTableEntries.stream().collect(
        Collectors.groupingBy(e -> e.getRouteId())
    );
    boolean firstValue = true;
    for (Map.Entry<Integer, List<RouteTableEntry>> entry : routeMap.entrySet()) {
      // Integer key = entry.getKey();
      List<RouteTableEntry> value = entry.getValue();
      routeListModel.addElement(value);
      if (firstValue) {
        routeList.setSelectedValue(value, true);
        firstValue = false;
      }
    }
  } // GEN-LAST:event_importRouteButtonActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton addNodeButton;
  private javax.swing.JButton addRouteButton;
  private javax.swing.JPanel buttonsPanel;
  private javax.swing.JButton delNodeButton;
  private javax.swing.JButton delRouteButton;
  private javax.swing.JButton importRouteButton;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JList<List<RouteTableEntry>> routeList;
  private javax.swing.JScrollPane routeListScrollPane;
  private javax.swing.JTable routeTable;
  private javax.swing.JScrollPane routeTableScrollPane;
  private javax.swing.JButton saveRouteButton;
  private javax.swing.JButton sendRouteButton;

  // End of variables declaration//GEN-END:variables
  private class RouteListModelCellRenderer
      extends
        DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus
    ) {
      JLabel label = (JLabel) super.getListCellRendererComponent(
          list, value, index, isSelected, cellHasFocus
      );
      label.setText("路线" + ((List<RouteTableEntry>) value).get(0).getRouteId());

      return label;
    }
  }

  private class RouteTableCellRenderer
      extends
        DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
    )
        throws IllegalArgumentException {

      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if (value == null) {
        setText("");
      }
      else if (value instanceof Integer) {
        Integer i = (Integer) value;
        Integer actionId;
        switch (column) {
          case RouteTableModel.ACTION1_ID_COLUMN:
          case RouteTableModel.ACTION2_ID_COLUMN:
          case RouteTableModel.ACTION3_ID_COLUMN:
          case RouteTableModel.ACTION4_ID_COLUMN:
            if (!mapRouteDisplayAction.containsKey(i)) {
              setText(Integer.toString(i));
              break;
            }
            setText(mapRouteDisplayAction.get(i).getActionName());
            break;
          case RouteTableModel.ACTION1_PARAM1_COLUMN:
          case RouteTableModel.ACTION2_PARAM1_COLUMN:
          case RouteTableModel.ACTION3_PARAM1_COLUMN:
          case RouteTableModel.ACTION4_PARAM1_COLUMN:
            actionId = (Integer) routeTable.getValueAt(row, column - 1);
            RouteDisplayAction routeDisplayAction = mapRouteDisplayAction.get(actionId);
            if (routeDisplayAction == null) {
              System.out.println("routeDisplayAction is null and actionId is " + actionId + "\n");
              break;
            }
            Map<Integer, String> param1 = mapRouteDisplayAction.get(actionId).getParam1();
            if (!param1.containsKey(i)) {
              setText(Integer.toString(i));
              break;
            }
            setText(param1.get(i));
            break;
          case RouteTableModel.ACTION1_PARAM2_COLUMN:
          case RouteTableModel.ACTION2_PARAM2_COLUMN:
          case RouteTableModel.ACTION3_PARAM2_COLUMN:
          case RouteTableModel.ACTION4_PARAM2_COLUMN:
            actionId = (Integer) routeTable.getValueAt(row, column - 2);
            Map<Integer, String> param2 = mapRouteDisplayAction.get(actionId).getParam2();
            if (!param2.containsKey(i)) {
              setText(Integer.toString(i));
              break;
            }
            setText(param2.get(i));
            break;
          default:
            break;
        }
      }
      else {
        throw new IllegalArgumentException("value");
      }
      return this;
    }
  }

  // 组合框的下拉显示renderer
  private class ParamListCellRenderer
      implements
        ListCellRenderer<Integer> {
    private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    Map<Integer, String> param;

    public ParamListCellRenderer(Map<Integer, String> param) {
      this.param = param;
    }

    @Override
    public Component getListCellRendererComponent(
        JList<? extends Integer> list,
        Integer value,
        int index,
        boolean isSelected,
        boolean cellHasFocus
    ) {

      JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
          list, value, index, isSelected, cellHasFocus
      );
      if (value != null) {
        label.setText(param.get(value));
      }
      else {
        label.setText(" ");
      }
      return label;
    }
  }

  private class MyCellEditor
      extends
        DefaultCellEditor
      implements
        FocusListener {

    JComboBox<?> comboBox;
    private int editedRow;
    private int editedColumn;
    private int oldValue;

    public MyCellEditor(JComboBox comboBox) {
      super(comboBox);
      this.comboBox = comboBox;
      comboBox.getEditor().getEditorComponent().addFocusListener(this);
      // setClickCountToStart(2);
    }

    @Override
    public void focusGained(FocusEvent e) {
      JTextComponent cell = (JTextComponent) e.getSource();
      String data = cell.getText();
      oldValue = Integer.valueOf(data);

      editedRow = routeTable.getEditingRow();
      editedColumn = routeTable.getEditingColumn();
    }

    @Override
    public void focusLost(FocusEvent e) {

      JTextComponent cell = (JTextComponent) e.getSource();
      String data = cell.getText();
      int newValue = Integer.valueOf(data);

      if (newValue != oldValue) {
        routeTableModel.setValueAt(newValue, editedRow, editedColumn);
      }
    }
  }

  private class MyComboBoxEditor
      implements
        ComboBoxEditor {

    private final JFormattedTextField textField = new JFormattedTextField(
        new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#0")))
    );

    @Override
    public Component getEditorComponent() {
      return textField;
    }

    @Override
    public void setItem(Object anObject) {
      if (anObject instanceof Integer) {
        textField.setText(Integer.toString((Integer) anObject));
      }
      else {
        textField.setText("");
      }
    }

    @Override
    public Object getItem() {
      if ("".equals(textField.getText())) {
        return 0;
      }
      return Integer.valueOf(textField.getText());
    }

    @Override
    public void selectAll() {
      textField.selectAll();
    }

    @Override
    public void addActionListener(ActionListener l) {
      textField.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
      textField.removeActionListener(l);
    }
  }
}
