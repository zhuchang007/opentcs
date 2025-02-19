// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.priorization.candidate;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import java.util.Comparator;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.strategies.basic.dispatching.AssignmentCandidate;
import org.opentcs.strategies.basic.dispatching.priorization.transportorder.TransportOrderComparatorDeadlineAtRiskFirst;

/**
 * Compares {@link AssignmentCandidate}s by their transport order's deadlines, ordering those with a
 * deadline at risk first.
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class CandidateComparatorDeadlineAtRiskFirst
    implements
      Comparator<AssignmentCandidate> {

  /**
   * A key used for selecting this comparator in a configuration setting.
   * Should be unique among all keys.
   */
  public static final String CONFIGURATION_KEY = "DEADLINE_AT_RISK_FIRST";

  /**
   * The comparator that compares the deadlines of transport orders, taking the critical threshold
   * into account.
   */
  private final Comparator<TransportOrder> delegate;

  @Inject
  public CandidateComparatorDeadlineAtRiskFirst(
      TransportOrderComparatorDeadlineAtRiskFirst delegate
  ) {
    this.delegate = requireNonNull(delegate, "delegate");
  }

  /**
   * Compares two candidates by the deadline of their transport order and the given threshold
   * indicating whether the remaining time for the deadline is considered critical
   * Note: this comparator imposes orderings that are inconsistent with equals.
   *
   * @see Comparator#compare(java.lang.Object, java.lang.Object)
   * @param candidate1 The first candidate
   * @param candidate2 The second candidate
   * @return the value 0 if the deadlines of candidate1 and candidate2 are both at risk or not;
   * a value less than 0 if only the deadline of candidate1 is at risk
   * and a value greater than 0 in all other cases.
   */
  @Override
  public int compare(AssignmentCandidate candidate1, AssignmentCandidate candidate2) {
    return delegate.compare(candidate1.getTransportOrder(), candidate2.getTransportOrder());
  }

}
