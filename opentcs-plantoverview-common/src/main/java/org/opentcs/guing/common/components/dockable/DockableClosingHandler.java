// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.components.dockable;

import static java.util.Objects.requireNonNull;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import com.google.inject.assistedinject.Assisted;
import jakarta.inject.Inject;
import org.opentcs.guing.common.application.ViewManager;

/**
 * Handles closing of a dockable.
 */
public class DockableClosingHandler
    implements
      CVetoClosingListener {

  /**
   * The dockable.
   */
  private final DefaultSingleCDockable dockable;
  /**
   * Manages the application's dockables.
   */
  private final DockingManager dockingManager;
  /**
   * Manages the application's views.
   */
  private final ViewManager viewManager;

  /**
   * Creates a new instance.
   *
   * @param dockable The dockable.
   * @param viewManager Manages the application's views.
   * @param dockingManager Manages the application's dockables.
   */
  @Inject
  public DockableClosingHandler(
      @Assisted
      DefaultSingleCDockable dockable,
      ViewManager viewManager,
      DockingManager dockingManager
  ) {
    this.dockable = requireNonNull(dockable, "dockable");
    this.viewManager = requireNonNull(viewManager, "viewManager");
    this.dockingManager = requireNonNull(dockingManager, "dockingManager");
  }

  @Override
  public void closing(CVetoClosingEvent event) {
  }

  @Override
  public void closed(CVetoClosingEvent event) {
    if (event.isExpected()) {
      dockingManager.removeDockable(dockable);
      viewManager.removeDockable(dockable);
    }
  }
}
