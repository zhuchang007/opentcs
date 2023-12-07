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
package uwant.vehicle.comm;

import com.google.inject.Inject;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.jsc.JSerialCommChannel;
import io.netty.channel.jsc.JSerialCommDeviceAddress;
import io.netty.channel.oio.OioEventLoopGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import javax.annotation.Nonnull;

import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import static org.opentcs.util.Assertions.checkState;
import org.slf4j.LoggerFactory;
import uwant.vehicle.UwtCommAdapterConfiguration;

/** @author zhuchang */
public class ComChannelManager implements ChannelManager {

  /** This class's Logger. */
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ComChannelManager.class);

  private UwtCommAdapterConfiguration comCommAdapterConfiguration;
  /** */
  private static String port = "COM1";
  /** Bootstraps the channel. */
  private Bootstrap bootstrap;
  /** */
  private EventLoopGroup workerGroup;
  /** */
  private ChannelFuture channelFuture;

  // private final Supplier<List<ChannelHandler>> channelSupplier;

  private final Set<ConnectionEventListener<Response>> listeners =
      Collections.synchronizedSet(new HashSet<>());

  /** Whether this component is initialized or not. */
  private boolean initialized;

  @Inject
  public ComChannelManager(
      //    @Nonnull ConnectionEventListener<I> connEventListener,
      /*Supplier<List<ChannelHandler>> channelSupplier*/
      @Nonnull UwtCommAdapterConfiguration comCommAdapterConfiguration) {
    // this.channelSupplier = requireNonNull(channelSupplier,"channelSupplier");
    this.comCommAdapterConfiguration =
        requireNonNull(comCommAdapterConfiguration, "comCommAdapterConfiguration");
    port = comCommAdapterConfiguration.comName();
  }

  @Override
  public void initialize() {
    if (initialized) {
      return;
    }
    workerGroup = new OioEventLoopGroup();
    bootstrap = new Bootstrap();
    bootstrap
        .group(workerGroup)
        .channel(JSerialCommChannel.class)
        .handler(
            new ChannelInitializer<JSerialCommChannel>() {
              @Override
              public void initChannel(JSerialCommChannel ch) throws Exception {
                ch.config()
                    .setBaudrate(comCommAdapterConfiguration.comBaudRate()); // 设置波特率为configuration默认值
              }
            });
    initialized = true;
    connect();
  }

  /** Initiates a connection (attempt) to the remote host and port. */
  public void connect() {
    checkState(isInitialized(), "Not initialized");
    if (isConnected()) {
      LOG.debug("Already connected, doing nothing.");
      return;
    }
    channelFuture = bootstrap.connect(new JSerialCommDeviceAddress(port));
  }

  public void disconnect() {
    if (!isConnected()) {
      return;
    }

    if (channelFuture != null) {
      channelFuture.channel().disconnect();
      channelFuture = null;
    }
  }

  @Override
  public void send(int agvId, Request telegram) {
    if (!isConnected()) {
      return;
    }
    channelFuture.channel().writeAndFlush(telegram);
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  public boolean isConnected() {
    return channelFuture != null && channelFuture.channel().isActive();
  }

  @Override
  public void terminate() {
    if (!initialized) {
      return;
    }

    disconnect();
    workerGroup.shutdownGracefully();
    workerGroup = null;
    bootstrap = null;
    listeners.clear();
    initialized = false;
  }

  @Override
  public void addListener(int agvId, ConnectionEventListener<Response> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(int agvId, ConnectionEventListener<Response> listener) {
    listeners.remove(listener);
  }

  @Override
  public int getListenerCount() {
    return listeners.size();
  }

  private List<ChannelHandler> getChannelHandlers() {
    return Arrays.asList(new ComTelegramDecoder(listeners), new ComTelegramEncoder());
  }
}
