// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.rmi;

import static java.util.Objects.requireNonNull;

import jakarta.inject.Inject;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import org.opentcs.access.rmi.ClientID;
import org.opentcs.access.rmi.factories.SocketFactoryProvider;
import org.opentcs.access.rmi.services.RegistrationName;
import org.opentcs.access.rmi.services.RemotePeripheralJobService;
import org.opentcs.access.to.peripherals.PeripheralJobCreationTO;
import org.opentcs.components.kernel.services.PeripheralJobService;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.peripherals.PeripheralJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the standard implementation of the {@link RemotePeripheralJobService} interface.
 * <p>
 * Upon creation, an instance of this class registers itself with the RMI registry by the name
 * {@link RegistrationName#REMOTE_PERIPHERAL_JOB_SERVICE}.
 * </p>
 */
public class StandardRemotePeripheralJobService
    extends
      StandardRemoteTCSObjectService
    implements
      RemotePeripheralJobService {

  /**
   * This class's logger.
   */
  private static final Logger LOG
      = LoggerFactory.getLogger(StandardRemotePeripheralJobService.class);
  /**
   * The peripheral job service to invoke methods on.
   */
  private final PeripheralJobService peripheralJobService;
  /**
   * The user manager.
   */
  private final UserManager userManager;
  /**
   * Provides configuration data.
   */
  private final RmiKernelInterfaceConfiguration configuration;
  /**
   * Provides socket factories used for RMI.
   */
  private final SocketFactoryProvider socketFactoryProvider;
  /**
   * Provides the registry with which this remote service registers.
   */
  private final RegistryProvider registryProvider;
  /**
   * Executes tasks modifying kernel data.
   */
  private final ExecutorService kernelExecutor;
  /**
   * The registry with which this remote service registers.
   */
  private Registry rmiRegistry;
  /**
   * Whether this remote service is initialized or not.
   */
  private boolean initialized;

  /**
   * Creates a new instance.
   *
   * @param peripheralJobService The peripheral job service.
   * @param userManager The user manager.
   * @param configuration This class' configuration.
   * @param socketFactoryProvider The socket factory provider used for RMI.
   * @param registryProvider The provider for the registry with which this remote service registers.
   * @param kernelExecutor Executes tasks modifying kernel data.
   */
  @Inject
  public StandardRemotePeripheralJobService(
      PeripheralJobService peripheralJobService,
      UserManager userManager,
      RmiKernelInterfaceConfiguration configuration,
      SocketFactoryProvider socketFactoryProvider,
      RegistryProvider registryProvider,
      @KernelExecutor
      ExecutorService kernelExecutor
  ) {
    super(peripheralJobService, userManager, kernelExecutor);
    this.peripheralJobService = requireNonNull(peripheralJobService, "transportOrderService");
    this.userManager = requireNonNull(userManager, "userManager");
    this.configuration = requireNonNull(configuration, "configuration");
    this.socketFactoryProvider = requireNonNull(socketFactoryProvider, "socketFactoryProvider");
    this.registryProvider = requireNonNull(registryProvider, "registryProvider");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    rmiRegistry = registryProvider.get();

    // Export this instance via RMI.
    try {
      LOG.debug("Exporting proxy...");
      UnicastRemoteObject.exportObject(
          this,
          configuration.remotePeripheralJobServicePort(),
          socketFactoryProvider.getClientSocketFactory(),
          socketFactoryProvider.getServerSocketFactory()
      );
      LOG.debug("Binding instance with RMI registry...");
      rmiRegistry.rebind(RegistrationName.REMOTE_PERIPHERAL_JOB_SERVICE, this);
    }
    catch (RemoteException exc) {
      LOG.error("Could not export or bind with RMI registry", exc);
      return;
    }

    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }

    try {
      LOG.debug("Unbinding from RMI registry...");
      rmiRegistry.unbind(RegistrationName.REMOTE_PERIPHERAL_JOB_SERVICE);
      LOG.debug("Unexporting RMI interface...");
      UnicastRemoteObject.unexportObject(this, true);
    }
    catch (RemoteException | NotBoundException exc) {
      LOG.warn("Exception shutting down RMI interface", exc);
    }

    initialized = false;
  }

  @Override
  public PeripheralJob createPeripheralJob(ClientID clientId, PeripheralJobCreationTO to)
      throws RemoteException {
    userManager.verifyCredentials(clientId, UserPermission.MODIFY_PERIPHERAL_JOBS);

    try {
      return kernelExecutor.submit(() -> peripheralJobService.createPeripheralJob(to)).get();
    }
    catch (InterruptedException | ExecutionException exc) {
      throw findSuitableExceptionFor(exc);
    }
  }
}
