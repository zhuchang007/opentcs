// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.routing.edgeevaluator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.components.kernel.routing.Edge;
import org.opentcs.components.kernel.routing.EdgeEvaluator;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.strategies.basic.routing.jgrapht.ShortestPathConfiguration;

/**
 */
class EdgeEvaluatorCompositeTest {

  private static final String EVALUATOR_MOCK = "EVALUATOR_MOCK";
  private static final String EVALUATOR_1 = "EVALUATOR_1";
  private static final String EVALUATOR_2 = "EVALUATOR_2";
  private static final String EVALUATOR_3 = "EVALUATOR_3";

  private Edge edge;
  private Vehicle vehicle;
  private EdgeEvaluator evaluatorMock;
  private ShortestPathConfiguration configuration;
  private final Map<String, EdgeEvaluator> evaluators = new HashMap<>();

  @BeforeEach
  void setUp() {
    Point srcPoint = new Point("srcPoint");
    Point dstPoint = new Point("dstPoint");

    edge = new Edge(
        new Path("pathName", srcPoint.getReference(), dstPoint.getReference()),
        true
    );
    vehicle = new Vehicle("someVehicle");

    configuration = mock(ShortestPathConfiguration.class);

    evaluatorMock = mock(EdgeEvaluator.class);

    evaluators.put(EVALUATOR_MOCK, evaluatorMock);
    evaluators.put(EVALUATOR_1, new FixedValueEdgeEvaluator(1.0));
    evaluators.put(EVALUATOR_2, new FixedValueEdgeEvaluator(0.9));
    evaluators.put(EVALUATOR_3, new FixedValueEdgeEvaluator(Double.POSITIVE_INFINITY));
  }

  @Test
  void notifyOnGraphComputation() {
    when(configuration.edgeEvaluators()).thenReturn(List.of(EVALUATOR_MOCK));
    EdgeEvaluatorComposite edgeEvaluator = new EdgeEvaluatorComposite(configuration, evaluators);

    verify(evaluatorMock, never()).onGraphComputationStart(vehicle);
    verify(evaluatorMock, never()).onGraphComputationEnd(vehicle);

    edgeEvaluator.onGraphComputationStart(vehicle);
    verify(evaluatorMock).onGraphComputationStart(vehicle);
    verify(evaluatorMock, never()).onGraphComputationEnd(vehicle);

    edgeEvaluator.onGraphComputationEnd(vehicle);
    verify(evaluatorMock).onGraphComputationStart(vehicle);
    verify(evaluatorMock).onGraphComputationEnd(vehicle);
  }

  @Test
  void computeZeroWithoutComponents() {
    EdgeEvaluatorComposite edgeEvaluator = new EdgeEvaluatorComposite(configuration, evaluators);

    verifyNoInteractions(evaluatorMock);
    assertThat(edgeEvaluator.computeWeight(edge, vehicle), is(0.0));
  }

  @Test
  void computeSumOfOneComponent() {
    when(configuration.edgeEvaluators()).thenReturn(List.of(EVALUATOR_1));
    EdgeEvaluatorComposite edgeEvaluator = new EdgeEvaluatorComposite(configuration, evaluators);

    verifyNoInteractions(evaluatorMock);
    assertThat(edgeEvaluator.computeWeight(edge, vehicle), is(1.0));
  }

  @Test
  void computeSumOfTwoComponents() {
    when(configuration.edgeEvaluators()).thenReturn(List.of(EVALUATOR_1, EVALUATOR_2));
    EdgeEvaluatorComposite edgeEvaluator = new EdgeEvaluatorComposite(configuration, evaluators);

    verifyNoInteractions(evaluatorMock);
    assertThat(edgeEvaluator.computeWeight(edge, vehicle), is(1.9));
  }

  @Test
  void computeInfinityIfAnyComponentReturnsInfinity() {
    when(configuration.edgeEvaluators()).thenReturn(List.of(EVALUATOR_1, EVALUATOR_2, EVALUATOR_3));
    EdgeEvaluatorComposite edgeEvaluator = new EdgeEvaluatorComposite(configuration, evaluators);

    verifyNoInteractions(evaluatorMock);
    assertThat(edgeEvaluator.computeWeight(edge, vehicle), is(Double.POSITIVE_INFINITY));
  }

  private static class FixedValueEdgeEvaluator
      implements
        EdgeEvaluator {

    private final double value;

    FixedValueEdgeEvaluator(double value) {
      this.value = value;
    }

    @Override
    public void onGraphComputationStart(Vehicle vehicle) {
    }

    @Override
    public void onGraphComputationEnd(Vehicle vehicle) {
    }

    @Override
    public double computeWeight(Edge edge, Vehicle vehicle) {
      return value;
    }
  }
}
