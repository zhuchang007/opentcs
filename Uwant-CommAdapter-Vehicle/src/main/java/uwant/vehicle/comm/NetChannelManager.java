/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.comm;

import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;
import org.opentcs.contrib.tcp.netty.ClientEntry;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import org.opentcs.contrib.tcp.netty.TcpServerChannelManager;
import org.slf4j.LoggerFactory;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import uwant.vehicle.UwtCommAdapterConfiguration;

public class NetChannelManager implements ChannelManager {
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NetChannelManager.class);

  private final UwtCommAdapterConfiguration commAdapterConfig;
  private TcpServerChannelManager<Response, Request> vehicleServer;
  private final Map<Integer, ClientEntry<Response>> clients = new HashMap<>();
  private boolean initialized;

  @Inject
  public NetChannelManager(@Nonnull UwtCommAdapterConfiguration comCommAdapterConfiguration){
    this.commAdapterConfig =
        requireNonNull(comCommAdapterConfiguration, "comCommAdapterConfiguration");
  }

  @Override
  public void send(int agvId, Request telegram) {
    vehicleServer.send(agvId, telegram);
  }

  @Override
  public void addListener(int agvId, ConnectionEventListener<Response> listener) {
    vehicleServer.register(agvId, listener, true);
  }

  @Override
  public void removeListener(int agvId, ConnectionEventListener<Response> listener) {
    vehicleServer.unregister(agvId);
  }

  @Override
  public int getListenerCount() {
    return clients.size();
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    vehicleServer =
        new TcpServerChannelManager<>(commAdapterConfig.tcpPort(),
            clients, this::getChannelHandlers, 5000, true);

    vehicleServer.initialize();

    initialized = true;
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public void terminate() {
    if (!isInitialized()) {
      return;
    }
    vehicleServer.terminate();
    initialized = false;
  }

  private List<ChannelHandler> getChannelHandlers() {
    return Arrays.asList(
        new NetTelegramDecodeToFrame(),
        new NetTelegramDecoder(),
        new NetTelegramEncoder(),
        new NetConnectionAssociator(clients));
  }
}
