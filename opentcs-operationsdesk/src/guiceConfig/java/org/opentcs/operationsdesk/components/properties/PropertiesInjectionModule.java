// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.operationsdesk.components.properties;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import jakarta.inject.Singleton;
import org.opentcs.customizations.plantoverview.PlantOverviewInjectionModule;
import org.opentcs.guing.base.components.properties.type.AbstractComplexProperty;
import org.opentcs.guing.base.components.properties.type.EnergyLevelThresholdSetProperty;
import org.opentcs.guing.base.components.properties.type.KeyValueProperty;
import org.opentcs.guing.base.components.properties.type.KeyValueSetProperty;
import org.opentcs.guing.base.components.properties.type.LinkActionsProperty;
import org.opentcs.guing.base.components.properties.type.LocationTypeActionsProperty;
import org.opentcs.guing.base.components.properties.type.OrderTypesProperty;
import org.opentcs.guing.base.components.properties.type.ResourceProperty;
import org.opentcs.guing.base.components.properties.type.SymbolProperty;
import org.opentcs.guing.common.components.dialogs.DetailsDialogContent;
import org.opentcs.guing.common.components.properties.PropertiesComponentsFactory;
import org.opentcs.guing.common.components.properties.SelectionPropertiesComponent;
import org.opentcs.guing.common.components.properties.panel.EnergyLevelThresholdSetPropertyEditorPanel;
import org.opentcs.guing.common.components.properties.panel.KeyValuePropertyEditorPanel;
import org.opentcs.guing.common.components.properties.panel.KeyValueSetPropertyViewerEditorPanel;
import org.opentcs.guing.common.components.properties.panel.LinkActionsEditorPanel;
import org.opentcs.guing.common.components.properties.panel.LocationTypeActionsEditorPanel;
import org.opentcs.guing.common.components.properties.panel.OrderTypesPropertyEditorPanel;
import org.opentcs.guing.common.components.properties.panel.PropertiesPanelFactory;
import org.opentcs.guing.common.components.properties.panel.ResourcePropertyViewerEditorPanel;
import org.opentcs.guing.common.components.properties.panel.SymbolPropertyEditorPanel;
import org.opentcs.guing.common.components.properties.table.CellEditorFactory;

/**
 * A Guice module for this package.
 */
public class PropertiesInjectionModule
    extends
      PlantOverviewInjectionModule {

  /**
   * Creates a new instance.
   */
  public PropertiesInjectionModule() {
  }

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(PropertiesPanelFactory.class));
    install(new FactoryModuleBuilder().build(CellEditorFactory.class));
    install(new FactoryModuleBuilder().build(PropertiesComponentsFactory.class));

    MapBinder<Class<? extends AbstractComplexProperty>, DetailsDialogContent> dialogContentMapBinder
        = MapBinder.newMapBinder(
            binder(),
            new TypeLiteral<Class<? extends AbstractComplexProperty>>() {
            },
            new TypeLiteral<DetailsDialogContent>() {
            }
        );
    dialogContentMapBinder
        .addBinding(KeyValueProperty.class)
        .to(KeyValuePropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(KeyValueSetProperty.class)
        .to(KeyValueSetPropertyViewerEditorPanel.class);
    dialogContentMapBinder
        .addBinding(LocationTypeActionsProperty.class)
        .to(LocationTypeActionsEditorPanel.class);
    dialogContentMapBinder
        .addBinding(LinkActionsProperty.class)
        .to(LinkActionsEditorPanel.class);
    dialogContentMapBinder
        .addBinding(SymbolProperty.class)
        .to(SymbolPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(OrderTypesProperty.class)
        .to(OrderTypesPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(EnergyLevelThresholdSetProperty.class)
        .to(EnergyLevelThresholdSetPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(ResourceProperty.class)
        .to(ResourcePropertyViewerEditorPanel.class);

    bind(SelectionPropertiesComponent.class)
        .in(Singleton.class);

  }

}
