// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.workingset;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import org.opentcs.components.kernel.OrderSequenceCleanupApproval;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.data.order.TransportOrder;

/**
 * Checks whether an order sequence may be removed.
 */
public class DefaultOrderSequenceCleanupApproval
    implements
      OrderSequenceCleanupApproval {

  private final TransportOrderPoolManager orderPoolManager;
  private final DefaultTransportOrderCleanupApproval defaultTransportOrderCleanupApproval;

  /**
   * Creates a new instance.
   *
   * @param orderPoolManager The order pool manager to be used.
   * @param defaultTransportOrderCleanupApproval Checks whether a transport order may be removed.
   */
  @Inject
  public DefaultOrderSequenceCleanupApproval(
      TransportOrderPoolManager orderPoolManager,
      DefaultTransportOrderCleanupApproval defaultTransportOrderCleanupApproval
  ) {
    this.orderPoolManager = requireNonNull(orderPoolManager, "orderPoolManager");
    this.defaultTransportOrderCleanupApproval
        = requireNonNull(
            defaultTransportOrderCleanupApproval,
            "defaultTransportOrderCleanupApproval"
        );
  }

  @Override
  public boolean test(OrderSequence seq) {
    if (!seq.isFinished()) {
      return false;
    }
    if (hasUnapprovedOrder(seq)) {
      return false;
    }
    return true;
  }

  private boolean hasUnapprovedOrder(OrderSequence seq) {
    return !(seq.getOrders()
        .stream()
        .map(
            reference -> orderPoolManager.getObjectRepo()
                .getObject(TransportOrder.class, reference)
        )
        .allMatch(defaultTransportOrderCleanupApproval));
  }
}
