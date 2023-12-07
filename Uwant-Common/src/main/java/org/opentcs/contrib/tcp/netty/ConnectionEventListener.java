package org.opentcs.contrib.tcp.netty;

public interface ConnectionEventListener<T> {

  void onIncomingTelegram(T paramT);

  void onConnect(Object object);

  void onFailedConnectionAttempt();

  void onDisconnect();

  void onIdle();
}
