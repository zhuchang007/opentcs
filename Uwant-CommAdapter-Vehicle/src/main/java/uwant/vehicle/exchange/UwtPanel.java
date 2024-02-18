/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.exchange;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import javax.swing.SwingUtilities;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.ServiceCallWrapper;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.drivers.vehicle.management.VehicleCommAdapterPanel;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.CallWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.vehicle.UwtProcessModel;
import uwant.vehicle.exchange.commands.SendRequestCommand;
import uwant.common.vehicle.telegrams.ActionRequest;
import uwant.common.vehicle.telegrams.StateResponse;

/** @author zhuchang */
public class UwtPanel extends VehicleCommAdapterPanel {

  private static final Logger LOG = LoggerFactory.getLogger(VehicleCommAdapterPanel.class);

  private final TCSObjectReference<Vehicle> vehicle;
  private UwtProcessModelTO processModelTO;

  /** The vehicle service used for interaction with the comm adapter. */
  private final VehicleService vehicleService;
  /** The call wrapper to use for service calls. */
  private final CallWrapper callWrapper;

  /**
   * Creates new form COMPanel
   *
   * @param vehicle
   * @param callWrapper
   * @param vehicleService
   * @param processModelTO
   */
  @Inject
  public UwtPanel(
      @Assisted TCSObjectReference<Vehicle> vehicle,
      @ServiceCallWrapper CallWrapper callWrapper,
      @Assisted VehicleService vehicleService,
      @Assisted VehicleProcessModelTO processModelTO) {
    initComponents();
    this.vehicle = vehicle;
    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.callWrapper = requireNonNull(callWrapper, "callWrapper");
    this.processModelTO = requireNonNull((UwtProcessModelTO) processModelTO);
    setButtonsEnabled(processModelTO.isCommAdapterConnected());
  }

  @Override
  public void processModelChange(String attributeChanged, VehicleProcessModelTO newProcessModel) {
    if (!(newProcessModel instanceof UwtProcessModelTO)) {
      return;
    }
    processModelTO = (UwtProcessModelTO) newProcessModel;
    if (Objects.equals(attributeChanged, UwtProcessModel.Attribute.CURRENT_STATE.name())) {
      updateStatePanel(processModelTO.getCurrentState());
    } else if (Objects.equals(attributeChanged, VehicleProcessModel.Attribute.COMM_ADAPTER_CONNECTED.name())) {
      setButtonsEnabled(processModelTO.isCommAdapterConnected());
    }
  }

  private void sendAdapterCommand(AdapterCommand command) {
    try {
      callWrapper.call(
          () -> vehicleService.sendCommAdapterCommand(vehicle, command));
    } catch (Exception ex) {
      LOG.warn("Error sending comm adapter command '{}'", command, ex);
    }
  }

  private void sendActionRequest(ActionRequest.Action action) {
    if (processModelTO == null || processModelTO.getCurrentState() == null) {
      return;
    }

    ActionRequest actionRequest =
        new ActionRequest(
            processModelTO.getCurrentState().getAddr(),
            processModelTO.getCurrentState().getAgvId(),
            action,
            speedComboBox.getSelectedIndex() + 1);
    sendAdapterCommand(new SendRequestCommand(actionRequest, -1));
  }

  private void setButtonsEnabled(Boolean b) {
    SwingUtilities.invokeLater(
      () -> {
        onLineLabel.setEnabled(b);
        fowardButton.setEnabled(b);
        backwardButton.setEnabled(b);
        patrolForwardButton.setEnabled(b);
        patrolBackwardButton.setEnabled(b);
        stopButton.setEnabled(b);
        turnLeftButton.setEnabled(b);
        turnRightButton.setEnabled(b);
        quickStopButton.setEnabled(b);
        speedComboBox.setEnabled(b);
    });
  }

  private void updateStatePanel(StateResponse stateResponse) {
    SwingUtilities.invokeLater(
      () -> {
        statusDirection.setText(stateResponse.getDirection().getName());
        statusSpeed.setText(stateResponse.getSpeedText());
        statusNode.setText(Integer.toString(stateResponse.getPositionId()));
        statusErrorCode.setText(stateResponse.getErrorCode());
        statusJacking.setText(stateResponse.getRollingState().getName());
        statusRolling.setText(stateResponse.getJackingState().getName());
        statusInput.setText(stateResponse.getInput());
    });
  }
  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    statusPanel = new javax.swing.JPanel();
    agvStatusPanel = new javax.swing.JPanel();
    onLineLabel = new javax.swing.JLabel();
    nullLabel = new javax.swing.JLabel();
    statusDirectionLabel = new javax.swing.JLabel();
    statusDirection = new javax.swing.JLabel();
    statusSpeedLabel = new javax.swing.JLabel();
    statusSpeed = new javax.swing.JLabel();
    statusNodeLabel = new javax.swing.JLabel();
    statusNode = new javax.swing.JLabel();
    statusErrorCodeLabel = new javax.swing.JLabel();
    statusErrorCode = new javax.swing.JLabel();
    statusJackLabel = new javax.swing.JLabel();
    statusJacking = new javax.swing.JLabel();
    statusRollerLabel = new javax.swing.JLabel();
    statusRolling = new javax.swing.JLabel();
    agvInputPanel = new javax.swing.JPanel();
    statusInput = new javax.swing.JLabel();
    manualPanel = new javax.swing.JPanel();
    sendButtonPanel = new javax.swing.JPanel();
    sendButtonPanel1 = new javax.swing.JPanel();
    fowardButton = new javax.swing.JButton();
    backwardButton = new javax.swing.JButton();
    turnLeftButton = new javax.swing.JButton();
    turnRightButton = new javax.swing.JButton();
    patrolForwardButton = new javax.swing.JButton();
    patrolBackwardButton = new javax.swing.JButton();
    stopButton = new javax.swing.JButton();
    quickStopButton = new javax.swing.JButton();
    speedComboBox = new javax.swing.JComboBox<>();

    setLayout(new java.awt.GridLayout(2, 0));

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/uwant/vehicle/commadapter/Bundle"); // NOI18N
    statusPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("COMPanel.statusPanelTitle"))); // NOI18N
    statusPanel.setLayout(new javax.swing.BoxLayout(statusPanel, javax.swing.BoxLayout.LINE_AXIS));

    agvStatusPanel.setMaximumSize(new java.awt.Dimension(260, 300));
    agvStatusPanel.setMinimumSize(new java.awt.Dimension(260, 300));
    agvStatusPanel.setPreferredSize(new java.awt.Dimension(280, 178));
    agvStatusPanel.setRequestFocusEnabled(false);
    agvStatusPanel.setLayout(new java.awt.GridLayout(8, 2));

    onLineLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    onLineLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uwant/vehicle/res/icons/online_icon32.png"))); // NOI18N
    onLineLabel.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/uwant/vehicle/res/icons/offline-icon32.png"))); // NOI18N
    agvStatusPanel.add(onLineLabel);
    agvStatusPanel.add(nullLabel);

    statusDirectionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    statusDirectionLabel.setText(bundle.getString("COMPanel.statusDirectionLabel")); // NOI18N
    agvStatusPanel.add(statusDirectionLabel);

    statusDirection.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    agvStatusPanel.add(statusDirection);

    statusSpeedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    statusSpeedLabel.setText(bundle.getString("COMPanel.statusSpeedLabel")); // NOI18N
    agvStatusPanel.add(statusSpeedLabel);

    statusSpeed.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    agvStatusPanel.add(statusSpeed);

    statusNodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    statusNodeLabel.setText(bundle.getString("COMPanel.statusNodeLabel")); // NOI18N
    agvStatusPanel.add(statusNodeLabel);

    statusNode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    agvStatusPanel.add(statusNode);

    statusErrorCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    statusErrorCodeLabel.setText(bundle.getString("COMPanel.statusErrorCodeLabel")); // NOI18N
    agvStatusPanel.add(statusErrorCodeLabel);

    statusErrorCode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    agvStatusPanel.add(statusErrorCode);

    statusJackLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    statusJackLabel.setText(bundle.getString("COMPanel.statusJackLabel")); // NOI18N
    agvStatusPanel.add(statusJackLabel);

    statusJacking.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    agvStatusPanel.add(statusJacking);

    statusRollerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    statusRollerLabel.setText(bundle.getString("COMPanel.statusRollerLabel")); // NOI18N
    agvStatusPanel.add(statusRollerLabel);

    statusRolling.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    agvStatusPanel.add(statusRolling);

    statusPanel.add(agvStatusPanel);

    statusInput.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("COMPanel.statusInputTitle"))); // NOI18N

    javax.swing.GroupLayout agvInputPanelLayout = new javax.swing.GroupLayout(agvInputPanel);
    agvInputPanel.setLayout(agvInputPanelLayout);
    agvInputPanelLayout.setHorizontalGroup(
      agvInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 400, Short.MAX_VALUE)
      .addGroup(agvInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agvInputPanelLayout.createSequentialGroup()
          .addGap(42, 42, 42)
          .addComponent(statusInput, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
          .addContainerGap()))
    );
    agvInputPanelLayout.setVerticalGroup(
      agvInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 306, Short.MAX_VALUE)
      .addGroup(agvInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agvInputPanelLayout.createSequentialGroup()
          .addContainerGap(32, Short.MAX_VALUE)
          .addComponent(statusInput, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addContainerGap(38, Short.MAX_VALUE)))
    );

    statusPanel.add(agvInputPanel);

    add(statusPanel);

    manualPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("COMPanel.manualPanelTitle"))); // NOI18N
    manualPanel.setMinimumSize(new java.awt.Dimension(100, 80));
    manualPanel.setLayout(new javax.swing.BoxLayout(manualPanel, javax.swing.BoxLayout.LINE_AXIS));

    sendButtonPanel.setMaximumSize(new java.awt.Dimension(260, 300));
    sendButtonPanel.setMinimumSize(new java.awt.Dimension(260, 300));
    sendButtonPanel.setPreferredSize(new java.awt.Dimension(280, 178));
    sendButtonPanel.setRequestFocusEnabled(false);
    sendButtonPanel.setLayout(new java.awt.BorderLayout());

    sendButtonPanel1.setMinimumSize(new java.awt.Dimension(1010, 300));
    sendButtonPanel1.setPreferredSize(new java.awt.Dimension(280, 300));

    fowardButton.setText(bundle.getString("COMPanel.forwardButton")); // NOI18N
    fowardButton.setPreferredSize(new java.awt.Dimension(95, 29));
    fowardButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fowardButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(fowardButton);

    backwardButton.setText(bundle.getString("COMPanel.backwardButton")); // NOI18N
    backwardButton.setPreferredSize(new java.awt.Dimension(95, 29));
    backwardButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        backwardButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(backwardButton);

    turnLeftButton.setText(bundle.getString("COMPanel.turnLeftButton")); // NOI18N
    turnLeftButton.setPreferredSize(new java.awt.Dimension(95, 29));
    turnLeftButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        turnLeftButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(turnLeftButton);

    turnRightButton.setText(bundle.getString("COMPanel.turnRightButton")); // NOI18N
    turnRightButton.setPreferredSize(new java.awt.Dimension(95, 29));
    turnRightButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        turnRightButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(turnRightButton);

    patrolForwardButton.setText(bundle.getString("COMPanel.patrolForwardButton")); // NOI18N
    patrolForwardButton.setPreferredSize(new java.awt.Dimension(95, 29));
    patrolForwardButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        patrolForwardButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(patrolForwardButton);

    patrolBackwardButton.setText(bundle.getString("COMPanel.patrolBackwardButton")); // NOI18N
    patrolBackwardButton.setPreferredSize(new java.awt.Dimension(95, 29));
    patrolBackwardButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        patrolBackwardButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(patrolBackwardButton);

    stopButton.setText(bundle.getString("COMPanel.stopButton")); // NOI18N
    stopButton.setPreferredSize(new java.awt.Dimension(95, 29));
    stopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        stopButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(stopButton);

    quickStopButton.setText(bundle.getString("COMPanel.quickStopButton")); // NOI18N
    quickStopButton.setPreferredSize(new java.awt.Dimension(95, 29));
    quickStopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        quickStopButtonActionPerformed(evt);
      }
    });
    sendButtonPanel1.add(quickStopButton);

    speedComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(bundle.getString("COMPanel.speedCombList").split(","))
    );
    speedComboBox.setPreferredSize(new java.awt.Dimension(95, 29));
    sendButtonPanel1.add(speedComboBox);

    sendButtonPanel.add(sendButtonPanel1, java.awt.BorderLayout.CENTER);

    manualPanel.add(sendButtonPanel);

    add(manualPanel);

    getAccessibleContext().setAccessibleName(bundle.getString("COMPanel.accessibleName")); // NOI18N
  }// </editor-fold>//GEN-END:initComponents

  private void fowardButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_fowardButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.FORWARD);
  } // GEN-LAST:event_fowardButtonActionPerformed

  private void stopButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_stopButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.STOP);
  } // GEN-LAST:event_stopButtonActionPerformed

  private void backwardButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_backwardButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.BACKWARD);
  } // GEN-LAST:event_backwardButtonActionPerformed

  private void turnLeftButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_turnLeftButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.TURN_LEFT);
  } // GEN-LAST:event_turnLeftButtonActionPerformed

  private void turnRightButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_turnRightButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.TURN_RIGHT);
  } // GEN-LAST:event_turnRightButtonActionPerformed

  private void patrolForwardButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_patrolForwardButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.PATROL_FORWARD);
  } // GEN-LAST:event_patrolForwardButtonActionPerformed

  private void patrolBackwardButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_patrolBackwardButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.PATROL_BACKWARD);
  } // GEN-LAST:event_patrolBackwardButtonActionPerformed

  private void quickStopButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_quickStopButtonActionPerformed
    // TODO add your handling code here:
    sendActionRequest(ActionRequest.Action.QUICK_STOP);
  } // GEN-LAST:event_quickStopButtonActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel agvInputPanel;
  private javax.swing.JPanel agvStatusPanel;
  private javax.swing.JButton backwardButton;
  private javax.swing.JButton fowardButton;
  private javax.swing.JPanel manualPanel;
  private javax.swing.JLabel nullLabel;
  private javax.swing.JLabel onLineLabel;
  private javax.swing.JButton patrolBackwardButton;
  private javax.swing.JButton patrolForwardButton;
  private javax.swing.JButton quickStopButton;
  private javax.swing.JPanel sendButtonPanel;
  private javax.swing.JPanel sendButtonPanel1;
  private javax.swing.JComboBox<String> speedComboBox;
  private javax.swing.JLabel statusDirection;
  private javax.swing.JLabel statusDirectionLabel;
  private javax.swing.JLabel statusErrorCode;
  private javax.swing.JLabel statusErrorCodeLabel;
  private javax.swing.JLabel statusInput;
  private javax.swing.JLabel statusJackLabel;
  private javax.swing.JLabel statusJacking;
  private javax.swing.JLabel statusNode;
  private javax.swing.JLabel statusNodeLabel;
  private javax.swing.JPanel statusPanel;
  private javax.swing.JLabel statusRollerLabel;
  private javax.swing.JLabel statusRolling;
  private javax.swing.JLabel statusSpeed;
  private javax.swing.JLabel statusSpeedLabel;
  private javax.swing.JButton stopButton;
  private javax.swing.JButton turnLeftButton;
  private javax.swing.JButton turnRightButton;
  // End of variables declaration//GEN-END:variables
}
