/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.comm;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import uwant.common.telegrams.Response;
import uwant.vehicle.telegrams.ActionResponse;
import uwant.vehicle.telegrams.NodeActionSetResponse;
import uwant.vehicle.telegrams.StateResponse;

public class NetTelegramDecoder extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    // ctx.fireChannelRead(msg);
    if (!(msg instanceof byte[])) {
      return;
    }
    byte[] byteMsg = new byte[StateResponse.TELEGRAM_LENGTH];
    //    ByteBuf buf = (ByteBuf) msg;
    //    byte[] byteMsg = new byte[buf.readableBytes()];
    //    buf.readBytes(byteMsg);
    System.arraycopy((byte[]) msg, 0, byteMsg, 2, StateResponse.TELEGRAM_LENGTH - 2);
    Response response;
    StateResponse response1 = new StateResponse(byteMsg);
    //System.out.println(response1.getHexRawContent());
    if (StateResponse.isStateResponse(byteMsg)) {
      response = new StateResponse(byteMsg);
    } else if (ActionResponse.isActionResponse(byteMsg)) {
      response = new ActionResponse(byteMsg);
    } else if (NodeActionSetResponse.isNodeActionSetResponse(byteMsg)) {
      response = new NodeActionSetResponse(byteMsg);
    } else {
      response = null;
      return;
    }

    // ReferenceCountUtil.release(buf);
    ctx.fireChannelRead(response);
  }
}
