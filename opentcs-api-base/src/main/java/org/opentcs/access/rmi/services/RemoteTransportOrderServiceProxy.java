// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.access.rmi.services;

import java.rmi.RemoteException;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.to.order.OrderSequenceCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.data.order.TransportOrder;

/**
 * The default implementation of the transport order service.
 * Delegates method invocations to the corresponding remote service.
 */
class RemoteTransportOrderServiceProxy
    extends
      RemoteTCSObjectServiceProxy<RemoteTransportOrderService>
    implements
      TransportOrderService {

  /**
   * Creates a new instance.
   */
  RemoteTransportOrderServiceProxy() {
  }

  @Override
  public OrderSequence createOrderSequence(OrderSequenceCreationTO to)
      throws KernelRuntimeException {
    checkServiceAvailability();

    try {
      return getRemoteService().createOrderSequence(getClientId(), to);
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }

  @Override
  public TransportOrder createTransportOrder(TransportOrderCreationTO to)
      throws ObjectUnknownException,
        ObjectExistsException,
        KernelRuntimeException {
    checkServiceAvailability();

    try {
      return getRemoteService().createTransportOrder(getClientId(), to);
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }

  @Override
  public void markOrderSequenceComplete(TCSObjectReference<OrderSequence> ref)
      throws ObjectUnknownException,
        KernelRuntimeException {
    checkServiceAvailability();

    try {
      getRemoteService().markOrderSequenceComplete(getClientId(), ref);
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }

  @Override
  public void updateTransportOrderIntendedVehicle(
      TCSObjectReference<TransportOrder> orderRef,
      TCSObjectReference<Vehicle> vehicleRef
  )
      throws ObjectUnknownException,
        IllegalArgumentException {
    checkServiceAvailability();

    try {
      getRemoteService().updateTransportOrderIntendedVehicle(getClientId(), orderRef, vehicleRef);
    }
    catch (RemoteException ex) {
      throw findSuitableExceptionFor(ex);
    }
  }
}
