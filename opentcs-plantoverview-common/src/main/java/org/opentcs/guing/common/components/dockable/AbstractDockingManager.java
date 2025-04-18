// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.components.dockable;

import static java.util.Objects.requireNonNull;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;

/**
 * Utility class for working with dockables.
 */
public abstract class AbstractDockingManager
    implements
      DockingManager {

  /**
   * PropertyChangeEvent when a floating dockable closes.
   */
  public static final String DOCKABLE_CLOSED = "DOCKABLE_CLOSED";
  /**
   * Map that contains all tab panes. They are stored by their id.
   */
  private final Map<String, CStack> tabPanes = new HashMap<>();
  /**
   * Control for the dockable panels.
   */
  private final CControl control;
  /**
   * The listeners for closing events.
   */
  private final List<PropertyChangeListener> listeners = new ArrayList<>();

  public AbstractDockingManager(CControl control) {
    this.control = requireNonNull(control, "control");
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Creates a new dockable.
   *
   * @param id The unique id for this dockable.
   * @param title The title text of this new dockable.
   * @param comp The JComponent wrapped by the new dockable.
   * @param closeable If the dockable can be closeable or not.
   * @return The newly created dockable.
   */
  public DefaultSingleCDockable createDockable(
      String id,
      String title,
      JComponent comp,
      boolean closeable
  ) {
    Objects.requireNonNull(id, "id is null");
    Objects.requireNonNull(title, "title is null");
    Objects.requireNonNull(comp, "comp is null");
    if (control == null) {
      return null;
    }
    DefaultSingleCDockable dockable = new DefaultSingleCDockable(id, title);
    dockable.setCloseable(closeable);
    dockable.add(comp);
    return dockable;
  }

  /**
   * Creates a new floating dockable.
   *
   * @param id The unique id for this dockable.
   * @param title The title text of this new dockable.
   * @param comp The JComponent wrapped by the new dockable.
   * @return The newly created dockable.
   */
  public DefaultSingleCDockable createFloatingDockable(
      String id,
      String title,
      JComponent comp
  ) {
    if (control == null) {
      return null;
    }
    final DefaultSingleCDockable dockable = new DefaultSingleCDockable(id, title);
    dockable.setCloseable(true);
    dockable.setFocusComponent(comp);
    dockable.add(comp);
    dockable.addVetoClosingListener(new CVetoClosingListener() {

      @Override
      public void closing(CVetoClosingEvent event) {
      }

      @Override
      public void closed(CVetoClosingEvent event) {
        fireFloatingDockableClosed(dockable);
      }
    });
    control.addDockable(dockable);
    dockable.setExtendedMode(ExtendedMode.EXTERNALIZED);
    Rectangle centerRectangle = control.getContentArea().getCenter().getBounds();
    dockable.setLocation(
        CLocation.external(
            (centerRectangle.width - comp.getWidth()) / 2,
            (centerRectangle.height - comp.getHeight()) / 2,
            comp.getWidth(),
            comp.getHeight()
        )
    );
    return dockable;
  }

  /**
   * Adds a dockable as tab to the tab pane identified by the given id.
   *
   * @param newTab The new dockable that shall be added.
   * @param id The ID of the tab pane.
   * @param index Index where to insert the dockable in the tab pane.
   */
  public void addTabTo(DefaultSingleCDockable newTab, String id, int index) {
    Objects.requireNonNull(newTab, "newTab is null.");
    Objects.requireNonNull(id, "id is null");
    CStack tabPane = tabPanes.get(id);
    if (tabPane != null) {
      control.addDockable(newTab);
      newTab.setWorkingArea(tabPane);
      tabPane.getStation().add(newTab.intern(), index);
      tabPane.getStation().setFrontDockable(newTab.intern());
    }
  }

  @Override
  public void removeDockable(SingleCDockable dockable) {
    Objects.requireNonNull(dockable, "dockable is null");
    if (control != null) {
      control.removeDockable(dockable);
    }
  }

  @Override
  public void removeDockable(String id) {
    Objects.requireNonNull(id);
    SingleCDockable dock = control.getSingleDockable(id);
    if (dock != null) {
      removeDockable(dock);
    }
  }

  /**
   * Returns the CControl.
   *
   * @return The CControl.
   */
  public CControl getCControl() {
    return control;
  }

  /**
   * Returns the whole component with all dockables, tab panes etc.
   *
   * @return The CContentArea of the CControl.
   */
  public CContentArea getContentArea() {
    if (control != null) {
      return control.getContentArea();
    }
    else {
      return null;
    }
  }

  /**
   * Returns the tab pane with the given id.
   *
   * @param id ID of the tab pane.
   * @return The tab pane or null if there is no tab pane with this id.
   */
  public CStack getTabPane(String id) {
    if (control != null) {
      return tabPanes.get(id);
    }
    else {
      return null;
    }
  }

  public abstract void reset();

  /**
   * Wraps all given JComponents into a dockable and deploys them on the CControl.
   */
  public abstract void initializeDockables();

  protected void addTabPane(String id, CStack tabPane) {
    tabPanes.put(id, tabPane);
  }

  /**
   * Hides a dockable (by actually removing it from its station).
   *
   * @param station The CStackDockStation the dockable belongs to.
   * @param dockable The dockable to hide.
   */
  public void hideDockable(CStackDockStation station, DefaultSingleCDockable dockable) {
    int index = station.indexOf(dockable.intern());

    if (index <= -1) {
      station.add(dockable.intern(), station.getDockableCount());
      index = station.indexOf(dockable.intern());
    }
    station.remove(index);
  }

  /**
   * Shows a dockable (by actually adding it to its station).
   *
   * @param station The CStackDockStation the dockable belongs to.
   * @param dockable The dockable to show.
   * @param index Where to add the dockable.
   */
  public void showDockable(
      CStackDockStation station,
      DefaultSingleCDockable dockable,
      int index
  ) {
    if (station.indexOf(dockable.intern()) <= -1) {
      station.add(dockable.intern(), index);
    }
  }

  /**
   * Sets the visibility status of a dockable with the given id.
   *
   * @param id The id of the dockable.
   * @param visible If it shall be visible or not.
   */
  public void setDockableVisibility(String id, boolean visible) {
    if (control != null) {
      SingleCDockable dockable = control.getSingleDockable(id);
      if (dockable != null) {
        dockable.setVisible(visible);
      }
    }
  }

  /**
   * Checks if the given dockable is docked to its CStackDockStation.
   *
   * @param station The station the dockable should be docked in.
   * @param dockable The dockable to check.
   * @return True if it is docked, false otherwise.
   */
  public boolean isDockableDocked(CStackDockStation station, DefaultSingleCDockable dockable) {
    return station.indexOf(dockable.intern()) <= -1;
  }

  /**
   * Fires a <code>PropertyChangeEvent</code> when a floatable dockable is closed
   * (eg a plugin panel).
   *
   * @param dockable The dockable that was closed.
   */
  private void fireFloatingDockableClosed(DefaultSingleCDockable dockable) {
    for (PropertyChangeListener listener : listeners) {
      listener.propertyChange(
          new PropertyChangeEvent(this, DOCKABLE_CLOSED, dockable, dockable)
      );
    }
  }
}
