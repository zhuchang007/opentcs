// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.math.path;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import org.opentcs.guing.base.components.properties.type.LengthProperty;
import org.opentcs.guing.base.model.elements.PathModel;
import org.opentcs.guing.base.model.elements.PointModel;

/**
 * Calculates the length of a path as the Euclidean distance between the start and end point.
 */
public class EuclideanDistance
    implements
      PathLengthFunction {

  private final PathLengthMath pathLengthMath;

  /**
   * Creates a new instance.
   *
   * @param pathLengthMath Provides a method for the euclidean distance.
   */
  @Inject
  public EuclideanDistance(
      @Nonnull
      PathLengthMath pathLengthMath
  ) {
    this.pathLengthMath = requireNonNull(pathLengthMath, "pathLengthMath");
  }

  @Override
  public double applyAsDouble(
      @Nonnull
      PathModel path
  ) {
    requireNonNull(path, "path");

    PointModel start = (PointModel) path.getStartComponent();
    PointModel end = (PointModel) path.getEndComponent();

    Coordinate x = new Coordinate(
        start.getPropertyModelPositionX().getValueByUnit(LengthProperty.Unit.MM),
        start.getPropertyModelPositionY().getValueByUnit(LengthProperty.Unit.MM)
    );
    Coordinate y = new Coordinate(
        end.getPropertyModelPositionX().getValueByUnit(LengthProperty.Unit.MM),
        end.getPropertyModelPositionY().getValueByUnit(LengthProperty.Unit.MM)
    );

    return pathLengthMath.euclideanDistance(x, y);
  }
}
