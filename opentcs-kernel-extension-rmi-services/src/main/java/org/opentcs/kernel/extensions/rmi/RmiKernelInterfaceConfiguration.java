// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.rmi;

import org.opentcs.access.rmi.services.RemoteKernelServicePortal;
import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/**
 * Provides methods to configure the {@link RemoteKernelServicePortal} and the
 * {@link KernelRemoteService}s.
 */
@ConfigurationPrefix(RmiKernelInterfaceConfiguration.PREFIX)
public interface RmiKernelInterfaceConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "rmikernelinterface";

  @ConfigurationEntry(
      type = "Boolean",
      description = {"Whether to enable the interface."},
      orderKey = "0",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START
  )
  Boolean enable();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the RMI.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_1"
  )
  int registryPort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote kernel service portal.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_3"
  )
  int remoteKernelServicePortalPort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote plant model service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_4"
  )
  int remotePlantModelServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote transport order service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_5"
  )
  int remoteTransportOrderServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote vehicle service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_6"
  )
  int remoteVehicleServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote notification service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_7"
  )
  int remoteNotificationServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote scheduler service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_8"
  )
  int remoteSchedulerServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote router service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_9"
  )
  int remoteRouterServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote dispatcher service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_10"
  )
  int remoteDispatcherServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote query service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_11"
  )
  int remoteQueryServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote peripheral service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_12"
  )
  int remotePeripheralServicePort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port of the remote peripheral job service.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_13"
  )
  int remotePeripheralJobServicePort();

  @ConfigurationEntry(
      type = "Long",
      description = "The interval for cleaning out inactive clients (in ms).",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "2_sweeping"
  )
  long clientSweepInterval();

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to use SSL to encrypt connections.",
      changesApplied = ConfigurationEntry.ChangesApplied.ON_APPLICATION_START,
      orderKey = "0_address_11"
  )
  boolean useSsl();
}
