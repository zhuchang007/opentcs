/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.netty.com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.telegrams.HexConvert;
import uwant.common.telegrams.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import uwant.common.netty.tcp.ConnectionEventListener;
import uwant.common.telegrams.Telegram;
import uwant.common.vehicle.telegrams.ActionResponse;
import uwant.common.vehicle.telegrams.NodeActionSetResponse;
import uwant.common.vehicle.telegrams.StateResponse;

/**
 * Decode the incoming data.
 *
 * @author zc
 */
public class ComTelegramDecoder extends ByteToMessageDecoder {

  /** This class's Logger. */
  private static final Logger LOG = LoggerFactory.getLogger(ComTelegramDecoder.class);

  private final Set<ConnectionEventListener<Response>> responseHandlers;

  public ComTelegramDecoder(Set<ConnectionEventListener<Response>> responseHandlers) {
    this.responseHandlers = requireNonNull(responseHandlers, "responseHandler");
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    LOG.debug("Decode incoming bytes: {}", in);
    if (in.readableBytes() >= Telegram.TELEGRAM_LENGTH) {
      byte[] telegramData;
      in.markReaderIndex();
      telegramData = new byte[Telegram.TELEGRAM_LENGTH];
      in.readBytes(telegramData);
      if (((telegramData[2] ^ telegramData[Telegram.TELEGRAM_LENGTH - 1]) & 0xff) == 0xff) {
        out.add(telegramData);
        LOG.debug(HexConvert.bytesToHex(telegramData));
        if (telegramData[3] == StateResponse.TYPE) {
          // 每个Vehicle的CommAdapter都处理
          responseHandlers.forEach(
              (responseHandler) -> {
                responseHandler.onIncomingTelegram(new StateResponse(telegramData));
              });
        } else if (telegramData[3] == ActionResponse.TYPE) {
          responseHandlers.forEach(
              (responseHandler) -> {
                responseHandler.onIncomingTelegram(new ActionResponse(telegramData));
              });
        } else if (telegramData[3] == NodeActionSetResponse.TYPE) {
          responseHandlers.forEach(
              (responseHandler) -> {
                responseHandler.onIncomingTelegram(new NodeActionSetResponse(telegramData));
              });
        }
      } else {
        in.resetReaderIndex();
        int readIndex = in.readerIndex();
        in.readerIndex(++readIndex);
      }
    }
  }
}
