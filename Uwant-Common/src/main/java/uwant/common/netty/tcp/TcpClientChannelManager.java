package uwant.common.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.opentcs.util.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientChannelManager<O, I> {

  private static final Logger LOG = LoggerFactory.getLogger(TcpClientChannelManager.class);

  private static final String LOGGING_HANDLER_NAME = "ChannelLoggingHandler";

  private final ConnectionEventListener<I> connectionEventListener;

  private final Supplier<List<ChannelHandler>> channelSupplier;

  private final int readTimeout;

  private Bootstrap bootstrap;

  private EventLoopGroup workerGroup;

  private ChannelFuture channelFuture;

  private boolean initialized;

  private ScheduledFuture<?> connectFuture;

  private final boolean loggingEnabled;

  public TcpClientChannelManager(
      @Nonnull ConnectionEventListener<I> connEventListener,
      Supplier<List<ChannelHandler>> channelSupplier,
      int readTimeout,
      boolean enableLogging) {
    this.connectionEventListener = Objects.requireNonNull(connEventListener, "connEventListener");
    this.channelSupplier = Objects.requireNonNull(channelSupplier, "channelSupplier");
    this.readTimeout = readTimeout;
    this.loggingEnabled = enableLogging;
  }

  public void initialize() {
    if (this.initialized) {
      return;
    }

    this.bootstrap = new Bootstrap();
    this.workerGroup = (EventLoopGroup) new NioEventLoopGroup();
    this.bootstrap.group(this.workerGroup);
    this.bootstrap.channel(NioSocketChannel.class);
    this.bootstrap.option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
    this.bootstrap.option(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
    this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(10000));
    this.bootstrap.handler(
        (ChannelHandler)
            new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) {
                if (TcpClientChannelManager.this.loggingEnabled) {
                  ch.pipeline()
                      .addFirst(
                          "ChannelLoggingHandler",
                          (ChannelHandler)
                              new LoggingHandler(TcpClientChannelManager.this.getClass()));
                }
                if (TcpClientChannelManager.this.readTimeout > 0) {
                  ch.pipeline()
                      .addLast(
                          new ChannelHandler[] {
                            (ChannelHandler)
                                new IdleStateHandler(
                                    (long) TcpClientChannelManager.this.readTimeout,
                                    0L,
                                    0L,
                                    TimeUnit.MILLISECONDS)
                          });
                }
                ch.pipeline()
                    .addLast(
                        new ChannelHandler[] {
                          (ChannelHandler)
                              new ClientConnectionDropNotifier(
                                  TcpClientChannelManager.this.connectionEventListener)
                        });
                for (ChannelHandler handler : TcpClientChannelManager.this.channelSupplier.get()) {
                  ch.pipeline().addLast(new ChannelHandler[] {handler});
                }
              }
            });

    this.initialized = true;
  }

  public boolean isInitialized() {
    return this.initialized;
  }

  public void terminate() {
    if (!this.initialized) {
      return;
    }

    cancelConnect();
    disconnect();
    this.workerGroup.shutdownGracefully();
    this.workerGroup = null;
    this.bootstrap = null;

    this.initialized = false;
  }

  public void connect(@Nonnull String host, int port) {
    Objects.requireNonNull(host, "host");
    Assertions.checkState(isInitialized(), "Not initialized");
    if (isConnected()) {
      LOG.debug("Already connected, doing nothing.");

      return;
    }
    LOG.debug("Initiating connection attempt to {}:{}...", host, Integer.valueOf(port));
    //    try {
    //      this.channelFuture = this.bootstrap.connect(host, port).sync();
    //    } catch (Exception ep) {
    //      ep.printStackTrace();
    //    }
    this.channelFuture = this.bootstrap.connect(host, port);

    this.channelFuture.addListener(
        future -> {
          if (future.isSuccess()) {
            this.connectionEventListener.onConnect(null);
          } else {

            this.connectionEventListener.onFailedConnectionAttempt();
          }
        });
    this.connectFuture = null;
  }

  public void scheduleConnect(@Nonnull String host, int port, long delay) {
    Objects.requireNonNull(host, "host");
    Assertions.checkState(isInitialized(), "Not initialized");
    Assertions.checkState((this.connectFuture == null), "Connection attempt already scheduled");

    this.connectFuture =
        this.workerGroup.schedule(() -> connect(host, port), delay, TimeUnit.MILLISECONDS);
  }

  public void cancelConnect() {
    if (this.connectFuture == null) {
      return;
    }
    this.connectFuture.cancel(false);
    this.connectFuture = null;
  }

  public void disconnect() {
    if (!isConnected()) {
      return;
    }
    if (this.channelFuture != null) {
      this.channelFuture.channel().disconnect();
      this.channelFuture = null;
    }
  }

  public boolean isConnected() {
    return (this.channelFuture != null && this.channelFuture.channel().isActive());
  }

  public void send(O telegram) {
    if (!isConnected()) {
      return;
    }
    try {
      this.channelFuture.channel().writeAndFlush(telegram).sync();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setLoggingEnabled(boolean enabled) {
    Assertions.checkState(this.initialized, "Not initialized.");

    if (this.channelFuture == null) {
      LOG.debug("No channel future available, doing nothing.");

      return;
    }
    ChannelPipeline pipeline = this.channelFuture.channel().pipeline();
    if (enabled && pipeline.get("ChannelLoggingHandler") == null) {
      pipeline.addFirst("ChannelLoggingHandler", (ChannelHandler) new LoggingHandler(getClass()));
    } else if (!enabled && pipeline.get("ChannelLoggingHandler") != null) {
      pipeline.remove("ChannelLoggingHandler");
    }
  }
}
