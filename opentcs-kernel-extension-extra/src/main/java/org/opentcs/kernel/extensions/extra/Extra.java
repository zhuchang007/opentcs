// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.extra;

import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import org.opentcs.access.to.order.DestinationCreationTO;
import org.opentcs.access.to.order.OrderSequenceCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.components.kernel.services.DispatcherService;
import org.opentcs.components.kernel.services.PlantModelService;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.drivers.vehicle.VehicleCommAdapterMessage;
import org.opentcs.virtualvehicle.LoopbackCommAdapterMessages;
import org.opentcs.virtualvehicle.LoopbackCommunicationAdapterDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Extra
    implements
      KernelExtension {

  private static final Logger LOG = LoggerFactory.getLogger(Extra.class);


  private final VehicleService vehicleService;
  private final TCSObjectService objectService;
  private final TransportOrderService orderService;
  private final DispatcherService dispatcherService;
  private final PlantModelService plantModelService;

  @Inject
  public Extra(
      VehicleService vehicleService,
      TCSObjectService objectService,
      TransportOrderService orderService,
      DispatcherService dispatcherService,
      PlantModelService plantModelService
  ) {
    this.vehicleService = vehicleService;
    this.objectService = objectService;
    this.orderService = orderService;
    this.dispatcherService = dispatcherService;
    this.plantModelService = plantModelService;
  }

  @Override
  public void initialize() {
    LOG.info("Initializing Extra Kernel Extension");

//    initPoint("Vehicle-01", "Point-0002");
//    initPoint("Vehicle-02", "Point-0004");
//    initPoint("Vehicle-03", "Point-0006");
//    initPoint("Vehicle-04", "Point-0010");
//
//    //Set<String> locations = objectService.fetch(Location.class).stream().map(Location::getName);
//
//    addAnOder("Vehicle-01", "Goods in south 02");
//    addAnOder("Vehicle-02", "Goods out 02");
//    addAnOder("Vehicle-03", "Working station 01");
//    addAnOder("Vehicle-04", "Storage 02");

    initPoint("Vehicle-01", "Point-0010");
    initPoint("Vehicle-02", "Point-0011");
   // addAnOder("Vehicle-01", "Goods out 01");
    addAnOderSequence();
    Path path = objectService.fetch(Path.class, "Point-0013 --- Point-0015").orElseThrow();
    plantModelService.updatePathLock(path.getReference(), true);

    dispatcherService.dispatch();
  }

  @Override
  public boolean isInitialized() {
    return false;
  }

  @Override
  public void terminate() {

  }

  private void initPoint(String vehicleName, String initPointName) {
    Vehicle vehicle = objectService.fetch(Vehicle.class, vehicleName).orElseThrow();

    vehicleService.updateVehicleIntegrationLevel(
        vehicle.getReference(),
        Vehicle.IntegrationLevel.TO_BE_UTILIZED
    );
    try {
      vehicleService.attachCommAdapter(
          vehicle.getReference(), new LoopbackCommunicationAdapterDescription()
      );
    }
    catch (ObjectUnknownException e) {
      LOG.warn(
          "Error attaching adapter Loopback to vehicle {}",
          vehicle.getName(),
          e
      );
    }

    vehicleService.enableCommAdapter(vehicle.getReference());

    vehicleService.sendCommAdapterMessage(
        vehicle.getReference(), new VehicleCommAdapterMessage(
            LoopbackCommAdapterMessages.INIT_POSITION, Map.of(
            LoopbackCommAdapterMessages.INIT_POSITION_PARAM_POSITION, initPointName
        )
        )
    );
  }

  private void addAnOder(String vehicleName, String locationName) {
    orderService.createTransportOrder(
        new TransportOrderCreationTO(
            "order-" + vehicleName, List.of(new DestinationCreationTO(locationName, "NOP"))
        )
    );
  }

  private void addAnOderSequence() {
    OrderSequenceCreationTO sequenceTO = new OrderSequenceCreationTO("MyOrderSequence");
    sequenceTO = sequenceTO.withIncompleteName(true).withFailureFatal(true).withIntendedVehicleName(
        "Vehicle-02"
    );

    OrderSequence orderSequence = orderService.createOrderSequence(sequenceTO);
    TransportOrderCreationTO orderTO1
        = new TransportOrderCreationTO(
            "MyOrder1",
            List.of(
                new DestinationCreationTO("Goods in south 01", "NOP")
            )
        )
            .withIncompleteName(true)
            .withWrappingSequence(orderSequence.getName())
            .withIntendedVehicleName("Vehicle-02");

    TransportOrderCreationTO orderTO2
        = new TransportOrderCreationTO(
            "MyOrder2",
            List.of(
                new DestinationCreationTO("Goods out 02", "NOP")
            )
        )
            .withIncompleteName(true)
            .withWrappingSequence(orderSequence.getName())
            .withIntendedVehicleName("Vehicle-02");

    orderService.createTransportOrder(orderTO1);
    orderService.createTransportOrder(orderTO2);
    orderService.createTransportOrder(orderTO1);
    orderService.createTransportOrder(orderTO2);


    orderService.markOrderSequenceComplete(
        orderSequence.getReference()
    );

  }
}
