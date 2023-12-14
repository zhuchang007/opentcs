package uwant.common.netty.tcp;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.Objects;

public class ClientConnectionDropNotifier
    extends ChannelDuplexHandler {

  private final ConnectionEventListener<?> connectionEventListener;

  public ClientConnectionDropNotifier(ConnectionEventListener<?> stateMessageHandler) {
    this.connectionEventListener = Objects.requireNonNull(stateMessageHandler, "stateMessageHandler");
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    this.connectionEventListener.onDisconnect();
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
      throws Exception {
    if (evt instanceof IdleStateEvent) {
      if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
        this.connectionEventListener.onIdle();
      }
    }
    else {

      super.userEventTriggered(ctx, evt);
    }
  }
}
