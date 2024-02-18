/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.telegrams;

import java.util.concurrent.*;
import uwant.common.event.SendRequestEvent;
import org.opentcs.util.event.EventBus;
import com.google.inject.assistedinject.Assisted;
import java.util.LinkedList;
import static java.util.Objects.requireNonNull;
import java.util.Queue;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.opentcs.customizations.ApplicationEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps {@link Request}s in a queue and matches them with incoming {@link Response}s.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class RequestResponseMatcherCom {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(RequestResponseMatcher.class);
  /**
   * The actual queue of requests.
   */
  private final Queue<WrapRequest> requests = new LinkedList<>();
  /**
   * The actual queue of requests.
   */
  private WrapRequest sendingRequest = null;
  /**
   * Sends the queued {@link Request}s.
   */
  private final TelegramSender telegramSender;

  private int sendCount = 0;

  private final EventBus eventBus;

  private ExecutorService service;

  private final String vehicleName;

  /**
   * Creates a new instance.
   *
   * @param vehicleName
   * @param telegramSender Sends the queued {@link Request}s.
   * @param eventBus
   */
  @Inject
  public RequestResponseMatcherCom(
      @Assisted String vehicleName,
      @Assisted TelegramSender telegramSender, @Nonnull @ApplicationEventBus EventBus eventBus) {
    this.vehicleName = vehicleName;
    this.telegramSender = requireNonNull(telegramSender, "telegramSender");
    this.eventBus = requireNonNull(eventBus, "eventBus");
  }

  public void enqueueRequest(@Nonnull Request request, int routeId) {
    requireNonNull(request, "request");
    LOG.debug("Enqueuing request: {}", request);
    WrapRequest wrapRequest = new WrapRequest(routeId, request);
    requests.add(wrapRequest);
    triggerNextRequestSending();
  }

  private void triggerNextRequestSending() {
    if (sendingRequest == null && !requests.isEmpty()) {
      sendingRequest = requests.poll();
      service = Executors.newSingleThreadExecutor();
      service.execute(this::sendingNextRequest);
    }
  }

  /**
   * Checks if a telegram is enqueued and sends it.
   */
  private void sendingNextRequest() {
    // 发送sendCount次失败后放弃本次request发送，发送下一个request
    try {
      while (sendingRequest != null) {
        telegramSender.sendTelegram(sendingRequest.getRequest());
        synchronized (this) {
          sendCount++;
        }
        TimeUnit.MILLISECONDS.sleep(2000);
        if (sendCount == 3) {
          LOG.info("Send {} for 3 times failed! Give up and send next!", sendingRequest.getRequest().getHexRawContent()); //todo-zc
          eventBus.onEvent(new SendRequestEvent(vehicleName, sendingRequest.getRequest(), sendingRequest.getRouteId(), sendCount, false));
          resetSendingRequest();
          triggerNextRequestSending();
        }
      }
    } catch (InterruptedException e) {
      LOG.debug("sending thread sleep interrupted!");
    }
  }

  public void tryMatchWithCurrentRequest(@Nonnull Response response) {
    requireNonNull(response, "response");
    if (sendingRequest != null && response.isResponseSuccessfulTo(sendingRequest.getRequest())) {
      LOG.info("send successful: " + sendCount); //todo-zc
      eventBus.onEvent(new SendRequestEvent(vehicleName, sendingRequest.getRequest(), sendingRequest.getRouteId(), sendCount, true));
      resetSendingRequest();
      service.shutdownNow();
      triggerNextRequestSending();
    }
  }

  private synchronized  void resetSendingRequest() {
    sendingRequest = null;
    sendCount = 0;
  }

  public static class WrapRequest {
    private final int routeId; // 如果routeId是-1，则是非调度命令
    private final Request request;

    public WrapRequest(int routeId, Request request) {
      this.routeId = routeId;
      this.request = request;
    }

    public int getRouteId() {
      return routeId;
    }

    public Request getRequest() {
      return request;
    }
  }
}
