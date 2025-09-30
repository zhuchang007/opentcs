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

import static java.util.Objects.requireNonNull;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.*;
import java.util.concurrent.*;
import javax.annotation.Nonnull;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uwant.common.netty.ChannelManager;
import uwant.common.netty.tcp.ConnectionEventListener;
import uwant.common.telegrams.Request;
import uwant.common.telegrams.RequestResponseMatcherCom;
import uwant.common.telegrams.Response;
import uwant.common.telegrams.TelegramSender;
import uwant.common.vehicle.telegrams.ActionResponse;
import uwant.common.vehicle.telegrams.NodeActionResponse;
import uwant.common.vehicle.telegrams.StateResponse;
import uwant.vehicle.exchange.UwtProcessModelTO;

/**
 * @author zhuchang
 */
@SuppressWarnings("deprecation")
public class UwtCommAdapter
    extends
      BasicVehicleCommAdapter
    implements
      ConnectionEventListener<Response>,
      TelegramSender {

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

  private final Timer checkConnectedTimer = new Timer();

  @Inject
  public UwtCommAdapter(
      @Assisted
      Vehicle vehicle,
      @Assisted
      ChannelManager vehicleChannelManager,
      UwtCommAdapterComponentsFactory uwtCommAdapterComponentsFactory,
      @KernelExecutor
      ScheduledExecutorService kernelExecutor,
      @ApplicationEventBus
      EventBus eventBus
  ) {
    super(new UwtProcessModel(vehicle), 2, "CHARGE", kernelExecutor);
    UwtCommAdapter.vehicleChannelManager = requireNonNull(
        vehicleChannelManager, "vehicleChannelManager"
    );
    this.vehicle = requireNonNull(vehicle, "vehicle");
    this.uwtCommAdapterComponentsFactory = requireNonNull(
        uwtCommAdapterComponentsFactory, "comCommAdapterComponentsFactory"
    );
    this.eventBus = requireNonNull(eventBus, "eventBus");
    this.agvId = parseAgvId();
  }

  @Override
  public void initialize() {
    super.initialize();
    requestResponseMatcher = uwtCommAdapterComponentsFactory.createRequestResponseMatcherCom(
        vehicle.getName(), this
    );
    checkConnectedTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        getProcessModel().setCommAdapterConnected(lastRecvCount != recvCount);
        lastRecvCount = recvCount;
      }
    }, 0, 3000);
  }

  @Override
  public void terminate() {
    super.terminate();
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
    else if (response instanceof ActionResponse || response instanceof NodeActionResponse) {
      requestResponseMatcher.tryMatchWithCurrentRequest(response);
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
  protected synchronized void connectVehicle() {
    //because of sharing only one channel, so initializing it only once
    if (vehicleChannelManager.getListenerCount() == 0) {
      vehicleChannelManager.initialize();
    }
    vehicleChannelManager.addListener(agvId, this);
  }

  @Override
  protected synchronized void disconnectVehicle() {
    vehicleChannelManager.removeListener(agvId, this);
    if (vehicleChannelManager.getListenerCount() == 0) {
      vehicleChannelManager.terminate();
    }
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
  public ExplainedBoolean canProcess(TransportOrder order) {
    return new ExplainedBoolean(true, "can process");
  }

  @Override
  public void onVehiclePaused(boolean paused) {

  }

  @Override
  public void sendCommand(MovementCommand cmd)
      throws IllegalArgumentException {

  }

  @Override
  protected VehicleProcessModelTO createCustomTransferableProcessModel() {
    return new UwtProcessModelTO()
        .setCurrentState(getProcessModel().getCurrentState())
        .setResponse(getProcessModel().getResponse());
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
//    int addr = stateResponse.getAddr();
//    int agvId = stateResponse.getAgvId();

    getProcessModel().setCurrentState(stateResponse);

//    checkForVehicleDirectionUpdate(previousState, currentState);
//    checkForVehiclePositionUpdate(previousState, currentState);
//    checkForVehicleLoadingStateUpdate(previousState, currentState);
//    checkForVehicleStateUpdate();
  }

}
