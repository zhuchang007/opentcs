/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.telegrams;

import uwant.common.event.SendRequestFailedEvent;
import uwant.common.event.SendRequestSuccessEvent;
import uwant.common.event.SendRequestEvent;
import org.opentcs.util.event.EventBus;
import com.google.inject.assistedinject.Assisted;
import java.util.LinkedList;
import static java.util.Objects.requireNonNull;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

  /** This class's logger. */
  private static final Logger LOG = LoggerFactory.getLogger(RequestResponseMatcher.class);
  /** The actual queue of requests. */
  private final Queue<WrapRequest> requests = new LinkedList<>();
  /** The actual queue of requests. */
  private WrapRequest currentRequest = null;
  /** Sends the queued {@link Request}s. */
  private final TelegramSender telegramSender;

  private int sendCount = 0;

  private final EventBus eventBus;

  private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> scheduledFuture;
  
  private final String vehicleName;

  /**
   * Creates a new instance.
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
    boolean emptyQueueBeforeEnqueue = requests.isEmpty();

    LOG.debug("Enqueuing request: {}", request);
    WrapRequest wrapRequest = new WrapRequest(routeId, request);
    requests.add(wrapRequest);
    triggerNextRequestSending();
  }

  private void triggerNextRequestSending() {
    if (currentRequest != null) {
      return;
    }

    if (!requests.isEmpty()) {
      currentRequest = requests.poll();
      if (scheduledFuture != null) {
        scheduledFuture.cancel(true);
      }
      scheduledFuture =
          service.scheduleAtFixedRate(
              () -> {
                checkForSendingNextRequest();
              },
              0,
              2000,
              TimeUnit.MILLISECONDS);
    } else {
      LOG.debug("No requests to be sent.");
    }
  }

  /** Checks if a telegram is enqueued and sends it. */
  public void checkForSendingNextRequest() {
    LOG.debug("Check for sending next request.");
    if (currentRequest != null) {
      // 发送sendCount次失败后放弃本次request发送，发送下一个request
      if (sendCount == 10) {
        Request request = currentRequest.getRequest();
        LOG.info(
            "Send {} for three times failed! Give up and send next!", request.getHexRawContent());
        eventBus.onEvent(new SendRequestFailedEvent(vehicleName, request));
        currentRequest = null;
        sendCount = 0;
        return;
      }

      telegramSender.sendTelegram(currentRequest.getRequest());
      sendCount++;
      eventBus.onEvent(
          new SendRequestEvent(
              vehicleName, currentRequest.getRequest(), currentRequest.getRouteId(), sendCount));
    } else {
      triggerNextRequestSending();
    }
  }

  public Request peekRequest() {
    if (currentRequest == null) {
      return null;
    }
    return currentRequest.getRequest();
  }

  public boolean tryMatchWithCurrentRequest(@Nonnull Response response) {
    requireNonNull(response, "response");
    if (currentRequest == null) {
      return false;
    }

    Request request = currentRequest.getRequest();
    if (request.getRawContent()[3] == response.getRawContent()[3] && response.getIsOk()) {
      sendCount = 0;
      eventBus.onEvent(new SendRequestSuccessEvent(vehicleName, request));
      currentRequest = null;
      triggerNextRequestSending();
      return true;
    }

    if (currentRequest != null) {
      LOG.info(
          "No request matching response with counter {}. Latest request counter is {}.",
          response.getId(),
          currentRequest.getRequest().getId());
    } else {
      LOG.info(
          "Received response with counter {}, but no request is waiting for a response.",
          response.getId());
    }

    return false;
  }

  /** Clears all requests stored in the queue. */
  public void clear() {
    requests.clear();
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
