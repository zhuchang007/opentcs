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
import uwant.common.telegrams.Response;
import uwant.common.vehicle.telegrams.ActionResponse;
import uwant.common.vehicle.telegrams.NodeActionSetResponse;
import uwant.common.vehicle.telegrams.StateResponse;

public class NetTelegramDecoder extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    // ctx.fireChannelRead(msg);
    if (!(msg instanceof byte[])) {
      return;
    }
    byte[] byteMsg = new byte[StateResponse.TELEGRAM_LENGTH];
    System.arraycopy(msg, 0, byteMsg, 2, StateResponse.TELEGRAM_LENGTH - 2);
    Response response;
    if (byteMsg[4] == StateResponse.TYPE) {
      response = new StateResponse(byteMsg);
    } else if (byteMsg[4] == ActionResponse.TYPE) {
      response = new ActionResponse(byteMsg);
    } else if (byteMsg[4] == NodeActionSetResponse.TYPE) {
      response = new NodeActionSetResponse(byteMsg);
    } else {
      return;
    }

    // ReferenceCountUtil.release(buf);
    ctx.fireChannelRead(response);
  }
}
