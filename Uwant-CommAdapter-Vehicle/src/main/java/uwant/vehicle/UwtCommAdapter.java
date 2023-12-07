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
package uwant.vehicle;

import javax.annotation.Nonnull;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.drivers.vehicle.MovementCommand;
import uwant.common.event.AgvOfflineEvent;
import uwant.common.event.AgvOnlineEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import uwant.common.dispatching.LoadAction;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.RequestResponseMatcherCom;
import uwant.common.telegrams.Response;
import uwant.common.event.SendRequestSuccessEvent;
import uwant.common.telegrams.TelegramSender;
import java.util.*;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.opentcs.components.kernel.services.PlantModelService;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.vehicle.comm.ChannelManager;
import uwant.vehicle.exchange.UwtProcessModelTO;
import uwant.vehicle.telegrams.ActionRequest;
import uwant.vehicle.telegrams.ActionResponse;
import uwant.vehicle.telegrams.NodeActionSetResponse;
import uwant.vehicle.telegrams.StateResponse;

/** @author zhuchang */
public class UwtCommAdapter extends BasicVehicleCommAdapter
    implements ConnectionEventListener<Response>, TelegramSender, EventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(UwtCommAdapter.class);

  private final Vehicle vehicle;
  /**
   * static com read source
   */
  private static ChannelManager vehicleChannelManager;
  /** Matches requests to responses and holds a queue for pending requests. */
  private RequestResponseMatcherCom requestResponseMatcher;

  private final UwtCommAdapterComponentsFactory uwtCommAdapterComponentsFactory;

  private long recvCount = 0;

  private final ScheduledExecutorService makeVehicleStateUnkown =
      Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> makeVehicleStateUnkownFuture;

  private EventBus eventBus;

  @Inject
  public UwtCommAdapter(
      @Assisted Vehicle vehicle,
      @Assisted ChannelManager vehicleChannelManager,
      UwtCommAdapterComponentsFactory uwtCommAdapterComponentsFactory,
      @KernelExecutor ScheduledExecutorService kernelExecutor,
      @ApplicationEventBus EventBus eventBus) {
    super(new UwtProcessModel(vehicle), 2, "CHARGE",kernelExecutor);
    UwtCommAdapter.vehicleChannelManager =
        requireNonNull(vehicleChannelManager, "vehicleChannelManager");
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.uwtCommAdapterComponentsFactory =
        requireNonNull(uwtCommAdapterComponentsFactory, "comCommAdapterComponentsFactory");
    this.eventBus = requireNonNull(eventBus, "eventBus");
  }

  @Override
  public void initialize() {
    super.initialize();
    requestResponseMatcher =
        uwtCommAdapterComponentsFactory.createRequestResponseMatcherCom(vehicle.getName(), this);
    eventBus.subscribe(this);
  }

  @Override
  public void terminate() {
    super.terminate();
  }

  @Override
  public synchronized void enable() {
    if (isEnabled()) {
      return;
    }
    if (vehicleChannelManager.getListenerCount() == 0) {
      vehicleChannelManager.initialize();
    }
    super.enable();
  }

  @Override
  public synchronized void disable() {
    if (!isEnabled()) {
      return;
    }
    super.disable();
    if (vehicleChannelManager.getListenerCount() == 0) {
      vehicleChannelManager.terminate();
    }
  }

  private boolean isRequestVehicleStart(Request request) {
    return (request.getRawContent()[3] == ActionRequest.TYPE
        && (request.getRawContent()[4] > 0 && request.getRawContent()[4] < 7));
  }

  @Override
  public synchronized ExplainedBoolean canProcess(List<String> operations) {
    return new ExplainedBoolean(true, "can process");
  }

  @Override
  public void processMessage(Object message) {
    // Process messages sent from the kernel or a kernel extension
  }

  @Override
  public synchronized void onIncomingTelegram(Response response) {
    // 如果小车返回的小车编号（用Telegram的id表示）不等于上层vehicle名字里小车编号，舍弃不处理
    if (response.getId() != parseVehicleNo()) {
      return;
    }

    // System.out.println(response);

    if (response instanceof StateResponse) {
      StateResponse stateResponse = (StateResponse) response;
      onStateResponse(stateResponse);
    } else if (response instanceof ActionResponse) {
      LOG.info("ActionResponse {} received!", response.getHexRawContent());
      // Check if the response matches the current request
      getProcessModel().setResponse(response);
      if (requestResponseMatcher.tryMatchWithCurrentRequest(response)) {
        // XXX Either ignore the message or close the connection
        // return;
        if (response.getIsOk()) {
          LOG.info("node ack received!");
        }
      }
    } else if (response instanceof NodeActionSetResponse) {
      LOG.info("NodeActionResponse {} received!", response.getHexRawContent());
      getProcessModel().setResponse(response);
      if (!requestResponseMatcher.tryMatchWithCurrentRequest(response)) {
        // return;
      }
    }

    // 从连接开始如果收到3个包，状态设置为IDLE，否则在5秒以后把车置为UNKNOWN状态
    recvCount++;
    if (recvCount == 3) {
      eventBus.onEvent(new AgvOnlineEvent(vehicle.getName()));
    }
    if (makeVehicleStateUnkownFuture != null) {
      makeVehicleStateUnkownFuture.cancel(false);
      makeVehicleStateUnkownFuture = null;
    }
    makeVehicleStateUnkownFuture =
        makeVehicleStateUnkown.schedule(
            () -> {
              eventBus.onEvent(new AgvOfflineEvent(vehicle.getName()));
            },
            5000,
            TimeUnit.MILLISECONDS);
  }

  @Override
  public void onConnect(Object object) {
    LOG.info("server connect sucessful!");
  }

  @Override
  public void onFailedConnectionAttempt() {}

  @Override
  public void onDisconnect() {}

  @Override
  public void onIdle() {}

  @Override
  public synchronized void sendTelegram(Request telegram) {
    requireNonNull(telegram, "telegram");
    if (!isVehicleConnected()) {
      LOG.debug("{}: Not connected - not sending request '{}'", getName(), telegram);
      return;
    }

    LOG.info("{}: Sending request '{}'", getName(), telegram.getHexRawContent());
    vehicleChannelManager.send(parseVehicleNo(), telegram);
  }

  @Override
  public void onEvent(Object event) {
    if (event instanceof AgvOnlineEvent) {
      AgvOnlineEvent agvOnlineEvent = (AgvOnlineEvent) event;
      if (Objects.equals(vehicle.getName(), agvOnlineEvent.getVehicleName())) {
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
      }
    } else if (event instanceof AgvOfflineEvent) {
      AgvOfflineEvent agvOfflineEvent = (AgvOfflineEvent) event;
      if (Objects.equals(vehicle.getName(), agvOfflineEvent.getVehicleName())) {
        recvCount = 0;
        if (makeVehicleStateUnkownFuture != null) {
          makeVehicleStateUnkownFuture.cancel(false);
          makeVehicleStateUnkownFuture = null;
        }
        getProcessModel().setVehicleState(Vehicle.State.UNKNOWN);
      }
    } else if (event instanceof SendRequestSuccessEvent) {
      SendRequestSuccessEvent sendRequestSuccessEvent = (SendRequestSuccessEvent) event;
      if (Objects.equals(vehicle.getName(), sendRequestSuccessEvent.getVehicleName())
          && isRequestVehicleStart(sendRequestSuccessEvent.getRequest())) {
        LOG.info(
            "Command {} successfully sent.",
            sendRequestSuccessEvent.getRequest().getHexRawContent());
//        setVehiclePosition(getProcessModel().getVehiclePosition());
      }
    }
  }

  @Override
  protected synchronized void connectVehicle() {
    vehicleChannelManager.addListener(parseVehicleNo(), this);
  }

  @Override
  protected synchronized void disconnectVehicle() {
    vehicleChannelManager.removeListener(parseVehicleNo(), this);
    eventBus.onEvent(new AgvOfflineEvent(vehicle.getName()));
  }

  @Override
  protected synchronized boolean isVehicleConnected() {
    return true;
  }

  @Override
  @Nonnull
  public final UwtProcessModel getProcessModel() {
    return (UwtProcessModel) super.getProcessModel();
  }

  @Override
  public void sendCommand(MovementCommand cmd)
      throws IllegalArgumentException {

  }

  @Override
  protected VehicleProcessModelTO createCustomTransferableProcessModel() {
    return new UwtProcessModelTO()
        .setVehicleRef(getProcessModel().getVehicleReference())
        .setPreviousState(getProcessModel().getPreviousState())
        .setCurrentState(getProcessModel().getCurrentState())
        .setResponse(getProcessModel().getResponse())
        .setRecvCount(recvCount);
  }

  private int parseVehicleNo() {
    String[] strs = vehicle.getName().split("\\D+"); // \\D是非数字
    int agvId0 = -1;
    for (String str : strs) {
      if (!"".equals(str)) {
        agvId0 = Integer.parseInt(str);
        break;
      }
    }
    return agvId0;
  }

  public RequestResponseMatcherCom getRequestResponseMatcher() {
    return requestResponseMatcher;
  }

  private void onStateResponse(StateResponse stateResponse) {
    int addr = stateResponse.getAddr();
    int agvId = stateResponse.getId();

    getProcessModel().setPreviousState(getProcessModel().getCurrentState());
    getProcessModel().setCurrentState(stateResponse);

    StateResponse previousState = getProcessModel().getPreviousState();
    StateResponse currentState = getProcessModel().getCurrentState();

//    checkForVehicleDirectionUpdate(previousState, currentState);
//    checkForVehiclePositionUpdate(previousState, currentState);
//    checkForVehicleLoadingStateUpdate(previousState, currentState);
//    checkForVehicleStateUpdate();
  }


}
