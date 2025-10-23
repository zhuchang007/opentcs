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
package uwant.vehicle.exchange;

import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import uwant.common.telegrams.Response;
import uwant.common.vehicle.telegrams.StateResponse;

/** @author zhuchang */
public class UwtProcessModelTO
    extends
      VehicleProcessModelTO {
  /** The vehicle reference. */
  private TCSObjectReference<Vehicle> vehicleRef;

  /** The current/most recent state reported by the vehicle. */
  private StateResponse currentState;
  /** The previous state reported by the vehicle. */

  private Response response;

  private long recvCount;

  public UwtProcessModelTO() {
  }

  /**
   * Returns the vehicle reference.
   *
   * @return The vehicle reference
   */
  public TCSObjectReference<Vehicle> getVehicleRef() {
    return vehicleRef;
  }

  /**
   * Returns the current/most recent state reported by the vehicle.
   *
   * @return The current/most recent state reported by the vehicle
   */
  public StateResponse getCurrentState() {
    return currentState;
  }

  /**
   * Sets the current/most recent state reported by the vehicle.
   *
   * @param currentState The current/most recent state reported by the vehicle.
   * @return This
   */
  public UwtProcessModelTO setCurrentState(StateResponse currentState) {
    this.currentState = currentState;
    return this;
  }

  /**
   * Returns the previous state reported by the vehicle.
   *
   * @return The previous state reported by the vehicle
   */
  public Response getResponse() {
    return response;
  }

  /**
   * Sets the previous state reported by the vehicle.
   *
   * @param response
   * @return This
   */
  public UwtProcessModelTO setResponse(Response response) {
    this.response = response;
    return this;
  }

}
