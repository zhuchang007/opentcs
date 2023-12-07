/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.comm;

import org.opentcs.components.Lifecycle;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.Response;

public interface ChannelManager extends Lifecycle {

  public void send(int agvId, Request telegram);

  public void addListener(int agvId, ConnectionEventListener<Response> listener);

  public void removeListener(int agvId, ConnectionEventListener<Response> listener);

  public int getListenerCount();
}
