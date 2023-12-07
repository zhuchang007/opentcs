package org.opentcs.contrib.tcp.netty;

import io.netty.channel.Channel;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientEntry<I> {

  private final Integer key;
  private final ConnectionEventListener<I> connectionEventListener;
  private Channel channel;

  public ClientEntry(
      @Nonnull Integer key, @Nonnull ConnectionEventListener<I> connectionEventListener) {
    this.key = Objects.requireNonNull(key, "key");
    this.connectionEventListener = Objects.requireNonNull(connectionEventListener);
  }

  @Nonnull
  public Integer getKey() {
    return this.key;
  }

  @Nonnull
  public ConnectionEventListener<I> getConnectionEventListener() {
    return this.connectionEventListener;
  }

  @Nullable
  public Channel getChannel() {
    return this.channel;
  }

  public void setChannel(@Nullable Channel channel) {
    this.channel = channel;
  }

  public boolean isConnected() {
    return (this.channel != null && this.channel.isActive());
  }

  public void disconnect() {
    if (!isConnected()) {
      return;
    }
    this.channel.disconnect();
    this.channel = null;
  }
}
