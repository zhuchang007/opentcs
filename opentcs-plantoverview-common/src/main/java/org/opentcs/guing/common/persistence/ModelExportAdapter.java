// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.guing.common.persistence;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;
import org.opentcs.access.to.model.PlantModelCreationTO;
import org.opentcs.guing.base.components.properties.type.KeyValueProperty;
import org.opentcs.guing.base.components.properties.type.KeyValueSetProperty;
import org.opentcs.guing.base.model.ModelComponent;
import org.opentcs.guing.base.model.elements.BlockModel;
import org.opentcs.guing.base.model.elements.LinkModel;
import org.opentcs.guing.base.model.elements.LocationModel;
import org.opentcs.guing.base.model.elements.LocationTypeModel;
import org.opentcs.guing.base.model.elements.PathModel;
import org.opentcs.guing.base.model.elements.PointModel;
import org.opentcs.guing.base.model.elements.VehicleModel;
import org.opentcs.guing.common.exchange.adapter.ProcessAdapter;
import org.opentcs.guing.common.exchange.adapter.ProcessAdapterUtil;
import org.opentcs.guing.common.model.SystemModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts {@link SystemModel} instances to plant model data.
 */
public class ModelExportAdapter {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ModelExportAdapter.class);
  /**
   * Creates process adapter instances.
   */
  private final ProcessAdapterUtil processAdapterUtil;

  /**
   * Creates a new instance.
   *
   * @param processAdapterUtil Creates process adapter instances.
   */
  @Inject
  public ModelExportAdapter(ProcessAdapterUtil processAdapterUtil) {
    this.processAdapterUtil = requireNonNull(processAdapterUtil, "processAdapterUtil");
  }

  /**
   * Converts the given {@link SystemModel} instance to plant model data.
   *
   * @param systemModel The model to be converted.
   * @return The converted model data.
   * @throws IllegalArgumentException If the given plant model was inconsistent in some way.
   */
  @Nonnull
  public PlantModelCreationTO convert(SystemModel systemModel)
      throws IllegalArgumentException {
    requireNonNull(systemModel, "model");

    PlantModelCreationTO plantModel = new PlantModelCreationTO(systemModel.getName())
        .withProperties(convertProperties(systemModel.getPropertyMiscellaneous()));

    long timeBefore = System.currentTimeMillis();
    plantModel = persist(systemModel.getLayoutModel(), systemModel, plantModel);
    LOG.debug(
        "Converting the LayoutModel took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    timeBefore = System.currentTimeMillis();
    for (PointModel model : systemModel.getPointModels()) {
      plantModel = persist(model, systemModel, plantModel);
    }
    LOG.debug(
        "Converting PointModels took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    timeBefore = System.currentTimeMillis();
    for (PathModel model : systemModel.getPathModels()) {
      plantModel = persist(model, systemModel, plantModel);
    }
    LOG.debug(
        "Converting PathModels took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    timeBefore = System.currentTimeMillis();
    for (LocationTypeModel model : systemModel.getLocationTypeModels()) {
      plantModel = persist(model, systemModel, plantModel);
    }
    LOG.debug(
        "Converting LocationTypeModels took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    timeBefore = System.currentTimeMillis();
    for (LocationModel model : systemModel.getLocationModels()) {
      plantModel = persist(model, systemModel, plantModel);
    }
    LOG.debug(
        "Converting LocationModels took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    timeBefore = System.currentTimeMillis();
    for (LinkModel model : systemModel.getLinkModels()) {
      plantModel = persist(model, systemModel, plantModel);
    }
    LOG.debug(
        "Converting LinkModels took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    timeBefore = System.currentTimeMillis();
    for (BlockModel model : systemModel.getBlockModels()) {
      plantModel = persist(model, systemModel, plantModel);
    }
    LOG.debug(
        "Converting BlockModels took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    timeBefore = System.currentTimeMillis();
    for (VehicleModel model : systemModel.getVehicleModels()) {
      plantModel = persist(model, systemModel, plantModel);
    }
    LOG.debug(
        "Converting VehicleModels took {} milliseconds.",
        System.currentTimeMillis() - timeBefore
    );

    return plantModel;
  }

  private Map<String, String> convertProperties(KeyValueSetProperty kvsp) {
    return kvsp.getItems().stream()
        .collect(Collectors.toMap(KeyValueProperty::getKey, KeyValueProperty::getValue));
  }

  private PlantModelCreationTO persist(
      ModelComponent component,
      SystemModel systemModel,
      PlantModelCreationTO plantModel
  ) {
    ProcessAdapter adapter = processAdapterUtil.processAdapterFor(component);
    return adapter.storeToPlantModel(component, systemModel, plantModel);
  }
}
