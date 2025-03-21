// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.base.model;

import java.util.List;
import org.opentcs.guing.base.model.elements.AbstractConnection;

/**
 * A {@link ModelComponent} that can be connected with another model component.
 */
public interface ConnectableModelComponent
    extends
      ModelComponent {

  /**
   * Adds a connection.
   *
   * @param connection The connection to be added.
   */
  void addConnection(AbstractConnection connection);

  /**
   * Returns all connections.
   *
   * @return All connections.
   */
  List<AbstractConnection> getConnections();

  /**
   * Removes a connection.
   *
   * @param connection The connection to be removed.
   */
  void removeConnection(AbstractConnection connection);
}
