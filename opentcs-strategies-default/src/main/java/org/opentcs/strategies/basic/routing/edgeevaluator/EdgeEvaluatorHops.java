// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.routing.edgeevaluator;

import org.opentcs.components.kernel.routing.Edge;
import org.opentcs.components.kernel.routing.EdgeEvaluator;
import org.opentcs.data.model.Vehicle;

/**
 * Uses a weight of 1 for every edge.
 */
public class EdgeEvaluatorHops
    implements
      EdgeEvaluator {

  /**
   * A key used for selecting this evaluator in a configuration setting.
   * Should be unique among all keys.
   */
  public static final String CONFIGURATION_KEY = "HOPS";

  public EdgeEvaluatorHops() {
  }

  @Override
  public void onGraphComputationStart(Vehicle vehicle) {
  }

  @Override
  public void onGraphComputationEnd(Vehicle vehicle) {
  }

  @Override
  public double computeWeight(Edge edge, Vehicle vehicle) {
    return 1;
  }
}
