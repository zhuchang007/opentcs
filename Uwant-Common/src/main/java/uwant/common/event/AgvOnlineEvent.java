/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.event;

import java.io.Serializable;

/** @author zhuchang */
public class AgvOnlineEvent implements Serializable {

  private static final long serialVersionUID = 5410298718983241569L;
  
  private final String vehicleName;

  public AgvOnlineEvent(String vehicleName) {
    this.vehicleName = vehicleName;
  }
  
  public String getVehicleName() {
      return vehicleName;
  }
  
}
