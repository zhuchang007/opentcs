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

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import uwant.common.telegrams.Response;
import uwant.common.vehicle.telegrams.StateResponse;

/** @author zhuchang */
public class UwtProcessModel
    extends
      VehicleProcessModel {
  /** The current/most recent state reported by the vehicle. */
  private StateResponse currentState;

  private Response response;

  public UwtProcessModel(Vehicle attachedVehicle) {
    super(attachedVehicle);
    // Initialize the state fields
    final byte[] dummyData = new byte[StateResponse.TELEGRAM_LENGTH];
    currentState = new StateResponse(dummyData);
  }

  @Nonnull
  public StateResponse getCurrentState() {
    return currentState;
  }

  public void setCurrentState(@Nonnull
  StateResponse currentState) {
    StateResponse oldValue = this.currentState;
    this.currentState = requireNonNull(currentState, "currentState");

    getPropertyChangeSupport()
        .firePropertyChange(Attribute.CURRENT_STATE.name(), oldValue, currentState);
  }

  @Nonnull
  public synchronized Response getResponse() {
    return response;
  }

  public synchronized void setResponse(@Nonnull
  Response response) {
    Response oldValue = this.response;
    this.response = requireNonNull(response, "response");

    getPropertyChangeSupport().firePropertyChange(Attribute.RESPONSE.name(), oldValue, response);
  }

  /** Model attributes specific to this implementation. */
  public enum Attribute {
    CURRENT_STATE,
    RESPONSE,
  }
}
