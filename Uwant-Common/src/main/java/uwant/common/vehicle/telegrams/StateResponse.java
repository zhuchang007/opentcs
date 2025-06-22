/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.vehicle.telegrams;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.primitives.Ints;
import uwant.common.telegrams.Response;
import static java.util.Objects.requireNonNull;

import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Represents a vehicle status response sent from the vehicle.
 *
 * @author zhangdan
 */
@SuppressWarnings("this-escape")
public class StateResponse extends Response {

  /** The response type.命令字 */
  public static final byte TYPE = 0x01;

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18nTelegrams.BUNDLE_PATH);

  private Direction direction;
  private int speed;
  private RollingState rollingState;
  private JackingState jackingState;

  private String errorCode;
  /** The id of the point at the vehicle's current position. */
  private int positionId;

  private BitSet input0;
  private BitSet input1;
  private BitSet input2;

  private static int num;

  /**
   * Creates a new instance.
   *
   * @param telegramData This telegram's raw content.
   */
  public StateResponse(byte[] telegramData) {
    super(telegramData);
    decodeTelegramContent();
  }

  /**
   * Returns the vehicle's operating state.
   *
   * @return The vehicle's operating state.
   */
  public Direction getDirection() {
    return direction;
  }

  /**
   * Returns the vehicle's load state.
   *
   * @return The vehicle's load state.
   */
  public int getSpeed() {
    return speed;
  }

  public String getSpeedText() {
    return speed + BUNDLE.getString("StateResponse.Speed.Unit");
  }

  public RollingState getRollingState() {
    return rollingState;
  }

  public JackingState getJackingState() {
    return jackingState;
  }

  /**
   * Returns the id of the last received order.
   *
   * @return The id of the last received order.
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * 返回节点位置id
   *
   * @return 节点id
   */
  public int getPositionId() {
    return positionId;
  }

  @Override
  public String toString() {
    return "StateResponse{"
        + (++num)
        + " :agvId:"
        + agvId
        + "direction:"
        + direction
        + "speed:"
        + speed
        + "rollingState:"
        + rollingState
        + "jackingState"
        + jackingState
        + "errorCode:"
        + errorCode
        + "positionId:"
        + positionId
        + "}";
  }


  protected void decodeTelegramContent() {
   super.decodeTelegramContent();
    direction = Direction.fromInt(Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[7]));
    speed = Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[8]);
    rollingState =
        RollingState.fromInt(
            Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[11]) & 0xF0 >> 4);
    jackingState =
        JackingState.fromInt(Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[11]) & 0x0F);
    errorCode = decodeIntToErrorCode(Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[18]));
    positionId = Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[19]);

    input0 = byte2BitSet(rawContent[4]);
    input1 = byte2BitSet(rawContent[5]);
    input2 = byte2BitSet(rawContent[6]);
  }

  public static BitSet byte2BitSet(byte oneByte) {
    BitSet bitSet = new BitSet(8);
    int index = 0;
    for (int j = 0; j <= 7; j++) {
      bitSet.set(index++, (oneByte & (1 << j)) >> j == 1 ? true : false);
    }
    return bitSet;
  }

  public String getInput() {
    StringBuilder input = new StringBuilder();
    int index = 0;
    for (int i = 0; i < 8; i++) {
      if (input0.get(i)) {
        input.append(INPUT0[i] + ": " + BUNDLE.getString("StateResponse.On") + ", ");
        if (index++ == 2) {
          input.append("\r\n");
          index = 0;
        }
      }
    }
    for (int i = 0; i < 8; i++) {
      if (input1.get(i)) {
        input.append(INPUT1[i] + ": " + BUNDLE.getString("StateResponse.On") + ", ");
      }
      if (index++ == 2) {
        input.append("\r\n");
        index = 0;
      }
    }
    for (int i = 0; i < 8; i++) {
      if (input2.get(i)) {
        input.append(INPUT2[i] + ": " + BUNDLE.getString("StateResponse.On") + ", ");
      }
      if (index++ == 2) {
        input.append("\r\n");
        index = 0;
      }
    }
    return input.toString();
  }

  public enum Direction {
    STOP,
    FORWARD,
    BACKWARD,
    TURNLEFT,
    TURNRIGHT,
    PATROL_FORWARD,
    PATROL_BACKWARD,
    UNKNOWN;

    private static Map<Direction, String> enumMap = new EnumMap<>(Direction.class);

    static {
      enumMap.put(STOP, BUNDLE.getString("StateResponse.Direction.STOP"));
      enumMap.put(FORWARD, BUNDLE.getString("StateResponse.Direction.FORWARD"));
      enumMap.put(BACKWARD, BUNDLE.getString("StateResponse.Direction.BACKWARD"));
      enumMap.put(TURNLEFT, BUNDLE.getString("StateResponse.Direction.TURNLEFT"));
      enumMap.put(TURNRIGHT, BUNDLE.getString("StateResponse.Direction.TURNRIGHT"));
      enumMap.put(PATROL_FORWARD, BUNDLE.getString("StateResponse.Direction.PATROL_FORWARD"));
      enumMap.put(PATROL_BACKWARD, BUNDLE.getString("StateResponse.Direction.PATROL_BACKWARD"));
      enumMap.put(UNKNOWN, BUNDLE.getString("StateResponse.Direction.UNKNOWN"));
    }

    public String getName() {
      return enumMap.get(this);
    }

    public static Direction fromInt(int direction) {
      switch (direction) {
        case 0:
          return STOP;
        case 1:
          return Direction.FORWARD;
        case 2:
          return Direction.BACKWARD;
        case 3:
          return Direction.TURNLEFT;
        case 4:
          return Direction.TURNRIGHT;
        case 5:
          return Direction.PATROL_FORWARD;
        case 6:
          return Direction.PATROL_BACKWARD;
        default:
          return Direction.UNKNOWN;
      }
    }
  }

  public enum RollingState {
    STOP,
    ROLLING_CLOCKWISE,
    ROLLING_COUNTER_CLOCKWISE,
    UNKNOWN;

    private static Map<RollingState, String> enumMap = new EnumMap<>(RollingState.class);

    static {
      enumMap.put(STOP, BUNDLE.getString("StateResponse.RollingState.STOP"));
      enumMap.put(
          ROLLING_CLOCKWISE, BUNDLE.getString("StateResponse.RollingState.ROLLING_CLOCKWISE"));
      enumMap.put(
          ROLLING_COUNTER_CLOCKWISE,
          BUNDLE.getString("StateResponse.RollingState.ROLLING_COUNTER_CLOCKWISE"));
      enumMap.put(UNKNOWN, BUNDLE.getString("StateResponse.RollingState.UNKNOWN"));
    }

    public String getName() {
      return enumMap.get(this);
    }

    public static RollingState fromInt(int state) {
      switch (state) {
        case 0:
          return RollingState.STOP;
        case 1:
          return RollingState.ROLLING_CLOCKWISE;
        case 2:
          return RollingState.ROLLING_COUNTER_CLOCKWISE;
        default:
          return RollingState.UNKNOWN;
      }
    }
  }

  public enum JackingState {
    STOP,
    ASCENDING,
    DESCENDING,
    UNKNOWN;

    private static Map<JackingState, String> enumMap = new EnumMap<>(JackingState.class);

    static {
      enumMap.put(STOP, BUNDLE.getString("StateResponse.JackingState.STOP"));
      enumMap.put(ASCENDING, BUNDLE.getString("StateResponse.JackingState.ASCENDING"));
      enumMap.put(DESCENDING, BUNDLE.getString("StateResponse.JackingState.DESCENDING"));
      enumMap.put(UNKNOWN, BUNDLE.getString("StateResponse.JackingState.UNKNOWN"));
    }

    public String getName() {
      return enumMap.get(this);
    }

    public static JackingState fromInt(int state) {
      switch (state) {
        case 0:
          return JackingState.STOP;
        case 1:
          return JackingState.ASCENDING;
        case 2:
          return JackingState.DESCENDING;
        default:
          return JackingState.UNKNOWN;
      }
    }
  }

  private String decodeIntToErrorCode(int errorCode) {
    if (errorCode < 0 || errorCode > ERRORCODE.length) {
      return ERRORCODE[ERRORCODE.length - 1];
    }
    return ERRORCODE[errorCode];
  }

  public static final String[] ERRORCODE = BUNDLE.getString("StateResponse.errorCode").split(",");
  public static final String[] INPUT0 = BUNDLE.getString("StateResponse.Input0").split(",");
  public static final String[] INPUT1 = BUNDLE.getString("StateResponse.Input1").split(",");
  public static final String[] INPUT2 = BUNDLE.getString("StateResponse.Input2").split(",");
}
