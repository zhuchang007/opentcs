// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.operationsdesk.application.action.course;

import static java.util.Objects.requireNonNull;
import static org.opentcs.operationsdesk.util.I18nPlantOverviewOperating.VEHICLEPOPUP_PATH;

import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Inject;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.SharedKernelServicePortal;
import org.opentcs.access.SharedKernelServicePortalProvider;
import org.opentcs.data.model.Vehicle;
import org.opentcs.guing.base.model.elements.VehicleModel;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class IntegrationLevelChangeAction
    extends
      AbstractAction {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(IntegrationLevelChangeAction.class);
  /**
   * This instance's resource bundle.
   */
  private final ResourceBundleUtil bundle = ResourceBundleUtil.getBundle(VEHICLEPOPUP_PATH);
  /**
   * The vehicles to change the level of.
   */
  private final Collection<VehicleModel> vehicles;
  /**
   * Sets the level to to change the vehicles to.
   */
  private final Vehicle.IntegrationLevel level;
  /**
   * Provides access to a portal.
   */
  private final SharedKernelServicePortalProvider portalProvider;

  /**
   * Creates a new instance.
   *
   * @param vehicles The selected vehicles.
   * @param level The level to to change the vehicles to.
   * @param portalProvider Provides access to a shared portal.
   */
  @Inject
  @SuppressWarnings("this-escape")
  public IntegrationLevelChangeAction(
      @Assisted
      Collection<VehicleModel> vehicles,
      @Assisted
      Vehicle.IntegrationLevel level,
      SharedKernelServicePortalProvider portalProvider
  ) {
    this.vehicles = requireNonNull(vehicles, "vehicles");
    this.level = requireNonNull(level, "level");
    this.portalProvider = requireNonNull(portalProvider, "portalProvider");

    String actionName;
    switch (level) {
      case TO_BE_NOTICED:
        actionName = bundle.getString("integrationLevelChangeAction.notice.name");
        break;
      case TO_BE_RESPECTED:
        actionName = bundle.getString("integrationLevelChangeAction.respect.name");
        break;
      case TO_BE_UTILIZED:
        actionName = bundle.getString("integrationLevelChangeAction.utilize.name");
        break;
      default:
        actionName = bundle.getString("integrationLevelChangeAction.ignore.name");
        break;
    }
    putValue(NAME, actionName);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    try (SharedKernelServicePortal sharedPortal = portalProvider.register()) {

      for (VehicleModel vehicle : vehicles) {
        sharedPortal.getPortal().getVehicleService().updateVehicleIntegrationLevel(
            vehicle.getVehicle().getReference(), level
        );
      }

    }
    catch (KernelRuntimeException e) {
      LOG.warn("Unexpected exception", e);
    }
  }
}
