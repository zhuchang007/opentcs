// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.dispatching.priorization;

import static org.opentcs.util.Assertions.checkArgument;

import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.Map;
import org.opentcs.strategies.basic.dispatching.AssignmentCandidate;
import org.opentcs.strategies.basic.dispatching.DefaultDispatcherConfiguration;
import org.opentcs.strategies.basic.dispatching.priorization.candidate.CandidateComparatorByOrderAge;
import org.opentcs.strategies.basic.dispatching.priorization.candidate.CandidateComparatorByOrderName;

/**
 * A composite of all configured transport order candidate comparators.
 */
public class CompositeOrderCandidateComparator
    implements
      Comparator<AssignmentCandidate> {

  /**
   * A comparator composed of all configured comparators, in the configured order.
   */
  private final Comparator<AssignmentCandidate> compositeComparator;

  @Inject
  public CompositeOrderCandidateComparator(
      DefaultDispatcherConfiguration configuration,
      Map<String, Comparator<AssignmentCandidate>> availableComparators
  ) {
    // At the end, if all other comparators failed to see a difference, compare by age.
    // As the age of two distinct transport orders may still be the same, finally compare by name.
    // Add configured comparators before these two.
    Comparator<AssignmentCandidate> composite
        = new CandidateComparatorByOrderAge().thenComparing(new CandidateComparatorByOrderName());

    for (String priorityKey : Lists.reverse(configuration.orderCandidatePriorities())) {
      Comparator<AssignmentCandidate> configuredComparator = availableComparators.get(priorityKey);
      checkArgument(
          configuredComparator != null,
          "Unknown order candidate priority key: '%s'",
          priorityKey
      );
      composite = configuredComparator.thenComparing(composite);
    }
    this.compositeComparator = composite;
  }

  @Override
  public int compare(AssignmentCandidate o1, AssignmentCandidate o2) {
    return compositeComparator.compare(o1, o2);
  }

}
