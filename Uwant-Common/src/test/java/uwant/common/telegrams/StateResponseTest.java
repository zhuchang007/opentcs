/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.common.telegrams;

import static uwant.common.vehicle.telegrams.StateResponse.byte2BitSet;

import java.util.BitSet;
import org.junit.Test;
import uwant.common.vehicle.telegrams.StateResponse;

public class StateResponseTest {
  @Test
  public void testStateResponse() {
    byte a = 11;
    BitSet bitSet = byte2BitSet(a);
    System.out.println(bitSet);
    System.out.println(bitSet.get(1));

    System.out.println(StateResponse.JackingState.ASCENDING.getName());

    StateResponse stateResponse = new StateResponse(
        new byte[]{
            0x00,
            0x01,
            (byte) 0x81,
            0x01,
            (byte) 0xff,
            (byte) 0xff,
            (byte) 0xff,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x01,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x21,
            0x6B,
            0x7E
        }
    );

    System.out.println(stateResponse.getInput());
  }
}
