/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.telegrams;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.primitives.Ints;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import uwant.common.vehicle.telegrams.StateResponse;

/**
 * A response represents an answer of a vehicle control to a request sent by the control system.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
@SuppressWarnings("this-escape")
public class Response extends Telegram {
  protected boolean isOK;
  /**
   * Creates a new instance.
   */
  public Response(byte[] telegramData) {
    super(telegramData);
    decodeTelegramContent();
  }

  /**
   * Checks whether this is a response to the given request.
   *
   * <p>This implementation only checks for matching telegram ids. Subclasses may want to extend
   * this check.
   *
   * @param request The request to check with.
   * @return {@code true} if, and only if, the given request's id matches this response's id.
   */
  public boolean isResponseSuccessfulTo(@Nonnull Request request) {
    requireNonNull(request, "request");
    return request.getAgvId() == getAgvId() && request.getCommandType() == getCommandType() && isOK;
  }

  protected void decodeTelegramContent() {
    addr = (rawContent[0] << 8) | (rawContent[1]);
    agvId = Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[2]) ^ 0x80;
    if(this.getCommandType() != StateResponse.TYPE) {
      isOK = Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, rawContent[4]) == 1;
    }
  }
}
