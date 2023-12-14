/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.netty;

import org.opentcs.components.Lifecycle;
import uwant.common.netty.tcp.ConnectionEventListener;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.Response;

public interface ChannelManager extends Lifecycle {

  void send(int agvId, Request telegram);

  void addListener(int agvId, ConnectionEventListener<Response> listener);

  void removeListener(int agvId, ConnectionEventListener<Response> listener);

  int getListenerCount();
}
