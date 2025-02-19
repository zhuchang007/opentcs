// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.priorization.transportorder;

import java.util.Comparator;
import org.opentcs.data.order.TransportOrder;

/**
 * Compares {@link TransportOrder}s by age.
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class TransportOrderComparatorByDeadline
    implements
      Comparator<TransportOrder> {

  /**
   * A key used for selecting this comparator in a configuration setting.
   * Should be unique among all keys.
   */
  public static final String CONFIGURATION_KEY = "BY_DEADLINE";

  /**
   * Creates a new instance.
   */
  public TransportOrderComparatorByDeadline() {
  }

  /**
   * Compares two orders by their deadline.
   * Note: this comparator imposes orderings that are inconsistent with equals.
   *
   * @see Comparator#compare(java.lang.Object, java.lang.Object)
   * @param order1 The first order.
   * @param order2 The second order.
   * @return the value 0 if order1 and order2 have the same deadline;
   * a value less than 0 if order1 has an earlier deadline than order2;
   * and a value greater than 0 otherwise.
   */
  @Override
  public int compare(TransportOrder order1, TransportOrder order2) {
    return order1.getDeadline().compareTo(order2.getDeadline());
  }

}
