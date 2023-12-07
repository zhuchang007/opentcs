package org.opentcs.contrib.tcp.netty;

public class ConnectionAssociatedEvent {

  private final Integer key;

  public ConnectionAssociatedEvent(Integer key) {
    this.key = key;
  }

  public Integer getKey() {
    return this.key;
  }
}
