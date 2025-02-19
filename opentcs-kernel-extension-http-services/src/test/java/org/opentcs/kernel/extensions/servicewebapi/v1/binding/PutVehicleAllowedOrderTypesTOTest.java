// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.v1.binding;

import java.util.List;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.kernel.extensions.servicewebapi.JsonBinder;

/**
 * Tests for {@link PutVehicleAllowedOrderTypesTO}.
 */
class PutVehicleAllowedOrderTypesTOTest {

  private JsonBinder jsonBinder;

  @BeforeEach
  void setUp() {
    jsonBinder = new JsonBinder();
  }

  @Test
  void jsonSample() {
    PutVehicleAllowedOrderTypesTO to
        = new PutVehicleAllowedOrderTypesTO(
            List.of(
                "some-orderType",
                "another-orderType",
                "orderType-3"
            )
        );

    Approvals.verify(jsonBinder.toJson(to));
  }
}
