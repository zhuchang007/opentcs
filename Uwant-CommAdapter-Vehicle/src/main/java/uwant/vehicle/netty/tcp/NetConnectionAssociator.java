/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.netty.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.netty.tcp.ClientEntry;
import uwant.common.netty.tcp.ConnectionAssociatedEvent;
import uwant.common.telegrams.Response;

import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Associates incoming messages with clients interested in these. Here it is always only one client.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class NetConnectionAssociator extends ChannelInboundHandlerAdapter {

  /** This class's Logger. */
  private static final Logger LOG = LoggerFactory.getLogger(NetConnectionAssociator.class);
  /** A pool of clients that may connect to a TcpServerChannelManager. */
  private final Map<Integer, ClientEntry<Response>> clientEntries;
  /** The associated client. */
  private ClientEntry<Response> client;
  /** TCP客户端标识 * */
  private Integer objectClient;

  public NetConnectionAssociator(Map<Integer, ClientEntry<Response>> clientEntries) {
    this.clientEntries = requireNonNull(clientEntries, "clientEntries");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    handleMessage(ctx, msg);
  }

  private void handleMessage(ChannelHandlerContext ctx, Object msg) {
    LOG.debug("Handling incoming message: {}", msg);
    if (!(msg instanceof Response)) {
      LOG.debug("Not an instance of byte[] - ignoring.");
      return;
    }

    if (objectClient == null) {
      LOG.debug("Received the first data from the client.");
      // client = clientEntries.get(objectClient);
      // 开始一个新的连接，分配一个新的Client标识
      // objectClient = new Object();

      objectClient = ((Response) msg).getAgvId();

      // Notify any listeners that the channel has been associated to this nickname, implicitly
      // notifying the comm adapter that a connection has been established.
      // 把objectClient传递给ConnectionEventListener的onConnect()函数
      ctx.fireUserEventTriggered(new ConnectionAssociatedEvent(objectClient));
    } else {
      client = clientEntries.get(objectClient);
    }

    if (Objects.nonNull(client)) {
      client.setChannel(ctx.channel());
      Response incomingData = (Response) msg;
      client.getConnectionEventListener().onIncomingTelegram(incomingData);
    }
  }
}
