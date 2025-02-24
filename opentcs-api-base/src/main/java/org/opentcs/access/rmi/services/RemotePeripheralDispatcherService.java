// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.access.rmi.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.opentcs.access.rmi.ClientID;
import org.opentcs.components.kernel.services.PeripheralDispatcherService;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.TCSResourceReference;
import org.opentcs.data.peripherals.PeripheralJob;

/**
 * Declares the methods provided by the {@link PeripheralDispatcherService} via RMI.
 *
 * <p>
 * The majority of the methods declared here have signatures analogous to their counterparts in
 * {@link PeripheralDispatcherService}, with an additional {@link ClientID} parameter which serves
 * the purpose of identifying the calling client and determining its permissions.
 * </p>
 * <p>
 * To avoid redundancy, the semantics of methods that only pass through their arguments are not
 * explicitly documented here again. See the corresponding API documentation in
 * {@link PeripheralDispatcherService} for these, instead.
 * </p>
 */
public interface RemotePeripheralDispatcherService
    extends
      Remote {

  // CHECKSTYLE:OFF
  void dispatch(ClientID clientId)
      throws RemoteException;

  void withdrawByLocation(ClientID clientId, TCSResourceReference<Location> ref)
      throws RemoteException;

  void withdrawByPeripheralJob(ClientID clientId, TCSObjectReference<PeripheralJob> ref)
      throws RemoteException;
  // CHECKSTYLE:ON
}
