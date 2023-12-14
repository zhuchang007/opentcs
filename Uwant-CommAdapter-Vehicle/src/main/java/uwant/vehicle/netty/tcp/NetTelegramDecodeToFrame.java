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
package uwant.vehicle.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import uwant.common.vehicle.telegrams.StateResponse;

/** @author SXXX */
public class NetTelegramDecodeToFrame extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    int actualLength = StateResponse.TELEGRAM_LENGTH - 2;
    if (in.readableBytes() >= actualLength) {
      byte[] telegramData;
      in.markReaderIndex();

      telegramData = new byte[actualLength];
      in.readBytes(telegramData);
      if (((telegramData[0] ^ telegramData[actualLength - 1]) & 0xff) == 0xff) {
        out.add(telegramData);
      } else {
        in.resetReaderIndex();
        int readIndex = in.readerIndex();
        in.readerIndex(++readIndex);
      }
    }
  }
}
