// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.access.rmi.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.opentcs.access.rmi.ClientID;
import org.opentcs.access.to.order.OrderSequenceCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.data.order.TransportOrder;

/**
 * Declares the methods provided by the {@link TransportOrderService} via RMI.
 *
 * <p>
 * The majority of the methods declared here have signatures analogous to their counterparts in
 * {@link TransportOrderService}, with an additional {@link ClientID} parameter which serves the
 * purpose of identifying the calling client and determining its permissions.
 * </p>
 * <p>
 * To avoid redundancy, the semantics of methods that only pass through their arguments are not
 * explicitly documented here again. See the corresponding API documentation in
 * {@link TransportOrderService} for these, instead.
 * </p>
 */
public interface RemoteTransportOrderService
    extends
      RemoteTCSObjectService,
      Remote {

  // CHECKSTYLE:OFF
  OrderSequence createOrderSequence(ClientID clientId, OrderSequenceCreationTO to)
      throws RemoteException;

  TransportOrder createTransportOrder(ClientID clientId, TransportOrderCreationTO to)
      throws RemoteException;

  void markOrderSequenceComplete(ClientID clientId, TCSObjectReference<OrderSequence> ref)
      throws RemoteException;

  void updateTransportOrderIntendedVehicle(
      ClientID clientId,
      TCSObjectReference<TransportOrder> orderRef,
      TCSObjectReference<Vehicle> vehicleRef
  )
      throws RemoteException;
  // CHECKSTYLE:ON
}
