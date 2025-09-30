package uwant.common.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.opentcs.util.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerChannelManager<I, O> {

  private static final Logger LOG = LoggerFactory.getLogger(TcpServerChannelManager.class);

  private static final String LOGGING_HANDLER_NAME = "ChannelLoggingHandler";

  private ServerBootstrap bootstrap;

  private final int port;

  private final Map<Integer, ClientEntry<I>> clientEntries;

  private final Supplier<List<ChannelHandler>> channelSupplier;

  private final boolean loggingInitially;

  private ChannelFuture serverChannelFuture;

  private boolean initialized;

  private final int readTimeout;

  public TcpServerChannelManager(
      int port,
      Map<Integer, ClientEntry<I>> clientEntries,
      Supplier<List<ChannelHandler>> channelSupplier,
      int readTimeout,
      boolean loggingInitially
  ) {
    Assertions.checkArgument((port > 0), "port <= 0: %s", new Object[]{Integer.valueOf(port)});
    this.port = port;
    this.clientEntries = Objects.requireNonNull(clientEntries, "clientEntries");
    this.channelSupplier = Objects.requireNonNull(channelSupplier, "channelSupplier");
    Assertions.checkArgument(
        (readTimeout >= 0), "readTimeout < 0: %s", new Object[]{Integer.valueOf(readTimeout)}
    );
    this.readTimeout = readTimeout;
    this.loggingInitially = loggingInitially;
  }

  public void initialize() {
    if (this.initialized) {
      return;
    }

    this.bootstrap = new ServerBootstrap();
    this.bootstrap.group(
        (EventLoopGroup) new NioEventLoopGroup(), (EventLoopGroup) new NioEventLoopGroup()
    );
    this.bootstrap.channel(NioServerSocketChannel.class);
    this.bootstrap.option(ChannelOption.SO_BACKLOG, Integer.valueOf(1));
    this.bootstrap.childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
    this.bootstrap.childOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
    this.bootstrap.childHandler(
        (ChannelHandler) new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            if (TcpServerChannelManager.this.loggingInitially) {
              ch.pipeline()
                  .addFirst(
                      "ChannelLoggingHandler",
                      (ChannelHandler) new LoggingHandler(TcpServerChannelManager.this.getClass())
                  );
            }
            if (TcpServerChannelManager.this.readTimeout > 0) {
              ch.pipeline()
                  .addLast(
                      new ChannelHandler[]{
                          (ChannelHandler) new IdleStateHandler(
                              (long) TcpServerChannelManager.this.readTimeout,
                              0L,
                              0L,
                              TimeUnit.MILLISECONDS
                          )
                      }
                  );
            }
            for (ChannelHandler handler : TcpServerChannelManager.this.channelSupplier.get()) {
              ch.pipeline().addLast(new ChannelHandler[]{handler});
            }
            ch.pipeline()
                .addLast(
                    new ServerConnectionStateNotifier<>(
                        TcpServerChannelManager.this.clientEntries
                    )
                );
          }
        }
    );

    //    try {
    //      this.serverChannelFuture = this.bootstrap.bind(this.port).sync();
    //    } catch (Exception ep) {
    //      ep.printStackTrace();
    //    }
    this.serverChannelFuture = this.bootstrap.bind(this.port);

    this.initialized = true;
  }

  public void terminate() {
    if (!this.initialized) {
      return;
    }

    this.serverChannelFuture.channel().close();
    this.serverChannelFuture = null;
    for (ClientEntry<I> clientEntry : this.clientEntries.values()) {
      clientEntry.disconnect();
    }
    this.clientEntries.clear();
    this.bootstrap.config().group().shutdownGracefully();
    this.bootstrap.config().childGroup().shutdownGracefully();

    this.initialized = false;
  }

  public boolean isInitialized() {
    return this.initialized;
  }

  public void register(
      Integer key, ConnectionEventListener<I> connectionEventListener, boolean enableLogging
  ) {
    Assertions.checkState(this.initialized, "Not initialized.");

    if (this.clientEntries.containsKey(key)) {
      LOG.warn("A handler for '{}' is already registered.", key);

      return;
    }
    LOG.debug("Registering handler for client '{}'", key);
    this.clientEntries.put(key, new ClientEntry<>(key, connectionEventListener));
  }

  public void unregister(Integer key) {
    Assertions.checkState(this.initialized, "Not initialized.");

    ClientEntry<I> client = this.clientEntries.remove(key);
    if (client != null) {
      client.disconnect();
    }
  }

  public void reregister(
      Integer key, ConnectionEventListener<I> messageHandler, boolean enableLogging
  ) {
    unregister(key);
    register(key, messageHandler, enableLogging);
  }

  public void closeClientConnection(Object key) {
    Assertions.checkState(this.initialized, "Not initialized.");

    if (isClientConnected(key)) {
      LOG.debug("Closing connection to client {}", key);
      ((ClientEntry) this.clientEntries.get(key)).getChannel().disconnect();
      ((ClientEntry) this.clientEntries.get(key)).setChannel(null);
    }
  }

  public boolean isClientConnected(Object key) {
    return (this.serverChannelFuture != null
        && this.clientEntries.containsKey(key)
        && ((ClientEntry) this.clientEntries.get(key)).getChannel() != null
        && ((ClientEntry) this.clientEntries.get(key)).getChannel().isActive());
  }

  public void send(Object key, O telegram) {
    Assertions.checkState(this.initialized, "Not initialized.");

    if (!isClientConnected(key)) {
      LOG.warn("Failed sending telegram {}. {} is not connected.", telegram, key);
      return;
    }
    LOG.debug("Sending telegram {} to {}.", telegram, key);

    ((ClientEntry) this.clientEntries.get(key)).getChannel().writeAndFlush(telegram);
  }

  public void setLoggingEnabled(Object key, boolean enabled) {
    Assertions.checkState(this.initialized, "Not initialized.");

    ClientEntry<I> entry = this.clientEntries.get(key);
    Assertions.checkArgument(
        (entry != null), "No client registered for key '%s'", new Object[]{key}
    );

    Channel channel = entry.getChannel();
    if (channel == null) {
      LOG.debug("No channel/pipeline for key '%s', doing nothing.");

      return;
    }
    ChannelPipeline pipeline = channel.pipeline();
    if (enabled && pipeline.get("ChannelLoggingHandler") == null) {
      pipeline.addFirst("ChannelLoggingHandler", (ChannelHandler) new LoggingHandler(getClass()));
    }
    else if (!enabled && pipeline.get("ChannelLoggingHandler") != null) {
      pipeline.remove("ChannelLoggingHandler");
    }
  }

  public int getPort() {
    return this.port;
  }

  public int getReadTimeout() {
    return this.readTimeout;
  }
}
