// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.extra;


import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import org.opentcs.access.to.model.PlantModelCreationTO;
import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.components.kernel.services.PlantModelService;
import org.opentcs.util.persistence.ModelParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Extra
    implements KernelExtension {

  private static final Logger LOG = LoggerFactory.getLogger(Extra.class);

  private final ModelParser modelParser;
  private final PlantModelService plantModelService;

  @Inject
  public Extra(ModelParser modelParser, PlantModelService plantModelService) {
    this.modelParser = modelParser;
    this.plantModelService = plantModelService;
  }

  @Override
  public void initialize() {
    LOG.info("Initializing Extra Kernel Extension");
    try {
//      String path = System.getProperty("user.dir");
//      LOG.info("Using path: {}", path);
      PlantModelCreationTO plantModelCreationTO = modelParser.readModel(
          new File("./Demo-01.xml"));
      plantModelService.createPlantModel(plantModelCreationTO);
    }
    catch (IOException exc) {
      LOG.error("Exception parsing input", exc);
      throw new IllegalStateException("Exception parsing input", exc);
    }
  }

  @Override
  public boolean isInitialized() {
    return false;
  }

  @Override
  public void terminate() {

  }
}
