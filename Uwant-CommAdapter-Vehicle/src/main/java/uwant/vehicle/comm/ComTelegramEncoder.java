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
package uwant.vehicle.comm;

import uwant.common.telegrams.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.vehicle.telegrams.ActionRequest;

/** @author zhuchang */
public class ComTelegramEncoder extends MessageToByteEncoder<Request> {

  private static final Logger LOG = LoggerFactory.getLogger(ComTelegramEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext chc, Request i, ByteBuf bb) throws Exception {
    LOG.debug("Encoding request of class {}", i.getClass().getName());

    if (i instanceof ActionRequest) {
      ActionRequest actionRequest = (ActionRequest) i;
      LOG.debug("Encoding order telegram: {}", actionRequest.getHexRawContent());
    }

    bb.writeBytes(i.getRawContent());
  }
}
