package uwant.common.netty.tcp;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConnectionStateNotifier<I> extends ChannelDuplexHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ServerConnectionStateNotifier.class);

  private final Map<Integer, ClientEntry<I>> clientEntries;

  private Integer key;

  private ConnectionEventListener<I> connectionEventListener;

  public ServerConnectionStateNotifier(Map<Integer, ClientEntry<I>> clientEntries) {
    this.clientEntries = Objects.requireNonNull(clientEntries, "clientEntries");
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    if (this.connectionEventListener != null) {
      LOG.debug("Disconnecting channel for key: '{}'.", this.key);
      ClientEntry<I> entry = this.clientEntries.get(this.key);
      if (entry != null) {
        entry.setChannel(null);
      }
      this.connectionEventListener.onDisconnect();
    }
    ctx.fireChannelInactive();
  }

  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE
          && this.connectionEventListener != null) {
        this.connectionEventListener.onIdle();
      }
    } else if (evt instanceof ConnectionAssociatedEvent) {
      this.key = ((ConnectionAssociatedEvent) evt).getKey();
      LOG.debug("Connection associated to key: '{}'", this.key);
      this.connectionEventListener =
          this.clientEntries.get(this.key).getConnectionEventListener();
      this.connectionEventListener.onConnect(this.key);
    }
    super.userEventTriggered(ctx, evt);
  }
}
