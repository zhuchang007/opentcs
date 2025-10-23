// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.extra;

import jakarta.inject.Singleton;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures the service web API extension.
 */
public class ExtraFuncModule
    extends
      KernelInjectionModule {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ExtraFuncModule.class);

  /**
   * Creates a new instance.
   */
  public ExtraFuncModule() {
  }

  @Override
  protected void configure() {
    extensionsBinderOperating().addBinding()
        .to(Extra.class)
        .in(Singleton.class);
  }
}
