// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.priorization.candidate;

import java.util.Comparator;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.strategies.basic.dispatching.AssignmentCandidate;
import org.opentcs.strategies.basic.dispatching.priorization.transportorder.TransportOrderComparatorByAge;

/**
 * Compares {@link AssignmentCandidate}s by age of the order.
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class CandidateComparatorByOrderAge
    implements
      Comparator<AssignmentCandidate> {

  /**
   * A key used for selecting this comparator in a configuration setting.
   * Should be unique among all keys.
   */
  public static final String CONFIGURATION_KEY = "BY_AGE";

  private final Comparator<TransportOrder> delegate = new TransportOrderComparatorByAge();

  /**
   * Creates a new instance.
   */
  public CandidateComparatorByOrderAge() {
  }

  /**
   * Compares two candidate by the age of the order.
   * Note: this comparator imposes orderings that are inconsistent with equals.
   *
   * @see Comparator#compare(java.lang.Object, java.lang.Object)
   * @param candidate1 The first candidate.
   * @param candidate2 The second candidate.
   * @return the value zero, if the transport order of
   * candidate1 and candidate2 have the same creation time;
   * a value less than zero, if candidate1 is older than candidate2.
   * a value greater than zero otherwise.
   */
  @Override
  public int compare(AssignmentCandidate candidate1, AssignmentCandidate candidate2) {
    return delegate.compare(candidate1.getTransportOrder(), candidate2.getTransportOrder());
  }

}
