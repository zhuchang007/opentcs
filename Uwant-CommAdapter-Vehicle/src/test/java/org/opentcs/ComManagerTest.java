/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.opentcs.configuration.ConfigurationBindingProvider;
import org.opentcs.configuration.gestalt.GestaltConfigurationBindingProvider;

public class ComManagerTest {
  @Test
  public void commManagerTest()
      throws InterruptedException {
    //    ComCommAdapterConfiguration configuration =
    //        configurationBindingProvider()
    //            .get(ComCommAdapterConfiguration.PREFIX, ComCommAdapterConfiguration.class);
    //    ComChannelManager vehicleChannelManager = new ComChannelManager(configuration);
    //    vehicleChannelManager.initialize();
    //    vehicleChannelManager.connect();
    //    Thread.sleep(10000);

    Bootstrap bootstrap = new Bootstrap(); // 5
    bootstrap
        .channel(NioSocketChannel.class) // 6
        .handler(
            new SimpleChannelInboundHandler<ByteBuf>() { // 7
              @Override
              protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in)
                  throws Exception {
                System.out.println("Reveived data");
              }
            }
        );
    bootstrap.group(new NioEventLoopGroup()); // 8 connectFuture =
    bootstrap.connect(new InetSocketAddress("localhost", 8088));
  }

  private static ConfigurationBindingProvider configurationBindingProvider() {
    Path path = Paths.get(
        System.getProperty("opentcs.base", "."), "config", "opentcs-kernel.properties"
    )
        .toAbsolutePath();
    return new GestaltConfigurationBindingProvider(path);
  }
}
