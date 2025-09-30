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
import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;
import uwant.vehicle.exchange.AdapterPanelComponentsFactory;
import uwant.vehicle.exchange.UwtCommAdapterPanelFactory;

/** @author zhuchang */
public class UwantControlCenterInjectionModule
    extends
      ControlCenterInjectionModule {
  public UwantControlCenterInjectionModule() {
  }

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(UwtCommAdapterPanelFactory.class);
  }
}
