// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.event;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import org.opentcs.components.Lifecycle;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class EventLogger
    implements
      EventHandler,
      Lifecycle {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EventLogger.class);
  /**
   * Where we register for events.
   */
  private final EventSource eventSource;
  /**
   * Whether this component is initialized.
   */
  private boolean initialized;

  @Inject
  public EventLogger(
      @ApplicationEventBus
      EventSource eventSource
  ) {
    this.eventSource = requireNonNull(eventSource, "eventSource");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    eventSource.subscribe(this);

    initialized = true;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }

    eventSource.unsubscribe(this);

    initialized = false;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void onEvent(Object event) {
    LOG.debug("Received event: {}", event);
  }

}
