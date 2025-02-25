// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.workingset;

import static java.util.Objects.requireNonNull;

import de.huxhorn.sulky.ulid.ULID;
import java.util.Optional;
import org.opentcs.access.to.CreationTO;
import org.opentcs.components.kernel.ObjectNameProvider;

/**
 * Provides names for objects based on ULIDs, prefixed with the name taken from a given
 * {@link CreationTO}.
 */
public class PrefixedUlidObjectNameProvider
    implements
      ObjectNameProvider {

  /**
   * Generates ULIDs for us.
   */
  private final ULID ulid = new ULID();
  /**
   * The previously generated ULID value.
   */
  private ULID.Value previousUlid = ulid.nextValue();

  /**
   * Creates a new instance.
   */
  public PrefixedUlidObjectNameProvider() {
  }

  @Override
  public String apply(CreationTO to) {
    requireNonNull(to, "to");

    Optional<ULID.Value> newValue = ulid.nextStrictlyMonotonicValue(previousUlid);
    while (newValue.isEmpty()) {
      newValue = ulid.nextStrictlyMonotonicValue(previousUlid);
    }
    previousUlid = newValue.get();
    return to.getName() + newValue.get().toString();
  }
}
