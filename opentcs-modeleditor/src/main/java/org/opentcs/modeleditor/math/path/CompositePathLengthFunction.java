// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.math.path;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import java.util.Map;
import org.opentcs.guing.base.model.elements.PathModel;

/**
 * A composite of various {@link PathLengthFunction}s for different {@link PathModel.Type}s which
 * falls back to {@link EuclideanDistance}.
 */
public class CompositePathLengthFunction
    implements
      PathLengthFunction {

  private final Map<PathModel.Type, PathLengthFunction> pathLengthFunctions;
  private final EuclideanDistance euclideanDistance;

  @Inject
  public CompositePathLengthFunction(
      @Nonnull
      Map<PathModel.Type, PathLengthFunction> pathLengthFunctions,
      @Nonnull
      EuclideanDistance euclideanDistance
  ) {
    this.pathLengthFunctions = requireNonNull(pathLengthFunctions, "pathLengthFunctions");
    this.euclideanDistance = requireNonNull(euclideanDistance, "euclideanDistance");
  }

  @Override
  public double applyAsDouble(PathModel path) {
    return pathLengthFunctions
        .getOrDefault((PathModel.Type) path.getPropertyPathConnType().getValue(), euclideanDistance)
        .applyAsDouble(path);
  }
}
