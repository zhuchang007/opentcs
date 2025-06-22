/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.exchange.commands;

import uwant.common.telegrams.Request;
import static java.util.Objects.requireNonNull;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import uwant.vehicle.UwtCommAdapter;

/**
 * A command for sending a telegram to the actual vehicle.
 *
 * @author zhuchang
 */
@SuppressWarnings("deprecation")
public class SendRequestCommand implements AdapterCommand {

  /** The request to send. */
  private final Request request;

  private final int routeId;

  /**
   * Creates a new instance.
   *
   * @param request The request to send.
   * @param routeId
   */
  public SendRequestCommand(Request request, int routeId) {
    this.request = requireNonNull(request, "request");
    this.routeId = routeId;
  }

  @Override
  public void execute(VehicleCommAdapter adapter) {
    if (!(adapter instanceof UwtCommAdapter)) {
      return;
    }

    UwtCommAdapter uwtCommAdapter = (UwtCommAdapter) adapter;
    uwtCommAdapter.getRequestResponseMatcher().enqueueRequest(request, routeId);
  }
}
