/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.comm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.telegrams.Request;
import uwant.vehicle.telegrams.StateResponse;

/**
 * Encodes outgoing data.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class NetTelegramEncoder extends MessageToByteEncoder<Request> {

  /** This class's Logger. */
  private static final Logger LOG = LoggerFactory.getLogger(NetTelegramEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out) throws Exception {
    LOG.debug("Encoding bytes: {}", msg);
    byte[] byteMsg = new byte[StateResponse.TELEGRAM_LENGTH - 2];
    System.arraycopy(msg.getRawContent(), 2, byteMsg, 0, StateResponse.TELEGRAM_LENGTH - 2);
    out.writeBytes(byteMsg);
  }
}
