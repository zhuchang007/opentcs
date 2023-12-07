/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.simulation;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TelegramEncoderClient extends MessageToByteEncoder<byte[]> {
  @Override
  protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
    out.writeBytes(msg);
  }
}
