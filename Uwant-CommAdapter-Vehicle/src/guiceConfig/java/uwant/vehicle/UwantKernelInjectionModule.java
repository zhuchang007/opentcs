/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uwant.vehicle;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.netty.ChannelManager;
import uwant.vehicle.netty.com.ComChannelManager;
import uwant.vehicle.netty.tcp.NetChannelManager;

/**
 * @author zhuchang
 */
public class UwantKernelInjectionModule
    extends KernelInjectionModule {

  private static final Logger LOG = LoggerFactory.getLogger(UwantKernelInjectionModule.class);

  public UwantKernelInjectionModule() {
  }

  @Override
  protected void configure() {

    UwtCommAdapterConfiguration config =
        getConfigBindingProvider()
            .get(UwtCommAdapterConfiguration.PREFIX, UwtCommAdapterConfiguration.class);

    bind(UwtCommAdapterConfiguration.class).toInstance(config);

    String commMode = String.valueOf(config.commMode());
    switch (commMode) {
      case "COM":
        bind(ChannelManager.class).to(ComChannelManager.class);
        LOG.info("Uwant communication adapter use serial port.");
        break;
      case "TCP":
        bind(ChannelManager.class).to(NetChannelManager.class);
        LOG.info("Uwant communication adapter use tcp.");
        break;
      default:
        LOG.info("Uwant communication adapter mode error,please us 'COM' or 'TCP'.");
        break;
    }

    install(new FactoryModuleBuilder().build(UwtCommAdapterComponentsFactory.class));
    vehicleCommAdaptersBinder().addBinding().to(UwtCommAdapterFactory.class);
  }
}
