/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.comm;

import uwant.common.telegrams.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import uwant.vehicle.telegrams.ActionResponse;
import uwant.vehicle.telegrams.NodeActionSetResponse;
import uwant.vehicle.telegrams.StateResponse;
// import org.apache.commons.codec.binary.Hex;

/**
 * Checks if the incoming data was sent by the {@link APTVCommAdapter}.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ComTelegramDecoder extends ByteToMessageDecoder {

  /** This class's Logger. */
  // private static final Logger LOG = LoggerFactory.getLogger(TelegramDecoder.class);

  private final Set<ConnectionEventListener<Response>> responseHandlers;

  public ComTelegramDecoder(Set<ConnectionEventListener<Response>> responseHandlers) {
    this.responseHandlers = requireNonNull(responseHandlers, "responseHandler");
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    // LOG.debug("Trying to decode incoming bytes: {}", in);
    if (in.readableBytes() >= 22) {
      byte[] telegramData;
      in.markReaderIndex();
      // LOG.debug("Checking if it's an state request");
      telegramData = new byte[22];
      in.readBytes(telegramData);
      if (((telegramData[2] ^ telegramData[21]) & 0xff) == 0xff) {
        out.add(telegramData);
        System.out.println(bytesToHexString(telegramData));
        if (StateResponse.isStateResponse(telegramData)) {
          // 每个Vehicle的CommAdapter都处理
          responseHandlers.forEach(
              (responseHandler) -> {
                responseHandler.onIncomingTelegram(new StateResponse(telegramData));
              });
        } else if (ActionResponse.isActionResponse(telegramData)) {
          responseHandlers.forEach(
              (responseHandler) -> {
                responseHandler.onIncomingTelegram(new ActionResponse(telegramData));
              });
        } else if (NodeActionSetResponse.isNodeActionSetResponse(telegramData)) {
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

  public String bytesToHexString(byte[] bArray) {
    StringBuffer sb = new StringBuffer(bArray.length);
    String sTemp;
    for (int i = 0; i < bArray.length; i++) {
      sTemp = Integer.toHexString(0xFF & bArray[i]);
      if (sTemp.length() < 2) {
        sb.append(0);
      }
      sb.append(sTemp.toUpperCase());
      sb.append(" ");
    }
    return sb.toString();
  }

  public static String toHexString1(byte b) {
    String s = Integer.toHexString(b & 0xFF);
    if (s.length() == 1) {
      return "0" + s;
    } else {
      return s;
    }
  }
}
