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

import java.util.concurrent.*;
import javax.annotation.Nonnull;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.drivers.vehicle.MovementCommand;
import uwant.common.event.AgvOfflineEvent;
import uwant.common.event.AgvOnlineEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.RequestResponseMatcherCom;
import uwant.common.telegrams.Response;
import uwant.common.event.SendRequestSuccessEvent;
import uwant.common.telegrams.TelegramSender;
import java.util.*;
import static java.util.Objects.requireNonNull;
import uwant.common.netty.tcp.ConnectionEventListener;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.netty.ChannelManager;
import uwant.vehicle.exchange.UwtProcessModelTO;
import uwant.common.vehicle.telegrams.ActionRequest;
import uwant.common.vehicle.telegrams.ActionResponse;
import uwant.common.vehicle.telegrams.NodeActionSetResponse;
import uwant.common.vehicle.telegrams.StateResponse;

/**
 * @author zhuchang
 */
public class UwtCommAdapter
    extends BasicVehicleCommAdapter
    implements ConnectionEventListener<Response>,
               TelegramSender,
               EventHandler {

  private static final Logger LOG = LoggerFactory.getLogger(UwtCommAdapter.class);

  private final Vehicle vehicle;
  private final int agvId;
  /**
   * static com read source, all the adapters share only one channel
   */
  private static ChannelManager vehicleChannelManager;
  /**
   * Matches requests to responses and holds a queue for pending requests.
   */
  private RequestResponseMatcherCom requestResponseMatcher;

  private final UwtCommAdapterComponentsFactory uwtCommAdapterComponentsFactory;

  private long recvCount = 0;
  private long lastRecvCount = 0;

  private EventBus eventBus;

  @Inject
  public UwtCommAdapter(
      @Assisted Vehicle vehicle,
      @Assisted ChannelManager vehicleChannelManager,
      UwtCommAdapterComponentsFactory uwtCommAdapterComponentsFactory,
      @KernelExecutor ScheduledExecutorService kernelExecutor,
      @ApplicationEventBus EventBus eventBus) {
    super(new UwtProcessModel(vehicle), 2, "CHARGE", kernelExecutor);
    UwtCommAdapter.vehicleChannelManager =
        requireNonNull(vehicleChannelManager, "vehicleChannelManager");
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.uwtCommAdapterComponentsFactory =
        requireNonNull(uwtCommAdapterComponentsFactory, "comCommAdapterComponentsFactory");
    this.eventBus = requireNonNull(eventBus, "eventBus");
    this.agvId = parseAgvId();
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
    //because of sharing only one channel, so initializing it only once
    if (vehicleChannelManager.getListenerCount() == 0) {
      vehicleChannelManager.initialize();
    }
    //每隔3秒检测小车是否一直在发包
    ((ScheduledExecutorService) getExecutor()).scheduleAtFixedRate(()->{
      synchronized (this) {
        if (lastRecvCount == recvCount) {
          eventBus.onEvent(new AgvOfflineEvent(vehicle.getName()));
        } else {
          eventBus.onEvent(new AgvOnlineEvent(vehicle.getName()));
        }
        lastRecvCount = recvCount;
      }
    },0,3000,TimeUnit.MILLISECONDS);
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
    // 通过小车编号来区分是不是本小车收到的对应包
    if (response.getAgvId() != agvId) {
      return;
    }

    if (response instanceof StateResponse) {
      StateResponse stateResponse = (StateResponse) response;
      onStateResponse(stateResponse);
    }
    else {
      if ((response instanceof ActionResponse || response instanceof NodeActionSetResponse)) {
        requestResponseMatcher.tryMatchWithCurrentRequest(response);
      }
    }

    recvCount++;

  }

  @Override
  public void onConnect(Object object) {
    LOG.info("server connect sucessful!");
  }

  @Override
  public void onFailedConnectionAttempt() {
  }

  @Override
  public void onDisconnect() {
  }

  @Override
  public void onIdle() {
  }

  @Override
  public synchronized void sendTelegram(Request telegram) {
    requireNonNull(telegram, "telegram");
    if (!isVehicleConnected()) {
      LOG.debug("{}: Not connected - not sending request '{}'", getName(), telegram);
      return;
    }

    LOG.info("{}: Sending request '{}'", getName(), telegram.getHexRawContent());
    vehicleChannelManager.send(agvId, telegram);
  }

  @Override
  public void onEvent(Object event) {
    if (event instanceof AgvOnlineEvent) {
      AgvOnlineEvent agvOnlineEvent = (AgvOnlineEvent) event;
      if (Objects.equals(vehicle.getName(), agvOnlineEvent.getVehicleName())) {
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
      }
    }
    else if (event instanceof AgvOfflineEvent) {
      AgvOfflineEvent agvOfflineEvent = (AgvOfflineEvent) event;
      if (Objects.equals(vehicle.getName(), agvOfflineEvent.getVehicleName())) {
        getProcessModel().setVehicleState(Vehicle.State.UNKNOWN);
      }
    }
    else if (event instanceof SendRequestSuccessEvent) {
      SendRequestSuccessEvent sendRequestSuccessEvent = (SendRequestSuccessEvent) event;
      if (Objects.equals(vehicle.getName(), sendRequestSuccessEvent.getVehicleName())) {
        LOG.info(
            "Command {} successfully sent.",
            sendRequestSuccessEvent.getRequest().getHexRawContent());
      }
    }
  }

  @Override
  protected synchronized void connectVehicle() {
    vehicleChannelManager.addListener(agvId, this);
  }

  @Override
  protected synchronized void disconnectVehicle() {
    vehicleChannelManager.removeListener(agvId, this);
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

  private int parseAgvId() {
    String[] strs = vehicle.getName().split("\\D+"); // \\D是非数字
    int agvId = -1;
    for (String str : strs) {
      if (!"".equals(str)) {
        agvId = Integer.parseInt(str);
        break;
      }
    }
    return agvId;
  }

  public RequestResponseMatcherCom getRequestResponseMatcher() {
    return requestResponseMatcher;
  }

  private void onStateResponse(StateResponse stateResponse) {
    int addr = stateResponse.getAddr();
    int agvId = stateResponse.getAgvId();

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
