// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.operationsdesk.application.action.course;

import static java.util.Objects.requireNonNull;
import static org.opentcs.operationsdesk.util.I18nPlantOverviewOperating.VEHICLEPOPUP_PATH;

import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Inject;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.SharedKernelServicePortal;
import org.opentcs.access.SharedKernelServicePortalProvider;
import org.opentcs.customizations.plantoverview.ApplicationFrame;
import org.opentcs.guing.base.model.elements.VehicleModel;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class WithdrawAction
    extends
      AbstractAction {

  /**
   * The ID for the 'withdraw regularly' action.
   */
  public static final String ID = "course.vehicle.withdrawTransportOrder";
  /**
   * The ID for the 'withdraw forcibly' action.
   */
  public static final String IMMEDIATELY_ID = "course.vehicle.withdrawTransportOrderImmediately";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(VEHICLEPOPUP_PATH);
  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(WithdrawAction.class);
  /**
   * The vehicles.
   */
  private final Collection<VehicleModel> vehicles;
  /**
   * Resource path to the correct lables.
   */
  private final boolean immediateAbort;
  /**
   * Provides access to a portal.
   */
  private final SharedKernelServicePortalProvider portalProvider;
  /**
   * The parent component for dialogs shown by this action.
   */
  private final Component dialogParent;

  /**
   * Creates a new instance.
   *
   * @param vehicles The selected vehicles.
   * @param immediateAbort Whether or not to abort immediately
   * @param portalProvider Provides access to a shared portal.
   * @param dialogParent The parent component for dialogs shown by this action.
   */
  @Inject
  @SuppressWarnings("this-escape")
  public WithdrawAction(
      @Assisted
      Collection<VehicleModel> vehicles,
      @Assisted
      boolean immediateAbort,
      SharedKernelServicePortalProvider portalProvider,
      @ApplicationFrame
      Component dialogParent
  ) {
    this.vehicles = requireNonNull(vehicles, "vehicles");
    this.immediateAbort = requireNonNull(immediateAbort, "immediateAbort");
    this.portalProvider = requireNonNull(portalProvider, "portalProvider");
    this.dialogParent = requireNonNull(dialogParent, "dialogParent");

    if (immediateAbort) {
      putValue(NAME, BUNDLE.getString("withdrawAction.withdrawImmediately.name"));
    }
    else {
      putValue(NAME, BUNDLE.getString("withdrawAction.withdraw.name"));
    }

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (immediateAbort) {
      int dialogResult
          = JOptionPane.showConfirmDialog(
              dialogParent,
              BUNDLE.getString("withdrawAction.optionPane_confirmWithdraw.message"),
              BUNDLE.getString("withdrawAction.optionPane_confirmWithdraw.title"),
              JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.WARNING_MESSAGE
          );

      if (dialogResult != JOptionPane.OK_OPTION) {
        return;
      }
    }

    try (SharedKernelServicePortal sharedPortal = portalProvider.register()) {

      for (VehicleModel vehicle : vehicles) {
        sharedPortal.getPortal().getDispatcherService().withdrawByVehicle(
            vehicle.getVehicle().getReference(), immediateAbort
        );
      }

    }
    catch (KernelRuntimeException e) {
      LOG.warn("Unexpected exception", e);
    }
  }

}
