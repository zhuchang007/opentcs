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
import io.netty.handler.codec.ByteToMessageDecoder;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import uwant.vehicle.telegrams.StateResponse;

import java.util.List;

public class TelegramDecoderClient extends ByteToMessageDecoder {
  private final ConnectionEventListener<byte[]> responseHandler;

  public TelegramDecoderClient(ConnectionEventListener<byte[]> responseHandler) {
    this.responseHandler = responseHandler;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    byte[] telegramData;
    telegramData = new byte[StateResponse.TELEGRAM_LENGTH];
    in.readBytes(telegramData);
    responseHandler.onIncomingTelegram(telegramData);
  }
}
