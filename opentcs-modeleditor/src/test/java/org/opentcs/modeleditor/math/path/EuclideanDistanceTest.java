// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.math.path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.guing.base.components.properties.type.LengthProperty;
import org.opentcs.guing.base.model.elements.PathModel;
import org.opentcs.guing.base.model.elements.PointModel;

/**
 * Unit tests for {@link EuclideanDistance}.
 */
class EuclideanDistanceTest {

  private PointModel startPoint;
  private PointModel endPoint;
  private PathModel pathModel;
  private EuclideanDistance function;
  private PathLengthMath pathLengthMath;

  @BeforeEach
  void setUp() {
    startPoint = new PointModel();
    endPoint = new PointModel();
    pathModel = new PathModel();
    pathModel.setConnectedComponents(startPoint, endPoint);

    pathLengthMath = mock();
    function = new EuclideanDistance(pathLengthMath);
  }

  @Test
  void verifyEuclideanDistanceIsUsed() {
    startPoint.getPropertyModelPositionX().setValueAndUnit(1000, LengthProperty.Unit.MM);
    startPoint.getPropertyModelPositionY().setValueAndUnit(1000, LengthProperty.Unit.MM);
    endPoint.getPropertyModelPositionX().setValueAndUnit(1000, LengthProperty.Unit.MM);
    endPoint.getPropertyModelPositionY().setValueAndUnit(1000, LengthProperty.Unit.MM);

    function.applyAsDouble(pathModel);

    verify(pathLengthMath).euclideanDistance(
        new Coordinate(1000, 1000),
        new Coordinate(1000, 1000)
    );
  }
}
