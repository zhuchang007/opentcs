// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.components.plantoverview;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.opentcs.access.Kernel;

/**
 * Produces plugin panels to extend an openTCS user interface.
 */
public interface PluggablePanelFactory {

  /**
   * Checks whether this factory produces panels that are available in the
   * passed <code>Kernel.State</code>.
   *
   * @param state The kernel state.
   * @return <code>true</code> if, and only if, this factory returns panels that
   * are available in the passed kernel state.
   */
  boolean providesPanel(Kernel.State state);

  /**
   * Returns a string describing the factory/the panels provided.
   * This should be a short string that can be displayed e.g. as a menu item for
   * selecting a factory/plugin panel to be displayed.
   *
   * @return A string describing the factory/the panels provided.
   */
  @Nonnull
  String getPanelDescription();

  /**
   * Returns a newly created panel.
   * If a reference to the kernel provider has not been set, yet, or has been
   * set to <code>null</code>, this method returns <code>null</code>.
   *
   * @param state The kernel state for which to create the panel.
   * @return A newly created panel.
   */
  @Nullable
  PluggablePanel createPanel(Kernel.State state);
}
